import { useQuery } from "react-query";
import { fetcher } from "../helper";
import { ExistingIssue } from "../Types/Issue";

export default function useIssuesQuery() {
    const result = useQuery<ExistingIssue[] | undefined, Error>("issues", () =>
        fetcher<Array<ExistingIssue>>("issues")
    );
    return {
        ...result,
        data: result?.data?.sort((a, b) => a.title.localeCompare(b.title)),
    };
}
