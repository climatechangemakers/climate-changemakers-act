import { useQuery } from "react-query";
import { fetcher } from "../helper";
import { Bill } from "../Types/Bill";

export default function useBillsQuery() {
    return useQuery("bills", () =>
        fetcher<Array<Bill & { id: number }>>("bills")
    );
}
