import { useMutation } from "react-query";
import { fetcher } from "../helper";

export default function useDeleteIssueMutation() {
    return useMutation<undefined, Error, number>((id: number) =>
        fetcher<undefined>(`/issues/${id}`, undefined, "DELETE")
    );
}
