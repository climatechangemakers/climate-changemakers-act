import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import useSWR from "swr";

export default function useAreas() {
    const { data, error } = useSWR<{ shortName: string; fullName: string }[], ErrorResponse>("/values/areas", fetcher, {
        revalidateIfStale: false,
        revalidateOnFocus: false,
        revalidateOnReconnect: false,
    });
    return { data, error };
}
