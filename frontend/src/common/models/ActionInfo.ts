export type ActionInfo = {
    initiatorEmail: string;
    legislators: Legislator[];
}

export type Legislator = {
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
    name: string;
    phoneNumbers: string[];
    role: string;
    siteUrl: string;
}