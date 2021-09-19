export type ActionInfo = {
    initiatorEmail: string;
    legislators: {
        imageUrl: string;
        area: {
            state: string;
            districtNumber?: number;
            districtPhoneNumber?: string;
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
    }[];
}