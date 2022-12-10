import { useMutation } from "react-query";
import { fetcher } from "../helper";

export default function useUpdateBillsForIssueMutation() {
    return useMutation<unknown, Error, { issueId: number; billIds: number[] }>(
        (formData) =>
            fetcher(`issues/${formData.issueId}/bills`, formData, "PUT")
    );
}
