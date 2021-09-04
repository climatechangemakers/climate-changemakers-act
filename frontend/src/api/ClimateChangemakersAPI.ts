import { ActionInfo } from "../models/ActionInfo";
import { IssuesResponse } from "../models/IssuesResponse";

const parseFetch = async<T>(response: Response): Promise<T | string> => {
    try {
        if (!response.ok)
            throw new Error("Failed to handle request");

        return await response.json() as Promise<T>;
    }
    catch (e) {
        return e.message;
    }
}

const post = async <T>(path: string, content: Object): Promise<T | string> =>
    parseFetch(
        await fetch("/api" + path, {
            method: 'POST',
            headers: {
                'content-type': 'application/json;charset=UTF-8'
            },
            body: JSON.stringify(content)
        })
    );

const get = async <T>(path: string): Promise<T | string> =>
    parseFetch(
        await fetch("/api" + path, {
            method: 'GET',
            headers: {
                'content-type': 'application/json;charset=UTF-8'
            },
        })
    );

export const initiateActionAPI = (email: string, streetAddress: string, city: string, state: string, postalCode: string, consentToTrackImpact: boolean, desiresInformationalEmails: boolean) =>
    post<ActionInfo>("/initiate-action", { email, streetAddress, city, state, postalCode, consentToTrackImpact, desiresInformationalEmails })

export const issueAPI = () =>
    get<IssuesResponse>("/issues")