import { Button } from "react-bootstrap";

type Props = {
    issue: string | undefined;
    setIssue: (issue: string) => void;
}

export default function PickYourIssue({ issue, setIssue }: Props) {
    return (
        <div className="pt-2 pb-3">
            <h2 className="text-start pb-3 step-height">Pick Your Issue</h2>
            <Button className="d-flex me-auto" onClick={() => setIssue("Issue XYZ")} disabled={!!issue}>Issue XYZ</Button>
        </div>
    )
}