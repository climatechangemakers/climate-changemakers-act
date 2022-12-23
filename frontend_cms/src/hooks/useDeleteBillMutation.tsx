import { useMutation } from "react-query";
import { fetcher } from "../helper";

export default function useDeleteBillMutation() {
    return useMutation<undefined, Error, number>((id: number) =>
        fetcher<undefined>(`/bills/${id}`, undefined, "DELETE")
    );
}
