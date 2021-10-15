import { Card, ProgressBar } from "react-bootstrap";
import styles from "./MobileScrollSpy.module.css";
import Step from "./Step";

type Props = {
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
    desktop?: boolean;
};

export default function MobileScrollSpy({ isEmailSent, isPhoneCallMade, isSocialPosted, desktop = false }: Props) {
    const linkState = (isComplete: boolean, isActive: boolean) =>
        isComplete ? "complete" : isActive ? "active" : "disabled";

    return (
        <Card className={desktop ? "position-fixed border-0" : "ms-3 me-3"}>
            <Card.Body className={`d-flex ${desktop ? "flex-column bg-dark-purple text-white" : ""} justify-content-between text-dark pt-3 pb-2 ps-0 pe-0`}>
                <Step
                    step={1}
                    state={linkState(isEmailSent, true)}
                    desktop={desktop}>
                    {desktop ? "Introduction" : "Intro"}
                </Step>
                <Step
                    step={2}
                    state={linkState(isEmailSent, true)}
                    desktop={desktop}>
                    {desktop ? "Send an Email" : "Email"}
                </Step>
                <Step
                    step={3}
                    state={linkState(isPhoneCallMade, isEmailSent)}
                    desktop={desktop}>
                    {desktop ? "Make a Call" : "Call"}
                </Step>
                <Step
                    step={4}
                    state={linkState(isSocialPosted, isPhoneCallMade)}
                    last
                    desktop={desktop}>
                    {desktop ? "Post on Social" : "Post"}
                </Step>
            </Card.Body>
        </Card>
    );
}
