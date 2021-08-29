import { Button } from "react-bootstrap";

type Props = {
    isSocialPosted: boolean;
    setIsSocialPosted: (bool: boolean) => void;
}

export default function PostOnSocial({ isSocialPosted, setIsSocialPosted }: Props) {
    return (
        <div className="pt-2 pb-3">
            <h3 className="text-start pb-3 step-height">Post on Social</h3>
            <Button className="d-flex me-auto" disabled={isSocialPosted} onClick={() => setIsSocialPosted(true)}>Tweet Now</Button>
        </div>
    )
}