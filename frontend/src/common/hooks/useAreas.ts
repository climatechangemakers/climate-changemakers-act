import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import useSWRImmutable from "swr/immutable";

export default function useAreas() {
    return {
        ...useSWRImmutable<{ shortName: string; fullName: string }[], ErrorResponse>("/values/areas", fetcher),
    };
}
