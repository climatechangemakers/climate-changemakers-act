import { useQuery } from "react-query";
import { fetcher } from "../helper";
import { ExisitingBill } from "../Types/Bill";

export default function useBillsQuery() {
    return useQuery("bills", () => fetcher<ExisitingBill[]>("bills"));
}
