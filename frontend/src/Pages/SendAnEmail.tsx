import { Button } from "react-bootstrap";
import { Issue } from "../models/IssuesResponse";

type Props = {
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
    selectedIssue: Issue;
}

export default function SendAnEmail({ isEmailSent, setIsEmailSent, selectedEmail }: Props) {
    console.log(selectedEmail);

    return (
        <div className="pt-2 pb-3">
            <h3 className="text-start pb-3 step-height">Send An Email</h3>
            <Button className="d-flex me-auto" disabled={isEmailSent} onClick={() => setIsEmailSent(true)}>Send Email</Button>
        </div>
    )
}