export type ActionInfo = {
    initiatorEmail: string;
    legislators: {
        imageUrl: string;
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
        phone: string;
        role: string;
        siteUrl: string;
    }[];
}