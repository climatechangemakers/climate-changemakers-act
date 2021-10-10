import { ActionInfo } from "common/models/ActionInfo";
import { AreasResponse } from "common/models/Areas";
import { FormInfo } from "common/models/FormInfo";
import { IssuesResponse } from "common/models/IssuesResponse";
import { PreComposedTweetResponse } from "common/models/PreComposedTweetResponse";

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

export const initiateActionAPI = (form: FormInfo) =>
    post<ActionInfo>("/initiate-action", {
        email: form.email,
        streetAddress: form.streetAddress,
        city: form.city,
        state: form.state,
        postalCode: form.postalCode,
        consentToTrackImpact: form.hasTrackingConsent,
        desiresInformationalEmails: form.hasEmailingConsent
    })

export const sendEmailAPI = (originatingEmailAddress: string, relatedIssueId: number, emailBody: string, contactedBioguideIds: string[]) =>
    post<null>("/send-email", { originatingEmailAddress, relatedIssueId, emailBody, contactedBioguideIds });

export const issueAPI = () =>
    get<IssuesResponse>("/issues")

export const areasAPI = () =>
    get<AreasResponse>("/values/areas");

export const preComposedTweetAPI = (issueId: number) =>
    get<PreComposedTweetResponse>(`/issues/${issueId}/precomposed-tweet`)

export const logTweetAPI = (originatingEmailAddress: string, relatedIssueId: number, contactedBioguideIds: ReadonlyArray<string>) =>
    post<null>("/log-tweet", { originatingEmailAddress, relatedIssueId, contactedBioguideIds });
