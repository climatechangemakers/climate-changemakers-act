import { useMutation } from "react-query";
import { fetcher } from "../helper";
import { ExistingIssue } from "../Types/Issue";

export default function useUpdateIssueMutation() {
    return useMutation<
        ExistingIssue | undefined,
        Error,
        ExistingIssue,
        unknown
    >((formData: ExistingIssue) =>
        fetcher<ExistingIssue | undefined, ExistingIssue>(
            `issues/${formData.id}`,
            formData,
            "PUT"
        )
    );
}
