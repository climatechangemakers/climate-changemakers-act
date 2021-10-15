# Climate Changemakers Act

Climate Changemakers is developing a tool which allows constituents to reach out to their legislators in an easy and effective way. Studies have shown that pre-created messaging to legislators is not nearly as effective as custom messaging, but custom messaging is hard! Climate Changemakers Act aims to make this easy by: 

1. Looking up a Changemaker's legislators
2. Providing a League of Conservation Voters score for their legislators
3. Guiding a Changemaker through creating personalized messaging
4. Making it easy to call, tweet, and email legislators. 

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
