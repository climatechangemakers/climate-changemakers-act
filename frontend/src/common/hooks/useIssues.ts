import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { Issue } from "common/models/Issue";
import useSWRImmutable from "swr/immutable";

export default function useIssues() {
    return {
        ...useSWRImmutable<{ focusIssue: Issue; otherIssues: Issue[] }, ErrorResponse>("/issues", fetcher),
    };
}
