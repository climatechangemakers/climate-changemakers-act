import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { FormInfo } from "common/models/FormInfo";
import useSWR from "swr";

export default function useMembershipInfo(formInfo: FormInfo | undefined) {
    const { data, error } = useSWR<{ isMember: boolean }, ErrorResponse>(
        [!formInfo?.email ? null : "/check-membership", JSON.stringify({ email: formInfo?.email ?? "" })],
        fetcher,
        {
            revalidateIfStale: false,
            revalidateOnFocus: false,
            revalidateOnReconnect: false,
        }
    );
    return { data, error };
}
