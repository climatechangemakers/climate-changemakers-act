import { Button } from "react-bootstrap";

type Props = {
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
}

export default function SendAnEmail({ isEmailSent, setIsEmailSent }: Props) {
    return (
        <div className="pt-2 pb-3">
            <h3 className="text-start pb-3">Send An Email</h3>
            <Button className="d-flex me-auto" disabled={isEmailSent} onClick={() => setIsEmailSent(true)}>Send Email</Button>
        </div>
    )
}