# Climate Changemakers Act

Climate Changemakers is developing a tool which allows constituents to reach out to their legislators in an easy and effective way. Studies have shown that pre-created messaging to legislators is not nearly as effective as custom messaging, but custom messaging is hard! Climate Changemakers Act aims to make this easy by: 

1. Looking up a Changemaker's legislators
2. Providing a League of Conservation Voters score for their legislators
3. Guiding a Changemaker through creating personalized messaging
4. Making it easy to call, tweet, and email legislators. 

## Backend

### Required API Keys

* [Google Civic Information API Key](https://cloud.google.com/docs/authentication/api-keys)

### Docker

The backend & frontend include a `Dockerfile`. You should have no need to install JDK/JRE/NODE if you're running the application through Docker. 

```shell
$ docker build --file=backend/backend.dockerfile  -t backend .
$ docker build --file=frontend/frontend.dockerfile  -t frontend .
$ GOOGLE_CIVIC_API_KEY=<YOUR_API_KEY> docker-compose -f docker-compose.yml up
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
  "postalCode": string // a valid postal code, required 
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
