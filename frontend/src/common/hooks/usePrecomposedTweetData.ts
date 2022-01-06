import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { ActionInfo } from "common/models/ActionInfo";
import useSWR from "swr";

export default function usePrecomposedTweetData(
    selectedIssueId: number | undefined,
    actionInfo: ActionInfo | undefined
) {
    const { data, error } = useSWR<{ tweet: string }, ErrorResponse>(
        selectedIssueId === undefined || !actionInfo?.legislators?.length
            ? null
            : `/issues/${selectedIssueId}/precomposed-tweet?${new URLSearchParams(
                  actionInfo.legislators.map((l) => ["bioguideIds", l.bioguideId])
              ).toString()}`,
        fetcher,
        {
            revalidateIfStale: false,
            revalidateOnFocus: false,
            revalidateOnReconnect: false,
        }
    );
    return { data, error };
}
