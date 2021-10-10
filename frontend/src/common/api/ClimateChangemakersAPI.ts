import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";

type FetchResponse<T> = {
    successful: boolean;
    error?: string;
    data?: T;
}

export const fetcher = async <T>(path: string) =>
    (await (await fetch("/api" + path)).json() as T);

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

export const sendEmailAPI = (
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
    contactedBioguideIds: string[]) =>
    post<void>("/send-email",
        {
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
            contactedBioguideIds
        });