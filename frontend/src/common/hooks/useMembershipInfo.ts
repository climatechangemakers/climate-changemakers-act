import { ErrorResponse, fetcher } from "common/api/ClimateChangemakersAPI";
import { FormInfo } from "common/models/FormInfo";
import useSWRImmutable from "swr/immutable";

export default function useMembershipInfo(formInfo: FormInfo | undefined) {
    return {
        ...useSWRImmutable<{ isMember: boolean }, ErrorResponse>(
            [!formInfo?.email ? null : "/check-membership", JSON.stringify({ email: formInfo?.email ?? "" })],
            fetcher
        ),
    };
}
