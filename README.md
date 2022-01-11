# Climate Changemakers Act

Climate Changemakers is developing a tool which allows constituents to reach out to their legislators in an easy and effective way. Studies have shown that pre-created messaging to legislators is not nearly as effective as custom messaging, but custom messaging is hard! Climate Changemakers Act aims to make this easy by: 

1. Looking up a Changemaker's legislators
2. Providing a League of Conservation Voters score for their legislators
3. Guiding a Changemaker through creating personalized messaging
4. Making it easy to call, tweet, and email legislators. 

## Database Schema 

### Issue

The `issue` table entries are currently entered manually into the database and are regularly used as foreign keys in database relations. Their structure looks like the following. 

| id | title | precomposed_tweet_template | image_url | description | 
| -- | --    | --                         | --        | --          |
| BIGINT NOT NULL PRIMARY KEY  | VARCHAR NOT NULL | VARCHAR NOT NULL | VARCHAR NOT NULL | VARCHAR NOT NULL |

### Focus Issue 

The `focus_issue` table simply tracks issues that are focus, and when they were focused. Therefore, it serves both to track the currently focused issue as well as the history of all focused issues. 

| issue_id | focused_at |
| --       | --         |
| BIGINT NOT NULL foreign key on `issue` | TIMESTAMP NOT NULL |

### Talking Points

Taling points are relevant only to a single issue. This is where content for an issue briefing is stored. 

| id | title | issue_id | content | relative_order_position | 
| -- | --    | --       | --      | --                      |
| BIGINT NOT NULL PRIMARY KEY | VARCHAR NOT NULL | BIGINT NOT NULL foreign key on `issue` | VARCHAR NOT NULL (formatted as valid HTML) | INTEGER NOT NULL | 

In a set of talking points for a given issue, the `relative_order_position` must be unique. This is the mechanism by which we order talking points within an issue briefing. 

### Member of Congress 

The `member_of_congress` table represents canonical information about Congressional representatives. 

| bioguide_id | full_name | legislative_role | state | congressional_district | party | dc_phone_number | twitter_handle | cwc_office_code |
| --          | --        | --               | --    |  --                    | --    |  --             |  --            | --              |
| VARCHAR NOT NULL PRIMARY KEY | VARCHAR NOT NULL | VARCHAR NOT NULL (valid entires sen, rep) | VARCHAR NOT NULL | SMALLINT (null only for senators) | VARCHAR NOT NULL | VARCHAR NOT NULL | VARCHAR NOT NULL | VARCHAR (null only for those unenrolled in CWC) | 

Unlike other entities in our database, we do not generate unique identifiers for members of congress. Congress already has several ways of uniquely identifying members, the most popular of which is `bioguide_id`. For example, the bioguide for Donald McEachin is `M001200`. Bioguides are used regularly as foreign keys within our database schema. 

Tangential to members of congress are their `district_office`. 

| bioguide_id | phone_number | lat | lng |
| --          | --           | --  | --  |
| VARCHAR NOT NULL (foreign key on `member_of_congress`) | VARCHAR | DOUBLE PRECISION | DOUBLE PRECISION | 


The district office, as of now, is primarily used for identifying district offices which are closest to the Changemaker contacting their memeber of congress. This is done by calculating the distance between the Chanegmaker's address and the district office's `lat`/`lng`. 

### LCV Scores

There are two types of LCV scores -- yearly and lifetime. 


#### `lcv_score_year`
| bioguide_id | score_year | score | 
| ---         |  --        |  --   |
| VARCHAR NOT NULL (foreign key on `member_of_congress`) | INTEGER NOT NULL | INTEGER NOT NULL |


#### `lcv_score_lifetime`

| bioguide_id | score | 
| ---         |  --   |
| VARCHAR NOT NULL (foreign key on `member_of_congress`) | INTEGER NOT NULL |

### Actions 

There are many actions that a Changemaker can take while contacting congress. These are the primary pieces of information that are useful when conducting constituent meetings as they indicate how important a given issue is to a congressperson's constituency. 

#### `action_initiate`

Indicates that a Changemaker has filled out the address form and made the network request to figure out who their members of congress are. 

| email | initiated_at | 
| --    | --           | 
| VARCHAR NOT NULL | TIMESTAMP NOT NULL | 

#### `action_contact_legislator`

This is a catch-all table where every contact action is recorded. It contains several foreign keys to other metadata tables.

| id | email | contacted_at | issue_id | contacted_bioguide_id | 
| -- | --    | --           | --       | --                    | 
| BIGINT PRIMARY KEY NOT NULL | VARCHAR NOT NULL | TIMESTAMP NOT NULL | BIGINT NOT NULL (foreign key on `issue`) | VARCHAR NOT NULL (foreign key on `member_of_congress`) | 

This table structure is useful for centralizing information common to all contact touch points. The metadata tables serve as extensions to this table should we ever need to track information specific to a certain contact method. 

#### `action_email_legislator`

| action_contact_legislator_id | 
| -- | 
| BIGINT NOT NULL (forein key on `action_contact_legislator`) 

#### `action_call_legislator`

| action_contact_legislator_id | 
| -- | 
| BIGINT NOT NULL (forein key on `action_contact_legislator`) 

#### `action_tweet_legislator`

| action_contact_legislator_id | 
| -- | 
| BIGINT NOT NULL (forein key on `action_contact_legislator`) 

#### `action_sign_up`

This tracks new recruits to Climate Changemakers who have signed up for our community as a result of using this tool. 

| email | signed_up_at | 
| --    | --           | 
| CITEXT NOT NULL | TIMESTAMP NOT NULL |

## Backend

### Required API Keys

For a local development environment to work, some API keys need to be set as environment variables. 

* [Geocodio API Key](https://www.geocod.io/)


### Docker

The backend, frontend, and database include a `Dockerfile`. You should have no need to install JDK/JRE/NODE/PostgresSQL if you're running the application through Docker. The local development process should be frictionless with `docker-compose`.

#### Setting up your environment

For the containers specified in `docker-compose.yml` to function properly, you'll need to define a `.env` file. Docker will read from this environment file while creating images and assign the correct environment variables as needed. 

The `.env` file needs to have the following keys defined. 

```
GEOCODIO_API_KEY=<API_KEY>
POSTGRES_PASSWORD=<PASSWORD>
POSTGRES_USER=<USER>
POSTGRES_DB=<DB_NAME>
POSTGRES_HOSTNAME=database
POSTGRES_PORT=5432
REACT_APP_PORT=3000
KOTLIN_PORT=8080
SCWC_API_KEY=<API_KEY>
SCWC_URL=<URL>
HCWC_API_KEY=<API_KEY>
HCWC_URL=<URL>
```

After that's done, you can initialize your environment. 

```shell
$ docker-compose up -d
```

### Initiating action 

`POST /initiate-action`

#### Request

```js
{
  "email": string, // a valid email, required
  "streetAddress": string, // a valid street address, required
  "city": string, // a valid city
  "state": string, // a valid 2 letter state code, required
  "postalCode": string, // a valid postal code, required 
  "consentToTrackImpact": boolean, // required
  "desiresInformationEmails": boolean // optional
}
```

#### Response

```js
{
    "initiatorEmail": string, // email of initiator, required
    "legislators": [
        {
            "name": string, // name of legislator, required
            "role": "string", // either "U.S. Senator or U.S. Representative, required
            "siteUrl": string, // legislator site, required
            "phone": string, // office phone number, required
            "imageUrl": string, // URL to image of legislator, optional
            "lcvScores": [ // League of Conservation Voters scores. At least 1 required
                {
                    "score": int, // score of legislator, required
                    "scoreType": {
                        "type": "lifetime" // either lifetime or yearly. required
                    }
                },
                {
                    "score": int,
                    "scoreType": {
                        "type": "year",
                        "year": int // The year this LCV score applies to. Required if type == "year"
                    }
                },
            ]
        },
    ]
}
```
