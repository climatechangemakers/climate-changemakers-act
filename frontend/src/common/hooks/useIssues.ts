import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { Issue } from "common/models/Issue";
import useSWR from "swr";

export default function useIssues() {
    const { data, error } = useSWR<{ focusIssue: Issue; otherIssues: Issue[] }, ErrorResponse>("/issues", fetcher, {
        revalidateIfStale: false,
        revalidateOnFocus: false,
        revalidateOnReconnect: false,
    });
    return { data, error };
}
