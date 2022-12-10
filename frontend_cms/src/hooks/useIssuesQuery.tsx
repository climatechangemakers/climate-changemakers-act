import { useQuery } from "react-query";
import { fetcher } from "../helper";
import { ExistingIssue } from "../Types/Issue";

export default function useIssuesQuery() {
    return useQuery("issues", () => fetcher<Array<ExistingIssue>>("issues"));
}
