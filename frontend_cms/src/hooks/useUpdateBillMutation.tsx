import { useMutation } from "react-query";
import { ExisitingBill } from "Types/Bill";
import { fetcher } from "../helper";

export default function useUpdateBillMutation() {
    return useMutation<
        ExisitingBill | undefined,
        Error,
        ExisitingBill,
        unknown
    >((formData: ExisitingBill) =>
        fetcher<ExisitingBill, ExisitingBill>(
            `/bills/${formData.id}`,
            formData,
            "PUT"
        )
    );
}
