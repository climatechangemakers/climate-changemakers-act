import { Button } from "react-bootstrap";

type Props = {
    isPhoneCallMade: boolean;
    setIsPhoneCallMade: (bool: boolean) => void;
}

export default function MakeAPhoneCall({ isPhoneCallMade, setIsPhoneCallMade }: Props) {
    return (
        <div className="pt-2 pb-3 step-height">
            <h3 className="text-start pb-3">Make a Phone Call</h3>
            <Button className="d-flex me-auto" disabled={isPhoneCallMade} onClick={() => setIsPhoneCallMade(true)}>Done With Calls</Button>
        </div>
    )
}