import { useMutation } from "react-query";
import { fetcher } from "../helper";
import { Bill } from "../Types/Bill";

export default function useCreateBillMutation() {
    return useMutation<undefined, Error, Bill>((formData) =>
        fetcher("bills", formData)
    );
}
