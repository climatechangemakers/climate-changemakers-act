--
-- PostgreSQL database dump
--

-- Dumped from database version 12.5
-- Dumped by pg_dump version 13.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: analysis; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA analysis;


--
-- Name: dbt_mchang; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA dbt_mchang;


--
-- Name: citext; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;


--
-- Name: EXTENSION citext; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION citext IS 'data type for case-insensitive character strings';


--
-- Name: action; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.action AS ENUM (
    'PHONE_CALLS',
    'CONSTITUENT_CONTACT',
    'PERSONAL_MEETING',
    'SOCIAL_MEDIA_CONTACT',
    'TOWN_HALL',
    'LOBBY_MEETING',
    'RELATIONAL_ORGANIZING_PERSONAL_MESSAGE',
    'RELATIONAL_ORGANIZING_SOCIAL_MEDIA',
    'BLOG_POST',
    'ARTICLE_BY_REPORTER',
    'PODCAST',
    'TV_BROADCAST',
    'LETTER_TO_THE_EDITOR',
    'OP_ED',
    'EDITORIAL',
    'PERSONALIZED_TALKING_POINTS',
    'PHONE_BANKING',
    'TEXT_BANKING',
    'FUNDRAISING',
    'OTHER'
);


--
-- Name: action_intent; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.action_intent AS ENUM (
    'ADVOCACY',
    'ELECTORAL'
);


--
-- Name: action_source; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.action_source AS ENUM (
    'HOUR_OF_ACTION',
    'ANYTIME_ACTION'
);


--
-- Name: audience; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.audience AS ENUM (
    'POLICYMAKER',
    'STAKEHOLDER',
    'PUBLIC',
    'OTHER'
);


--
-- Name: hoa_event_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.hoa_event_type AS ENUM (
    'ADV_PERSONALIZE_TALKING_POINTS_AND_PERSONAL_NETWORK_OUTREACH',
    'ADV_POLICYMAKER_OUTREACH',
    'ADV_KEY_STAKEHOLDER_AND_PUBLIC_OUTREACH',
    'ADV_CLIMATE_CONVERSATION_WITH_POLICYMAKER',
    'ELECTORAL'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: actions_raw; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.actions_raw (
    email public.citext,
    full_name character varying(256),
    date date NOT NULL,
    action public.action NOT NULL,
    intent public.action_intent NOT NULL,
    count integer,
    source public.action_source NOT NULL,
    audience public.audience,
    other_form_inputs jsonb,
    form_response_id character varying(256),
    CONSTRAINT positive_count CHECK ((count > 0))
);


--
-- Name: COLUMN actions_raw.count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.actions_raw.count IS 'Update the advocacy response form to allow only positive counts';


--
-- Name: hoa_events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hoa_events (
    id integer NOT NULL,
    date date NOT NULL,
    campaign character varying(256) NOT NULL,
    type public.hoa_event_type NOT NULL
);


--
-- Name: actions; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.actions AS
 SELECT (actions_raw.date - (date_part('dow'::text, actions_raw.date))::integer) AS hoa_week,
    actions_raw.date,
    actions_raw.email,
    actions_raw.full_name,
    actions_raw.action,
    actions_raw.intent,
    actions_raw.count,
    actions_raw.source,
    actions_raw.audience,
    hoa_events.campaign,
    hoa_events.type
   FROM (public.actions_raw
     LEFT JOIN public.hoa_events ON ((actions_raw.date = hoa_events.date)));


--
-- Name: contacts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contacts (
    email character varying(256),
    airtable_id character varying(256),
    first_name character varying(256),
    last_name character varying(256),
    slack_member_id character varying(256),
    signup_date timestamp with time zone,
    slack_joined_date timestamp with time zone,
    slack_last_active_date timestamp with time zone,
    state character varying(32),
    referred_by character varying(256),
    one_on_one_status character varying(256),
    one_on_one_greeter character varying(256),
    is_experienced boolean,
    mailchimp_status character varying(256)
);


--
-- Name: luma_attendance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.luma_attendance (
    event_id integer,
    email public.citext NOT NULL,
    signedup_at timestamp with time zone,
    did_join_event boolean NOT NULL
);


--
-- Name: hoa_attendance; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.hoa_attendance AS
 WITH pre_luma_actions AS (
         SELECT actions_raw.email,
            actions_raw.full_name,
            actions_raw.date,
            actions_raw.action,
            actions_raw.intent,
            actions_raw.count,
            actions_raw.source,
            actions_raw.audience,
            actions_raw.other_form_inputs,
            actions_raw.form_response_id
           FROM public.actions_raw
          WHERE ((actions_raw.date < '2021-01-31'::date) AND (actions_raw.source = 'HOUR_OF_ACTION'::public.action_source))
        ), attendance_from_actions AS (
         SELECT pre_luma_actions.date,
            NULL::integer AS event_id,
            pre_luma_actions.full_name,
            lower((pre_luma_actions.email)::text) AS email,
            COALESCE(c1.airtable_id, c2.airtable_id) AS airtable_id
           FROM ((pre_luma_actions
             LEFT JOIN public.contacts c1 ON ((lower((pre_luma_actions.email)::text) = lower((c1.email)::text))))
             LEFT JOIN public.contacts c2 ON ((lower((pre_luma_actions.full_name)::text) = ((lower((c2.first_name)::text) || ' '::text) || lower((c2.last_name)::text)))))
        ), attendance_from_luma AS (
         SELECT hoa_events.date,
            hoa_events.id AS event_id,
            NULL::text AS full_name,
            lower((luma_attendance.email)::text) AS email,
            contacts.airtable_id
           FROM ((public.luma_attendance
             JOIN public.hoa_events ON ((luma_attendance.event_id = hoa_events.id)))
             LEFT JOIN public.contacts ON ((lower((luma_attendance.email)::text) = lower((contacts.email)::text))))
          WHERE luma_attendance.did_join_event
        ), attendance_by_date AS (
         SELECT attendance_from_actions.date,
            attendance_from_actions.event_id,
            attendance_from_actions.full_name,
            attendance_from_actions.email,
            attendance_from_actions.airtable_id
           FROM attendance_from_actions
        UNION
         SELECT attendance_from_luma.date,
            attendance_from_luma.event_id,
            attendance_from_luma.full_name,
            attendance_from_luma.email,
            attendance_from_luma.airtable_id
           FROM attendance_from_luma
        )
 SELECT DISTINCT attendance_by_date.date,
    attendance_by_date.event_id,
    attendance_by_date.full_name,
    lower(attendance_by_date.email) AS email,
    attendance_by_date.airtable_id
   FROM attendance_by_date;


--
-- Name: hoa_attendance_by_hoa_week; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.hoa_attendance_by_hoa_week AS
 SELECT DISTINCT (hoa_attendance.date - (date_part('dow'::text, hoa_attendance.date))::integer) AS hoa_week,
    hoa_attendance.full_name,
    hoa_attendance.email,
    hoa_attendance.airtable_id
   FROM analysis.hoa_attendance;


--
-- Name: hoa_segments; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.hoa_segments AS
 WITH attendance AS (
         SELECT h.date,
            NULL::text AS airtable_id,
            l.email
           FROM (public.hoa_events h
             JOIN public.luma_attendance l ON (((l.event_id = h.id) AND (l.did_join_event IS TRUE))))
        ), merged_members AS (
         SELECT c.airtable_id,
            sum(
                CASE
                    WHEN (a.date >= (now() - '60 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_two_months,
            sum(
                CASE
                    WHEN (a.date >= (now() - '30 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_one_month
           FROM (public.contacts c
             LEFT JOIN attendance a ON ((((a.email)::text = (c.email)::text) OR (a.airtable_id = (c.airtable_id)::text))))
          GROUP BY c.airtable_id
        )
 SELECT merged_members.airtable_id,
        CASE
            WHEN (merged_members.hoas_in_past_two_months = 0) THEN 0
            WHEN ((merged_members.hoas_in_past_two_months > 0) AND (merged_members.hoas_in_past_one_month = 0)) THEN 1
            WHEN (merged_members.hoas_in_past_one_month = 1) THEN 2
            WHEN (merged_members.hoas_in_past_one_month = 2) THEN 3
            WHEN (merged_members.hoas_in_past_one_month >= 3) THEN 4
            ELSE '-1'::integer
        END AS segment
   FROM merged_members;


--
-- Name: yellow_brick_road; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.yellow_brick_road AS
 WITH merged_members AS (
         SELECT c.airtable_id,
            (c.signup_date IS NOT NULL) AS did_signup,
            (c.slack_joined_date IS NOT NULL) AS did_join_slack,
            count(a.date) AS num_hoas_attended
           FROM (public.contacts c
             LEFT JOIN analysis.hoa_attendance a ON (((c.airtable_id)::text = (a.airtable_id)::text)))
          GROUP BY c.airtable_id, (c.signup_date IS NOT NULL), (c.slack_joined_date IS NOT NULL)
        ), member_stats AS (
         SELECT merged_members.airtable_id,
            merged_members.did_signup,
            merged_members.did_join_slack,
            merged_members.num_hoas_attended,
                CASE
                    WHEN (merged_members.num_hoas_attended >= 5) THEN '7: Attended 5th HoA'::text
                    WHEN (merged_members.num_hoas_attended = 4) THEN '6: Attended 4th HoA'::text
                    WHEN (merged_members.num_hoas_attended = 3) THEN '5: Attended 3rd HoA'::text
                    WHEN (merged_members.num_hoas_attended = 2) THEN '4: Attended 2nd HoA'::text
                    WHEN (merged_members.num_hoas_attended = 1) THEN '3: Attended 1st HoA'::text
                    WHEN merged_members.did_join_slack THEN '2: Joined Slack'::text
                    ELSE '1: Signed Up'::text
                END AS progress
           FROM merged_members
        )
 SELECT member_stats.airtable_id,
    member_stats.did_signup,
    member_stats.did_join_slack,
    member_stats.num_hoas_attended,
    member_stats.progress
   FROM member_stats;


--
-- Name: actions; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.actions AS
 SELECT (actions_raw.date - (date_part('dow'::text, actions_raw.date))::integer) AS hoa_week,
    actions_raw.date,
    actions_raw.email,
    actions_raw.full_name,
    actions_raw.action,
    actions_raw.intent,
    actions_raw.count,
    actions_raw.source,
    actions_raw.audience,
    hoa_events.campaign,
    hoa_events.type
   FROM (public.actions_raw
     LEFT JOIN public.hoa_events ON ((actions_raw.date = hoa_events.date)));


--
-- Name: hoa_attendance; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.hoa_attendance AS
 WITH pre_luma_actions AS (
         SELECT actions_raw.email,
            actions_raw.full_name,
            actions_raw.date,
            actions_raw.action,
            actions_raw.intent,
            actions_raw.count,
            actions_raw.source,
            actions_raw.audience,
            actions_raw.other_form_inputs,
            actions_raw.form_response_id
           FROM public.actions_raw
          WHERE ((actions_raw.date < '2021-01-31'::date) AND (actions_raw.source = 'HOUR_OF_ACTION'::public.action_source))
        ), attendance_from_actions AS (
         SELECT pre_luma_actions.date,
            NULL::integer AS event_id,
            pre_luma_actions.full_name,
            lower((pre_luma_actions.email)::text) AS email,
            COALESCE(c1.airtable_id, c2.airtable_id) AS airtable_id
           FROM ((pre_luma_actions
             LEFT JOIN public.contacts c1 ON ((lower((pre_luma_actions.email)::text) = lower((c1.email)::text))))
             LEFT JOIN public.contacts c2 ON ((lower((pre_luma_actions.full_name)::text) = ((lower((c2.first_name)::text) || ' '::text) || lower((c2.last_name)::text)))))
        ), attendance_from_luma AS (
         SELECT hoa_events.date,
            hoa_events.id AS event_id,
            NULL::text AS full_name,
            lower((luma_attendance.email)::text) AS email,
            contacts.airtable_id
           FROM ((public.luma_attendance
             JOIN public.hoa_events ON ((luma_attendance.event_id = hoa_events.id)))
             LEFT JOIN public.contacts ON ((lower((luma_attendance.email)::text) = lower((contacts.email)::text))))
          WHERE luma_attendance.did_join_event
        ), attendance_by_date AS (
         SELECT attendance_from_actions.date,
            attendance_from_actions.event_id,
            attendance_from_actions.full_name,
            attendance_from_actions.email,
            attendance_from_actions.airtable_id
           FROM attendance_from_actions
        UNION
         SELECT attendance_from_luma.date,
            attendance_from_luma.event_id,
            attendance_from_luma.full_name,
            attendance_from_luma.email,
            attendance_from_luma.airtable_id
           FROM attendance_from_luma
        )
 SELECT DISTINCT attendance_by_date.date,
    attendance_by_date.event_id,
    attendance_by_date.full_name,
    lower(attendance_by_date.email) AS email,
    attendance_by_date.airtable_id
   FROM attendance_by_date;


--
-- Name: hoa_attendance_by_hoa_week; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.hoa_attendance_by_hoa_week AS
 SELECT DISTINCT (hoa_attendance.date - (date_part('dow'::text, hoa_attendance.date))::integer) AS hoa_week,
    hoa_attendance.full_name,
    hoa_attendance.email,
    hoa_attendance.airtable_id
   FROM dbt_mchang.hoa_attendance;


--
-- Name: hoa_segments; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.hoa_segments AS
 WITH attendance AS (
         SELECT h.date,
            NULL::text AS airtable_id,
            l.email
           FROM (public.hoa_events h
             JOIN public.luma_attendance l ON (((l.event_id = h.id) AND (l.did_join_event IS TRUE))))
        ), merged_members AS (
         SELECT c.airtable_id,
            sum(
                CASE
                    WHEN (a.date >= (now() - '60 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_two_months,
            sum(
                CASE
                    WHEN (a.date >= (now() - '30 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_one_month
           FROM (public.contacts c
             LEFT JOIN attendance a ON ((((a.email)::text = (c.email)::text) OR (a.airtable_id = (c.airtable_id)::text))))
          GROUP BY c.airtable_id
        )
 SELECT merged_members.airtable_id,
        CASE
            WHEN (merged_members.hoas_in_past_two_months = 0) THEN 0
            WHEN ((merged_members.hoas_in_past_two_months > 0) AND (merged_members.hoas_in_past_one_month = 0)) THEN 1
            WHEN (merged_members.hoas_in_past_one_month = 1) THEN 2
            WHEN (merged_members.hoas_in_past_one_month = 2) THEN 3
            WHEN (merged_members.hoas_in_past_one_month >= 3) THEN 4
            ELSE '-1'::integer
        END AS segment
   FROM merged_members;


--
-- Name: yellow_brick_road; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.yellow_brick_road AS
 WITH merged_members AS (
         SELECT c.airtable_id,
            (c.signup_date IS NOT NULL) AS did_signup,
            (c.slack_joined_date IS NOT NULL) AS did_join_slack,
            count(a.date) AS num_hoas_attended
           FROM (public.contacts c
             LEFT JOIN dbt_mchang.hoa_attendance a ON (((c.airtable_id)::text = (a.airtable_id)::text)))
          GROUP BY c.airtable_id, (c.signup_date IS NOT NULL), (c.slack_joined_date IS NOT NULL)
        ), member_stats AS (
         SELECT merged_members.airtable_id,
            merged_members.did_signup,
            merged_members.did_join_slack,
            merged_members.num_hoas_attended,
                CASE
                    WHEN (merged_members.num_hoas_attended >= 5) THEN '7: Attended 5th HoA'::text
                    WHEN (merged_members.num_hoas_attended = 4) THEN '6: Attended 4th HoA'::text
                    WHEN (merged_members.num_hoas_attended = 3) THEN '5: Attended 3rd HoA'::text
                    WHEN (merged_members.num_hoas_attended = 2) THEN '4: Attended 2nd HoA'::text
                    WHEN (merged_members.num_hoas_attended = 1) THEN '3: Attended 1st HoA'::text
                    WHEN merged_members.did_join_slack THEN '2: Joined Slack'::text
                    ELSE '1: Signed Up'::text
                END AS progress
           FROM merged_members
        )
 SELECT member_stats.airtable_id,
    member_stats.did_signup,
    member_stats.did_join_slack,
    member_stats.num_hoas_attended,
    member_stats.progress
   FROM member_stats;


--
-- Name: attendance_preview; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.attendance_preview (
    airtable_id character varying(256),
    action_date date NOT NULL,
    action_source character varying(256) NOT NULL,
    action character varying(256) NOT NULL,
    hoa_week date NOT NULL
);


--
-- Name: hoa_events_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hoa_events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: hoa_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.hoa_events_id_seq OWNED BY public.hoa_events.id;


--
-- Name: segments; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.segments AS
 WITH attendance AS (
         SELECT h.date,
            NULL::text AS airtable_id,
            l.email
           FROM (public.hoa_events h
             JOIN public.luma_attendance l ON (((l.event_id = h.id) AND (l.did_join_event IS TRUE))))
        ), merged_members AS (
         SELECT c.airtable_id,
            sum(
                CASE
                    WHEN (a.date >= (now() - '60 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_two_months,
            sum(
                CASE
                    WHEN (a.date >= (now() - '30 days'::interval)) THEN 1
                    ELSE 0
                END) AS hoas_in_past_one_month
           FROM (public.contacts c
             LEFT JOIN attendance a ON ((((a.email)::text = (c.email)::text) OR (a.airtable_id = (c.airtable_id)::text))))
          GROUP BY c.airtable_id
        )
 SELECT merged_members.airtable_id,
        CASE
            WHEN (merged_members.hoas_in_past_two_months = 0) THEN 0
            WHEN ((merged_members.hoas_in_past_two_months > 0) AND (merged_members.hoas_in_past_one_month = 0)) THEN 1
            WHEN (merged_members.hoas_in_past_one_month = 1) THEN 2
            WHEN (merged_members.hoas_in_past_one_month = 2) THEN 3
            WHEN (merged_members.hoas_in_past_one_month >= 3) THEN 4
            ELSE '-1'::integer
        END AS segment
   FROM merged_members;


--
-- Name: hoa_events id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_events ALTER COLUMN id SET DEFAULT nextval('public.hoa_events_id_seq'::regclass);


--
-- Name: hoa_events hoa_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_events
    ADD CONSTRAINT hoa_events_pkey PRIMARY KEY (id);


--
-- Name: luma_attendance luma_attendance_event_id_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.luma_attendance
    ADD CONSTRAINT luma_attendance_event_id_email_key UNIQUE (event_id, email);


--
-- Name: contacts unique_airtable_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contacts
    ADD CONSTRAINT unique_airtable_id UNIQUE (airtable_id);


--
-- Name: attendance_preview attendance_preview_airtable_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attendance_preview
    ADD CONSTRAINT attendance_preview_airtable_id_fkey FOREIGN KEY (airtable_id) REFERENCES public.contacts(airtable_id) ON DELETE RESTRICT;


--
-- Name: luma_attendance luma_attendance_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.luma_attendance
    ADD CONSTRAINT luma_attendance_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.hoa_events(id) ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

