import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { ActionInfo } from "common/models/ActionInfo";
import useSWRImmutable from "swr/immutable";

export default function usePrecomposedTweetData(
    selectedIssueId: number | undefined,
    actionInfo: ActionInfo | undefined
) {
    return {
        ...useSWRImmutable<{ tweet: string }, ErrorResponse>(
            selectedIssueId === undefined || !actionInfo?.legislators?.length
                ? null
                : `/issues/${selectedIssueId}/precomposed-tweet?${new URLSearchParams(
                      actionInfo.legislators.map((l) => ["bioguideIds", l.bioguideId])
                  ).toString()}`,
            fetcher
        ),
    };
}
