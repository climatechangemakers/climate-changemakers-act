import { ActionInfo } from '../models/ActionInfo';
import { IssuesResponse } from '../models/IssuesResponse';

type FetchResponse<T> = {
    successful: boolean;
    error?: string;
    data?: T;
}

const parseFetch = async<T>(response: Response): Promise<FetchResponse<T>> => {
    try {
        if (!response.ok)
            return { successful: false }

        if (response.status === 204)
            return { successful: true };

        return {
            successful: true,
            data: await response.json() as T
        }
    }
    catch (e: any) {
        return { successful: false, error: e?.message }
    }
}

const post = async <T>(path: string, content: Object): Promise<FetchResponse<T>> =>
    parseFetch(
        await fetch("/api" + path, {
            method: 'POST',
            headers: {
                'content-type': 'application/json;charset=UTF-8'
            },
            body: JSON.stringify(content)
        })
    );

const get = async <T>(path: string): Promise<FetchResponse<T>> =>
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

export const sendEmailAPI = (originatingEmailAddress: string, relatedIssueId: number, emailBody: string, contactedBioguideIds: string[]) =>
    post<null>("/send-email", { originatingEmailAddress, relatedIssueId, emailBody, contactedBioguideIds });

export const issueAPI = () =>
    get<IssuesResponse>("/issues")