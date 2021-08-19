const post = async <T>(path: string, content: Object): Promise<T | string> => {
    const response = await fetch(path, {
        method: 'POST',
        headers: {
            'content-type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(content)
    });

    try {
        if (!response.ok)
            throw new Error("Failed to handle request");

        return await response.json() as Promise<T>;
    }
    catch (e) {
        return e.message;
    }
}

export const initiateActionAPI = (email: string, streetAddress: string, city: string, state: string, postalCode: string) =>
    post<{
        initiatorEmail: string;
        legislators: {
            imageUrl: string;
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
        }[]
    }>("/initiate-action", { email, streetAddress, city, state, postalCode })
