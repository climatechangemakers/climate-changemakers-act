import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";

type FetchResponse<Data, Error = string> = {
    successful: boolean;
    error?: Error;
    data?: Data;
};

export type ErrorResponse = Error & {
    status: number;
};

export const fetcher = async <T>(path: string) => {
    const res = await fetch("/api" + path);

    if (!res.ok) {
        const error = new Error(`Failed to fetch ${path}: ${res.statusText}`) as ErrorResponse;
        error.status = res.status;

        throw error;
    }

    return (await res.json()) as T;
};

const parseFetch = async <Data, Error = string>(response: Response): Promise<FetchResponse<Data, Error>> => {
    try {
        if (!response.ok) return { successful: false, error: (await response.json()) as Error };

        if (response.status === 204) return { successful: true };

        return {
            successful: true,
            data: (await response.json()) as Data,
        };
    } catch (e: any) {
        return { successful: false, error: e?.message };
    }
};

const post = async <Data, Error = string>(path: string, content: Object): Promise<FetchResponse<Data, Error>> =>
    parseFetch(
        await fetch("/api" + path, {
            method: "POST",
            headers: {
                "content-type": "application/json;charset=UTF-8",
            },
            body: JSON.stringify(content),
        })
    );

const retryThreeTimes = async <Data, Error = string>(fetch: () => Promise<FetchResponse<Data, Error>>) => {
    let response = await fetch();
    if (!response.successful) {
        for (var i = 0; i < 3; i++) {
            response = await fetch();
            if (response.successful) return response;
        }
    }
    return response;
};

export const initiateActionAPI = (form: FormInfo) =>
    post<ActionInfo>("/initiate-action", {
        email: form.email,
        streetAddress: form.streetAddress,
        city: form.city,
        state: form.state,
        postalCode: form.postalCode,
        consentToTrackImpact: form.hasTrackingConsent,
        desiresInformationalEmails: form.hasEmailingConsent,
    });

export const sendEmailAPI = async (
    originatingEmailAddress: string,
    title: string,
    firstName: string,
    lastName: string,
    streetAddress: string,
    city: string,
    state: string,
    postalCode: string,
    relatedTopics: string[],
    emailSubject: string,
    emailBody: string,
    relatedIssueId: number,
    contactedBioguideIds: string[]
) =>
    retryThreeTimes(() =>
        post<void, string | { failedBioguideIds: string[] }>("/send-email", {
            originatingEmailAddress,
            title,
            firstName,
            lastName,
            streetAddress,
            city,
            state,
            postalCode,
            relatedTopics,
            emailSubject,
            emailBody,
            relatedIssueId,
            contactedBioguideIds,
        })
    );

export const logTweetAPI = (
    originatingEmailAddress: string,
    relatedIssueId: number,
    contactedBioguideIds: ReadonlyArray<string>
) => post<void>("/log-tweet", { originatingEmailAddress, relatedIssueId, contactedBioguideIds });

export const logCallAPI = (
    originatingEmailAddress: string,
    relatedIssueId: number,
    contactedPhoneNumber: string,
    contactedBioguideId: string
) => post<void>("/log-call", { originatingEmailAddress, relatedIssueId, contactedPhoneNumber, contactedBioguideId });
