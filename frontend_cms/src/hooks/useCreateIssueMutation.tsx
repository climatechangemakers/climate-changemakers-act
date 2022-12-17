import { useMutation } from "react-query";
import { fetcher } from "../helper";
import { ExistingIssue, IssueInfo } from "../Types/Issue";

export default function useCreateIssueMutation() {
    return useMutation<ExistingIssue | undefined, Error, IssueInfo>(
        (formData: IssueInfo) =>
            fetcher<ExistingIssue | undefined, IssueInfo>("issues", formData)
    );
}
