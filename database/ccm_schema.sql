--
-- PostgreSQL database dump
--

-- Dumped from database version 12.7
-- Dumped by pg_dump version 14.1

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
    'ADV_SHARE_AND_INVITE',
    'ADV_POLICYMAKER_OUTREACH',
    'ADV_KEY_STAKEHOLDER_AND_PUBLIC_OUTREACH',
    'ADV_CLIMATE_CONVERSATION_WITH_POLICYMAKER',
    'ADV_PREP_TALKING_CLIMATE_WITH_POLICYMAKERS',
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
-- Name: hoa_attendance_zoom_with_action; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hoa_attendance_zoom_with_action (
    date date NOT NULL,
    email public.citext NOT NULL,
    first_name character varying NOT NULL,
    last_name character varying NOT NULL,
    city character varying NOT NULL,
    zip_code character varying NOT NULL,
    state character varying NOT NULL,
    action character varying NOT NULL
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
            COALESCE(c1.airtable_id, c2.airtable_id) AS airtable_id,
            NULL::text AS action
           FROM ((pre_luma_actions
             LEFT JOIN public.contacts c1 ON ((lower((pre_luma_actions.email)::text) = lower((c1.email)::text))))
             LEFT JOIN public.contacts c2 ON ((lower((pre_luma_actions.full_name)::text) = ((lower((c2.first_name)::text) || ' '::text) || lower((c2.last_name)::text)))))
        ), attendance_from_luma AS (
         SELECT hoa_events.date,
            hoa_events.id AS event_id,
            NULL::text AS full_name,
            lower((luma_attendance.email)::text) AS email,
            contacts.airtable_id,
            NULL::text AS action
           FROM ((public.luma_attendance
             JOIN public.hoa_events ON ((luma_attendance.event_id = hoa_events.id)))
             LEFT JOIN public.contacts ON ((lower((luma_attendance.email)::text) = lower((contacts.email)::text))))
          WHERE (luma_attendance.did_join_event AND (hoa_events.date < '2021-09-15'::date))
        ), attendance_from_zoom_with_actions AS (
         SELECT haz.date,
            NULL::integer AS event_id,
            (((haz.first_name)::text || ' '::text) || (haz.last_name)::text) AS full_name,
            lower((haz.email)::text) AS email,
            contacts.airtable_id,
            haz.action
           FROM (public.hoa_attendance_zoom_with_action haz
             LEFT JOIN public.contacts ON ((lower((haz.email)::text) = lower((contacts.email)::text))))
          WHERE (haz.date >= '2021-09-15'::date)
        ), attendance_by_date AS (
         SELECT attendance_from_actions.date,
            attendance_from_actions.event_id,
            attendance_from_actions.full_name,
            attendance_from_actions.email,
            attendance_from_actions.airtable_id,
            attendance_from_actions.action
           FROM attendance_from_actions
        UNION
         SELECT attendance_from_luma.date,
            attendance_from_luma.event_id,
            attendance_from_luma.full_name,
            attendance_from_luma.email,
            attendance_from_luma.airtable_id,
            attendance_from_luma.action
           FROM attendance_from_luma
        UNION
         SELECT attendance_from_zoom_with_actions.date,
            attendance_from_zoom_with_actions.event_id,
            attendance_from_zoom_with_actions.full_name,
            attendance_from_zoom_with_actions.email,
            attendance_from_zoom_with_actions.airtable_id,
            attendance_from_zoom_with_actions.action
           FROM attendance_from_zoom_with_actions
        )
 SELECT DISTINCT attendance_by_date.date,
    attendance_by_date.event_id,
    attendance_by_date.full_name,
    lower(attendance_by_date.email) AS email,
    attendance_by_date.airtable_id,
    attendance_by_date.action
   FROM attendance_by_date;


--
-- Name: contacts_hydrated; Type: VIEW; Schema: analysis; Owner: -
--

CREATE VIEW analysis.contacts_hydrated AS
 WITH airtable_id_to_hoa_count AS (
         SELECT contacts_1.airtable_id,
            count(hoa_attendance.date) AS hoa_count
           FROM (public.contacts contacts_1
             LEFT JOIN analysis.hoa_attendance hoa_attendance USING (airtable_id))
          GROUP BY contacts_1.airtable_id
        )
 SELECT contacts.email,
    contacts.airtable_id,
    contacts.first_name,
    contacts.last_name,
    contacts.slack_member_id,
    contacts.signup_date,
    contacts.slack_joined_date,
    contacts.slack_last_active_date,
    contacts.state,
    contacts.referred_by,
    contacts.one_on_one_status,
    contacts.one_on_one_greeter,
    contacts.is_experienced,
    contacts.mailchimp_status,
    airtable_id_to_hoa_count.hoa_count
   FROM (public.contacts contacts
     LEFT JOIN airtable_id_to_hoa_count USING (airtable_id));


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
            c.hoa_count AS num_hoas_attended
           FROM analysis.contacts_hydrated c
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
            COALESCE(c1.airtable_id, c2.airtable_id) AS airtable_id,
            NULL::text AS action
           FROM ((pre_luma_actions
             LEFT JOIN public.contacts c1 ON ((lower((pre_luma_actions.email)::text) = lower((c1.email)::text))))
             LEFT JOIN public.contacts c2 ON ((lower((pre_luma_actions.full_name)::text) = ((lower((c2.first_name)::text) || ' '::text) || lower((c2.last_name)::text)))))
        ), attendance_from_luma AS (
         SELECT hoa_events.date,
            hoa_events.id AS event_id,
            NULL::text AS full_name,
            lower((luma_attendance.email)::text) AS email,
            contacts.airtable_id,
            NULL::text AS action
           FROM ((public.luma_attendance
             JOIN public.hoa_events ON ((luma_attendance.event_id = hoa_events.id)))
             LEFT JOIN public.contacts ON ((lower((luma_attendance.email)::text) = lower((contacts.email)::text))))
          WHERE (luma_attendance.did_join_event AND (hoa_events.date < '2021-09-15'::date))
        ), attendance_from_zoom_with_actions AS (
         SELECT haz.date,
            NULL::integer AS event_id,
            (((haz.first_name)::text || ' '::text) || (haz.last_name)::text) AS full_name,
            lower((haz.email)::text) AS email,
            contacts.airtable_id,
            haz.action
           FROM (public.hoa_attendance_zoom_with_action haz
             LEFT JOIN public.contacts ON ((lower((haz.email)::text) = lower((contacts.email)::text))))
          WHERE (haz.date >= '2021-09-15'::date)
        ), attendance_by_date AS (
         SELECT attendance_from_actions.date,
            attendance_from_actions.event_id,
            attendance_from_actions.full_name,
            attendance_from_actions.email,
            attendance_from_actions.airtable_id,
            attendance_from_actions.action
           FROM attendance_from_actions
        UNION
         SELECT attendance_from_luma.date,
            attendance_from_luma.event_id,
            attendance_from_luma.full_name,
            attendance_from_luma.email,
            attendance_from_luma.airtable_id,
            attendance_from_luma.action
           FROM attendance_from_luma
        UNION
         SELECT attendance_from_zoom_with_actions.date,
            attendance_from_zoom_with_actions.event_id,
            attendance_from_zoom_with_actions.full_name,
            attendance_from_zoom_with_actions.email,
            attendance_from_zoom_with_actions.airtable_id,
            attendance_from_zoom_with_actions.action
           FROM attendance_from_zoom_with_actions
        )
 SELECT DISTINCT attendance_by_date.date,
    attendance_by_date.event_id,
    attendance_by_date.full_name,
    lower(attendance_by_date.email) AS email,
    attendance_by_date.airtable_id,
    attendance_by_date.action
   FROM attendance_by_date;


--
-- Name: contacts_hydrated; Type: VIEW; Schema: dbt_mchang; Owner: -
--

CREATE VIEW dbt_mchang.contacts_hydrated AS
 WITH airtable_id_to_hoa_count AS (
         SELECT contacts_1.airtable_id,
            count(hoa_attendance.date) AS hoa_count
           FROM (public.contacts contacts_1
             LEFT JOIN dbt_mchang.hoa_attendance hoa_attendance USING (airtable_id))
          GROUP BY contacts_1.airtable_id
        )
 SELECT contacts.email,
    contacts.airtable_id,
    contacts.first_name,
    contacts.last_name,
    contacts.slack_member_id,
    contacts.signup_date,
    contacts.slack_joined_date,
    contacts.slack_last_active_date,
    contacts.state,
    contacts.referred_by,
    contacts.one_on_one_status,
    contacts.one_on_one_greeter,
    contacts.is_experienced,
    contacts.mailchimp_status,
    airtable_id_to_hoa_count.hoa_count
   FROM (public.contacts contacts
     LEFT JOIN airtable_id_to_hoa_count USING (airtable_id));


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
            c.hoa_count AS num_hoas_attended
           FROM dbt_mchang.contacts_hydrated c
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
-- Name: action_call_legislator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_call_legislator (
    action_contact_legislator_id bigint NOT NULL
);


--
-- Name: action_contact_legislator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_contact_legislator (
    id bigint NOT NULL,
    email character varying NOT NULL,
    contacted_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    issue_id bigint NOT NULL,
    contacted_bioguide_id character varying NOT NULL
);


--
-- Name: action_contact_legislator_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.action_contact_legislator ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.action_contact_legislator_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: action_email_legislator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_email_legislator (
    action_contact_legislator_id bigint NOT NULL
);


--
-- Name: action_initiate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_initiate (
    email public.citext NOT NULL,
    initiated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: action_sign_up; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_sign_up (
    email public.citext NOT NULL,
    signed_up_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: action_tool_database_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_tool_database_version (
    version integer NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: action_tweet_legislator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_tweet_legislator (
    action_contact_legislator_id bigint NOT NULL
);


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
-- Name: district_office; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.district_office (
    bioguide_id character varying NOT NULL,
    phone_number character varying,
    lat double precision,
    long double precision
);


--
-- Name: example_issue_why_statement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.example_issue_why_statement (
    issue_id bigint NOT NULL,
    statement character varying NOT NULL
);


--
-- Name: focus_issue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.focus_issue (
    issue_id bigint NOT NULL,
    focused_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
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
-- Name: issue; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issue (
    id bigint NOT NULL,
    title character varying NOT NULL,
    precomposed_tweet_template character varying NOT NULL,
    image_url character varying NOT NULL,
    description character varying NOT NULL
);


--
-- Name: issue_and_focus; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.issue_and_focus AS
 SELECT issue.id,
    issue.title,
    issue.precomposed_tweet_template,
    issue.image_url,
    issue.description,
        CASE
            WHEN (( SELECT focus_issue.issue_id
               FROM public.focus_issue
              ORDER BY focus_issue.focused_at DESC
             LIMIT 1) = issue.id) THEN true
            ELSE false
        END AS is_focused
   FROM public.issue;


--
-- Name: issue_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.issue ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.issue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: lcv_score_lifetime; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lcv_score_lifetime (
    bioguide_id character varying NOT NULL,
    score integer NOT NULL
);


--
-- Name: lcv_score_year; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lcv_score_year (
    bioguide_id character varying NOT NULL,
    score_year integer NOT NULL,
    score integer NOT NULL
);


--
-- Name: member_of_congress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.member_of_congress (
    bioguide_id character varying NOT NULL,
    full_name character varying NOT NULL,
    legislative_role character varying NOT NULL,
    state character varying NOT NULL,
    congressional_district smallint,
    party character varying NOT NULL,
    dc_phone_number character varying NOT NULL,
    twitter_handle character varying NOT NULL,
    cwc_office_code character varying
);


--
-- Name: schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


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
-- Name: talking_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.talking_point (
    id bigint NOT NULL,
    title character varying NOT NULL,
    issue_id bigint NOT NULL,
    content character varying NOT NULL,
    relative_order_position integer NOT NULL
);


--
-- Name: talking_point_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.talking_point ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.talking_point_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: hoa_events id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_events ALTER COLUMN id SET DEFAULT nextval('public.hoa_events_id_seq'::regclass);


--
-- Data for Name: district_office; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.district_office (bioguide_id, phone_number, lat, long) FROM stdin;
A000055	256-734-6043	34.181059	-86.840631
A000055	256-546-0201	34.01421	-86.006846
A000055	205-221-2310	33.83438	-87.274935
A000055	256-381-3450	34.7412348	-87.6794142
A000369	775-777-7705	40.8355439	-115.7586349
A000369	775-686-5760	39.4715058	-119.791273
A000370	704-344-9950	35.215055	-80.8452162
A000371	909-890-4445	34.068627	-117.270925
A000372	706-228-1980	33.4875735	-82.0846666
A000372	478-272-4030	32.5383565	-82.9084154
A000372	912-243-9452	32.4484951	-81.781533
A000372	912-403-3311	32.2046881	-82.3763265
A000375	325-675-9779	32.4443053	-99.7346145
A000375	806-763-1611	33.5830588	-101.8456125
A000377	701-354-6700	46.80868	-100.78881
A000377	701-353-6665	46.86533	-96.83182
A000378	712-890-3117	41.25685	-95.85104
A000378	202-225-5476	41.04909	-94.36326
A000378	202-225-5476	41.58781	-93.6113
B000490	229-439-8067	31.57858	-84.1542
B000490	706-320-9477	32.4625755	-84.9922596
B000490	478-803-2631	32.8356646	-83.6267623
B000574	503-231-2300	45.5293348	-122.6555902
B000575	573-334-7044	37.3064303	-89.5241403
B000575	573-442-8151	38.9606243	-92.3229127
B000575	816-471-7141	39.1021449	-94.5825038
B000575	314-725-4484	38.62588	-90.196
B000575	417-877-7814	37.180415	-93.240439
B000755	936-441-5700	30.2844625	-95.4613914
B000755	936-439-9532	30.7240052	-95.5535049
B000944	513-684-1021	39.1007745	-84.5120256
B000944	216-522-7272	41.4964399	-81.6975439
B000944	614-469-2083	39.9666883	-83.000974
B000944	440-242-4100	41.4685695	-82.1792684
B001135	828-350-2437	35.594066	-82.55792
B001135	252-977-9522	35.9379802	-77.7977748
B001135	888-848-1833	34.2376985	-77.9492582
B001135	336-631-5125	36.0960116	-80.2748025
B001230	715-450-3754	46.54692	-90.80933
B001230	715-832-8424	44.8088044	-91.5330934
B001230	920-498-2668	44.5151063	-88.0413549
B001230	608-796-0045	43.8109182	-91.249764
B001230	608-264-5338	43.0750656	-89.3864714
B001230	414-297-4451	43.0388214	-87.9200848
B001230	715-261-2611	44.9591474	-89.6628755
B001236	870-863-4641	33.2120448	-92.664046
B001236	479-573-0189	35.383512	-94.420331
B001236	870-268-6925	35.8404281	-90.7036855
B001236	501-372-7153	34.7456872	-92.2865752
B001236	479-725-0400	36.2535183	-94.1393049
B001236	870-424-0129	36.3499306	-92.3729619
B001236	870-672-6941	34.478784	-91.5466409
B001243	423-541-2939	35.0455268	-85.3100155
B001243	731-660-3971	35.68031	-88.85355
B001243	423-753-4009	36.30306	-82.51024
B001243	865-540-3781	35.96225	-83.91771
B001243	901-527-9199	35.14132	-90.05396
B001243	800-657-6910	36.16286	-86.77797
B001248	940-497-5031	33.1213325	-97.0327015
B001251	919-908-0164	36.0098844	-78.9533296
B001251	252-237-9816	35.728383	-77.9131079
B001257	727-232-2921	28.261156	-82.674347
B001257	727-940-5860	28.121418	-82.741612
B001260	941-747-9081	27.4945988	-82.5724073
B001260	941-951-6643	27.3353999	-82.5382987
B001261	307-261-6413	42.8524124	-106.3248373
B001261	307-772-2451	41.1374591	-104.8189674
B001261	307-856-6642	43.0238609	-108.386731
B001261	307-362-5012	41.5742346	-109.2423116
B001261	307-672-6456	44.7974506	-106.9562525
B001267	719-587-0096	37.4685012	-105.8659434
B001267	719-328-1100	38.8400247	-104.8228748
B001267	303-455-7600	39.7364704	-104.994978
B001267	970-259-1710	37.2725939	-107.8799634
B001267	970-224-2200	40.5716714	-105.0766516
B001267	970-241-6631	39.0688046	-108.5648398
B001267	719-542-7550	38.2795281	-104.508253
B001270	323-965-1422	34.0626436	-118.3381029
B001274	256-355-9400	34.6069984	-86.9849204
B001274	256-718-5155	34.8002305	-87.6771285
B001274	256-551-0190	34.7252133	-86.5977423
B001275	812-465-6484	37.9722214	-87.5705016
B001275	\N	38.3919787	-86.9304537
B001275	812-232-0523	39.466224	-87.4042291
B001275	\N	38.6911192	-87.5207388
B001277	203-330-0598	41.1749972	-73.1931954
B001277	860-258-6940	41.7665316	-72.6719747
B001278	503-469-6010	45.4900502	-122.8078224
B001281	614-220-0003	39.9633834	-82.9878075
B001282	859-219-1366	38.0283634	-84.4193762
B001285	805-379-1779	34.1993342	-119.1783991
B001285	805-379-1779	34.1794905	-118.8726791
B001286	309-966-1813	40.6855433	-89.5987706
B001286	309-786-3406	41.5089919	-90.5662359
B001286	815-968-8011	42.2720192	-89.0952944
B001287	916-635-0505	38.5553768	-121.3738588
B001288	856-338-8922	39.9443128	-75.1296174
B001288	973-639-8700	40.7344571	-74.1657213
B001291	832-780-0966	29.7089323	-95.1212624
B001291	409-883-8075	30.0800116	-93.7314932
B001291	409-331-8066	30.7753424	-94.4153986
B001292	703-658-5403	38.8976	-77.07119
B001295	618-622-0766	38.8906416	-90.1839515
B001295	618-457-5787	37.7277729	-89.2134992
B001295	618-622-0766	38.7015611	-90.1488745
B001295	618-513-5294	38.317936	-88.9049835
B001295	618-622-0766	38.5925108	-89.9141608
B001296	267-335-5643	40.0381812	-75.1172342
B001296	215-335-3355	40.0730543	-75.049904
B001296	215-426-4616	39.98239	-75.11967
B001297	720-639-9165	39.4092665	-104.8684125
B001297	970-702-2136	40.4205693	-104.7446355
B001298	402-938-0300	41.2350981	-96.1296555
B001299	260-702-4750	41.0737372	-85.1404535
B001300	310-831-1799	33.8322643	-118.2630272
B001300	310-831-1799	33.8946744	-118.226945
B001300	310-831-1799	33.7398775	-118.2839378
B001300	310-831-1799	33.9556898	-118.2051037
B001302	480-699-8239	33.3712904	-111.6870648
B001303	302-830-2330	39.7473432	-75.5471367
B001303	302-858-4773	38.6903978	-75.3887458
B001304	410-266-3249	38.9746861	-76.5612782
B001304	301-458-2600	38.9044794	-76.8406561
B001305	336-998-1313	36.0038879	-80.4394348
B001305	336-858-5013	36.0515139	-79.9611368
B001307	317-563-5567	39.75697	-86.52325
B001309	865-523-3772	35.96225	-83.91771
B001309	865-984-5464	35.75488	-83.96911
B001310	260-427-2167	41.0796	-85.13801
B001310	219-937-9650	41.61616	-87.51962
B001310	317-822-8240	39.76888	-86.15612
B001310	574-288-6302	41.67813	-86.25266
C000059	951-277-0042	33.8784386	-117.5759232
C000127	425-303-0114	47.9781562	-122.2076204
C000127	509-946-8106	46.2775762	-119.2760977
C000127	206-220-6400	47.6045895	-122.3354608
C000127	509-353-2507	47.6585992	-117.4260094
C000127	253-572-2281	47.2535757	-122.4390697
C000127	360-696-7838	45.628864	-122.6641029
C000141	410-962-4436	39.2871649	-76.6156372
C000141	301-860-0414	38.9550571	-76.8280981
C000141	301-777-2957	39.6495579	-78.7636025
C000141	301-762-2974	39.0895234	-77.1511348
C000141	410-546-4250	38.3652837	-75.6023764
C000174	302-674-3308	39.1566624	-75.5311574
C000174	302-856-7690	38.6902745	-75.3852914
C000174	302-573-6291	39.739183	-75.5492666
C000266	513-684-2723	39.100836	-84.513238
C000266	513-421-8704	39.4341674	-84.2086104
C000537	803-799-1100	34.0032484	-81.0330283
C000537	843-355-1211	33.6637604	-79.8313355
C000537	803-854-4700	33.4737478	-80.4819747
C000754	615-736-5295	36.1623269	-86.7817797
C000880	208-334-1776	43.6086015	-116.1956172
C000880	208-664-5490	47.6826921	-116.7920799
C000880	208-522-9779	43.4936265	-112.0425928
C000880	208-743-1492	46.422158	-117.028638
C000880	208-236-6775	42.8649373	-112.4428822
C000880	208-734-2515	42.5776169	-114.47751
C001035	207-622-8414	44.310824	-69.783697
C001035	207-945-0417	44.8046399	-68.7737891
C001035	207-283-1101	43.493177	-70.4548329
C001035	207-493-7873	46.860458	-68.013198
C001035	207-784-6969	44.097439	-70.217972
C001035	207-780-3575	43.6566885	-70.2551294
C001047	304-347-5372	37.779625	-81.188172
C001047	304-347-5372	38.3511449	-81.6380602
C001047	304-262-9285	39.4607489	-77.986887
C001047	304-292-2310	39.6256581	-79.9631255
C001051	512-246-1600	30.5234565	-97.688835
C001051	254-933-1392	31.0718016	-97.4233588
C001053	580-357-2131	34.6044481	-98.3995211
C001053	405-329-6500	35.2053158	-97.4788041
C001053	580-436-5375	34.7723614	-96.6790693
C001055	808-650-6688	21.30973	-157.86005
C001056	512-469-6034	30.2681008	-97.7448471
C001056	972-239-1310	32.9400414	-96.8235975
C001056	956-423-0162	26.1914148	-97.6940201
C001056	713-572-3337	29.7620629	-95.4159532
C001056	806-472-7533	33.5849492	-101.8519083
C001056	210-224-7485	29.4267857	-98.4895764
C001056	903-593-0902	32.3522862	-95.3002871
C001059	559-495-1620	36.7345489	-119.7858717
C001059	209-384-1620	37.3070723	-120.4812216
C001061	660-584-7373	39.0754557	-93.7170832
C001061	816-833-4545	39.09278	-94.4201
C001061	816-842-4545	39.070874	-94.587124
C001063	956-725-0639	27.5423782	-99.4887937
C001063	956-424-3942	26.2161009	-98.324999
C001063	956-487-5603	26.3834188	-98.8582305
C001063	210-271-2851	29.4270611	-98.4862276
C001066	813-871-2817	27.9771817	-82.4852065
C001067	718-287-1142	40.6536853	-73.9516805
C001068	901-544-4131	35.1497482	-90.0517538
C001069	860-741-6011	41.9904484	-72.5723972
C001069	860-886-0139	41.524272	-72.078968
C001070	610-782-9470	40.6010783	-75.4749998
C001070	814-357-0314	40.9138418	-77.7641528
C001070	814-874-5080	42.1289745	-80.0840689
C001070	717-231-7540	40.2619774	-76.8829619
C001070	215-405-9660	39.9532694	-75.1736461
C001070	412-803-7370	40.4376694	-79.9974916
C001070	570-941-0930	41.4073687	-75.6654743
C001072	317-283-6516	39.8063243	-86.1513962
C001075	318-448-7176	31.2852075	-92.4767735
C001075	225-929-7711	30.423884	-91.1325791
C001075	337-493-5398	30.2305579	-93.2196294
C001075	337-261-1400	30.1948666	-92.014654
C001075	504-838-0130	30.0112259	-90.1541748
C001075	318-324-2111	32.5175209	-92.1087384
C001075	318-798-3215	32.448457	-93.7198668
C001078	703-256-3071	38.8338994	-77.1967528
C001078	571-408-4407	38.6752583	-77.2815925
C001080	909-625-5394	34.1079585	-117.7212331
C001080	626-304-0110	34.1367185	-118.1327226
C001084	401-729-5600	41.8610791	-71.3986461
C001087	501-843-3043	34.9730598	-92.0167235
C001087	870-377-5571	33.88781	-91.49025
C001087	870-203-0540	35.8220219	-90.6760171
C001087	870-424-2075	36.3499306	-92.3707731
C001088	302-736-5601	39.1566624	-75.5311574
C001088	302-573-6345	39.7473432	-75.5471367
C001090	570-341-1050	41.4093981	-75.6643973
C001090	570-371-0317	41.2441677	-75.8791709
C001091	210-348-8216	29.4175269	-98.4846276
C001095	870-864-8582	33.2120448	-92.664046
C001095	870-933-6223	35.8404281	-90.7036855
C001095	501-223-9081	34.7456872	-92.2865752
C001095	479-751-0879	36.174571	-94.1176151
C001096	701-224-0355	46.8090113	-100.7882597
C001096	701-356-2216	46.8653775	-96.8314895
C001096	701-738-4880	47.921087	-97.090537
C001096	701-839-0255	48.2328101	-101.2926299
C001097	818-221-3718	34.2449325	-118.4495976
C001098	512-916-5834	30.269401	-97.7390867
C001098	214-599-8749	32.8092691	-96.8059281
C001098	713-718-3057	29.758844	-95.3650242
C001098	956-686-7339	26.2019273	-98.2313667
C001098	210-340-2885	29.533858	-98.5619881
C001098	903-593-5130	32.3480097	-95.3009807
C001101	617-354-0292	42.390572	-71.151338
C001101	508-319-9757	42.2787415	-71.4170035
C001103	912-265-9010	31.149693	-81.49532
C001103	912-352-0101	32.017594	-81.110403
C001108	270-408-1865	37.0851283	-88.595174
C001108	270-487-9509	36.7006713	-85.6929017
C001108	270-487-9509	37.3296043	-87.498937
C001109	307-261-6595	42.8524124	-106.3248373
C001109	307-772-2595	41.1374591	-104.8189674
C001109	307-414-1677	44.2920649	-105.5066014
C001109	307-673-4608	44.7974845	-106.9548869
C001112	805-546-8348	35.2838648	-120.6538213
C001112	805-546-8348	34.434901	-119.746862
C001112	805-730-1710	34.434901	-120.4405464
C001117	\N	41.8672	-88.25793
C001118	540-432-2391	38.44978	-78.86637
C001118	434-845-8306	37.41441	-79.14153
C001118	540-857-2672	37.2694	-79.94051
C001118	540-885-3861	38.14838	-79.07444
C001119	651-846-2120	44.7684	-93.2863
C001120	713-860-1330	30.05138	-95.22975
C001121	720-748-7514	39.65758	-104.83538
D000096	773-533-7520	41.8807415	-87.6990211
D000191	541-269-2609	43.367748	-124.213429
D000191	541-465-6732	44.0516985	-123.0857882
D000191	541-440-3523	43.2087597	-123.3445182
D000197	303-844-4988	39.7260392	-104.9832743
D000216	203-562-3718	41.3080224	-72.9226768
D000399	512-916-5921	30.269401	-97.7390867
D000399	210-704-1080	29.427604	-98.4951151
D000482	412-283-4451	40.34915	-80.02294
D000482	412-664-4049	40.352477	-79.858547
D000482	412-390-1499	40.4273525	-79.9675163
D000563	618-351-1122	37.7233822	-89.2173032
D000563	312-353-4952	41.8784454	-87.63
D000563	309-786-5173	41.5090535	-90.5782533
D000563	217-492-4062	39.7965137	-89.6451404
D000600	305-470-8555	25.8109245	-80.3361915
D000600	239-348-1620	26.1833954	-81.7038021
D000610	561-470-5440	26.365483	-80.1673666
D000610	954-255-8336	26.27311	-80.25343
D000610	\N	26.1194405	-80.1439123
D000610	954-972-6454	26.2420954	-80.2050177
D000615	864-224-7401	34.5490603	-82.6746057
D000615	864-681-1028	34.4772923	-81.9380738
D000616	423-472-7500	35.160062	-84.8840222
D000616	931-381-9920	35.61587	-87.035826
D000616	615-896-1986	35.8461158	-86.3940965
D000616	931-962-3180	35.1850086	-86.1112284
D000617	425-485-0085	47.7965458	-122.2097757
D000617	360-416-7879	48.419499	-122.3357879
D000618	406-245-6822	45.7810512	-108.5125313
D000618	406-587-3446	45.6788798	-111.0390251
D000618	406-453-0148	47.5067244	-111.3009733
D000618	406-665-4126	45.731817	-107.606432
D000618	406-443-3189	46.596642	-112.036443
D000618	406-257-3765	48.1966357	-114.3118812
D000618	406-549-8198	46.8699041	-113.9930208
D000618	406-482-9010	47.7106368	-104.1606344
D000619	217-403-4690	40.090932	-88.2501244
D000619	217-791-6224	39.8396384	-88.954852
D000619	618-205-8660	38.737077	-89.9548986
D000619	309-252-8834	40.5094581	-88.9846312
D000619	217-791-6224	39.7888553	-89.6124109
D000619	217-824-5117	39.5480891	-89.2954995
D000623	510-620-1000	37.9367699	-122.3428337
D000623	925-933-2660	37.9043441	-122.0583137
D000624	313-278-2936	42.3034918	-83.2627983
D000624	734-481-1100	42.2406978	-83.6164891
D000626	937-322-1120	39.9237363	-83.8097194
D000626	937-339-1524	40.0578289	-84.2463325
D000626	513-779-5400	39.3317585	-84.4066223
D000627	321-388-9808	28.517804	-81.4818644
D000628	850-785-0812	30.167961	-85.671895
D000628	850-891-8610	30.4395286	-84.2826688
D000629	913-621-0832	38.9858	-94.67027
D000630	\N	42.27706	-74.91709
D000630	845-443-2930	41.93346	-74.01604
D000630	\N	42.45476	-75.06093
D000631	215-884-4300	40.10015	-75.15143
D000631	610-382-1250	40.11381	-75.34223
E000215	650-323-2984	37.4431725	-122.1606451
E000294	763-241-6848	45.2839958	-93.5648066
E000295	319-365-4504	41.9731139	-91.6645212
E000295	712-352-1167	41.2608863	-95.8525576
E000295	563-322-0677	41.5213296	-90.5758645
E000295	515-284-4574	41.5864152	-93.6201532
E000295	712-252-1550	42.4963261	-96.4075837
E000296	215-276-0340	40.063831	-75.153345
E000299	915-541-1400	31.76012	-106.48618
F000062	559-485-7430	36.7374775	-119.7836709
F000062	310-914-7300	34.0476223	-118.444656
F000062	619-231-9712	32.7143481	-117.1647085
F000062	415-393-0707	37.7887748	-122.4026656
F000449	402-727-0888	41.4355946	-96.4988074
F000449	402-438-1598	40.8108404	-96.7033345
F000449	402-379-2064	42.0317036	-97.4144702
F000450	828-265-0240	36.2033658	-81.6584959
F000450	336-778-0211	36.0225285	-80.3758084
F000454	630-585-7672	41.758276	-88.249771
F000454	815-280-5876	41.5253876	-88.1380854
F000459	423-745-4671	35.441192	-84.594681
F000459	423-756-2342	35.0450875	-85.3082146
F000459	865-576-1976	36.02409	-84.234908
F000462	561-998-9045	26.3730924	-80.1203093
F000463	308-234-2361	40.6986843	-99.0819184
F000463	402-441-4600	40.8177383	-96.7095183
F000463	402-391-3411	41.2673318	-96.0980857
F000463	308-630-2329	41.8680501	-103.6483008
F000465	770-683-2033	33.3996108	-84.7263616
F000466	215-579-8102	40.2162341	-74.9308492
F000468	\N	29.74946	-95.47373
F000469	208-667-0127	47.69725	-116.80272
F000469	208-743-1388	46.42199	-117.02845
F000469	208-888-3188	43.61003	-116.3929
G000359	803-933-0112	34.0016459	-81.0461408
G000359	843-669-1505	34.1974074	-79.7733804
G000359	864-250-1417	34.8487536	-82.4004437
G000359	843-849-3887	32.8060149	-79.8879337
G000359	864-646-4090	34.65072	-82.782997
G000359	803-366-2828	34.9245162	-81.0247705
G000377	817-338-0909	32.7270777	-97.3597301
G000386	319-363-6832	41.9731139	-91.6645212
G000386	712-322-7103	41.2608863	-95.8525576
G000386	563-322-4331	41.5213296	-90.5758645
G000386	515-288-1145	41.5864152	-93.6201532
G000386	712-233-1860	42.4963261	-96.4075837
G000386	319-232-6657	42.4953784	-92.339384
G000546	573-221-3400	39.7065377	-91.3643168
G000546	816-792-3976	39.3060799	-94.684966
G000546	816-749-0800	39.7685973	-94.8548563
G000551	623-536-3388	33.4487419	-112.3501093
G000551	928-343-7933	32.5960917	-114.7109846
G000551	520-622-6788	32.163554	-110.969568
G000552	903-236-8597	32.4970151	-94.7389465
G000552	936-632-3180	31.3368848	-94.7268845
G000552	903-938-8386	32.5447093	-94.367349
G000552	936-715-9514	31.6036806	-94.6565524
G000552	903-561-6349	32.3031236	-95.2868435
G000553	713-383-9234	29.6765115	-95.4252617
G000555	518-431-0120	42.655222	-73.7492819
G000555	716-854-9725	42.8747911	-78.851165
G000555	315-376-6118	\N	\N
G000555	212-688-6262	40.7550353	-73.9718588
G000555	631-249-2825	40.7707877	-73.4102476
G000555	585-263-6250	43.1576631	-77.613515
G000555	315-448-0470	43.0501042	-76.1543204
G000555	845-875-4585	\N	\N
G000558	270-842-9896	36.9671412	-86.4282633
G000558	\N	37.7493662	-87.1617598
G000558	\N	37.8379989	-85.9440374
G000559	530-753-5301	38.5466673	-121.7395708
G000559	707-438-1822	38.2576953	-122.0494716
G000563	419-207-0650	40.8690512	-82.318261
G000563	330-737-1631	40.797956	-81.374289
G000565	\N	35.1904088	-114.0525545
G000565	480-882-2697	33.3557193	-111.4526421
G000565	928-445-1683	34.5427185	-112.4690897
G000568	276-525-1405	36.7086253	-81.9811305
G000568	276-525-1405	36.8667619	-82.7753732
G000568	540-381-5671	37.1296267	-80.4095978
G000574	602-256-0551	33.452957	-112.0734511
G000576	920-907-0624	43.7522376	-88.4509945
G000577	225-442-1731	30.4265187	-91.1316524
G000577	225-686-4413	30.4830241	-90.7486419
G000577	985-448-4103	29.795623	-90.8011997
G000578	850-479-1183	30.4533965	-86.6552739
G000578	850-479-1183	30.4670273	-87.2032093
G000579	920-301-4500	44.43087	-88.1046
G000581	888-217-0261	27.5968913	-98.4176561
G000581	888-217-0261	27.7605991	-98.2413715
G000581	361-209-3027	27.2260914	-98.1424972
G000581	956-682-5545	26.21101	-98.23214
G000581	830-358-0497	29.57597	-97.94818
G000582	787-723-6333	\N	\N
G000583	201-389-1100	40.9506625	-74.143801
G000583	973-940-1117	41.0581593	-74.7544134
G000583	973-814-4076	41.129452	-74.2672991
G000583	973-814-4076	40.7599515	-74.9823375
G000583	973-814-4076	40.8857964	-74.046227
G000583	973-814-4076	41.2025042	-74.4855857
G000586	773-342-0774	41.92478	-87.70907
G000587	832-325-3150	29.77243	-95.22857
G000588	330-599-7037	40.85516	-81.42644
G000588	440-783-3696	41.31615	-81.85719
G000589	214-765-6789	32.80522	-96.62753
G000590	202-225-2811	36.52823	-87.35903
G000590	\N	35.92501	-86.86902
G000591	769-241-6120	32.27296	-89.98226
G000591	601-693-6681	32.36409	-88.70031
G000591	662-324-0007	33.45683	-88.68608
G000592	207-249-7400	44.8023	-68.77015
G000592	207-492-6009	46.85918	-68.01637
G000592	207-241-6767	44.09552	-70.21691
H000874	301-474-0119	39.0089504	-76.8952351
H000874	301-843-1577	38.5923982	-76.9415779
H001038	716-852-3501	42.8747911	-78.851165
H001038	716-282-1274	43.0967702	-79.0554227
H001042	808-522-8970	21.3036893	-157.862353
H001046	505-346-6601	35.0833514	-106.6521937
H001046	505-325-5030	36.7863602	-108.1112628
H001046	575-523-6561	32.3041597	-106.7758242
H001046	575-622-7113	33.39647	-104.521106
H001046	505-988-6647	35.6889043	-105.9354058
H001047	203-333-6600	41.1763593	-73.1898756
H001047	203-353-9400	41.0514745	-73.5428955
H001052	410-588-5670	39.535151	-76.3464629
H001052	410-643-5425	38.975324	-76.28993
H001052	443-944-8624	38.3652997	-75.600858
H001053	573-442-9311	38.9230515	-92.3365352
H001053	816-884-3411	38.6630256	-94.3644632
H001053	417-532-5582	37.6821821	-92.6645922
H001056	\N	46.6639928	-122.9672327
H001056	360-695-6292	45.6281298	-122.6643142
H001058	616-414-5516	43.0647606	-86.2346593
H001058	616-570-0917	42.881882	-85.763622
H001061	701-250-4618	46.8090113	-100.7882597
H001061	701-239-5389	46.8778235	-96.7894805
H001061	701-746-8972	47.9255358	-97.0327393
H001061	701-838-1361	48.2351551	-101.2950057
H001061	701-609-2727	\N	\N
H001066	702-963-9360	36.19944	-115.1215
H001067	704-786-1612	35.4150032	-80.6027893
H001067	910-997-2070	35.054944	-78.877151
H001067	910-246-5374	35.219259	-79.405317
H001068	707-407-3585	40.8035929	-124.1682054
H001068	707-962-0933	39.4461329	-123.8042883
H001068	707-981-8967	38.2315024	-122.6330448
H001068	415-258-9657	37.9738069	-122.5267
H001068	707-962-0905	39.1598605	-123.2154259
H001071	478-457-0007	33.1204264	-83.2592841
H001071	770-207-1776	33.7942004	-83.7130977
H001071	770-207-1776	33.4671424	-82.4986906
H001072	501-358-3481	35.0868894	-92.4381401
H001072	501-324-5941	34.7651263	-92.3397715
H001074	812-288-3999	38.2842586	-85.7435028
H001074	317-851-8710	39.48083	-86.05373
H001081	860-223-8412	41.55442	-73.04104
H001085	610-295-0815	40.33695	-75.92123
H001088	507-323-6090	44.19115	-93.94899
H001088	507-323-6090	44.00307	-92.48603
H001090	209-579-5458	37.70589	-121.07707
I000024	580-234-5105	36.3993287	-97.8803343
I000024	918-426-0933	34.931573	-95.7671124
I000024	405-604-0917	35.5361791	-97.5879455
I000024	918-748-5111	36.1341719	-95.9676084
J000032	713-227-7740	29.7758442	-95.3269342
J000032	713-655-0050	29.7519042	-95.3736058
J000032	713-691-4882	29.85686	-95.42124
J000032	713-861-4070	29.8026	-95.4038
J000126	214-922-8885	32.7946832	-96.8263983
J000288	770-987-2291	33.704214	-84.1763731
J000289	419-663-1426	40.8042672	-82.9752194
J000289	419-999-6455	40.7369756	-84.161425
J000289	419-663-1426	41.2425971	-82.6150462
J000292	740-432-2366	40.0243891	-81.5899869
J000292	740-534-9431	38.5344281	-82.6854708
J000292	740-376-0868	39.413511	-81.454609
J000292	330-337-6951	40.9010799	-80.8561222
J000293	414-276-7282	43.0382241	-87.90456
J000293	920-230-7250	44.0174081	-88.5343632
J000293	608-240-9629	43.1317039	-89.289711
J000294	718-373-0033	40.5809199	-73.9696891
J000294	718-237-2211	40.6856283	-73.9757756
J000295	440-352-3939	41.679062	-81.335902
J000295	330-357-4139	41.3183054	-81.4478297
J000298	206-674-0040	47.6119653	-122.3397313
J000299	318-840-0309	32.5579513	-93.7228425
J000299	318-357-5731	31.7479383	-93.0970551
J000299	337-392-3146	31.0884912	-93.2312895
J000301	605-622-1060	45.45894	-98.48235
J000301	605-646-6454	44.08216	-103.26037
J000301	605-275-2868	43.55057	-96.72981
J000302	814-656-6081	40.4756	-78.42257
J000302	717-753-6344	39.93775	-77.66387
J000302	\N	40.01453	-79.0702
K000009	216-767-5933	41.4502564	-81.8160502
K000009	440-288-1500	41.4685695	-82.1792684
K000009	419-259-7500	41.6538759	-83.528238
K000188	715-831-9214	44.8120136	-91.500022
K000188	608-782-2558	43.8109182	-91.249764
K000367	612-727-5220	44.9751443	-93.2505863
K000367	218-287-2219	46.873119	-96.774188
K000367	507-288-5321	44.0314822	-92.479679
K000367	218-741-9690	47.5302888	-92.5465059
K000368	\N	31.55364	-110.26691
K000368	\N	32.24289	-110.89202
K000375	508-771-6868	41.6511319	-70.2939487
K000375	508-999-6462	41.6339983	-70.9267013
K000375	508-746-9000	41.9636518	-70.6753841
K000376	724-282-2557	40.8580044	-79.8948869
K000376	814-454-8190	42.1373875	-80.0848493
K000376	724-342-7170	41.2315486	-80.5079773
K000378	815-431-9271	41.346247	-88.841051
K000378	815-708-8032	42.2743104	-88.9601839
K000378	815-432-0580	40.7762446	-87.7383019
K000380	810-238-8627	43.01504	-83.68977
K000381	360-373-9725	47.5669568	-122.6258917
K000381	360-797-3623	48.113181	-123.431753
K000381	253-272-3515	47.2535757	-122.4390697
K000382	603-226-1002	43.2048069	-71.5355091
K000382	603-444-7700	44.3063587	-71.7713705
K000382	603-595-2006	42.7606386	-71.4647244
K000383	207-622-8292	44.3676495	-69.7940768
K000383	207-945-8000	44.8046399	-68.7737891
K000383	207-764-5124	46.6766073	-67.9956649
K000383	207-352-5216	43.493662	-70.457006
K000384	276-525-4790	36.709042	-81.981768
K000384	434-792-0976	36.5867166	-79.389396
K000384	703-361-3192	38.7515843	-77.4755912
K000384	804-771-2221	37.5374358	-77.4362913
K000384	540-682-5693	37.268654	-79.9408098
K000384	757-518-1674	36.842768	-76.133277
K000385	773-321-2001	41.6944193	-87.6012886
K000385	708-679-0078	41.117146	-87.861261
K000385	708-679-0078	41.5042785	-87.7382453
K000386	315-253-4068	42.9318995	-76.5664577
K000386	315-253-4068	43.072187	-77.0368578
K000386	315-423-5657	43.4554199	-76.511258
K000386	315-423-5657	43.0461194	-76.1516167
K000388	662-327-0748	33.4985949	-88.4258776
K000388	662-687-1525	34.9303984	-88.4336389
K000388	662-258-7240	33.5403014	-89.2688098
K000388	662-449-3090	34.8226252	-89.9956387
K000388	662-841-8808	34.2570023	-88.7081126
K000389	408-436-2720	37.3494432	-121.9434873
K000391	847-413-1959	42.042839	-88.0382131
K000392	731-412-1037	36.03304	-89.38925
K000392	901-682-4422	35.1034459	-89.8641093
K000392	731-423-4848	35.6149736	-88.8184144
K000394	\N	39.88539	-74.89656
K000394	\N	39.95223	-74.19681
L000174	802-863-2525	44.4755314	-73.2112826
L000174	802-229-0569	44.260997	-72.5775843
L000397	408-271-8700	37.3475248	-121.8993988
L000491	405-373-1958	35.5916684	-97.717028
L000551	\N	37.7667491	-122.2422766
L000551	\N	37.8879742	-122.2932172
L000551	510-763-0370	37.8052016	-122.2745116
L000551	\N	37.7784791	-122.2250837
L000551	\N	37.7260242	-122.1540198
L000557	860-278-8888	41.7584184	-72.6757241
L000559	401-732-9400	41.6944274	-71.4705546
L000560	360-733-4500	48.7523829	-122.4785173
L000560	425-252-3188	47.9781562	-122.2076204
L000562	617-428-2000	42.3455095	-71.035511
L000562	508-586-5555	42.080643	-71.023661
L000562	617-657-6305	42.2526962	-71.0049509
L000564	719-520-0055	38.948315	-104.807763
L000564	719-520-0055	38.842985	-106.128575
L000566	419-354-8700	41.3891803	-83.6512062
L000566	419-782-1996	41.2881704	-84.3608987
L000566	419-422-7791	41.038435	-83.6516735
L000569	573-635-7232	38.5813109	-92.2067952
L000569	636-239-2276	38.5560438	-91.0132469
L000569	636-327-7055	38.8122807	-90.8518717
L000570	505-324-1005	36.7378735	-108.2167002
L000570	505-863-0582	35.5265052	-108.7413892
L000570	505-454-3038	35.59437	-105.22224
L000570	505-994-0499	35.3109178	-106.6851151
L000570	505-984-8950	35.6605914	-105.9630964
L000570	575-461-3029	35.1718132	-103.7221881
L000575	405-231-4941	35.4776534	-97.5145337
L000575	918-581-7651	36.1533467	-95.992873
L000576	417-781-1041	37.0555214	-94.4831701
L000576	417-889-1800	37.1590571	-93.2294018
L000577	801-392-9633	41.2206774	-111.9726125
L000577	801-524-5933	40.7667476	-111.8871382
L000577	435-628-5514	37.1079122	-113.5893448
L000578	530-878-5035	38.9527935	-121.0815891
L000578	530-343-1000	39.77249	-121.87422
L000578	530-223-5898	40.565758	-122.351892
L000579	562-436-3828	33.7689325	-118.1930229
L000579	714-243-4088	33.775733	-117.941206
L000581	248-356-2052	42.483811	-83.2598863
L000582	323-651-1040	34.062551	-118.3402319
L000582	310-321-7664	33.9015419	-118.383875
L000583	770-429-1776	33.8852658	-84.4613029
L000583	770-429-1776	34.1658228	-84.7997815
L000583	770-429-1776	34.085747	-84.523949
L000585	309-205-9556	40.5029607	-88.9230848
L000585	217-245-1431	39.7336397	-90.2299815
L000585	309-671-7027	40.6949004	-89.591743
L000585	217-670-1653	39.7995745	-89.648127
L000586	850-558-9450	30.4463425	-84.2881976
L000586	904-354-1652	30.336823	-81.667581
L000590	702-963-9336	36.02796	-115.11836
L000591	757-364-7650	36.84256	-76.13213
L000592	586-498-7122	42.51766	-83.0287
L000593	949-281-2449	33.48052	-117.69774
L000593	760-599-5000	33.18403	-117.32722
M000087	718-932-1804	40.7672194	-73.9223996
M000087	718-349-5972	40.7155784	-73.9498714
M000087	212-860-0606	40.7828262	-73.9506841
M000133	617-565-8519	42.3613091	-71.0593927
M000133	508-677-0523	41.6999176	-71.1587266
M000133	413-785-4610	42.1032165	-72.5929441
M000312	978-466-3552	42.5279312	-71.7607974
M000312	413-341-8700	42.3185388	-72.6283162
M000312	508-831-7356	42.2628409	-71.7903444
M000355	270-781-1673	36.9948956	-86.4430273
M000355	859-578-0188	39.0575706	-84.542929
M000355	859-224-8286	38.0134107	-84.5524557
M000355	606-864-2026	37.1258183	-84.0809548
M000355	502-582-6304	38.2469833	-85.7624677
M000355	270-442-4554	37.0792561	-88.6172508
M000639	856-757-5353	39.8730588	-75.0471844
M000639	973-645-3030	40.7344571	-74.1657213
M000687	410-685-9199	\N	\N
M000687	410-818-2120	\N	\N
M000687	443-364-5413	\N	\N
M000934	785-628-6401	38.8728651	-99.3296381
M000934	785-539-8973	39.1928877	-96.6064052
M000934	913-393-0711	38.927647	-94.8584293
M000934	620-232-2286	37.4105276	-94.7044411
M000934	316-269-9259	37.6865773	-97.3371211
M001111	425-259-6515	47.9781562	-122.2076204
M001111	206-553-5545	47.6045895	-122.3354608
M001111	509-624-9515	47.6575963	-117.4232771
M001111	253-572-3636	47.2535757	-122.4390697
M001111	360-696-7797	45.6276649	-122.657351
M001111	509-453-7462	46.6030785	-120.5008956
M001137	347-230-4032	40.5907811	-73.798273
M001137	718-725-6000	40.7030995	-73.80205
M001143	651-224-9191	44.9605297	-93.1943159
M001153	907-271-3735	61.2171561	-149.9043364
M001153	907-456-0233	64.8427991	-147.7219443
M001153	907-586-7277	58.3003324	-134.4205554
M001153	907-225-6880	55.3492023	-131.6673863
M001153	907-262-4220	60.4804766	-151.0725394
M001153	907-376-7665	61.5826697	-149.4286826
M001156	828-669-0600	35.619814	-82.3197859
M001156	704-833-0096	35.2641049	-81.1825879
M001156	828-327-6100	35.7313122	-81.3022794
M001157	512-473-2357	30.3864963	-97.7528485
M001157	979-830-8497	30.1510408	-96.3912448
M001157	281-398-1247	29.7918796	-95.7318468
M001157	281-255-8372	30.096319	-95.625406
M001159	509-684-3481	48.5392665	-117.9052868
M001159	509-353-2374	47.6575963	-117.4232771
M001159	509-529-9358	46.0668398	-118.3382053
M001160	414-297-1140	43.0344363	-87.9056528
M001163	916-498-5600	38.583455	-121.4987519
M001165	661-327-3611	35.3750182	-119.0467701
M001166	925-754-0716	37.9662464	-121.7741565
M001166	209-476-8552	37.9845267	-121.3334181
M001169	860-549-8463	41.7559191	-72.6646397
M001176	541-318-1298	44.0577537	-121.3095974
M001176	541-465-6750	44.0516985	-123.0857882
M001176	541-608-9102	42.3266304	-122.8712245
M001176	541-278-1129	45.6721567	-118.7845998
M001176	503-326-3386	45.5160884	-122.6748937
M001176	503-362-8102	44.9389422	-123.0380741
M001177	916-786-5560	38.7438059	-121.2464852
M001180	304-284-8506	39.6414678	-79.962809
M001180	304-422-5972	39.2653465	-81.5612631
M001180	304-232-3801	40.068749	-80.723545
M001183	304-342-5855	38.3590792	-81.6350089
M001183	304-368-0567	39.4846406	-80.1429364
M001183	304-264-4626	39.486004	-77.9589126
M001184	606-324-9898	38.4791287	-82.6379039
M001184	859-426-0080	39.0486598	-84.5777416
M001184	502-265-9119	38.4084825	-85.3793849
M001185	845-561-1259	41.5036879	-74.010127
M001188	718-358-6364	40.7626072	-73.8062197
M001188	718-358-6364	40.7148445	-73.8310816
M001190	918-283-6262	36.30985	-95.61233
M001190	918-423-5951	34.9325288	-95.769391
M001190	918-687-2533	35.75024	-95.34046
M001194	231-942-5070	44.2512349	-85.4006138
M001194	989-631-2552	43.6125894	-84.2447609
M001195	304-925-5964	38.3517371	-81.6320052
M001195	304-264-8810	39.4607489	-77.986887
M001196	978-531-1669	42.5204343	-70.8944236
M001198	785-829-9000	38.8403082	-97.6077412
M001198	620-765-7800	37.9722335	-100.8489985
M001199	772-336-2877	27.2741604	-80.3430288
M001199	561-530-7778	26.8097707	-80.0578969
M001199	772-403-0900	27.2006096	-80.2582352
M001199	561-530-7778	26.78415	-80.10731
M001200	804-486-1840	37.5534549	-77.4731247
M001200	757-942-6050	36.7299686	-76.5840834
M001202	888-205-5421	28.546165	-81.3744028
M001202	888-205-5421	28.8121096	-81.2686115
M001203	908-547-3307	40.5674	-74.61027
M001204	\N	40.31559	-76.57823
M001204	570-871-6370	40.68623	-76.19489
M001205	304-250-6177	37.77843	-81.17965
M001205	\N	37.26693	-81.22171
M001205	304-522-2201	38.41913	-82.44445
N000002	718-373-3198	40.6298149	-74.0100219
N000002	212-367-7350	40.7283398	-74.0062211
N000015	413-442-0946	42.4521544	-73.2553498
N000015	413-785-0325	42.1049876	-72.5831648
N000147	202-408-9041	38.90257	-77.00623
N000147	202-678-8900	38.86535	-76.99073
N000179	626-350-0150	34.0866787	-118.0289184
N000181	559-323-5235	36.8268342	-119.7008959
N000181	559-733-3861	36.3304563	-119.2914737
N000188	856-427-7000	39.8719711	-75.0138274
N000189	509-713-7374	46.3444932	-119.2718385
N000189	509-433-7760	\N	\N
N000189	509-452-3243	46.6030785	-120.5008956
N000191	303-335-1045	40.02088	-105.26139
N000191	970-372-3971	40.57127	-105.07662
O000171	520-316-0839	32.8770219	-111.7541607
O000171	928-286-5338	35.2019797	-111.6486132
O000171	928-304-0131	32.3363922	-111.0318291
O000172	\N	40.74924	-73.89132
O000173	612-333-1272	44.98287	-93.27497
P000034	732-571-1140	40.299971	-73.998411
P000034	732-249-8892	40.4962932	-74.4427577
P000096	201-935-2248	40.8946254	-73.975403
P000096	201-935-2248	40.8126716	-74.1243659
P000096	973-472-4510	40.8612025	-74.1238089
P000096	973-523-5152	40.9151292	-74.1699154
P000197	415-556-4862	37.7791976	-122.4121784
P000449	513-684-3265	39.0993058	-84.5103687
P000449	216-522-7095	41.5047711	-81.6917985
P000449	614-469-6774	39.9617817	-83.0020276
P000449	419-259-3895	41.6518781	-83.5353055
P000523	919-967-7924	35.9455529	-79.0136236
P000523	919-859-5999	35.7866594	-78.6442275
P000593	303-274-7944	39.7390492	-105.1404146
P000595	313-226-6020	42.3311478	-83.0532168
P000595	616-233-9150	42.9699746	-85.6709742
P000595	517-377-1508	42.7328928	-84.553479
P000595	906-226-4554	46.545668	-87.413111
P000595	248-608-8040	42.682816	-83.137692
P000595	989-754-0112	43.438206	-83.9377388
P000595	231-947-7773	44.7556947	-85.6448991
P000597	207-774-5019	43.651525	-70.254135
P000597	207-873-5713	44.5488273	-69.6296044
P000599	321-632-1776	28.246385	-80.736905
P000601	228-864-7670	30.40959	-89.05056
P000601	601-582-3246	31.3279296	-89.2911006
P000601	228-202-8104	30.3652731	-88.5554872
P000603	270-782-8303	36.991545	-86.4427761
P000604	862-229-2994	40.7023481	-74.228127
P000604	201-369-0392	40.7077679	-74.0826434
P000604	973-645-3213	40.73821	-74.182222
P000605	717-603-4980	40.30758	-76.84917
P000605	717-635-9504	40.264725	-76.909221
P000607	608-365-8001	42.4975224	-89.0369931
P000607	608-258-9800	43.0737508	-89.3816999
P000608	858-455-5550	32.8755637	-117.2121916
P000609	205-968-1290	33.4391291	-86.7225137
P000609	205-280-6846	32.8389716	-86.6312874
P000609	205-625-4160	33.9464211	-86.4781082
P000610	340-778-5900	17.712475	-64.882701
P000610	340-774-4408	18.334264	-64.919169
P000613	831-424-2229	36.6737182	-121.6575375
P000613	831-429-1976	36.9778501	-122.0226351
P000614	603-285-4300	43.20588	-70.87583
P000615	812-799-5230	39.19891	-85.91904
P000616	952-563-4593	44.82514	-93.30211
P000617	\N	42.2987	-71.05997
P000618	949-668-6600	33.67771	-117.8568
Q000023	773-267-5926	41.9600352	-87.7536327
Q000023	773-267-5926	41.9407628	-87.6538984
R000122	401-943-3100	41.7566184	-71.4607281
R000122	401-528-5200	41.8262057	-71.4109836
R000395	606-439-0794	37.3598755	-83.2591463
R000395	606-886-0844	37.6921145	-82.7793413
R000395	800-632-8588	37.0976576	-84.6167349
R000486	323-721-8790	34.0062357	-118.1519972
R000515	773-779-2400	41.6793889	-87.6811963
R000575	256-236-5655	33.6595488	-85.8294458
R000575	334-745-6221	32.6481909	-85.3772489
R000576	410-628-2701	39.454911	-76.640205
R000577	330-630-7311	41.099949	-81.478217
R000577	800-856-4152	41.235888	-80.819818
R000577	330-740-0193	41.1012396	-80.6529585
R000584	208-342-7985	43.618145	-116.2024194
R000584	208-667-6130	47.6826921	-116.7920799
R000584	208-523-5541	43.4836745	-112.0514001
R000584	208-743-0792	46.422158	-117.028638
R000584	208-236-6817	42.8649373	-112.4428822
R000584	208-734-6780	42.5785998	-114.456626
R000585	607-654-7566	42.1441654	-77.0580681
R000585	315-759-5229	42.868166	-76.980885
R000585	716-708-6369	42.0953958	-79.2406941
R000585	716-379-8434	42.07941	-78.430057
R000595	239-318-6464	26.6426923	-81.8727155
R000595	904-354-4300	30.32909	-81.65997
R000595	305-596-4224	25.7001209	-80.3372066
R000595	407-254-2573	28.5400755	-81.3780508
R000595	561-775-3360	26.8380427	-80.1114094
R000595	850-433-2603	30.405605	-87.213161
R000595	850-599-9100	30.438114	-84.281289
R000595	813-853-1099	27.9514709	-82.4604482
R000597	843-679-9781	34.1959092	-79.8017864
R000597	843-445-6459	33.7081253	-78.8721272
R000599	951-765-2304	33.7472608	-116.9677534
R000599	760-424-8888	33.7302529	-116.3043432
R000600	684-633-3601	-14.2775712	-170.6899787
R000602	516-739-3008	40.7258629	-73.6328165
R000603	910-253-6111	34.0583353	-78.1626772
R000603	919-938-3040	35.46515	-78.387203
R000603	910-395-0202	34.2415626	-77.8669625
R000605	605-225-0366	45.4595325	-98.4883495
R000605	605-224-1450	44.3691728	-100.35182
R000605	605-343-5035	44.0818193	-103.2403111
R000605	605-336-0486	43.5506861	-96.7279008
R000606	301-354-1000	39.083791	-77.1481645
R000608	702-388-0205	36.07191	-115.28661
R000608	775-337-0110	39.52994	-119.81414
R000609	904-831-5205	30.2539737	-81.5959646
R000610	724-219-4200	40.29199	-79.52448
R000612	931-854-9430	36.16183	-85.50005
R000612	615-206-8204	36.37974	-86.48063
R000614	210-821-5024	29.51566	-98.45396
R000615	801-524-4380	40.76664	-111.88714
S000033	802-862-0697	44.4802081	-73.2130702
S000033	800-339-9834	44.4173546	-72.0264647
S000148	585-263-5866	43.1576631	-77.613515
S000148	518-431-4070	42.6551387	-73.749201
S000148	607-772-6792	42.1008749	-75.9123579
S000148	716-846-4111	42.8890285	-78.8795419
S000148	631-753-0978	40.7708436	-73.4078757
S000148	212-486-4430	40.7550353	-73.9718588
S000148	914-734-1532	41.2905102	-73.9167297
S000148	315-423-5471	43.0501042	-76.1543204
S000185	757-380-1000	36.9788711	-76.4304902
S000320	205-731-1384	33.517386	-86.810488
S000320	256-772-0460	34.6471339	-86.7746022
S000320	251-694-4164	30.6938886	-88.0431615
S000320	334-223-7303	32.3752753	-86.3089916
S000320	205-759-5047	33.2101964	-87.5631689
S000344	818-501-9200	34.1615172	-118.4483015
S000510	425-793-5180	47.469273	-122.2153306
S000522	732-780-3035	40.2402775	-74.3081202
S000522	609-585-7878	40.178635	-74.671842
S000522	609-585-7878	40.1027135	-74.5068462
S000770	313-961-4330	42.3307555	-83.048152
S000770	517-203-1760	42.757677	-84.485392
S000770	810-720-4172	43.0201574	-83.6930672
S000770	616-975-0052	43.037652	-85.588081
S000770	906-228-8756	46.5484343	-87.4269822
S000770	231-929-1031	44.7222478	-85.642375
S001145	847-328-3409	42.0884495	-87.8103273
S001145	773-506-7100	41.9827237	-87.6595983
S001145	847-328-3409	42.0466618	-87.6829625
S001148	208-334-1953	43.6174275	-116.2020512
S001148	208-523-6701	43.4936265	-112.0425928
S001148	208-734-7219	42.56422	-114.49355
S001150	818-450-2900	34.1815906	-118.3083786
S001150	323-315-5555	34.1014609	-118.3095706
S001156	562-860-5050	33.9163272	-118.0663246
S001157	770-210-5073	33.5257191	-84.3547213
S001157	770-432-5405	33.874631	-84.5279704
S001165	908-820-0692	40.6662148	-74.1968162
S001165	201-309-0301	40.7248331	-74.0607631
S001165	201-558-0800	40.7850274	-74.0161301
S001168	410-295-1679	38.9793786	-76.4949839
S001168	301-421-4078	39.102063	-76.941686
S001168	410-832-8890	39.4023248	-76.6073615
S001172	308-384-3900	40.9168985	-98.3583418
S001172	308-633-6333	41.8785841	-103.6569419
S001175	650-342-0300	37.5496382	-122.3167359
S001176	985-340-2185	30.517849	-90.4789453
S001176	985-879-2300	29.5979361	-90.7180572
S001176	985-893-9064	30.4172835	-90.0434162
S001176	504-837-1259	29.9992548	-90.1242066
S001177	670-532-2647	\N	\N
S001177	670-323-2647	\N	\N
S001177	670-433-2647	\N	\N
S001180	503-557-1324	45.3571432	-122.6070483
S001180	503-588-9100	44.9425483	-123.0352226
S001181	603-752-6300	44.4781896	-71.1708276
S001181	603-542-4872	43.3727196	-72.3373946
S001181	603-750-3004	43.1952108	-70.8748699
S001181	603-358-6604	42.9323699	-72.2794587
S001181	603-647-7500	42.9940731	-71.4638668
S001181	603-883-0196	42.7633363	-71.4659542
S001183	480-946-2411	33.61738	-111.89853
S001184	803-771-6112	34.0108841	-81.0384094
S001184	864-233-5366	34.849407	-82.40024
S001184	843-727-4525	32.8769943	-80.0121928
S001185	334-877-4414	32.4071252	-87.0214629
S001185	205-254-1960	33.5132889	-86.8058256
S001185	334-262-1919	32.3759486	-86.3054822
S001185	205-752-5380	33.2071459	-87.56926
S001189	229-396-5175	31.4548661	-83.5107367
S001189	478-971-1776	32.6144617	-83.6893055
S001190	847-383-4870	42.1964098	-87.9362154
S001190	\N	42.3565821	-88.0740214
S001190	\N	42.36129	-87.83472
S001191	602-598-7327	33.50965	-112.03408
S001192	801-364-5550	40.7686249	-111.8789585
S001192	435-627-1500	37.1096135	-113.5887467
S001193	510-370-3322	37.6950519	-122.0722708
S001194	808-523-2061	21.3036893	-157.862353
S001195	573-335-0101	37.3029285	-89.5558479
S001195	573-756-9755	37.7803665	-90.4203215
S001195	573-609-2996	36.7847526	-90.4294579
S001195	573-364-2455	37.9315472	-91.7789873
S001195	417-255-1515	36.7283104	-91.8529136
S001196	518-743-0964	43.30977	-73.64131
S001196	518-561-2324	44.6964439	-73.4524475
S001196	315-782-3150	43.974587	-75.907664
S001197	308-233-3677	40.715284	-99.086211
S001197	402-476-1400	40.8084319	-96.7048142
S001197	402-550-8040	41.2618733	-96.1778064
S001197	308-632-6032	41.8624626	-103.6650529
S001198	907-271-5915	61.2171561	-149.9043364
S001198	907-456-0261	64.8383052	-147.7082477
S001198	907-586-7277	58.3003324	-134.4205554
S001198	907-225-6880	55.3492023	-131.6673863
S001198	907-262-4040	60.480503	-151.0747996
S001198	907-357-9956	61.5826697	-149.4286826
S001199	717-969-6132	39.8018282	-76.9852327
S001199	717-393-0667	40.0368908	-76.3031804
S001199	717-969-6133	39.90324	-76.59862
S001200	407-452-1171	28.2906297	-81.4123113
S001200	202-615-1308	28.1083914	-81.628392
S001200	202-322-4476	28.36525	-81.2749461
S001200	202-600-0843	27.8999663	-81.596459
S001200	202-600-0843	28.0263894	-81.732919
S001201	631-923-4100	40.873976	-73.411319
S001201	718-631-0400	40.7683191	-73.7394598
S001203	218-284-8721	46.8750559	-96.7677958
S001203	218-722-2390	46.7825302	-92.1069752
S001203	651-221-1016	44.9374118	-93.0843115
S001203	507-288-2003	44.0524692	-92.4763781
S001204	\N	13.4763888	144.7455451
S001207	973-526-5668	40.86193	-74.41507
S001208	517-993-0510	42.74097	-84.56793
S001209	804-401-4110	37.6518	-77.58431
S001209	202-225-2815	38.20557	-77.58501
S001211	602-956-2463	33.48161	-111.98764
S001212	218-355-0862	46.35587	-94.20074
S001212	\N	45.57578	-93.22133
S001212	218-355-0726	47.48942	-92.88417
S001212	218-481-6396	46.83671	-92.21649
S001213	608-752-4050	42.68247	-89.02214
S001213	262-637-0510	42.72521	-87.78431
S001213	262-654-1901	42.6381671	-87.9002263
S001214	941-575-9101	26.93505	-82.05085
S001215	202-227-7397	42.47818	-83.47075
S001216	425-657-1001	47.54425	-122.0602
S001217	850-942-8415	30.44252	-84.2819
T000193	601-866-9003	32.348821	-90.459947
T000193	662-335-9003	33.4079337	-91.051782
T000193	662-455-9003	33.5146884	-90.1821178
T000193	601-946-9003	32.339995	-90.214478
T000193	662-326-9003	34.2568675	-90.2711465
T000193	662-741-9003	33.8783982	-90.7298777
T000250	605-225-8823	45.4614099	-98.4898984
T000250	605-348-7551	44.0872508	-103.239745
T000250	605-334-9596	43.4995298	-96.7639144
T000460	707-226-9898	38.2491248	-122.2743897
T000460	707-542-7182	38.4620056	-122.7253838
T000460	707-645-1888	38.1023237	-122.2557289
T000461	610-434-1444	40.569891	-75.519529
T000461	814-453-3010	42.1289745	-80.0840689
T000461	717-782-3951	40.261919	-76.882739
T000461	215-241-1090	39.94829	-75.1441
T000461	412-803-3501	40.4376735	-79.9996749
T000461	570-820-4088	41.2419893	-75.8773626
T000461	814-266-5970	40.273143	-78.848055
T000463	937-225-2843	39.7586771	-84.1945335
T000464	406-252-0550	45.783552	-108.509375
T000464	406-586-4450	45.6795093	-111.0370333
T000464	406-723-3277	46.01461	-112.538136
T000464	402-452-9585	47.5065688	-111.3040376
T000464	406-449-5401	46.5865426	-112.020799
T000464	406-257-3360	48.1958316	-114.3128666
T000464	406-728-3003	46.8704714	-113.9961779
T000467	814-353-0215	40.8956202	-77.7877153
T000467	814-670-0432	41.43636	-79.70781
T000468	702-220-9823	36.1673606	-115.1506179
T000469	518-465-0700	42.6553697	-73.7616111
T000469	518-843-3400	42.9387067	-74.1884752
T000469	518-374-4547	42.8143015	-73.9398087
T000472	951-222-0203	33.9787895	-117.3724531
T000474	909-481-6474	34.0707079	-117.5807122
T000474	\N	34.0993394	-117.3721013
T000476	704-509-9087	35.3458971	-80.8466732
T000476	252-329-0371	35.5709032	-77.3592441
T000476	828-693-8750	35.314451	-82.460467
T000476	336-885-0685	36.008425	-79.985673
T000476	919-856-4630	35.7799621	-78.633881
T000479	972-202-4150	33.07129	-96.81871
T000480	864-241-0175	34.84962	-82.39957
T000480	864-583-3264	34.95094	-81.93319
T000481	313-203-7540	42.27338	-83.13439
T000482	\N	42.70637	-71.15331
T000482	978-459-0101	42.64756	-71.30745
T000483	301-926-0300	39.11684	-77.20114
U000031	269-385-0039	42.2926475	-85.5791097
U000031	269-982-1986	42.105351	-86.484035
U000040	630-549-2190	41.8620585	-88.2002888
V000081	718-599-3658	40.7085893	-73.9592284
V000081	212-619-2606	40.7137324	-74.0008018
V000130	619-422-5963	32.6410939	-117.0811877
V000130	760-312-9900	32.7956143	-115.5618357
V000131	214-741-1387	32.7687234	-96.8364642
V000131	817-920-9086	32.7547849	-97.2139494
V000132	361-230-9776	27.7499027	-98.0720001
V000132	956-544-8352	25.93642	-97.49335
V000132	956-276-4497	26.1404901	-97.6538364
V000132	956-520-8273	26.1602727	-97.9944049
V000133	609-625-5008	39.45148	-74.7267
W000187	323-757-8900	33.94388	-118.2777646
W000437	228-871-7017	30.3665745	-89.0969882
W000437	662-429-1002	34.822334	-89.994967
W000437	601-965-4644	32.294793	-90.1837769
W000437	662-844-5010	34.2594314	-88.7067348
W000779	541-330-9142	44.0577537	-121.3095974
W000779	541-431-0229	44.0516985	-123.0857882
W000779	541-962-7691	45.326715	-118.0926554
W000779	541-858-5122	42.3253466	-122.8769125
W000779	503-326-7525	45.5293348	-122.6555902
W000779	503-589-4555	44.9304368	-123.0290967
W000795	803-642-6416	33.5823882	-81.7275886
W000795	803-939-0041	33.9993356	-81.0889668
W000797	305-936-5724	25.9528671	-80.1390699
W000797	954-845-1179	26.130384	-80.333636
W000798	517-780-9075	42.247186	-84.414954
W000800	802-652-2450	44.4626812	-73.2177683
W000802	401-453-5294	41.8235873	-71.4113021
W000804	804-730-6595	37.6130093	-77.3335134
W000804	540-659-2734	38.4781455	-77.4211435
W000804	804-443-0668	37.9307412	-76.8625874
W000805	276-628-8158	36.7095562	-81.9767798
W000805	757-441-3079	36.8461399	-76.293138
W000805	804-775-2314	37.5374358	-77.4362913
W000805	540-857-2676	37.270958	-79.942561
W000805	703-442-0670	38.9145473	-77.2196271
W000806	352-241-9230	28.5552249	-82.3881192
W000806	352-241-9204	28.8363417	-82.3321296
W000806	352-241-9220	28.80855	-81.87609
W000806	352-383-3552	28.9188822	-81.970805
W000808	954-921-3682	26.01104	-80.16059
W000808	305-690-5905	25.9436377	-80.2046576
W000808	954-989-2688	25.993095	-80.2038997
W000809	479-424-1146	35.34916	-94.3664
W000809	870-741-6900	36.2326224	-93.1068643
W000809	479-464-0446	36.3046472	-94.1867522
W000812	636-779-5449	38.5980031	-90.4902674
W000813	574-204-2645	41.6614853	-86.1788818
W000813	574-223-4373	41.0670871	-86.2158526
W000814	409-835-0108	30.0825032	-94.098555
W000814	979-285-0231	29.0445713	-95.4537198
W000814	281-316-0231	29.5035441	-95.1045214
W000815	\N	39.0911309	-84.2738952
W000815	513-474-7777	39.0731109	-84.3352704
W000815	513-605-1380	38.9507705	-83.404366
W000816	512-473-8910	30.2720399	-97.7409012
W000816	817-774-2575	32.3462862	-97.3858312
W000817	617-565-3170	42.3613091	-71.0593927
W000817	413-788-2690	42.1032165	-72.5929441
W000821	870-864-8946	33.2122274	-92.6633531
W000821	501-609-9796	34.5131753	-93.0513324
W000821	479-667-0075	35.4864789	-93.8267906
W000821	870-536-8178	34.2211366	-92.0026041
W000822	609-883-0026	40.2793143	-74.8302945
W000823	386-279-0707	29.02759	-81.30621
W000823	386-302-0442	29.55515	-81.23422
W000823	386-238-9711	29.1264	-81.02174
W000825	703-234-3800	39.02961	-77.40825
Y000033	907-271-5978	61.1884605	-149.8942221
Y000033	907-456-0210	64.8435165	-147.7220032
Y000062	502-582-5129	38.2486082	-85.7622452
Y000062	502-935-6934	38.1458873	-85.8399034
Z000017	631-209-4235	40.9172285	-72.6627645
Z000017	631-289-1097	40.7671697	-73.0138358
Y000064	317-226-6700	39.770723	-86.15703
Y000064	812-542-4820	38.3332536	-85.8184877
Y000064	\N	37.975188	-87.567486
Y000064	\N	41.0737372	-85.1404535
D000622	618-722-7070	38.5135	-89.98093
D000622	618-677-7000	37.73428	-89.21251
D000622	312-886-3506	41.8784454	-87.63
D000622	309-606-7060	41.51114	-90.57433
D000622	217-528-6124	39.800468	-89.6509028
H001076	603-752-6190	44.469939	-71.1821158
H001076	603-622-2204	43.0009812	-71.4662599
H001076	603-880-3314	42.7617932	-71.4674553
H001076	603-433-4445	43.0856076	-70.8098029
H001076	603-622-2204	43.2024699	-71.5416652
C001113	702-388-5020	36.1651058	-115.1424211
C001113	775-686-5750	39.5215352	-119.810287
B001301	231-944-7633	44.7066718	-85.5961732
B001301	906-273-2227	46.547038	-87.4227729
E000297	718-450-8241	40.863512	-73.895791
E000297	212-663-3900	40.8092343	-73.9474317
C001111	727-318-6770	27.85592	-82.7954
C001111	727-318-6770	27.772067	-82.64355
C001111	727-318-6770	27.7569	-82.66288
H001077	337-703-6105	30.22335	-92.01907
H001077	337-656-2833	30.2305579	-93.2196295
V000128	301-545-1500	39.08895	-77.15119
V000128	301-797-2826	39.64151	-77.7191
V000128	667-212-4610	39.3114748	-76.6216137
V000128	410-263-1325	38.9785971	-76.4976378
V000128	410-221-2074	38.5650251	-76.0752663
V000128	301-322-6560	38.90526	-76.83685
K000393	318-445-2892	31.2934776	-92.5156529
K000393	225-926-8033	30.4233173	-91.1027159
K000393	985-851-0956	29.5979835	-90.7180594
K000393	337-269-5980	30.1948666	-92.014654
K000393	337-436-6255	32.5175209	-91.102734
K000393	985-809-8153	30.4182357	-90.0470795
K000393	318-361-1489	32.5175209	-92.1087384
K000393	504-581-6190	29.9484696	-90.0711728
K000393	318-670-5192	32.515033	-93.7502759
C001110	714-621-0102	33.7673604	-117.8703812
E000298	316-262-8992	37.6780708	-97.246671
G000585	213-481-1427	34.0569399	-118.2618953
N000190	803-327-1114	34.9343895	-80.9997773
N000190	\N	35.076577	-81.642889
C001114	801-851-2500	40.232986	-111.6578512
H001079	601-965-4459	32.3004269	-90.1867448
H001079	662-236-1018	34.3672392	-89.5212463
H001079	228-867-9710	30.3701149	-89.0870838
L000588	724-206-4860	40.65095	-80.3124
L000588	412-344-5583	40.38423	-80.04388
L000589	623-776-7911	33.6373437	-112.2108779
C001115	361-884-2222	27.7917948	-97.3931982
C001115	361-894-6446	28.8476874	-96.9978986
B001306	614-523-2555	40.1107173	-83.0074644
H001082	918-935-3222	36.0445807	-95.9535553
M001206	585-232-4850	43.1576631	-77.613515
S001205	610-626-1913	39.94101	-75.25719
W000826	484-781-6000	40.60131	-75.47465
W000826	610-333-1170	40.69081	-75.21124
M001208	470-773-6330	33.91263	-84.35953
A000376	972-972-7949	32.95034	-96.73449
H001089	573-334-5995	37.3039	-89.52491
H001089	202-860-5207	38.96012	-92.32279
H001089	816-960-4694	39.05193	-94.5903
H001089	417-869-4433	37.2088	-93.29409
H001089	314-354-7060	38.62588	-90.196
K000395	570-374-9469	\N	\N
K000395	570-996-6550	\N	\N
K000395	570-322-3961	\N	\N
B001311	740-218-5300	\N	\N
M001210	252-931-1003	\N	\N
M001210	910-937-6929	\N	\N
\.


--
-- Data for Name: example_issue_why_statement; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.example_issue_why_statement (issue_id, statement) FROM stdin;
\.


--
-- Data for Name: focus_issue; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.focus_issue (issue_id, focused_at) FROM stdin;
5	2021-11-17 02:48:32.678903
6	2021-12-08 04:18:24.338615
7	2022-01-05 13:16:45.77685
8	2022-01-19 01:54:20.83749
\.


--
-- Data for Name: issue; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issue (id, title, precomposed_tweet_template, image_url, description) FROM stdin;
5	Build Back Better Act Climate Provisions	The BBB will deliver huge emissions reductions and consumer savings by 2030. %s do everything in your power to retain the bill’s climate provisions.\n\nClimate impact data: https://twitter.com/JesseJenkins/status/1459245283666137088?s=20\nConsumer savings: https://rmi.org/rmi-reality-check-proposed-clean-energy-incentives-would-save-electricity-customers-billions/	https://images.fastcompany.net/image/upload/w_1280,f_auto,q_auto,fl_lossy/wp-cms/uploads/2021/11/p-1-90692231-the-build-back-better-ev-tax-credit-still-favors-car-purchases-over-bikes.jpg	The Build Back Better Act is the cornerstone of the Biden administration’s plan to meet U.S. commitments under the Paris agreement.
6	End Fossil Fuel Subsidies	It’s time for the U.S. government to #StopFundingFossils. Taxpayers should not be forced to prop up polluters and prolong the displacement of fossil fuels by renewable energy. %s, end govt support for coal, oil & gas.	https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/1632954706742-4Q1Y88SNBEDEWR3V45FI/zbynek-burival-GrmwVnVSSdU-unsplash.jpg	The U.S. government uses public money to prop up the fossil fuel industry, and it needs to stop.
7	Democracy Reform	When Americans face barriers to voting or their votes are diluted by gerrymandering, that isn't democracy. Voting reform is climate action & the Senate must pass the Freedom to Vote Act without delay. We can't afford to silence the pro-climate majority. %s	https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/f703d0be-22fa-48e3-9b55-b069abfce197/element5-digital-ls8Kc0P9hAA-unsplash.jpg	There’s no climate justice without strengthening our democracy.
8	Clean Electricity Tax Incentives	The clean power incentives in #BuildBackBetter could lower electricity emissions by 73%. They're also a huge job creator and could net 600k new jobs per year. It's time for the Senate to step up and get this bill across the finish line. %s	https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/78bd591d-35ea-463f-bd67-1905742c95a9/rawfilm-4y2TkE8NYXY-unsplash.jpg?format=500w	The clean electricity tax incentives in the Build Back Better Act would put the U.S. on track to reach net-zero power by 2035.
\.


--
-- Data for Name: lcv_score_lifetime; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lcv_score_lifetime (bioguide_id, score) FROM stdin;
F000062	90
G000555	95
S000148	92
S001217	4
R000595	6
S000320	13
H001042	95
S001194	98
H001089	4
B000575	6
B001243	3
C001075	7
K000393	3
C001070	93
T000461	5
S000770	90
P000595	93
R000608	96
C001113	95
M000639	95
B001288	93
P000449	19
B000944	94
H001076	98
S001181	96
K000367	92
S001203	98
J000293	3
B001230	97
S001191	76
B001261	7
V000128	98
C000141	92
C001098	3
C001056	5
D000618	7
T000464	88
S001198	8
M001153	18
B001267	88
H001061	9
C001096	3
D000563	88
D000622	90
H001046	93
R000615	7
L000577	6
W000779	91
M001176	99
M001169	96
B001277	97
C001095	4
B001236	8
I000024	5
L000575	4
E000295	4
G000386	18
M000133	94
W000817	93
Y000064	5
B001310	4
B001135	9
T000476	10
K000384	95
W000805	88
C000127	93
M001111	91
C001047	17
M001183	49
C000174	84
C001088	94
P000603	8
M000355	8
R000122	97
W000802	97
G000359	13
S001184	4
H001079	15
W000437	6
C001035	60
K000383	89
R000584	7
C000880	6
S001197	2
F000463	5
M000934	8
R000605	5
T000250	10
L000174	94
S000033	90
L000578	2
H001068	98
G000559	91
M001177	4
T000460	93
M001163	97
B001287	94
M001166	95
H001090	98
D000623	99
P000197	94
L000551	97
S001175	91
S001193	92
C001059	54
K000389	98
E000215	97
L000397	91
P000613	97
N000181	3
M001165	3
C001112	94
G000061	21
B001285	96
C001080	98
S001150	98
C001097	88
S000344	97
A000371	96
N000179	91
L000582	91
G000585	97
T000474	96
R000599	95
B001270	88
S001156	94
R000486	96
T000472	98
C000059	7
W000187	92
B001300	98
P000618	94
C001110	93
L000579	98
L000593	98
V000130	96
P000608	93
J000020	33
Z000017	14
S001201	98
R000602	94
M001137	90
M001188	98
V000081	94
J000294	96
C001067	95
N000002	97
M000087	96
E000297	99
O000172	96
M001185	90
D000630	98
T000469	97
S001196	38
R000585	13
K000386	34
M001206	94
H001038	95
G000578	12
D000628	4
R000609	9
L000586	87
W000823	26
M001202	91
P000599	6
S001200	99
D000627	97
W000806	6
B001257	10
C001111	93
C001066	93
B001260	23
S001214	4
M001199	29
F000462	98
D000610	92
W000797	93
W000808	91
D000600	13
R000575	5
A000055	3
B001274	8
P000609	1
S001185	81
C001055	95
W000812	4
L000569	3
H001053	3
C001061	89
G000546	4
L000576	3
S001195	1
B001309	10
F000459	4
D000616	2
C000754	83
R000612	6
G000590	2
K000392	4
C001068	97
S001176	4
H001077	3
J000299	3
G000577	5
F000466	81
B001296	97
E000296	96
D000631	98
S001205	98
H001085	98
W000826	96
C001090	96
M001204	8
P000605	3
S001199	6
K000395	5
J000302	6
R000610	10
T000467	5
K000376	5
L000588	88
D000482	80
B001301	11
H001058	5
M001194	7
K000380	96
U000031	28
W000798	4
S001208	96
L000592	100
S001215	96
D000624	97
T000481	96
L000581	96
T000468	95
A000369	7
L000590	96
H001066	84
N000188	93
V000133	82
K000394	98
S000522	62
G000583	88
P000034	96
M001203	98
S001165	91
P000096	94
P000604	91
S001207	96
W000822	97
C000266	12
W000815	2
B001281	95
J000289	3
L000566	3
J000292	4
G000563	4
D000626	3
K000009	84
T000463	10
B001306	11
R000577	91
J000295	12
G000588	24
P000614	98
K000382	95
H001088	6
C001119	98
P000616	98
M001143	94
O000173	96
E000294	3
S001212	16
S001213	20
P000607	97
K000188	90
M001160	94
G000576	3
T000165	0
G000579	8
O000171	92
K000368	73
G000551	96
G000565	5
B001302	5
S001183	6
G000574	97
L000589	1
S001211	98
C001109	2
C001103	4
B000490	55
F000465	5
J000288	96
M001208	96
S001189	3
H001071	0
L000583	1
A000372	1
S001157	84
H001052	3
R000576	89
S001168	96
B001304	93
H000874	82
T000483	98
M000687	86
R000606	99
G000552	4
C001120	14
T000479	10
G000589	4
F000468	88
B000755	3
G000553	87
M001157	8
G000377	6
W000814	2
G000581	80
E000299	96
J000032	83
A000375	2
C001091	96
R000614	0
W000816	2
B001248	3
C001115	0
C001063	47
G000587	94
J000126	87
C001051	4
A000376	96
V000131	90
V000132	77
D000399	97
B001291	2
Y000033	9
D000197	96
N000191	98
B001297	3
L000564	3
C001121	98
P000593	87
A000377	6
R000515	81
K000385	95
G000586	100
Q000023	98
C001117	98
D000096	93
K000391	98
S001145	98
S001190	95
F000454	94
B001295	7
D000619	10
U000040	98
K000378	8
B001286	90
L000585	3
L000570	97
S001192	4
C001114	2
B001278	98
B000574	96
D000191	92
S001180	73
L000557	93
C001069	96
D000216	95
H001047	95
H001081	98
C001087	5
H001072	7
W000809	4
W000821	2
H001082	2
M001190	1
L000491	5
C001053	7
A000378	94
N000015	93
M000312	99
T000482	96
C001101	96
M001196	96
P000617	98
L000562	95
K000375	96
W000813	4
B001299	2
B001307	4
P000615	10
C001072	93
B001275	6
H001074	15
B001251	90
M001210	11
P000523	92
F000450	4
R000603	4
H001067	2
B001311	0
M001156	5
A000370	97
B001305	4
W000804	10
L000591	96
S000185	92
M001200	92
C001118	0
S001209	96
B001292	96
G000568	6
W000825	98
C001078	97
D000617	95
L000560	92
H001056	12
N000189	5
M001159	4
K000381	95
J000298	97
S001216	98
S000510	91
M001180	8
M001195	3
M001205	12
B001303	96
C001108	5
G000558	5
Y000062	94
M001184	10
R000395	8
B001282	3
C001084	98
L000559	96
W000795	3
D000615	3
T000480	10
N000190	2
C000537	85
R000597	2
K000388	2
T000193	83
G000591	10
P000601	3
P000597	96
G000592	96
F000469	4
S001148	9
F000449	21
B001298	16
S001172	4
M001198	6
D000629	94
E000298	4
J000301	8
W000800	95
N000147	100
G000582	14
S001204	93
P000610	79
R000600	14
S001177	100
\.


--
-- Data for Name: lcv_score_year; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lcv_score_year (bioguide_id, score_year, score) FROM stdin;
L000578	2020	5
H001068	2020	100
G000559	2020	100
M001177	2020	0
T000460	2020	100
M001163	2020	100
B001287	2020	100
M001166	2020	100
H001090	2020	100
D000623	2020	100
P000197	2020	100
L000551	2020	100
S001175	2020	100
S001193	2020	100
C001059	2020	100
K000389	2020	100
E000215	2020	100
L000397	2020	90
P000613	2020	100
N000181	2020	10
M001165	2020	14
C001112	2020	100
G000061	2020	21
B001285	2020	100
C001080	2020	100
S001150	2020	100
C001097	2020	100
S000344	2020	100
A000371	2020	100
N000179	2020	95
L000582	2020	95
G000585	2020	100
T000474	2020	95
R000599	2020	100
B001270	2020	100
S001156	2020	95
R000486	2020	100
T000472	2020	100
C000059	2020	14
W000187	2020	100
B001300	2020	100
P000618	2020	100
C001110	2020	100
L000579	2020	100
L000593	2020	100
V000130	2020	100
P000608	2020	100
J000020	2020	33
Z000017	2020	24
S001201	2020	100
R000602	2020	90
M001137	2020	100
M001188	2020	100
V000081	2020	100
J000294	2020	100
C001067	2020	100
N000002	2020	100
M000087	2020	100
E000297	2020	100
O000172	2020	95
M001185	2020	100
D000630	2020	100
T000469	2020	100
S001196	2020	48
R000585	2020	48
K000386	2020	62
M001206	2020	90
H001038	2020	100
G000578	2020	10
D000628	2020	5
R000609	2020	14
L000586	2020	100
W000823	2020	14
M001202	2020	95
P000599	2020	19
S001200	2020	100
D000627	2020	100
W000806	2020	10
B001257	2020	19
C001111	2020	100
C001066	2020	100
B001260	2020	33
S001214	2020	0
M001199	2020	29
F000462	2020	100
D000610	2020	100
W000797	2020	100
W000808	2020	95
D000600	2020	29
R000575	2020	5
A000055	2020	5
B001274	2020	0
P000609	2020	0
S001185	2020	90
C001055	2020	100
W000812	2020	24
L000569	2020	5
H001053	2020	10
C001061	2020	86
G000546	2020	14
L000576	2020	10
S001195	2020	5
B001309	2020	10
F000459	2020	10
D000616	2020	10
C000754	2020	100
R000612	2020	10
G000590	2020	5
K000392	2020	10
C001068	2020	100
S001176	2020	14
H001077	2020	10
J000299	2020	0
G000577	2020	5
F000466	2020	84
B001296	2020	100
E000296	2020	100
D000631	2020	100
S001205	2020	100
H001085	2020	100
W000826	2020	95
C001090	2020	100
M001204	2020	10
P000605	2020	5
S001199	2020	14
K000395	2020	5
J000302	2020	10
R000610	2020	14
T000467	2020	10
K000376	2020	19
L000588	2020	95
D000482	2020	95
B001301	2020	14
H001058	2020	29
M001194	2020	19
K000380	2020	100
U000031	2020	62
W000798	2020	5
S001208	2020	95
L000592	2020	100
S001215	2020	100
D000624	2020	100
T000481	2020	95
L000581	2020	100
T000468	2020	100
A000369	2020	24
L000590	2020	95
H001066	2020	100
N000188	2020	100
V000133	2020	67
K000394	2020	100
S000522	2020	81
G000583	2020	100
P000034	2020	100
M001203	2020	100
S001165	2020	100
P000096	2020	100
P000604	2020	86
S001207	2020	100
W000822	2020	100
C000266	2020	14
W000815	2020	5
B001281	2020	95
J000289	2020	5
L000566	2020	10
J000292	2020	14
G000563	2020	19
D000626	2020	10
K000009	2020	90
T000463	2020	33
B001306	2020	24
R000577	2020	100
J000295	2020	33
G000588	2020	29
P000614	2020	100
K000382	2020	100
H001088	2020	5
C001119	2020	100
P000616	2020	100
M001143	2020	100
O000173	2020	95
E000294	2020	10
S001212	2020	29
S001213	2020	19
P000607	2020	100
K000188	2020	90
M001160	2020	100
G000576	2020	5
T000165	2020	0
G000579	2020	19
O000171	2020	100
K000368	2020	93
G000551	2020	100
G000565	2020	5
B001302	2020	0
S001183	2020	14
G000574	2020	100
L000589	2020	0
S001211	2020	100
C001109	2020	5
C001103	2020	14
B000490	2020	100
F000465	2020	10
J000288	2020	100
M001208	2020	95
S001189	2020	10
H001071	2020	0
L000583	2020	0
A000372	2020	0
S001157	2020	100
H001052	2020	0
R000576	2020	100
S001168	2020	100
B001304	2020	95
H000874	2020	100
T000483	2020	100
M000687	2020	100
R000606	2020	100
G000552	2020	5
C001120	2020	19
T000479	2020	14
G000589	2020	0
F000468	2020	100
B000755	2020	5
G000553	2020	100
M001157	2020	33
G000377	2020	14
W000814	2020	0
G000581	2020	95
E000299	2020	95
J000032	2020	100
A000375	2020	0
C001091	2020	100
R000614	2020	0
W000816	2020	10
B001248	2020	10
C001115	2020	0
C001063	2020	90
G000587	2020	100
J000126	2020	95
C001051	2020	10
A000376	2020	100
V000131	2020	100
V000132	2020	100
D000399	2020	100
B001291	2020	0
Y000033	2020	33
D000197	2020	100
N000191	2020	100
B001297	2020	0
L000564	2020	5
C001121	2020	100
P000593	2020	100
A000377	2020	0
R000515	2020	100
K000385	2020	100
G000586	2020	100
Q000023	2020	100
C001117	2020	100
D000096	2020	100
K000391	2020	100
S001145	2020	95
S001190	2020	100
F000454	2020	100
B001295	2020	24
D000619	2020	38
U000040	2020	100
K000378	2020	29
B001286	2020	100
L000585	2020	14
L000570	2020	100
S001192	2020	14
C001114	2020	5
B001278	2020	100
B000574	2020	100
D000191	2020	100
S001180	2020	86
L000557	2020	100
C001069	2020	100
D000216	2020	100
H001047	2020	100
H001081	2020	100
C001087	2020	10
H001072	2020	19
W000809	2020	10
W000821	2020	10
H001082	2020	0
M001190	2020	5
L000491	2020	10
C001053	2020	10
A000378	2020	95
N000015	2020	100
M000312	2020	100
T000482	2020	95
C001101	2020	100
M001196	2020	100
P000617	2020	100
L000562	2020	100
K000375	2020	95
W000813	2020	14
B001299	2020	5
B001307	2020	5
P000615	2020	10
C001072	2020	100
B001275	2020	14
H001074	2020	10
B001251	2020	100
M001210	2020	10
P000523	2020	100
F000450	2020	10
R000603	2020	14
H001067	2020	5
B001311	2020	0
M001156	2020	14
A000370	2020	95
B001305	2020	10
W000804	2020	19
L000591	2020	95
S000185	2020	100
M001200	2020	100
C001118	2020	0
S001209	2020	95
B001292	2020	100
G000568	2020	14
W000825	2020	100
C001078	2020	100
D000617	2020	100
L000560	2020	100
H001056	2020	33
N000189	2020	14
M001159	2020	14
K000381	2020	100
J000298	2020	95
S001216	2020	100
S000510	2020	100
M001180	2020	29
M001195	2020	10
M001205	2020	19
B001303	2020	95
C001108	2020	10
G000558	2020	14
Y000062	2020	100
M001184	2020	0
R000395	2020	10
B001282	2020	10
C001084	2020	100
L000559	2020	100
W000795	2020	10
D000615	2020	5
T000480	2020	8
N000190	2020	0
C000537	2020	100
R000597	2020	5
K000388	2020	5
T000193	2020	100
G000591	2020	10
P000601	2020	10
P000597	2020	100
G000592	2020	95
F000469	2020	5
S001148	2020	19
F000449	2020	43
B001298	2020	24
S001172	2020	5
M001198	2020	5
D000629	2020	90
E000298	2020	0
J000301	2020	10
W000800	2020	100
N000147	2020	100
G000582	2020	0
S001204	2020	100
P000610	2020	100
R000600	2020	0
S001177	2020	100
F000062	2020	92
G000555	2020	100
S000148	2020	100
S001217	2020	0
R000595	2020	15
S000320	2020	0
H001042	2020	92
S001194	2020	100
H001089	2020	0
B000575	2020	15
B001243	2020	0
C001075	2020	0
K000393	2020	0
C001070	2020	92
T000461	2020	8
S000770	2020	92
P000595	2020	92
R000608	2020	92
C001113	2020	92
M000639	2020	92
B001288	2020	100
P000449	2020	15
B000944	2020	92
H001076	2020	92
S001181	2020	92
K000367	2020	62
S001203	2020	92
J000293	2020	0
B001230	2020	92
S001191	2020	62
B001261	2020	0
V000128	2020	92
C000141	2020	92
C001098	2020	0
C001056	2020	8
D000618	2020	15
T000464	2020	92
S001198	2020	8
M001153	2020	15
B001267	2020	77
H001061	2020	15
C001096	2020	15
D000563	2020	92
D000622	2020	92
H001046	2020	85
R000615	2020	0
L000577	2020	0
W000779	2020	92
M001176	2020	92
M001169	2020	85
B001277	2020	92
C001095	2020	15
B001236	2020	15
I000024	2020	0
L000575	2020	0
E000295	2020	8
G000386	2020	8
M000133	2020	77
W000817	2020	62
Y000064	2020	15
B001310	2020	0
B001135	2020	15
T000476	2020	15
K000384	2020	92
W000805	2020	92
C000127	2020	92
M001111	2020	91
C001047	2020	15
M001183	2020	54
C000174	2020	92
C001088	2020	92
P000603	2020	8
M000355	2020	23
R000122	2020	100
W000802	2020	100
G000359	2020	15
S001184	2020	8
H001079	2020	8
W000437	2020	15
C001035	2020	46
K000383	2020	77
R000584	2020	0
C000880	2020	0
S001197	2020	0
F000463	2020	0
M000934	2020	0
R000605	2020	0
T000250	2020	8
L000174	2020	92
S000033	2020	62
\.


--
-- Data for Name: member_of_congress; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.member_of_congress (bioguide_id, full_name, legislative_role, state, congressional_district, party, dc_phone_number, twitter_handle, cwc_office_code) FROM stdin;
B000944	Sherrod Brown	sen	OH	\N	Democrat	202-224-2315	SenSherrodBrown	SOH01
C000127	Maria Cantwell	sen	WA	\N	Democrat	202-224-3441	SenatorCantwell	\N
C000141	Benjamin L. Cardin	sen	MD	\N	Democrat	202-224-4524	SenatorCardin	\N
C000174	Thomas R. Carper	sen	DE	\N	Democrat	202-224-2441	SenatorCarper	\N
C001070	Robert P. Casey, Jr.	sen	PA	\N	Democrat	202-224-6324	SenBobCasey	\N
F000062	Dianne Feinstein	sen	CA	\N	Democrat	202-224-3841	SenFeinstein	\N
K000367	Amy Klobuchar	sen	MN	\N	Democrat	202-224-3244	SenAmyKlobuchar	\N
M000639	Robert Menendez	sen	NJ	\N	Democrat	202-224-4744	SenatorMenendez	\N
S000033	Bernard Sanders	sen	VT	\N	Independent	202-224-5141	SenSanders	SVT01
S000770	Debbie Stabenow	sen	MI	\N	Democrat	202-224-4822	SenStabenow	\N
T000464	Jon Tester	sen	MT	\N	Democrat	202-224-2644	SenatorTester	\N
W000802	Sheldon Whitehouse	sen	RI	\N	Democrat	202-224-2921	SenWhitehouse	\N
B001261	John Barrasso	sen	WY	\N	Republican	202-224-6441	SenJohnBarrasso	\N
W000437	Roger F. Wicker	sen	MS	\N	Republican	202-224-6253	SenatorWicker	\N
C001035	Susan M. Collins	sen	ME	\N	Republican	202-224-2523	SenatorCollins	SME02
C001056	John Cornyn	sen	TX	\N	Republican	202-224-2934	JohnCornyn	STX02
D000563	Richard J. Durbin	sen	IL	\N	Democrat	202-224-2152	SenatorDurbin	\N
G000359	Lindsey Graham	sen	SC	\N	Republican	202-224-5972	GrahamBlog	\N
I000024	James M. Inhofe	sen	OK	\N	Republican	202-224-4721	InhofePress	\N
M000355	Mitch McConnell	sen	KY	\N	Republican	202-224-2541	McConnellPress	\N
M001176	Jeff Merkley	sen	OR	\N	Democrat	202-224-3753	SenJeffMerkley	SOR02
R000122	Jack Reed	sen	RI	\N	Democrat	202-224-4642	SenJackReed	\N
R000584	James E. Risch	sen	ID	\N	Republican	202-224-2752	SenatorRisch	\N
S001181	Jeanne Shaheen	sen	NH	\N	Democrat	202-224-2841	SenatorShaheen	SNH02
W000805	Mark R. Warner	sen	VA	\N	Democrat	202-224-2023	MarkWarner	SVA02
G000555	Kirsten E. Gillibrand	sen	NY	\N	Democrat	202-224-4451	GillibrandNY	\N
C001088	Christopher A. Coons	sen	DE	\N	Democrat	202-224-5042	ChrisCoons	\N
M001183	Joe Manchin, III	sen	WV	\N	Democrat	202-224-3954	Sen_JoeManchin	\N
R000600	Aumua Amata Coleman Radewagen	rep	AS	0	Republican	202-225-8577	RepAmata	HAQ00
B001230	Tammy Baldwin	sen	WI	\N	Democrat	202-224-5653	SenatorBaldwin	\N
B001267	Michael F. Bennet	sen	CO	\N	Democrat	202-224-5852	SenatorBennet	\N
B001243	Marsha Blackburn	sen	TN	\N	Republican	202-224-3344	MarshaBlackburn	\N
B001277	Richard Blumenthal	sen	CT	\N	Democrat	202-224-2823	SenBlumenthal	\N
B000575	Roy Blunt	sen	MO	\N	Republican	202-224-5721	RoyBlunt	\N
B001236	John Boozman	sen	AR	\N	Republican	202-224-4843	JohnBoozman	\N
B001135	Richard Burr	sen	NC	\N	Republican	202-224-3154	SenatorBurr	\N
C001047	Shelley Moore Capito	sen	WV	\N	Republican	202-224-6472	SenCapito	\N
C001075	Bill Cassidy	sen	LA	\N	Republican	202-224-5824	SenBillCassidy	\N
C000880	Mike Crapo	sen	ID	\N	Republican	202-224-6142	MikeCrapo	\N
G000386	Chuck Grassley	sen	IA	\N	Republican	202-224-3744	ChuckGrassley	\N
H001046	Martin Heinrich	sen	NM	\N	Democrat	202-224-5521	MartinHeinrich	SNM01
H001042	Mazie K. Hirono	sen	HI	\N	Democrat	202-224-6361	MazieHirono	SHI01
H001061	John Hoeven	sen	ND	\N	Republican	202-224-2551	SenJohnHoeven	\N
J000293	Ron Johnson	sen	WI	\N	Republican	202-224-5323	SenRonJohnson	\N
L000575	James Lankford	sen	OK	\N	Republican	202-224-5754	SenatorLankford	\N
L000174	Patrick J. Leahy	sen	VT	\N	Democrat	202-224-4242	SenatorLeahy	SVT03
L000577	Mike Lee	sen	UT	\N	Republican	202-224-5444	SenMikeLee	\N
L000570	Ben Ray Luján	sen	NM	\N	Democrat	202-224-6621	SenatorLujan	SNM02
M000133	Edward J. Markey	sen	MA	\N	Democrat	202-224-2742	SenMarkey	\N
M000934	Jerry Moran	sen	KS	\N	Republican	202-224-6521	JerryMoran	\N
M001153	Lisa Murkowski	sen	AK	\N	Republican	202-224-6665	LisaMurkowski	\N
M001169	Christopher Murphy	sen	CT	\N	Democrat	202-224-4041	senmurphyoffice	\N
M001111	Patty Murray	sen	WA	\N	Democrat	202-224-2621	PattyMurray	SWA03
P000603	Rand Paul	sen	KY	\N	Republican	202-224-4343	RandPaul	\N
P000595	Gary C. Peters	sen	MI	\N	Democrat	202-224-6221	SenGaryPeters	\N
P000449	Rob Portman	sen	OH	\N	Republican	202-224-3353	SenRobPortman	\N
R000595	Marco Rubio	sen	FL	\N	Republican	202-224-3041	SenRubioPress	SFL03
S000148	Charles E. Schumer	sen	NY	\N	Democrat	202-224-6542	SenSchumer	SNY03
S001184	Tim Scott	sen	SC	\N	Republican	202-224-6121	SenatorTimScott	\N
S000320	Richard C. Shelby	sen	AL	\N	Republican	202-224-5744	SenShelby	\N
T000250	John Thune	sen	SD	\N	Republican	202-224-2321	SenJohnThune	\N
T000461	Patrick J. Toomey	sen	PA	\N	Republican	202-224-4254	SenToomey	\N
V000128	Chris Van Hollen	sen	MD	\N	Democrat	202-224-4654	ChrisVanHollen	\N
W000779	Ron Wyden	sen	OR	\N	Democrat	202-224-5244	RonWyden	\N
Y000064	Todd Young	sen	IN	\N	Republican	202-224-5623	SenToddYoung	\N
S001194	Brian Schatz	sen	HI	\N	Democrat	202-224-3934	SenBrianSchatz	\N
C001095	Tom Cotton	sen	AR	\N	Republican	202-224-2353	SenTomCotton	\N
S001191	Kyrsten Sinema	sen	AZ	\N	Democrat	202-224-4521	SenatorSinema	\N
D000622	Tammy Duckworth	sen	IL	\N	Democrat	202-224-2854	SenDuckworth	\N
W000817	Elizabeth Warren	sen	MA	\N	Democrat	202-224-4543	SenWarren	\N
K000383	Angus S. King, Jr.	sen	ME	\N	Independent	202-224-5344	SenAngusKing	SME01
D000618	Steve Daines	sen	MT	\N	Republican	202-224-2651	SteveDaines	\N
C001096	Kevin Cramer	sen	ND	\N	Republican	202-224-2043	SenKevinCramer	SND01
F000463	Deb Fischer	sen	NE	\N	Republican	202-224-6551	SenatorFischer	SNE01
C001098	Ted Cruz	sen	TX	\N	Republican	202-224-5922	SenTedCruz	\N
K000384	Tim Kaine	sen	VA	\N	Democrat	202-224-4024	timkaine	SVA01
B001288	Cory A. Booker	sen	NJ	\N	Democrat	202-224-3224	SenBooker	\N
S001198	Dan Sullivan	sen	AK	\N	Republican	202-224-3004	SenDanSullivan	\N
E000295	Joni Ernst	sen	IA	\N	Republican	202-224-3254	SenJoniErnst	SIA02
T000476	Thom Tillis	sen	NC	\N	Republican	202-224-6342	senthomtillis	\N
R000605	Mike Rounds	sen	SD	\N	Republican	202-224-5842	SenatorRounds	\N
S001197	Ben Sasse	sen	NE	\N	Republican	202-224-4224	SenSasse	\N
K000393	John Kennedy	sen	LA	\N	Republican	202-224-4623	SenJohnKennedy	\N
H001076	Margaret Wood Hassan	sen	NH	\N	Democrat	202-224-3324	Senatorhassan	\N
C001113	Catherine Cortez Masto	sen	NV	\N	Democrat	202-224-3542	sencortezmasto	\N
M001198	Roger Marshall	sen	KS	\N	Republican	202-224-4774	SenatorMarshall	SKS02
R000608	Jacky Rosen	sen	NV	\N	Democrat	202-224-6244	SenJackyRosen	SNV01
S001203	Tina Smith	sen	MN	\N	Democrat	202-224-5641	SenTinaSmith	\N
H001079	Cindy Hyde-Smith	sen	MS	\N	Republican	202-224-5054	SenHydeSmith	\N
S001217	Rick Scott	sen	FL	\N	Republican	202-224-5274	SenRickScott	\N
B001310	Mike Braun	sen	IN	\N	Republican	202-224-4814	SenatorBraun	\N
H001089	Josh Hawley	sen	MO	\N	Republican	202-224-6154	SenHawleyPress	\N
R000615	Mitt Romney	sen	UT	\N	Republican	202-224-5251	SenatorRomney	\N
K000377	Mark Kelly	sen	AZ	\N	Democrat	202-224-2235	SenMarkKelly	SAZ03
L000571	Cynthia M. Lummis	sen	WY	\N	Republican	202-224-3424	SenLummis	SWY02
T000278	Tommy Tuberville	sen	AL	\N	Republican	202-224-4124	SenTuberville	SAL02
H000273	John W. Hickenlooper	sen	CO	\N	Democrat	202-224-5941	SenatorHick	SCO02
H000601	Bill Hagerty	sen	TN	\N	Republican	202-224-4944	SenatorHagerty	STN02
P000145	Alex Padilla	sen	CA	\N	Democrat	202-224-3553	SenAlexPadilla	SCA03
O000174	Jon Ossoff	sen	GA	\N	Democrat	202-224-3521	SenOssoff	SGA02
W000790	Raphael G. Warnock	sen	GA	\N	Democrat	202-224-3643	SenatorWarnock	SGA03
A000055	Robert B. Aderholt	rep	AL	4	Republican	202-225-4876	Robert_Aderholt	HAL04
B001270	Karen Bass	rep	CA	37	Democrat	202-225-7084	RepKarenBass	HCA37
B001257	Gus M. Bilirakis	rep	FL	12	Republican	202-225-5755	RepGusBilirakis	HFL12
B000490	Sanford D. Bishop, Jr.	rep	GA	2	Democrat	202-225-3631	SanfordBishop	HGA02
B000574	Earl Blumenauer	rep	OR	3	Democrat	202-225-4811	BlumenauerMedia	HOR03
B000755	Kevin Brady	rep	TX	8	Republican	202-225-4901	RepKevinBrady	HTX08
B001274	Mo Brooks	rep	AL	5	Republican	202-225-4801	RepMoBrooks	HAL05
B001260	Vern Buchanan	rep	FL	16	Republican	202-225-5015	VernBuchanan	HFL16
B001275	Larry Bucshon	rep	IN	8	Republican	202-225-4636	RepLarryBucshon	HIN08
B001248	Michael C. Burgess	rep	TX	26	Republican	202-225-7772	MichaelCBurgess	HTX26
B001251	G. K. Butterfield	rep	NC	1	Democrat	202-225-3101	GKButterfield	HNC01
C000059	Ken Calvert	rep	CA	42	Republican	202-225-1986	KenCalvert	HCA42
C001072	André Carson	rep	IN	7	Democrat	202-225-4011	RepAndreCarson	HIN07
C001051	John R. Carter	rep	TX	31	Republican	202-225-3864	JudgeCarter	HTX31
C001066	Kathy Castor	rep	FL	14	Democrat	202-225-3376	USRepKCastor	HFL14
C000266	Steve Chabot	rep	OH	1	Republican	202-225-2216	RepSteveChabot	HOH01
C001080	Judy Chu	rep	CA	27	Democrat	202-225-5464	RepJudyChu	HCA27
C001084	David N. Cicilline	rep	RI	1	Democrat	202-225-4911	RepCicilline	HRI01
C001067	Yvette D. Clarke	rep	NY	9	Democrat	202-225-6231	RepYvetteClarke	HNY09
C001061	Emanuel Cleaver	rep	MO	5	Democrat	202-225-4535	RepCleaver	HMO05
C000537	James E. Clyburn	rep	SC	6	Democrat	202-225-3315	WhipClyburn	HSC06
C001068	Steve Cohen	rep	TN	9	Democrat	202-225-3265	RepCohen	HTN09
C001053	Tom Cole	rep	OK	4	Republican	202-225-6165	TomColeOK04	HOK04
C001078	Gerald E. Connolly	rep	VA	11	Democrat	202-225-1492	GerryConnolly	HVA11
C000754	Jim Cooper	rep	TN	5	Democrat	202-225-4311	RepJimCooper	HTN05
C001059	Jim Costa	rep	CA	16	Democrat	202-225-3341	RepJimCosta	HCA16
C001069	Joe Courtney	rep	CT	2	Democrat	202-225-2076	RepJoeCourtney	HCT02
C001087	Eric A. "Rick" Crawford	rep	AR	1	Republican	202-225-4076	RepRickCrawford	HAR01
C001063	Henry Cuellar	rep	TX	28	Democrat	202-225-1640	RepCuellar	HTX28
D000096	Danny K. Davis	rep	IL	7	Democrat	202-225-5006	RepDannyDavis	HIL07
D000191	Peter A. DeFazio	rep	OR	4	Democrat	202-225-6416	RepPeterDeFazio	HOR04
D000197	Diana DeGette	rep	CO	1	Democrat	202-225-4431	RepDianaDeGette	HCO01
D000216	Rosa L. DeLauro	rep	CT	3	Democrat	202-225-3661	RosaDeLauro	HCT03
D000616	Scott DesJarlais	rep	TN	4	Republican	202-225-6831	DesJarlaisTN04	HTN04
D000610	Theodore E. Deutch	rep	FL	22	Democrat	202-225-3001	RepTedDeutch	HFL22
D000600	Mario Diaz-Balart	rep	FL	25	Republican	202-225-4211	MarioDB	HFL25
D000399	Lloyd Doggett	rep	TX	35	Democrat	202-225-4865	RepLloydDoggett	HTX35
D000482	Michael F. Doyle	rep	PA	18	Democrat	202-225-2135	USRepMikeDoyle	HPA18
D000615	Jeff Duncan	rep	SC	3	Republican	202-225-5301	RepJeffDuncan	HSC03
E000215	Anna G. Eshoo	rep	CA	18	Democrat	202-225-8104	RepAnnaEshoo	HCA18
F000459	Charles J. "Chuck" Fleischmann	rep	TN	3	Republican	202-225-3271	RepChuck	HTN03
F000449	Jeff Fortenberry	rep	NE	1	Republican	202-225-4806	JeffFortenberry	HNE01
F000450	Virginia Foxx	rep	NC	5	Republican	202-225-2071	VirginiaFoxx	HNC05
G000559	John Garamendi	rep	CA	3	Democrat	202-225-1880	RepGaramendi	HCA03
G000563	Bob Gibbs	rep	OH	7	Republican	202-225-6265	RepBobGibbs	HOH07
G000552	Louie Gohmert	rep	TX	1	Republican	202-225-3035	RepLouieGohmert	HTX01
G000565	Paul A. Gosar	rep	AZ	4	Republican	202-225-2315	RepGosar	HAZ04
G000377	Kay Granger	rep	TX	12	Republican	202-225-5071	RepKayGranger	HTX12
G000546	Sam Graves	rep	MO	6	Republican	202-225-7041	RepSamGraves	HMO06
G000553	Al Green	rep	TX	9	Democrat	202-225-7508	RepAlGreen	HTX09
G000568	H. Morgan Griffith	rep	VA	9	Republican	202-225-3861	RepMGriffith	HVA09
G000551	Raúl M. Grijalva	rep	AZ	3	Democrat	202-225-2435	RepraulGrijalva	HAZ03
G000558	Brett Guthrie	rep	KY	2	Republican	202-225-3501	RepGuthrie	HKY02
H001052	Andy Harris	rep	MD	1	Republican	202-225-5311	RepAndyHarrisMD	HMD01
H001053	Vicky Hartzler	rep	MO	4	Republican	202-225-2876	RepHartzler	HMO04
H001056	Jaime Herrera Beutler	rep	WA	3	Republican	202-225-3536	HerreraBeutler	HWA03
H001038	Brian Higgins	rep	NY	26	Democrat	202-225-3306	RepBrianHiggins	HNY26
H001047	James A. Himes	rep	CT	4	Democrat	202-225-5541	JAHimes	HCT04
H000874	Steny H. Hoyer	rep	MD	5	Democrat	202-225-4131	LeaderHoyer	HMD05
H001058	Bill Huizenga	rep	MI	2	Republican	202-225-4401	RepHuizenga	HMI02
J000032	Sheila Jackson Lee	rep	TX	18	Democrat	202-225-3816	JacksonLeeTX18	HTX18
J000292	Bill Johnson	rep	OH	6	Republican	202-225-5705	RepBillJohnson	HOH06
J000126	Eddie Bernice Johnson	rep	TX	30	Democrat	202-225-8885	RepEBJ	HTX30
J000288	Henry C. "Hank" Johnson, Jr.	rep	GA	4	Democrat	202-225-1605	RepHankJohnson	HGA04
J000289	Jim Jordan	rep	OH	4	Republican	202-225-2676	Jim_Jordan	HOH04
K000009	Marcy Kaptur	rep	OH	9	Democrat	202-225-4146	RepMarcyKaptur	HOH09
K000375	William R. Keating	rep	MA	9	Democrat	202-225-3111	USRepKeating	HMA09
K000376	Mike Kelly	rep	PA	16	Republican	202-225-5406	MikeKellyPA	HPA16
K000188	Ron Kind	rep	WI	3	Democrat	202-225-5506	RepRonKind	HWI03
K000378	Adam Kinzinger	rep	IL	16	Republican	202-225-3635	RepKinzinger	HIL16
L000564	Doug Lamborn	rep	CO	5	Republican	202-225-4422	RepDLamborn	HCO05
L000559	James R. Langevin	rep	RI	2	Democrat	202-225-2735	JimLangevin	HRI02
L000560	Rick Larsen	rep	WA	2	Democrat	202-225-2605	RepRickLarsen	HWA02
L000557	John B. Larson	rep	CT	1	Democrat	202-225-2265	RepJohnLarson	HCT01
L000566	Robert E. Latta	rep	OH	5	Republican	202-225-6405	BobLatta	HOH05
L000551	Barbara Lee	rep	CA	13	Democrat	202-225-2661	RepBarbaraLee	HCA13
L000397	Zoe Lofgren	rep	CA	19	Democrat	202-225-3072	RepZoeLofgren	HCA19
L000576	Billy Long	rep	MO	7	Republican	202-225-6536	USRepLong	HMO07
L000491	Frank D. Lucas	rep	OK	3	Republican	202-225-5565	RepFrankLucas	HOK03
L000569	Blaine Luetkemeyer	rep	MO	3	Republican	202-225-2956	RepBlaine	HMO03
L000562	Stephen F. Lynch	rep	MA	8	Democrat	202-225-8273	RepStephenLynch	HMA08
M000087	Carolyn B. Maloney	rep	NY	12	Democrat	202-225-7944	RepMaloney	HNY12
M001163	Doris O. Matsui	rep	CA	6	Democrat	202-225-7163	DorisMatsui	HCA06
M001165	Kevin McCarthy	rep	CA	23	Republican	202-225-2915	GOPLeader	HCA23
M001157	Michael T. McCaul	rep	TX	10	Republican	202-225-2401	RepMcCaul	HTX10
M001177	Tom McClintock	rep	CA	4	Republican	202-225-2511	RepMcClintock	HCA04
M001143	Betty McCollum	rep	MN	4	Democrat	202-225-6631	BettyMcCollum04	HMN04
M000312	James P. McGovern	rep	MA	2	Democrat	202-225-6101	RepMcGovern	HMA02
M001156	Patrick T. McHenry	rep	NC	10	Republican	202-225-2576	PatrickMcHenry	HNC10
M001180	David B. McKinley	rep	WV	1	Republican	202-225-4172	RepMcKinley	HWV01
M001159	Cathy McMorris Rodgers	rep	WA	5	Republican	202-225-2006	CathyMcMorris	HWA05
M001166	Jerry McNerney	rep	CA	9	Democrat	202-225-1947	RepMcNerney	HCA09
M001137	Gregory W. Meeks	rep	NY	5	Democrat	202-225-3461	RepGregoryMeeks	HNY05
M001160	Gwen Moore	rep	WI	4	Democrat	202-225-4572	RepGwenMoore	HWI04
N000002	Jerrold Nadler	rep	NY	10	Democrat	202-225-5635	RepJerryNadler	HNY10
N000179	Grace F. Napolitano	rep	CA	32	Democrat	202-225-5256	GraceNapolitano	HCA32
N000015	Richard E. Neal	rep	MA	1	Democrat	202-225-5601	RepRichardNeal	HMA01
N000147	Eleanor Holmes Norton	rep	DC	0	Democrat	202-225-8050	EleanorNorton	HDC00
N000181	Devin Nunes	rep	CA	22	Republican	202-225-2523	RepDevinNunes	HCA22
P000601	Steven M. Palazzo	rep	MS	4	Republican	202-225-5772	CongPalazzo	HMS04
P000034	Frank Pallone, Jr.	rep	NJ	6	Democrat	202-225-4671	FrankPallone	HNJ06
P000096	Bill Pascrell, Jr.	rep	NJ	9	Democrat	202-225-5751	BillPascrell	HNJ09
P000197	Nancy Pelosi	rep	CA	12	Democrat	202-225-4965	SpeakerPelosi	HCA12
P000593	Ed Perlmutter	rep	CO	7	Democrat	202-225-2645	RepPerlmutter	HCO07
P000597	Chellie Pingree	rep	ME	1	Democrat	202-225-6116	ChelliePingree	HME01
P000599	Bill Posey	rep	FL	8	Republican	202-225-3671	CongBillPosey	HFL08
P000523	David E. Price	rep	NC	4	Democrat	202-225-1784	RepDavidEPrice	HNC04
Q000023	Mike Quigley	rep	IL	5	Democrat	202-225-4061	RepMikeQuigley	HIL05
R000585	Tom Reed	rep	NY	23	Republican	202-225-3161	TomReedNY23	HNY23
R000395	Harold Rogers	rep	KY	5	Republican	202-225-4601	RepHalRogers	HKY05
R000575	Mike Rogers	rep	AL	3	Republican	202-225-3261	RepMikeRogersAL	HAL03
R000486	Lucille Roybal-Allard	rep	CA	40	Democrat	202-225-1766	RepRoybalAllard	HCA40
R000576	C. A. Dutch Ruppersberger	rep	MD	2	Democrat	202-225-3061	Call_Me_Dutch	HMD02
R000515	Bobby L. Rush	rep	IL	1	Democrat	202-225-4372	RepBobbyRush	HIL01
R000577	Tim Ryan	rep	OH	13	Democrat	202-225-5261	RepTimRyan	HOH13
S001177	Gregorio Kilili Camacho Sablan	rep	MP	0	Democrat	202-225-2646	Kilili_Sablan	HMP00
S001168	John P. Sarbanes	rep	MD	3	Democrat	202-225-4016	RepSarbanes	HMD03
S001176	Steve Scalise	rep	LA	1	Republican	202-225-3015	SteveScalise	HLA01
S001145	Janice D. Schakowsky	rep	IL	9	Democrat	202-225-2111	JanSchakowsky	HIL09
S001150	Adam B. Schiff	rep	CA	28	Democrat	202-225-4176	RepAdamSchiff	HCA28
S001180	Kurt Schrader	rep	OR	5	Democrat	202-225-5711	RepSchrader	HOR05
S001183	David Schweikert	rep	AZ	6	Republican	202-225-2190	RepDavid	HAZ06
S001189	Austin Scott	rep	GA	8	Republican	202-225-6531	AustinScottGA08	HGA08
S001157	David Scott	rep	GA	13	Democrat	202-225-2939	RepDavidScott	HGA13
S000185	Robert C. "Bobby" Scott	rep	VA	3	Democrat	202-225-8351	BobbyScott	HVA03
S001185	Terri Sewell	rep	AL	7	Democrat	202-225-2665	RepTerriSewell	HAL07
S000344	Brad Sherman	rep	CA	30	Democrat	202-225-5911	BradSherman	HCA30
S001148	Michael K. Simpson	rep	ID	2	Republican	202-225-5531	CongMikeSimpson	HID02
S001165	Albio Sires	rep	NJ	8	Democrat	202-225-7919	RepSires	HNJ08
S000510	Adam Smith	rep	WA	9	Democrat	202-225-8901	RepAdamSmith	HWA09
S001172	Adrian Smith	rep	NE	3	Republican	202-225-6435	RepAdrianSmith	HNE03
S000522	Christopher H. Smith	rep	NJ	4	Republican	202-225-3765	RepChrisSmith	HNJ04
S001175	Jackie Speier	rep	CA	14	Democrat	202-225-3531	RepSpeier	HCA14
S001156	Linda T. Sánchez	rep	CA	38	Democrat	202-225-6676	RepLindaSanchez	HCA38
T000193	Bennie G. Thompson	rep	MS	2	Democrat	202-225-5876	BennieGThompson	HMS02
T000460	Mike Thompson	rep	CA	5	Democrat	202-225-3311	RepThompson	HCA05
T000467	Glenn Thompson	rep	PA	15	Republican	202-225-5121	CongressmanGT	HPA15
T000469	Paul Tonko	rep	NY	20	Democrat	202-225-5076	RepPaulTonko	HNY20
T000463	Michael R. Turner	rep	OH	10	Republican	202-225-6465	RepMikeTurner	HOH10
U000031	Fred Upton	rep	MI	6	Republican	202-225-3761	RepFredUpton	HMI06
V000081	Nydia M. Velázquez	rep	NY	7	Democrat	202-225-2361	NydiaVelazquez	HNY07
W000798	Tim Walberg	rep	MI	7	Republican	202-225-6276	RepWalberg	HMI07
W000797	Debbie Wasserman Schultz	rep	FL	23	Democrat	202-225-7931	RepDWStweets	HFL23
W000187	Maxine Waters	rep	CA	43	Democrat	202-225-2201	RepMaxineWaters	HCA43
W000806	Daniel Webster	rep	FL	11	Republican	202-225-1002	RepWebster	HFL11
W000800	Peter Welch	rep	VT	0	Democrat	202-225-4115	PeterWelch	HVT00
W000795	Joe Wilson	rep	SC	2	Republican	202-225-2452	RepJoeWilson	HSC02
W000808	Frederica S. Wilson	rep	FL	24	Democrat	202-225-4506	RepWilson	HFL24
W000804	Robert J. Wittman	rep	VA	1	Republican	202-225-4261	RobWittman	HVA01
W000809	Steve Womack	rep	AR	3	Republican	202-225-4301	Rep_SteveWomack	HAR03
Y000062	John A. Yarmuth	rep	KY	3	Democrat	202-225-5401	RepJohnYarmuth	HKY03
Y000033	Don Young	rep	AK	0	Republican	202-225-5765	RepDonYoung	HAK00
A000369	Mark E. Amodei	rep	NV	2	Republican	202-225-6155	MarkAmodeiNV2	HNV02
B001278	Suzanne Bonamici	rep	OR	1	Democrat	202-225-0855	RepBonamici	HOR01
D000617	Suzan K. DelBene	rep	WA	1	Democrat	202-225-6311	RepDelBene	HWA01
M001184	Thomas Massie	rep	KY	4	Republican	202-225-3465	RepThomasMassie	HKY04
P000604	Donald M. Payne, Jr.	rep	NJ	10	Democrat	202-225-3436	RepDonaldPayne	HNJ10
F000454	Bill Foster	rep	IL	11	Democrat	202-225-3515	RepBillFoster	HIL11
T000468	Dina Titus	rep	NV	1	Democrat	202-225-5965	RepDinaTitus	HNV01
L000578	Doug LaMalfa	rep	CA	1	Republican	202-225-3076	RepLaMalfa	HCA01
H001068	Jared Huffman	rep	CA	2	Democrat	202-225-5161	RepHuffman	HCA02
B001287	Ami Bera	rep	CA	7	Democrat	202-225-5716	RepBera	HCA07
S001193	Eric Swalwell	rep	CA	15	Democrat	202-225-5065	RepSwalwell	HCA15
B001285	Julia Brownley	rep	CA	26	Democrat	202-225-5811	RepBrownley	HCA26
C001097	Tony Cárdenas	rep	CA	29	Democrat	202-225-6131	RepCardenas	HCA29
R000599	Raul Ruiz	rep	CA	36	Democrat	202-225-5330	RepRaulRuizMD	HCA36
T000472	Mark Takano	rep	CA	41	Democrat	202-225-2305	RepMarkTakano	HCA41
L000579	Alan S. Lowenthal	rep	CA	47	Democrat	202-225-7924	RepLowenthal	HCA47
V000130	Juan Vargas	rep	CA	51	Democrat	202-225-8045	RepJuanVargas	HCA51
P000608	Scott H. Peters	rep	CA	52	Democrat	202-225-0508	RepScottPeters	HCA52
F000462	Lois Frankel	rep	FL	21	Democrat	202-225-9890	RepLoisFrankel	HFL21
D000619	Rodney Davis	rep	IL	13	Republican	202-225-2371	RodneyDavis	HIL13
B001286	Cheri Bustos	rep	IL	17	Democrat	202-225-5905	RepCheri	HIL17
W000813	Jackie Walorski	rep	IN	2	Republican	202-225-3915	RepWalorski	HIN02
B001282	Andy Barr	rep	KY	6	Republican	202-225-4706	RepAndyBarr	HKY06
K000380	Daniel T. Kildee	rep	MI	5	Democrat	202-225-3611	RepDanKildee	HMI05
W000812	Ann Wagner	rep	MO	2	Republican	202-225-1621	RepAnnWagner	HMO02
H001067	Richard Hudson	rep	NC	8	Republican	202-225-3715	RepRichHudson	HNC08
K000382	Ann Kuster	rep	NH	2	Democrat	202-225-5206	RepAnnieKuster	HNH02
M001188	Grace Meng	rep	NY	6	Democrat	202-225-2601	RepGraceMeng	HNY06
J000294	Hakeem S. Jeffries	rep	NY	8	Democrat	202-225-5936	RepJeffries	HNY08
M001185	Sean Patrick Maloney	rep	NY	18	Democrat	202-225-5441	RepSeanMaloney	HNY18
W000815	Brad R. Wenstrup	rep	OH	2	Republican	202-225-3164	RepBradWenstrup	HOH02
B001281	Joyce Beatty	rep	OH	3	Democrat	202-225-4324	RepBeatty	HOH03
J000295	David P. Joyce	rep	OH	14	Republican	202-225-5731	RepDaveJoyce	HOH14
M001190	Markwayne Mullin	rep	OK	2	Republican	202-225-2701	RepMullin	HOK02
P000605	Scott Perry	rep	PA	10	Republican	202-225-5836	RepScottPerry	HPA10
C001090	Matt Cartwright	rep	PA	8	Democrat	202-225-5546	RepCartwright	HPA08
R000597	Tom Rice	rep	SC	7	Republican	202-225-9895	RepTomRice	HSC07
W000814	Randy K. Weber, Sr.	rep	TX	14	Republican	202-225-2831	TXRandy14	HTX14
C001091	Joaquin Castro	rep	TX	20	Democrat	202-225-3236	JoaquinCastrotx	HTX20
W000816	Roger Williams	rep	TX	25	Republican	202-225-9896	RepRWilliams	HTX25
V000131	Marc A. Veasey	rep	TX	33	Democrat	202-225-9897	RepVeasey	HTX33
V000132	Filemon Vela	rep	TX	34	Democrat	202-225-9901	RepFilemonVela	HTX34
S001192	Chris Stewart	rep	UT	2	Republican	202-225-9730	RepChrisStewart	HUT02
K000381	Derek Kilmer	rep	WA	6	Democrat	202-225-5916	RepDerekKilmer	HWA06
P000607	Mark Pocan	rep	WI	2	Democrat	202-225-2906	RepMarkPocan	HWI02
K000385	Robin L. Kelly	rep	IL	2	Democrat	202-225-0773	RepRobinKelly	HIL02
S001195	Jason Smith	rep	MO	8	Republican	202-225-4404	RepJasonSmith	HMO08
C001101	Katherine M. Clark	rep	MA	5	Democrat	202-225-2836	RepKClark	HMA05
N000188	Donald Norcross	rep	NJ	1	Democrat	202-225-6501	DonaldNorcross	HNJ01
A000370	Alma S. Adams	rep	NC	12	Democrat	202-225-1510	RepAdams	HNC12
P000609	Gary J. Palmer	rep	AL	6	Republican	202-225-4921	USRepGaryPalmer	HAL06
H001072	J. Hill	rep	AR	2	Republican	202-225-2506	RepFrenchHill	HAR02
W000821	Bruce Westerman	rep	AR	4	Republican	202-225-3772	RepWesterman	HAR04
G000574	Ruben Gallego	rep	AZ	7	Democrat	202-225-4065	RepRubenGallego	HAZ07
D000623	Mark DeSaulnier	rep	CA	11	Democrat	202-225-2095	RepDeSaulnier	HCA11
A000371	Pete Aguilar	rep	CA	31	Democrat	202-225-3201	reppeteaguilar	HCA31
L000582	Ted Lieu	rep	CA	33	Democrat	202-225-3976	RepTedLieu	HCA33
T000474	Norma J. Torres	rep	CA	35	Democrat	202-225-6161	NormaJTorres	HCA35
B001297	Ken Buck	rep	CO	4	Republican	202-225-4676	RepKenBuck	HCO04
C001103	Earl L. "Buddy" Carter	rep	GA	1	Republican	202-225-5831	RepBuddyCarter	HGA01
H001071	Jody B. Hice	rep	GA	10	Republican	202-225-4101	congressmanhice	HGA10
L000583	Barry Loudermilk	rep	GA	11	Republican	202-225-2931	RepLoudermilk	HGA11
A000372	Rick W. Allen	rep	GA	12	Republican	202-225-2823	reprickallen	HGA12
B001295	Mike Bost	rep	IL	12	Republican	202-225-5661	RepBost	HIL12
G000577	Garret Graves	rep	LA	6	Republican	202-225-3901	RepGarretGraves	HLA06
M001196	Seth Moulton	rep	MA	6	Democrat	202-225-8020	teammoulton	HMA06
M001194	John R. Moolenaar	rep	MI	4	Republican	202-225-3561	RepMoolenaar	HMI04
D000624	Debbie Dingell	rep	MI	12	Democrat	202-225-4071	RepDebDingell	HMI12
L000581	Brenda L. Lawrence	rep	MI	14	Democrat	202-225-5802	RepLawrence	HMI14
E000294	Tom Emmer	rep	MN	6	Republican	202-225-2331	RepTomEmmer	HMN06
R000603	David Rouzer	rep	NC	7	Republican	202-225-2731	RepDavidRouzer	HNC07
W000822	Bonnie Watson Coleman	rep	NJ	12	Democrat	202-225-5801	RepBonnie	HNJ12
Z000017	Lee M. Zeldin	rep	NY	1	Republican	202-225-3826	RepLeeZeldin	HNY01
R000602	Kathleen M. Rice	rep	NY	4	Democrat	202-225-5516	RepKathleenRice	HNY04
S001196	Elise M. Stefanik	rep	NY	21	Republican	202-225-4611	RepStefanik	HNY21
K000386	John Katko	rep	NY	24	Republican	202-225-3701	RepJohnKatko	HNY24
B001296	Brendan F. Boyle	rep	PA	2	Democrat	202-225-6111	CongBoyle	HPA02
B001291	Brian Babin	rep	TX	36	Republican	202-225-1555	RepBrianBabin	HTX36
B001292	Donald S. Beyer, Jr.	rep	VA	8	Democrat	202-225-4376	RepDonBeyer	HVA08
P000610	Stacey E. Plaskett	rep	VI	0	Democrat	202-225-1790	staceyplaskett	HVI00
N000189	Dan Newhouse	rep	WA	4	Republican	202-225-5816	RepNewhouse	HWA04
G000576	Glenn Grothman	rep	WI	6	Republican	202-225-2476	RepGrothman	HWI06
M001195	Alexander Mooney	rep	WV	2	Republican	202-225-2711	RepAlexMooney	HWV02
K000388	Trent Kelly	rep	MS	1	Republican	202-225-4306	reptrentkelly	HMS01
L000585	Darin LaHood	rep	IL	18	Republican	202-225-6201	RepLaHood	HIL18
D000626	Warren Davidson	rep	OH	8	Republican	202-225-6205	WarrenDavidson	HOH08
C001108	James Comer	rep	KY	1	Republican	202-225-3115	RepJamesComer	HKY01
E000296	Dwight Evans	rep	PA	3	Democrat	202-225-4001	RepDwightEvans	HPA03
S001190	Bradley Scott Schneider	rep	IL	10	Democrat	202-225-4835	repschneider	HIL10
O000171	Tom O’Halleran	rep	AZ	1	Democrat	202-225-3361	repohalleran	HAZ01
B001302	Andy Biggs	rep	AZ	5	Republican	202-225-2635	RepAndyBiggsAZ	HAZ05
K000389	Ro Khanna	rep	CA	17	Democrat	202-225-2631	RepRoKhanna	HCA17
P000613	Jimmy Panetta	rep	CA	20	Democrat	202-225-2861	RepJimmyPanetta	HCA20
C001112	Salud O. Carbajal	rep	CA	24	Democrat	202-225-3601	RepCarbajal	HCA24
B001300	Nanette Diaz Barragán	rep	CA	44	Democrat	202-225-8220	RepBarragan	HCA44
C001110	J. Luis Correa	rep	CA	46	Democrat	202-225-2965	reploucorrea	HCA46
B001303	Lisa Blunt Rochester	rep	DE	0	Democrat	202-225-4165	RepLBR	HDE00
G000578	Matt Gaetz	rep	FL	1	Republican	202-225-4136	RepMattGaetz	HFL01
D000628	Neal P. Dunn	rep	FL	2	Republican	202-225-5235	drnealdunnfl2	HFL02
R000609	John H. Rutherford	rep	FL	4	Republican	202-225-2501	RepRutherfordFL	HFL04
L000586	Al Lawson, Jr.	rep	FL	5	Democrat	202-225-0123	RepAlLawsonJr	HFL05
M001202	Stephanie N. Murphy	rep	FL	7	Democrat	202-225-4035	RepStephMurphy	HFL07
S001200	Darren Soto	rep	FL	9	Democrat	202-225-9889	RepDarrenSoto	HFL09
D000627	Val Butler Demings	rep	FL	10	Democrat	202-225-2176	RepValDemings	HFL10
C001111	Charlie Crist	rep	FL	13	Democrat	202-225-5961	repcharliecrist	HFL13
M001199	Brian J. Mast	rep	FL	18	Republican	202-225-3026	repbrianmast	HFL18
F000465	A. Drew Ferguson IV	rep	GA	3	Republican	202-225-5901	RepDrewFerguson	HGA03
K000391	Raja Krishnamoorthi	rep	IL	8	Democrat	202-225-3711	congressmanraja	HIL08
B001299	Jim Banks	rep	IN	3	Republican	202-225-4436	RepJimBanks	HIN03
H001074	Trey Hollingsworth	rep	IN	9	Republican	202-225-5315	reptrey	HIN09
H001077	Clay Higgins	rep	LA	3	Republican	202-225-2031	RepClayHiggins	HLA03
J000299	Mike Johnson	rep	LA	4	Republican	202-225-2777	RepMikeJohnson	HLA04
B001304	Anthony Brown	rep	MD	4	Democrat	202-225-8699	RepAnthonyBrown	HMD04
R000606	Jamie Raskin	rep	MD	8	Democrat	202-225-5341	repraskin	HMD08
B001301	Jack Bergman	rep	MI	1	Republican	202-225-4735	RepJackBergman	HMI01
B001305	Ted Budd	rep	NC	13	Republican	202-225-4531	RepTedBudd	HNC13
B001298	Don Bacon	rep	NE	2	Republican	202-225-4155	repdonbacon	HNE02
G000583	Josh Gottheimer	rep	NJ	5	Democrat	202-225-4465	RepJoshG	HNJ05
S001201	Thomas R. Suozzi	rep	NY	3	Democrat	202-225-3335	RepTomSuozzi	HNY03
E000297	Adriano Espaillat	rep	NY	13	Democrat	202-225-4365	RepEspaillat	HNY13
F000466	Brian K. Fitzpatrick	rep	PA	1	Republican	202-225-4276	repbrianfitz	HPA01
S001199	Lloyd Smucker	rep	PA	11	Republican	202-225-2411	RepSmucker	HPA11
G000582	Jenniffer González-Colón	rep	PR	0	Republican	202-225-2615	repjenniffer	HPR00
K000392	David Kustoff	rep	TN	8	Republican	202-225-4714	repdavidkustoff	HTN08
G000581	Vicente Gonzalez	rep	TX	15	Democrat	202-225-2531	RepGonzalez	HTX15
A000375	Jodey C. Arrington	rep	TX	19	Republican	202-225-4005	RepArrington	HTX19
M001200	A. Donald McEachin	rep	VA	4	Democrat	202-225-6365	RepMcEachin	HVA04
J000298	Pramila Jayapal	rep	WA	7	Democrat	202-225-3106	RepJayapal	HWA07
G000579	Mike Gallagher	rep	WI	8	Republican	202-225-5665	RepGallagher	HWI08
C001109	Liz Cheney	rep	WY	0	Republican	202-225-2311	RepLizCheney	HWY00
E000298	Ron Estes	rep	KS	4	Republican	202-225-6216	RepRonEstes	HKS04
N000190	Ralph Norman	rep	SC	5	Republican	202-225-5501	RepRalphNorman	HSC05
G000585	Jimmy Gomez	rep	CA	34	Democrat	202-225-6235	RepJimmyGomez	HCA34
C001114	John R. Curtis	rep	UT	3	Republican	202-225-7751	RepJohnCurtis	HUT03
L000588	Conor Lamb	rep	PA	17	Democrat	202-225-2301	RepConorLamb	HPA17
L000589	Debbie Lesko	rep	AZ	8	Republican	202-225-4576	RepDLesko	HAZ08
C001115	Michael Cloud	rep	TX	27	Republican	202-225-7742	RepCloudTX	HTX27
B001306	Troy Balderson	rep	OH	12	Republican	202-225-5355	RepBalderson	HOH12
H001082	Kevin Hern	rep	OK	1	Republican	202-225-2211	repkevinhern	HOK01
M001206	Joseph D. Morelle	rep	NY	25	Democrat	202-225-3615	RepJoeMorelle	HNY25
S001205	Mary Gay Scanlon	rep	PA	5	Democrat	202-225-2011	RepMGS	HPA05
W000826	Susan Wild	rep	PA	7	Democrat	202-225-6411	RepSusanWild	HPA07
C001055	Ed Case	rep	HI	1	Democrat	202-225-2726	RepEdCase	HHI01
H001066	Steven Horsford	rep	NV	4	Democrat	202-225-9894	RepHorsford	HNV04
K000368	Ann Kirkpatrick	rep	AZ	2	Democrat	202-225-2542	RepKirkpatrick	HAZ02
S001211	Greg Stanton	rep	AZ	9	Democrat	202-225-9888	RepGregStanton	HAZ09
H001090	Josh Harder	rep	CA	10	Democrat	202-225-4540	RepJoshHarder	HCA10
P000618	Katie Porter	rep	CA	45	Democrat	202-225-5611	RepKatiePorter	HCA45
L000593	Mike Levin	rep	CA	49	Democrat	202-225-3906	RepMikeLevin	HCA49
N000191	Joe Neguse	rep	CO	2	Democrat	202-225-2161	RepJoeNeguse	HCO02
C001121	Jason Crow	rep	CO	6	Democrat	202-225-7882	RepJasonCrow	HCO06
H001081	Jahana Hayes	rep	CT	5	Democrat	202-225-4476	RepJahanaHayes	HCT05
W000823	Michael Waltz	rep	FL	6	Republican	202-225-2706	RepWaltzPress	HFL06
S001214	W. Gregory Steube	rep	FL	17	Republican	202-225-5792	RepGregSteube	HFL17
M001208	Lucy McBath	rep	GA	6	Democrat	202-225-4501	replucymcbath	HGA06
S001204	Michael F. Q. San Nicolas	rep	GU	0	Democrat	202-225-1188	GuamCongressman	HGU00
A000378	Cynthia Axne	rep	IA	3	Democrat	202-225-5476	RepCindyAxne	HIA03
F000469	Russ Fulcher	rep	ID	1	Republican	202-225-6611	RepRussFulcher	HID01
G000586	Jesús G. "Chuy" García	rep	IL	4	Democrat	202-225-8203	RepChuyGarcia	HIL04
C001117	Sean Casten	rep	IL	6	Democrat	202-225-4561	RepCasten	HIL06
U000040	Lauren Underwood	rep	IL	14	Democrat	202-225-2976	RepUnderwood	HIL14
B001307	James R. Baird	rep	IN	4	Republican	202-225-5037	RepJimBaird	HIN04
P000615	Greg Pence	rep	IN	6	Republican	202-225-3021	RepGregPence	HIN06
D000629	Sharice Davids	rep	KS	3	Democrat	202-225-2865	RepDavids	HKS03
T000482	Lori Trahan	rep	MA	3	Democrat	202-225-3411	RepLoriTrahan	HMA03
P000617	Ayanna Pressley	rep	MA	7	Democrat	202-225-5111	RepPressley	HMA07
T000483	David J. Trone	rep	MD	6	Democrat	202-225-2721	repdavidtrone	HMD06
S001208	Elissa Slotkin	rep	MI	8	Democrat	202-225-4872	RepSlotkin	HMI08
L000592	Andy Levin	rep	MI	9	Democrat	202-225-4961	RepAndyLevin	HMI09
S001215	Haley M. Stevens	rep	MI	11	Democrat	202-225-8171	RepHaleyStevens	HMI11
T000481	Rashida Tlaib	rep	MI	13	Democrat	202-225-5126	RepRashida	HMI13
H001088	Jim Hagedorn	rep	MN	1	Republican	202-225-2472	RepHagedorn	HMN01
C001119	Angie Craig	rep	MN	2	Democrat	202-225-2271	RepAngieCraig	HMN02
P000616	Dean Phillips	rep	MN	3	Democrat	202-225-2871	RepDeanPhillips	HMN03
O000173	Ilhan Omar	rep	MN	5	Democrat	202-225-4755	Ilhan	HMN05
S001212	Pete Stauber	rep	MN	8	Republican	202-225-6211	RepPeteStauber	HMN08
G000591	Michael Guest	rep	MS	3	Republican	202-225-5031	RepMichaelGuest	HMS03
A000377	Kelly Armstrong	rep	ND	0	Republican	202-225-2611	RepArmstrongND	HND00
P000614	Chris Pappas	rep	NH	1	Democrat	202-225-5456	RepChrisPappas	HNH01
V000133	Jefferson Van Drew	rep	NJ	2	Republican	202-225-6572	congressmanJVD	HNJ02
K000394	Andy Kim	rep	NJ	3	Democrat	202-225-4765	RepAndyKimNJ	HNJ03
M001203	Tom Malinowski	rep	NJ	7	Democrat	202-225-5361	RepMalinowski	HNJ07
S001207	Mikie Sherrill	rep	NJ	11	Democrat	202-225-5034	RepSherrill	HNJ11
L000590	Susie Lee	rep	NV	3	Democrat	202-225-3252	RepSusieLee	HNV03
O000172	Alexandria Ocasio-Cortez	rep	NY	14	Democrat	202-225-3965	RepAOC	HNY14
D000630	Antonio Delgado	rep	NY	19	Democrat	202-225-5614	repdelgado	HNY19
G000588	Anthony Gonzalez	rep	OH	16	Republican	202-225-3876	RepAGonzalez	HOH16
D000631	Madeleine Dean	rep	PA	4	Democrat	202-225-4731	RepDean	HPA04
H001085	Chrissy Houlahan	rep	PA	6	Democrat	202-225-4315	RepHoulahan	HPA06
M001204	Daniel Meuser	rep	PA	9	Republican	202-225-6511	RepMeuser	HPA09
J000302	John Joyce	rep	PA	13	Republican	202-225-2431	RepJohnJoyce	HPA13
R000610	Guy Reschenthaler	rep	PA	14	Republican	202-225-2065	GReschenthaler	HPA14
T000480	William R. Timmons IV	rep	SC	4	Republican	202-225-6030	RepTimmons	HSC04
J000301	Dusty Johnson	rep	SD	0	Republican	202-225-2801	RepDustyJohnson	HSD00
B001309	Tim Burchett	rep	TN	2	Republican	202-225-5435	RepTimBurchett	HTN02
R000612	John Rose	rep	TN	6	Republican	202-225-4231	RepJohnRose	HTN06
G000590	Mark E. Green	rep	TN	7	Republican	202-225-2811	RepMarkGreen	HTN07
C001120	Dan Crenshaw	rep	TX	2	Republican	202-225-6565	RepDanCrenshaw	HTX02
T000479	Van Taylor	rep	TX	3	Republican	202-225-4201	RepVanTaylor	HTX03
G000589	Lance Gooden	rep	TX	5	Republican	202-225-3484	Lancegooden	HTX05
F000468	Lizzie Fletcher	rep	TX	7	Democrat	202-225-2571	RepFletcher	HTX07
E000299	Veronica Escobar	rep	TX	16	Democrat	202-225-4831	RepEscobar	HTX16
R000614	Chip Roy	rep	TX	21	Republican	202-225-4236	RepChipRoy	HTX21
G000587	Sylvia R. Garcia	rep	TX	29	Democrat	202-225-1688	RepSylviaGarcia	HTX29
A000376	Colin Z. Allred	rep	TX	32	Democrat	202-225-2231	RepColinAllred	HTX32
L000591	Elaine G. Luria	rep	VA	2	Democrat	202-225-4215	RepElaineLuria	HVA02
C001118	Ben Cline	rep	VA	6	Republican	202-225-5431	RepBenCline	HVA06
S001209	Abigail Davis Spanberger	rep	VA	7	Democrat	202-225-2815	RepSpanberger	HVA07
W000825	Jennifer Wexton	rep	VA	10	Democrat	202-225-5136	RepWexton	HVA10
S001216	Kim Schrier	rep	WA	8	Democrat	202-225-7761	RepKimSchrier	HWA08
S001213	Bryan Steil	rep	WI	1	Republican	202-225-3031	RepBryanSteil	HWI01
M001205	Carol D. Miller	rep	WV	3	Republican	202-225-3452	RepCarolMiller	HWV03
G000592	Jared F. Golden	rep	ME	2	Democrat	202-225-6306	repgolden	HME02
K000395	Fred Keller	rep	PA	12	Republican	202-225-3731	RepFredKeller	HPA12
B001311	Dan Bishop	rep	NC	9	Republican	202-225-1976	RepDanBishop	HNC09
M001210	Gregory F. Murphy	rep	NC	3	Republican	202-225-3415	RepGregMurphy	HNC03
M000687	Kweisi Mfume	rep	MD	7	Democrat	202-225-4741	RepKweisiMfume	HMD07
T000165	Thomas P. Tiffany	rep	WI	7	Republican	202-225-3365	RepTiffany	HWI07
G000061	Mike Garcia	rep	CA	25	Republican	202-225-1956	repmikegarcia	HCA25
J000020	Chris Jacobs	rep	NY	27	Republican	202-225-5265	RepJacobs	HNY27
I000056	Darrell Issa	rep	CA	50	Republican	202-225-5672	RepDarrellIssa	HCA50
S000250	Pete Sessions	rep	TX	17	Republican	202-225-6105	PeteSessions	HTX17
V000129	David G. Valadao	rep	CA	21	Republican	202-225-4695	dgvaladao	HCA21
C001054	Jerry L. Carl	rep	AL	1	Republican	202-225-4931	RepJerryCarl	HAL01
M001212	Barry Moore	rep	AL	2	Republican	202-225-2901	RepBarryMoore	HAL02
O000019	Jay Obernolte	rep	CA	8	Republican	202-225-5861	JayObernolte	HCA08
K000397	Young Kim	rep	CA	39	Republican	202-225-4111	RepYoungKim	HCA39
S001135	Michelle Steel	rep	CA	48	Republican	202-225-2415	RepSteel	HCA48
J000305	Sara Jacobs	rep	CA	53	Democrat	202-225-2040	RepSaraJacobs	HCA53
B000825	Lauren Boebert	rep	CO	3	Republican	202-225-4761	RepBoebert	HCO03
C001039	Kat Cammack	rep	FL	3	Republican	202-225-5744	RepKatCammack	HFL03
F000472	C. Scott Franklin	rep	FL	15	Republican	202-225-1252	RepFranklin	HFL15
D000032	Byron Donalds	rep	FL	19	Republican	202-225-2536	RepDonaldsPress	HFL19
G000593	Carlos A. Gimenez	rep	FL	26	Republican	202-225-2778	RepCarlos	HFL26
S000168	Maria Elvira Salazar	rep	FL	27	Republican	202-225-3931	RepMariaSalazar	HFL27
W000788	Nikema Williams	rep	GA	5	Democrat	202-225-3801	RepNikema	HGA05
B001312	Carolyn Bourdeaux	rep	GA	7	Democrat	202-225-4272	RepBourdeaux	HGA07
C001116	Andrew S. Clyde	rep	GA	9	Republican	202-225-9893	Rep_Clyde	HGA09
G000596	Marjorie Taylor Greene	rep	GA	14	Republican	202-225-5211	RepMTG	HGA14
K000396	Kaiali’i Kahele	rep	HI	2	Democrat	202-225-4906	RepKahele	HHI02
H001091	Ashley Hinson	rep	IA	1	Republican	202-225-2911	RepAshleyHinson	HIA01
M001215	Mariannette Miller-Meeks	rep	IA	2	Republican	202-225-6576	RepMMM	HIA02
F000446	Randy Feenstra	rep	IA	4	Republican	202-225-4426	RepFeenstra	HIA04
N000192	Marie Newman	rep	IL	3	Democrat	202-225-5701	RepMarieNewman	HIL03
M001211	Mary E. Miller	rep	IL	15	Republican	202-225-5271	RepMaryMiller	HIL15
M001214	Frank J. Mrvan	rep	IN	1	Democrat	202-225-2461	RepMrvan	HIN01
S000929	Victoria Spartz	rep	IN	5	Republican	202-225-2276	RepSpartz	HIN05
M000871	Tracey Mann	rep	KS	1	Republican	202-225-2715	RepMann	HKS01
L000266	Jake LaTurner	rep	KS	2	Republican	202-225-6601	RepLaTurner	HKS02
A000148	Jake Auchincloss	rep	MA	4	Democrat	202-225-5931	RepAuchincloss	HMA04
M001186	Peter Meijer	rep	MI	3	Republican	202-225-3831	RepMeijer	HMI03
M001136	Lisa C. McClain	rep	MI	10	Republican	202-225-2106	RepLisaMcClain	HMI10
F000470	Michelle Fischbach	rep	MN	7	Republican	202-225-2165	RepFischbach	HMN07
B001224	Cori Bush	rep	MO	1	Democrat	202-225-2406	RepCori	HMO01
R000103	Matthew M. Rosendale	rep	MT	0	Republican	202-225-3211	RepRosendale	HMT00
R000305	Deborah K. Ross	rep	NC	2	Democrat	202-225-3032	RepDeborahRoss	HNC02
M001135	Kathy E. Manning	rep	NC	6	Democrat	202-225-3065	RepKManning	HNC06
C001104	Madison Cawthorn	rep	NC	11	Republican	202-225-6401	RepCawthorn	HNC11
H001084	Yvette Herrell	rep	NM	2	Republican	202-225-2365	RepHerrell	HNM02
L000273	Teresa Leger Fernandez	rep	NM	3	Democrat	202-225-6190	RepTeresaLF	HNM03
G000597	Andrew R. Garbarino	rep	NY	2	Republican	202-225-7896	RepGarbarino	HNY02
M000317	Nicole Malliotakis	rep	NY	11	Republican	202-225-3371	RepMalliotakis	HNY11
T000486	Ritchie Torres	rep	NY	15	Democrat	202-225-4361	RepRitchie	HNY15
B001223	Jamaal Bowman	rep	NY	16	Democrat	202-225-2464	RepBowman	HNY16
J000306	Mondaire Jones	rep	NY	17	Democrat	202-225-6506	RepMondaire	HNY17
B000740	Stephanie I. Bice	rep	OK	5	Republican	202-225-2132	RepBice	HOK05
B000668	Cliff Bentz	rep	OR	2	Republican	202-225-6730	RepBentz	HOR02
M000194	Nancy Mace	rep	SC	1	Republican	202-225-3176	RepNancyMace	HSC01
H001086	Diana Harshbarger	rep	TN	1	Republican	202-225-6356	RepHarshbarger	HTN01
F000246	Pat Fallon	rep	TX	4	Republican	202-225-6673	RepPatFallon	HTX04
P000048	August Pfluger	rep	TX	11	Republican	202-225-3605	RepPfluger	HTX11
J000304	Ronny Jackson	rep	TX	13	Republican	202-225-3706	RepRonnyJackson	HTX13
N000026	Troy E. Nehls	rep	TX	22	Republican	202-225-5951	RepTroyNehls	HTX22
G000594	Tony Gonzales	rep	TX	23	Republican	202-225-4511	RepTonyGonzales	HTX23
V000134	Beth Van Duyne	rep	TX	24	Republican	202-225-6605	RepBethVanDuyne	HTX24
M001213	Blake D. Moore	rep	UT	1	Republican	202-225-0453	RepBlakeMoore	HUT01
O000086	Burgess Owens	rep	UT	4	Republican	202-225-3011	RepBurgessOwens	HUT04
G000595	Bob Good	rep	VA	5	Republican	202-225-4711	RepBobGood	HVA05
S001159	Marilyn Strickland	rep	WA	10	Democrat	202-225-9740	RepStricklandWA	HWA10
F000471	Scott Fitzgerald	rep	WI	5	Republican	202-225-5101	RepFitzgerald	HWI05
T000478	Claudia Tenney	rep	NY	22	Republican	202-225-3665	RepTenney	HNY22
L000595	Julia Letlow	rep	LA	5	Republican	202-225-8490	RepJuliaLetlow	HLA05
C001125	Troy A. Carter	rep	LA	2	Democrat	202-225-6636	RepTroyCarter	HLA02
S001218	Melanie A. Stansbury	rep	NM	1	Democrat	202-225-6316	Rep_Stansbury	HNM01
E000071	Jake Ellzey	rep	TX	6	Republican	202-225-2002	RepEllzey	HTX06
\.


--
-- Data for Name: schema_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	2	<< Flyway Baseline >>	BASELINE	<< Flyway Baseline >>	\N	null	2021-12-22 17:23:28.340378	0	t
\.


--
-- Data for Name: talking_point; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.talking_point (id, title, issue_id, content, relative_order_position) FROM stdin;
3	Introduction	5	<p>The <a href="https://www.congress.gov/bill/117th-congress/house-bill/5376">Build Back Better Act</a>  (BBB) is the cornerstone of the Biden administration’s plan to meet U.S. commitments under the Paris agreement. Coupled with robust executive action, the BBB would put the U.S. on track to achieve 50% emissions reduction by 2030 (below 2005 levels). The legislation couples emissions reductions with significant investments in environmental justice communities and would create thousands of new jobs over the next eight years.</p>	1
4	Timeline	5	<p>The House of Representatives is expected to vote on the legislation this week. A group of House Democratic moderates have refused to commit to voting “yes” until they see the estimated budget impact from the Congressional Budget Office. After the BBB passes the House, it will then be considered by the Senate, which can propose amendments and modifications in its own version (which the House will then have to agree to). Senator Joe Manchin has also insisted that he must see the projected budget impact before committing his support.</p>	2
5	Clear Policy Ask	5	<h2>The Build Back Better Act must pass with its existing climate provisions at minimum, which total <b>$555 billion.</b></h2>	3
12	Fossil fuel subsidies are economically inefficient policies	6	<p>They price carbon at far below its social cost to society, and on a global scale, they are economically regressive policies that benefit the wealthiest 20%. Externalities from supporting the fossil fuel industry cost the U.S. $649 billion every year.</p>	5
25	The clean electricity tax incentives in BBB will significantly reduce emissions	8	<p>The bill’s tax credits alone would reduce power-sector emissions by <a href="https://rhg.com/research/build-back-better-clean-energy-tax-credits/">64–73%</a> below the 2005 baseline. When combined with the Infrastructure Investment and Jobs Act signed into law in 2021, the Build Back Better Act would put the U.S. <a href="https://www.dropbox.com/s/gckss8qyzflelhn/REPEAT_Prelim_Report_Addendum_111221.pdf?dl=0">within striking distance</a> of its target under the Paris Agreement (50% economy-wide emissions reduction by 2030). The gap can be filled with a combination of executive actions, state legislation, and corporate accountability.</p>	3
6	Major climate funding in Build Back Better	5	<ul><li><p><strong>Clean energy tax credits, </strong>which will deliver the bulk of emissions reductions in the bill. Tax incentives include electric vehicles, clean electricity, clean buildings, and decarbonized industry. ($304 billion)</p></li><li><p><strong>Clean electricity modernization and transmission </strong>($45 billion) </p></li><li><p><strong>A Green Bank</strong>, called the Greenhouse Gas Reduction Fund. It provides low-cost financing for clean energy projects and commits at least 40% of investments to environmental justice communities. ($29 billion)</p></li><li><p><strong>Clean transportation</strong>, including EV charging infrastructure, public transit, passenger rail, and e-bikes. ($25 billion)</p></li><li><p><strong>Agriculture decarbonization. </strong>($21 billion)</p></li><li><p><strong>A Civilian Climate Corps</strong>, which will provide 300,000 Americans with dignified work in facilitating the energy transition. ($19 billion)</p></li><li><p><strong>Direct environmental justice remediation,</strong> which will reduce and monitor air pollution and lead, invest directly in communities, and identify environmental inequities. ($15 billion)</p></li><li><p><strong>Clean buildings</strong>, including rebates for efficiency upgrades, retrofits, and clean appliances. ($14 billion)</p></li></ul>	4
11	Fossil fuel companies spend public money on private lobbying	6	<p>Fossil energy companies earn a greater than 13,000% return on investment while slashing thousands of jobs. In 2020, the oil, gas, and coal industries spent more than $115 million lobbying Congress in defense of their <a href="https://www.sanders.senate.gov/wp-content/uploads/EPWA-Summary-One-Pager-vfinal.pdf" target="">$15 billion</a> in giveaways. Eliminating subsidies to the industry is a step toward fighting corruption and preventing the abuse of taxpayers’ hard-earned money.</p>	4
13	Ending fossil fuel subsidies is politically popular	6	<p>According to polling from <a href="https://www.dataforprogress.org/blog/8/6/voters-support-ending-fossil-fuel-subsidies" target="">Data for Progress</a>, 47% of voters are in favor of rolling back all tax incentives for fossil fuel companies, compared to only 30% opposed.</p><img data-src="https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/1632954160476-UPORIMF33IAOYEYXW2Z1/Screen+Shot+2021-09-29+at+6.22.26+PM.png" data-image="https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/1632954160476-UPORIMF33IAOYEYXW2Z1/Screen+Shot+2021-09-29+at+6.22.26+PM.png" data-image-dimensions="1274x782" data-image-focal-point="0.5,0.5" data-load="false" data-image-id="6154e730dc901a7e7235edd2" data-type="image" data-image-resolution="500w" src="https://images.squarespace-cdn.com/content/v1/5f4d66c32e63212b309348ad/1632954160476-UPORIMF33IAOYEYXW2Z1/Screen+Shot+2021-09-29+at+6.22.26+PM.png?format=500w">	6
26	Americans strongly support clean electricity incentives in Build Back Better	8	<p>A <a href="https://www.filesforprogress.org/datasets/2022/1/dfp_direct_pay_toplines.pdf">Data for Progress poll</a> from December 2021 shows 67% support for the tax credits, compared with 15% opposed. Similarly, Data for Progress polling shows 63% support for clean electricity performance standards and 67% support for increased research and development of clean energy technologies. And comprehensively, the Build Back Better Act remains popular with the American public, enjoying <a href="https://www.filesforprogress.org/datasets/2022/1/dfp-iia-jan13-toplines.pdf">65% support </a>as of January 2022.</p>	7
27	Clean electricity incentives are an enormous job creator	8	<p>The BBB clean electricity tax credits alone can generate 600,000 jobs per year on net (as in, this accounts for job loss in the fossil fuel industry).</p>	4
28	Clean electricity incentives will reduce pollution other than carbon dioxide	8	<p>Over the first five years, the tax credits are <a href="https://rhg.com/research/build-back-better-clean-electricity/">projected</a> to reduce nitrous oxides by up to 62% and sulfur dioxides by up to 84%. Despite the innocuous name “laughing gas,” nitrous oxide is a potent greenhouse gas. Sulfur dioxide <a href="https://ww2.arb.ca.gov/resources/sulfur-dioxide-and-health">exacerbates asthma</a> and suppresses forest growth, further reducing Earth’s capacity to absorb carbon dioxide.</p>	6
29	Introduction	8	<p>Clean power is the bedrock of a zero-emissions economy. If we successfully electrify everything, we’ll see a major uptick in electricity use, making it all the more imperative that we eliminate electricity-sector emissions. </p><p>Until the United States substantially phases out fossil fuels from its energy mix, clean energy technologies will require significant investment from the federal government. The Build Back Better (BBB) Act allocates <em>roughly</em> <a href="https://www.jct.gov/CMSPages/GetFile.aspx?guid=3fbf5966-deea-41eb-a159-eda2c599e56b">$162 billion</a> in a few different types of clean electricity tax credits, most of them technology-neutral, to be spent over 5–10 years. The potential benefits of these tax credits are massive and would put the U.S. on track to meet its emissions targets under the Paris Agreement, which require cutting greenhouse gas emissions in half by 2030. By 2031, the BBB clean electricity tax credits could reduce power-sector emissions by <a href="https://rhg.com/research/build-back-better-clean-energy-tax-credits/">up to 73%</a>, even after the bill’s heaviest-hitting emissions reduction policy, the Clean Electricity Performance Program, was cut. </p><p>The good news is that this bill has already passed the House of Representatives. But as the 2022 midterm election cycle picks up, the window to pass this legislation through the Senate appears to be narrowing. If it closes, the potential for progress on climate policy this year will vastly diminish. </p>	1
7	Introduction	6	<p>Direct U.S. taxpayer support to fossil fuel companies totals <a href="https://www.eesi.org/papers/view/fact-sheet-fossil-fuel-subsidies-a-closer-look-at-tax-breaks-and-societal-costs" target="">$20.5 billion</a> per year, while indirect subsidies may reach <a href="https://www.eesi.org/papers/view/fact-sheet-proposals-to-reduce-fossil-fuel-subsidies-2021" target=""><strong>$649 <em>billion</em> </strong></a>annually. “Direct” subsidies include direct cash payments, while “indirect” subsidies refer to tax incentives plus externalities that result from benefits to the fossil fuel industry.</p><p>Broadly, U.S. fossil fuel subsidies generally take the form of 1) tax incentives, 2) passively allowing fossil fuel companies to exploit tax loopholes and use creative accounting methods, 3) federally funded investment in fossil energy development projects, and 4) pricing fossil fuels below their social cost in the absence of emissions pricing. The first three can be repealed by an act of Congress, while the fourth can be tackled by introducing a carbon pricing scheme in tandem with repealing subsidies.</p><p>Earlier this year, President Biden included the repeal of fossil fuel subsidies in his budget proposal to Congress, which was projected to save the U.S. government <a href="https://www.evergreenaction.com/blog/democrats-must-end-taxpayer-handouts-to-big-oil-in-the-reconciliation-bill" target="">$121 billion</a> over 10 years. And in November, the House passed the Build Back Better Act, which begins to chip away at fossil fuel subsidies by closing tax loopholes that allowed oil and gas companies to benefit from income earned overseas. The bill also reinstates an EPA <a href="https://www.epa.gov/superfund/what-superfund">Superfund</a> clean-up tax levied on heavy polluters, which effectively eliminates a “subsidy” under which these companies had not been required to pay for their pollution.</p>	1
10	Pricing fossil fuels efficiently would cause a dramatic decline in global emissions	6	<p>The International Monetary Fund found that efficient oil, gas, and coal pricing by 2025 would lead to a <a href="https://www.imf.org/en/Publications/WP/Issues/2021/09/23/Still-Not-Getting-Energy-Prices-Right-A-Global-and-Country-Update-of-Fossil-Fuel-Subsidies-466004" target="">36% decline</a> in global emissions. This puts us well on track to keep warming below 2 degrees Celsius. Current global fossil fuel subsidies reached <a href="https://www.theguardian.com/environment/2021/oct/06/fossil-fuel-industry-subsidies-of-11m-dollars-a-minute-imf-finds" target="">$5.9 trillion</a> in 2020, or $11 million every minute.</p>	3
14	Removing subsidies at home will strengthen U.S. credibility abroad	6	Fossil fuel subsidies are pervasive in all of the world’s top oil and gas economies, not just the United States. Previous initiatives by the G-20 have been unsuccessful in achieving global fossil fuel subsidy reform, but the U.S. has an opportunity to lead by example and reinvigorate G-20 efforts.	7
15	Fossil fuel companies currently benefit from handouts unique to their industry, even as they deliver diminishing returns	6	To name a few unique perks: they cash in using the intangible drilling deduction, deductions and credit for royalty payments paid to foreign governments (!), incentives for enhanced oil recovery (a method of drilling using injected CO2), and financial support for the exploration and production of new oil and gas wells. This money would be better spent diversified across industries that will grow and flourish in the 21st century.	8
8	Clear Policy Ask	6	<h4>Congress must end all taxpayer-funded support for the fossil fuel industry. Both chambers must pass the End Polluter Welfare Act (S.1167/HR.2102). The Senate must also retain existing fossil fuel subsidy repeal in the Build Back Better Act.</h4><p><br>The End Polluter Welfare Act would eliminate publicly funded fossil fuel subsidies and save American taxpayers up to $150 billion over the next 10 years by:</p><ul><li><p>Abolishing dozens of tax loopholes and subsidies throughout the federal tax code that benefit oil, gas, and coal special interests.</p></li><li><p>Updating below-market royalty rates for oil and gas production on federal lands, recouping royalties from offshore drilling in public waters, and ensuring competitive bidding and leasing practices for coal development on federal lands.</p></li><li><p>Prohibiting taxpayer-funded fossil fuel research and development.</p></li><li><p>Ending federal support for international oil, gas, and coal projects and supporting the global community’s fight to move away from dirty fossil fuels.</p></li></ul><p>Other proposed bills to eliminate taxpayer subsidies to fossil fuels include the Clean Energy for America Act (S.2118) and the End Oil &amp; Gas Tax Subsidies Act (H.R.2184).</p><p><strong><em>Check whether your members of Congress already cosponsor fossil fuel subsidy legislation: </em></strong></p><ul><li><p>End Polluter Welfare Act (<a href="https://www.congress.gov/bill/117th-congress/house-bill/2102/cosponsors?q=%7B%22search%22%3A%5B%22End%20polluter%20welfare%20act%22%2C%22End%22%2C%22polluter%22%2C%22welfare%22%2C%22act%22%5D%7D&amp;r=1&amp;s=1" target="">House</a>) (<a href="https://www.congress.gov/bill/117th-congress/senate-bill/1167/cosponsors?q=%7B%22search%22%3A%5B%22End%20polluter%20welfare%20act%22%2C%22End%22%2C%22polluter%22%2C%22welfare%22%2C%22act%22%5D%7D&amp;r=2&amp;s=1" target="">Senate</a>)</p></li><li><p>Clean Energy for America Act (<a href="https://www.congress.gov/bill/117th-congress/senate-bill/1298/cosponsors?q=%7B%22search%22%3A%5B%22clean%20energy%20for%20america%20act%22%2C%22clean%22%2C%22energy%22%2C%22for%22%2C%22america%22%2C%22act%22%5D%7D&amp;r=1&amp;s=3" target="">Senate</a>)</p></li><li><p>End Oil &amp; Gas Tax Subsidies Act (<a href="https://www.congress.gov/bill/117th-congress/house-bill/2184/cosponsors?q=%7B%22search%22%3A%5B%22end%20oil%20%26%20gas%20tax%20subsidies%20act%22%2C%22end%22%2C%22oil%22%2C%22gas%22%2C%22tax%22%2C%22subsidies%22%2C%22act%22%5D%7D&amp;r=1&amp;s=2" target="">House</a>)</p></li></ul>	2
9	Eliminating fossil fuel subsidies would save billions in revenue for the federal government	6	<p>For climate advocates and budget hawks, eliminating these subsidies is a win-win. The Biden administration projected savings of $121 billion over a decade, which could be used to fund critical public health, education, infrastructure, and social initiatives instead of raising taxes. If included in the Build Back Better Act, it could help offset the cost of other provisions in the bill.</p>	9
16	States are enacting voting restrictions at record pace	7	<p>At least 30 laws have been passed in 2021 alone that limit voting access and eligibility. Texas is about to adopt the most restrictive voting measures in the country with the passage of <a href="https://apnews.com/article/health-texas-voting-coronavirus-pandemic-election-2020-770f916d1d3743fbdc791ae7da502d58?utm_campaign=SocialFlow&amp;utm_medium=AP&amp;utm_source=Twitter">SB.1</a>. States are also entering a period of congressional redistricting after the 2020 Census, and only <a href="https://ballotpedia.org/Redistricting_commissions">six</a> states have independent, nonpolitical redistricting commissions.</p>	3
17	Introduction	7	<p>In 2021, <a href="https://www.brennancenter.org/our-work/research-reports/voting-laws-roundup-july-2021">18 states</a> passed a combined 30 laws that make it more difficult to vote. A landmark piece of legislation, the Voting Rights Act of 1965, used to help neutralize restrictive state voting laws by requiring states with a history of discrimination to obtain preclearance from the federal government before implementing a new law. But in 2013 and again in 2021, the Supreme Court struck down two critical sections of the VRA, effectively killing the preclearance system and limiting the federal government’s ability to challenge state voting laws in court.</p><p>The continued disenfranchisement of marginalized Americans has major consequences for climate action. The fossil fuel industry has a stronghold on the political system and is well-resourced and well-organized enough to exert lobbying influence and fill the coffers of Super PACs that fund campaigns. This means that many elected officials are biased in favor of fossil energy companies before they are even sworn into office, and the climate movement is playing offense.</p><p>Fortunately, climate advocates have public opinion on our side. Polling shows that <a href="https://climatecommunication.yale.edu/visualizations-data/ycom-us/">a strong majority </a>of Americans believe in, and are concerned about, a warming planet. Non-white Americans <a href="https://climatecommunication.yale.edu/publications/race-ethnicity-and-public-responses-to-climate-change/">report</a> higher rates of concern about the climate crisis, but they’re also the population targeted by new voting restrictions across the country. Expanding voting rights for all citizens will lead to greater climate accountability and electing more climate champions into office at every level of government.</p>	1
18	Climate change will disproportionately impact communities of color, whose votes are often the ones being suppressed	7	<p>These are the same communities often targeted by restrictive voting measures. If communities of color aren’t able to advocate for themselves at the ballot box, they will be neglected in future policy decisions. A history of institutionalized discrimination confirms this. Further, eliminating partisan gerrymandering will help prevent marginalized communities from being clustered in districts that do not represent the natural geographic distribution. </p>	5
19	Democracy reform is critical for passing bold climate policy	7	<p>Surveys show that the majority of Americans believe that transitioning to a clean energy economy should be a priority, but the will of the majority is often ignored due to a combination of corporate influence in politics and deliberate attempts to suppress access to the ballot box. To meaningfully address climate change is to break free of minority rule. </p>	4
20	Clear Policy Ask	7	<h3>The Senate must pass the Freedom to Vote Act (<a href="https://www.congress.gov/bill/117th-congress/senate-bill/2747">S. 2747</a>) to prevent the spread of anti-democratic voting restrictions.</h3><p>To pass this bill, the Senate must either amend filibuster rules to exempt voting rights legislation from the required 60-vote threshold, or else 9 Republicans must join the Democrats in support of the bill. Senate Majority Leader Chuck Schumer is pushing to amend Senate filibuster rules, which Democrats can accomplish with a simple majority.</p><p>The Freedom to Vote Act is a pared-down version of the For the People Act, which passed the House in March of 2021. It would neutralize restrictive voting laws at the state level and curb the influence of dark money by:</p><ul data-rte-list="default"><li><p>Banning partisan gerrymandering</p></li><li><p>Enabling all voters to request mail-in ballots, with a minimum number of drop boxes provided</p></li><li><p>Requiring 15 days of early voting</p></li><li><p>Providing automatic voter registration with a driver’s license, plus Election Day registration</p></li><li><p>Imposing mandatory voter ID that is more flexible than many states’ voter ID laws</p></li><li><p>Making Election Day a holiday</p></li><li><p>Setting higher standards for purging old voter rolls</p></li><li><p>Blocks Georgia’s law against allowing food and beverage in polling lines</p></li><li><p>Increasing election security, including a requirement that candidates report attempts by foreign governments to influence their campaigns</p></li><li><p>Requiring donors of $10,000+ to report contributions to PACs<br></p></li></ul><p><strong><em>Note: All 50 Democratic senators cosponsor the Freedom to Vote Act, while none of the 50 Republican senators has cosponsored.</em></strong></p>	2
21	The political influence of the fossil fuel industry runs deep, keeping politicians beholden to special interests	7	<p>The fossil fuel industry supports candidates who will keep them in business, and greater enfranchisement will serve to hold politicians more accountable to their electorates. Requiring large contributions to PACs, rather than just candidates, will help counter the influence of fossil fuel dark money in our elections.</p>	7
22	An overwhelming majority of Americans (69%) support democracy reform	7	<p><a href="https://www.filesforprogress.org/datasets/2021/4/dfp-vox-hr-1.pdf">Polling</a> on a more sweeping democracy reform bill that failed in the Senate, the For the People Act, showed overwhelming support among the public. The issue of voting rights isn't a matter of left vs. right—it's about common-sense reforms to make sure the voices of the American people are heard.</p>	6
23	The BBB tax incentives invest in environmental justice communities	8	<p>The clean electricity incentives include specific tax credits for generating clean power in low-income communities. These communities, which are often majority-BIPOC, suffer disproportionately from fossil fuel power generation and from the adverse effects of climate change.</p>	5
24	Clear Policy Ask	8	<h3>The Senate must pass legislation with over $160 billion in clean electricity tax incentives to put the U.S. on track to cut emissions in half by 2030.</h3><p>Clean electricity tax incentives of this magnitude have already passed the House via the <a href="https://www.congress.gov/bill/117th-congress/house-bill/5376">Build Back Better Act</a>, which includes over $500 billion in total climate spending. The Senate can pass the Build Back Better Act with a simple majority. </p><p>The clean electricity incentives included in the Build Back Better Act are: </p><ul data-rte-list="default"><li><p>New production and investment tax credits for renewable power (technology-neutral), plus extensions of existing credits</p></li><li><p>Tax credit for clean electricity production in low-income communities</p></li><li><p>Electric transmission investment credit</p></li><li><p>Cost recovery for grid improvement</p></li><li><p>Nuclear power production credit</p></li><li><p>Carbon sequestration tax credit</p></li></ul>	2
\.


--
-- Name: issue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.issue_id_seq', 8, true);


--
-- Name: talking_point_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.talking_point_id_seq', 29, true);


--
-- Name: action_contact_legislator action_contact_legislator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_contact_legislator
    ADD CONSTRAINT action_contact_legislator_pkey PRIMARY KEY (id);


--
-- Name: hoa_attendance_zoom_with_action hoa_attendance_zoom_with_action_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_attendance_zoom_with_action
    ADD CONSTRAINT hoa_attendance_zoom_with_action_pkey PRIMARY KEY (date, email);


--
-- Name: hoa_events hoa_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_events
    ADD CONSTRAINT hoa_events_pkey PRIMARY KEY (id);


--
-- Name: issue issue_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_pkey PRIMARY KEY (id);


--
-- Name: lcv_score_lifetime lcv_score_lifetime_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lcv_score_lifetime
    ADD CONSTRAINT lcv_score_lifetime_pkey PRIMARY KEY (bioguide_id);


--
-- Name: lcv_score_year lcv_score_year_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lcv_score_year
    ADD CONSTRAINT lcv_score_year_pkey PRIMARY KEY (bioguide_id, score_year);


--
-- Name: luma_attendance luma_attendance_event_id_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.luma_attendance
    ADD CONSTRAINT luma_attendance_event_id_email_key UNIQUE (event_id, email);


--
-- Name: member_of_congress member_of_congress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.member_of_congress
    ADD CONSTRAINT member_of_congress_pkey PRIMARY KEY (bioguide_id);


--
-- Name: talking_point relative_ordering_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.talking_point
    ADD CONSTRAINT relative_ordering_unique UNIQUE (issue_id, relative_order_position);


--
-- Name: schema_history schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schema_history
    ADD CONSTRAINT schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: talking_point talking_point_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.talking_point
    ADD CONSTRAINT talking_point_pkey PRIMARY KEY (id);


--
-- Name: contacts unique_airtable_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contacts
    ADD CONSTRAINT unique_airtable_id UNIQUE (airtable_id);


--
-- Name: ix_hoa_attendance_zoom_with_action_action; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ix_hoa_attendance_zoom_with_action_action ON public.hoa_attendance_zoom_with_action USING btree (action);


--
-- Name: ix_hoa_attendance_zoom_with_action_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ix_hoa_attendance_zoom_with_action_date ON public.hoa_attendance_zoom_with_action USING btree (date);


--
-- Name: ix_hoa_attendance_zoom_with_action_email; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ix_hoa_attendance_zoom_with_action_email ON public.hoa_attendance_zoom_with_action USING btree (email text_ops);


--
-- Name: schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX schema_history_s_idx ON public.schema_history USING btree (success);


--
-- Name: action_call_legislator action_call_legislator_action_contact_legislator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_call_legislator
    ADD CONSTRAINT action_call_legislator_action_contact_legislator_id_fkey FOREIGN KEY (action_contact_legislator_id) REFERENCES public.action_contact_legislator(id) ON DELETE CASCADE;


--
-- Name: action_contact_legislator action_contact_legislator_contacted_bioguide_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_contact_legislator
    ADD CONSTRAINT action_contact_legislator_contacted_bioguide_id_fkey FOREIGN KEY (contacted_bioguide_id) REFERENCES public.member_of_congress(bioguide_id);


--
-- Name: action_contact_legislator action_contact_legislator_issue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_contact_legislator
    ADD CONSTRAINT action_contact_legislator_issue_id_fkey FOREIGN KEY (issue_id) REFERENCES public.issue(id) ON DELETE CASCADE;


--
-- Name: action_email_legislator action_email_legislator_action_contact_legislator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_email_legislator
    ADD CONSTRAINT action_email_legislator_action_contact_legislator_id_fkey FOREIGN KEY (action_contact_legislator_id) REFERENCES public.action_contact_legislator(id) ON DELETE CASCADE;


--
-- Name: action_tweet_legislator action_tweet_legislator_action_contact_legislator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.action_tweet_legislator
    ADD CONSTRAINT action_tweet_legislator_action_contact_legislator_id_fkey FOREIGN KEY (action_contact_legislator_id) REFERENCES public.action_contact_legislator(id) ON DELETE CASCADE;


--
-- Name: attendance_preview attendance_preview_airtable_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attendance_preview
    ADD CONSTRAINT attendance_preview_airtable_id_fkey FOREIGN KEY (airtable_id) REFERENCES public.contacts(airtable_id) ON DELETE RESTRICT;


--
-- Name: district_office constraint_fk_bioguide; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.district_office
    ADD CONSTRAINT constraint_fk_bioguide FOREIGN KEY (bioguide_id) REFERENCES public.member_of_congress(bioguide_id);


--
-- Name: lcv_score_lifetime constraint_fk_bioguide; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lcv_score_lifetime
    ADD CONSTRAINT constraint_fk_bioguide FOREIGN KEY (bioguide_id) REFERENCES public.member_of_congress(bioguide_id) ON DELETE CASCADE;


--
-- Name: lcv_score_year constraint_fk_bioguide; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lcv_score_year
    ADD CONSTRAINT constraint_fk_bioguide FOREIGN KEY (bioguide_id) REFERENCES public.member_of_congress(bioguide_id) ON DELETE CASCADE;


--
-- Name: example_issue_why_statement example_issue_why_statement_issue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.example_issue_why_statement
    ADD CONSTRAINT example_issue_why_statement_issue_id_fkey FOREIGN KEY (issue_id) REFERENCES public.issue(id) ON DELETE CASCADE;


--
-- Name: focus_issue focus_issue_issue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.focus_issue
    ADD CONSTRAINT focus_issue_issue_id_fkey FOREIGN KEY (issue_id) REFERENCES public.issue(id) ON DELETE CASCADE;


--
-- Name: luma_attendance luma_attendance_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.luma_attendance
    ADD CONSTRAINT luma_attendance_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.hoa_events(id) ON DELETE RESTRICT;


--
-- Name: talking_point talking_point_issue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.talking_point
    ADD CONSTRAINT talking_point_issue_id_fkey FOREIGN KEY (issue_id) REFERENCES public.issue(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

