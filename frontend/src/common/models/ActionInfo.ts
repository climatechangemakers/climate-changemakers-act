export type ActionInfo = {
    initiatorEmail: string;
    legislators: Legislator[];
}

export type Legislator = {
    name: string;
    bioguideId: string;
    imageUrl: string;
    twitter: string;
    area: {
        state: string;
        districtNumber?: number;
    };
    partyAffiliation: string;
    lcvScores: {
        score: number;
        scoreType: {
            type: "lifetime";
        } | {
            type: "year";
            year: number;
        };
    }[];
    phoneNumbers: string[];
    role: string;
    siteUrl: string;
}