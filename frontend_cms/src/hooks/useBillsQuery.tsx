import { useQuery } from "react-query";
import { fetcher } from "../helper";
import { ExisitingBill } from "../Types/Bill";

export default function useBillsQuery() {
    const result = useQuery<ExisitingBill[] | undefined, Error>("bills", () =>
        fetcher<ExisitingBill[]>("bills")
    );
    return {
        ...result,
        data: result?.data?.sort((a, b) => a.id - b.id),
    };
}
