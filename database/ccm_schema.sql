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
-- Name: action_initiate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.action_initiate (
    email public.citext NOT NULL,
    initiated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
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
-- Data for Name: lcv_score_lifetime; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lcv_score_lifetime (bioguide_id, score) FROM stdin;
F000062	90
H001075	90
G000555	95
S000148	92
S001217	4
R000595	6
S000320	13
J000300	78
H001042	95
S001194	98
H001089	4
B000575	6
B001243	3
A000360	20
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
M001197	8
S001191	76
B001261	7
E000285	5
P000612	4
L000594	8
V000128	98
C000141	92
C001098	3
C001056	5
D000618	7
T000464	88
S001198	8
M001153	18
B001267	88
G000562	11
H001061	9
C001096	3
D000563	88
D000622	90
U000039	96
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
R000307	9
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
C001094	5
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
C001124	94
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
C001123	98
R000486	96
T000472	98
C000059	7
W000187	92
B001300	98
P000618	94
C001110	93
L000579	98
R000616	92
L000593	98
H001048	2
V000130	96
P000608	93
D000598	97
J000020	33
Z000017	14
K000210	19
S001201	98
R000602	94
M001137	90
M001188	98
V000081	94
J000294	96
C001067	95
N000002	97
R000613	96
M000087	96
E000297	99
O000172	96
S000248	93
E000179	94
L000480	94
M001185	90
D000630	98
T000469	97
S001196	38
B001308	98
R000585	13
K000386	34
M001206	94
H001038	95
G000578	12
D000628	4
Y000065	2
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
S001210	8
B001260	23
S001214	4
M001199	29
R000607	23
H000324	85
F000462	98
D000610	92
W000797	93
W000808	91
D000600	13
M001207	98
S001206	98
B001289	0
R000591	4
R000575	5
A000055	3
B001274	8
P000609	1
S001185	81
C001055	95
G000571	92
C001049	90
W000812	4
L000569	3
H001053	3
C001061	89
G000546	4
L000576	3
S001195	1
R000582	4
B001309	10
F000459	4
D000616	2
C000754	83
R000612	6
G000590	2
K000392	4
C001068	97
S001176	4
R000588	77
H001077	3
J000299	3
A000374	2
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
A000367	18
M001194	7
K000380	96
U000031	28
W000798	4
S001208	96
L000592	100
M001201	6
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
F000455	93
B001306	11
R000577	91
J000295	12
S001187	8
G000588	24
P000614	98
K000382	95
H001088	6
C001119	98
P000616	98
M001143	94
O000173	96
E000294	3
P000258	34
S001212	16
S001213	20
P000607	97
K000188	90
M001160	94
S000244	24
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
H001092	100
C001103	4
B000490	55
F000465	5
J000288	96
L000287	93
M001208	96
W000810	4
S001189	3
C001093	4
H001071	0
L000583	1
A000372	1
S001157	84
G000560	3
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
R000601	2
G000589	4
W000827	4
F000468	88
B000755	3
G000553	87
M001157	8
C001062	2
G000377	6
T000238	2
W000814	2
G000581	80
E000299	96
F000461	3
J000032	83
A000375	2
C001091	96
R000614	0
O000168	2
H001073	13
M001158	3
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
G000584	7
Y000033	9
D000197	96
N000191	98
T000470	7
B001297	3
L000564	3
C001121	98
P000593	87
A000377	6
R000515	81
K000385	95
L000563	91
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
S000364	6
K000378	8
B001286	90
L000585	3
H001080	98
T000484	96
L000570	97
B001250	3
S001192	4
C001114	2
M001209	82
B001278	98
W000791	10
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
H001083	92
F000467	94
L000565	91
A000378	94
K000362	4
N000015	93
M000312	99
T000482	96
K000379	96
C001101	96
M001196	96
P000617	98
L000562	95
K000375	96
V000108	83
W000813	4
B001299	2
B001307	4
B001284	8
P000615	10
C001072	93
B001275	6
H001074	15
B001251	90
H001065	2
M001210	11
P000523	92
F000450	4
W000819	1
R000603	4
H001067	2
B001311	0
M001156	5
M001187	2
A000370	97
B001305	4
W000804	10
L000591	96
S000185	92
M001200	92
R000611	12
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
H001064	96
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
C001122	92
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
W000824	8
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
C001094	2020	15
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
C001124	2020	90
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
C001123	2020	100
R000486	2020	100
T000472	2020	100
C000059	2020	14
W000187	2020	100
B001300	2020	100
P000618	2020	100
C001110	2020	100
L000579	2020	100
R000616	2020	100
L000593	2020	100
H001048	2020	0
V000130	2020	100
P000608	2020	100
D000598	2020	95
J000020	2020	33
Z000017	2020	24
K000210	2020	52
S001201	2020	100
R000602	2020	90
M001137	2020	100
M001188	2020	100
V000081	2020	100
J000294	2020	100
C001067	2020	100
N000002	2020	100
R000613	2020	100
M000087	2020	100
E000297	2020	100
O000172	2020	95
S000248	2020	86
E000179	2020	100
L000480	2020	100
M001185	2020	100
D000630	2020	100
T000469	2020	100
S001196	2020	48
B001308	2020	100
R000585	2020	48
K000386	2020	62
M001206	2020	90
H001038	2020	100
G000578	2020	10
D000628	2020	5
Y000065	2020	0
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
S001210	2020	5
B001260	2020	33
S001214	2020	0
M001199	2020	29
R000607	2020	19
H000324	2020	100
F000462	2020	100
D000610	2020	100
W000797	2020	100
W000808	2020	95
D000600	2020	29
M001207	2020	100
S001206	2020	100
B001289	2020	0
R000591	2020	5
R000575	2020	5
A000055	2020	5
B001274	2020	0
P000609	2020	0
S001185	2020	90
C001055	2020	100
G000571	2020	76
C001049	2020	95
W000812	2020	24
L000569	2020	5
H001053	2020	10
C001061	2020	86
G000546	2020	14
L000576	2020	10
S001195	2020	5
R000582	2020	14
B001309	2020	10
F000459	2020	10
D000616	2020	10
C000754	2020	100
R000612	2020	10
G000590	2020	5
K000392	2020	10
C001068	2020	100
S001176	2020	14
R000588	2020	95
H001077	2020	10
J000299	2020	0
A000374	2020	5
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
A000367	2020	14
M001194	2020	19
K000380	2020	100
U000031	2020	62
W000798	2020	5
S001208	2020	95
L000592	2020	100
M001201	2020	14
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
F000455	2020	90
B001306	2020	24
R000577	2020	100
J000295	2020	33
S001187	2020	33
G000588	2020	29
P000614	2020	100
K000382	2020	100
H001088	2020	5
C001119	2020	100
P000616	2020	100
M001143	2020	100
O000173	2020	95
E000294	2020	10
P000258	2020	76
S001212	2020	29
S001213	2020	19
P000607	2020	100
K000188	2020	90
M001160	2020	100
S000244	2020	5
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
H001092	2020	100
C001103	2020	14
B000490	2020	100
F000465	2020	10
J000288	2020	100
L000287	2020	100
M001208	2020	95
W000810	2020	5
S001189	2020	10
C001093	2020	10
H001071	2020	0
L000583	2020	0
A000372	2020	0
S001157	2020	100
G000560	2020	5
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
R000601	2020	14
G000589	2020	0
W000827	2020	0
F000468	2020	100
B000755	2020	5
G000553	2020	100
M001157	2020	33
C001062	2020	0
G000377	2020	14
T000238	2020	5
W000814	2020	0
G000581	2020	95
E000299	2020	95
F000461	2020	5
J000032	2020	100
A000375	2020	0
C001091	2020	100
R000614	2020	0
O000168	2020	10
H001073	2020	43
M001158	2020	0
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
G000584	2020	19
Y000033	2020	33
D000197	2020	100
N000191	2020	100
T000470	2020	10
B001297	2020	0
L000564	2020	5
C001121	2020	100
P000593	2020	100
A000377	2020	0
R000515	2020	100
K000385	2020	100
L000563	2020	90
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
S000364	2020	14
K000378	2020	29
B001286	2020	100
L000585	2020	14
H001080	2020	100
T000484	2020	95
L000570	2020	100
B001250	2020	10
S001192	2020	14
C001114	2020	5
M001209	2020	76
B001278	2020	100
W000791	2020	19
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
H001083	2020	90
F000467	2020	95
L000565	2020	100
A000378	2020	95
K000362	2020	5
N000015	2020	100
M000312	2020	100
T000482	2020	95
K000379	2020	100
C001101	2020	100
M001196	2020	100
P000617	2020	100
L000562	2020	100
K000375	2020	95
V000108	2020	95
W000813	2020	14
B001299	2020	5
B001307	2020	5
B001284	2020	24
P000615	2020	10
C001072	2020	100
B001275	2020	14
H001074	2020	10
B001251	2020	100
H001065	2020	10
M001210	2020	10
P000523	2020	100
F000450	2020	10
W000819	2020	5
R000603	2020	14
H001067	2020	5
B001311	2020	0
M001156	2020	14
M001187	2020	17
A000370	2020	95
B001305	2020	10
W000804	2020	19
L000591	2020	95
S000185	2020	100
M001200	2020	100
R000611	2020	5
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
H001064	2020	100
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
C001122	2020	90
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
W000824	2020	5
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
H001075	2020	85
G000555	2020	100
S000148	2020	100
S001217	2020	0
R000595	2020	15
S000320	2020	0
J000300	2020	69
H001042	2020	92
S001194	2020	100
H001089	2020	0
B000575	2020	15
B001243	2020	0
A000360	2020	15
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
M001197	2020	15
S001191	2020	62
B001261	2020	0
E000285	2020	0
P000612	2020	8
L000594	2020	8
V000128	2020	92
C000141	2020	92
C001098	2020	0
C001056	2020	8
D000618	2020	15
T000464	2020	92
S001198	2020	8
M001153	2020	15
B001267	2020	77
G000562	2020	15
H001061	2020	15
C001096	2020	15
D000563	2020	92
D000622	2020	92
U000039	2020	92
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
R000307	2020	15
R000605	2020	0
T000250	2020	8
L000174	2020	92
S000033	2020	62
\.


--
-- Name: hoa_events hoa_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hoa_events
    ADD CONSTRAINT hoa_events_pkey PRIMARY KEY (id);


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

