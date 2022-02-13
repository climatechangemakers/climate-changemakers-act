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

export const fetcher = async <T>(path: string, payload?: string) => {
    const res = await fetch("/api" + path, {
        method: payload ? "POST" : "GET",
        ...(payload && { body: payload }),
        headers: {
            accept: "application/json",
            "Content-Type": "application/json",
        },
    });

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

        if (response.status === 204 || response.status === 201) return { successful: true };

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
) => {
    let bioguidesToSend = contactedBioguideIds;
    for (let i = 0; i < 4; i++) {
        const response = await post<void, string | { failedBioguideIds: string[] }>("/send-email", {
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
            contactedBioguideIds: bioguidesToSend,
        });
        if (typeof response.error === "string")
            return {
                successful: false,
                error: response.error,
            };
        else if (response.error?.failedBioguideIds) bioguidesToSend = response.error?.failedBioguideIds;
        else
            return {
                successful: true,
            };
    }
    return { succesful: false, error: bioguidesToSend };
};

export const logTweetAPI = (
    originatingEmailAddress: string,
    relatedIssueId: number,
    contactedBioguideIds: ReadonlyArray<string>
) => post<void>("/log-tweet", { originatingEmailAddress, relatedIssueId, contactedBioguideIds });

export const logCallAPI = (originatingEmailAddress: string, relatedIssueId: number, contactedBioguideId: string) =>
    post<void>("/log-call", { originatingEmailAddress, relatedIssueId, contactedBioguideId });

export const signUpAPI = (
    email: string,
    firstName: string,
    lastName: string,
    state: string,
    postalCode: string,
    referral: string,
    actionReason: string,
    socialVerification: string,
    priorExperience: boolean
) =>
    post<void>("/sign-up", {
        email,
        firstName,
        lastName,
        state,
        postalCode,
        referral,
        actionReason,
        socialVerification,
        priorExperience,
    });
