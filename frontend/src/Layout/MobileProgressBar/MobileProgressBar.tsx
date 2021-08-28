import { ProgressBar } from "react-bootstrap";
import styles from "./MobileProgressBar.module.css"
import ProgressLabel from "./ProgressLabel";

type Props = {
    isActionInfo: boolean;
    isIssue: boolean;
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
}

export default function MobileProgressBar({ isActionInfo, isIssue, isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    const percentDone = (+isActionInfo + +isIssue + +isEmailSent + +isPhoneCallMade + +isSocialPosted) * 20;
    return (
        <div className="position-relative">
            <ProgressBar variant="success" now={percentDone} />
            <div className={`${styles.stepsContainer} d-flex justify-content-between fs-6`}>
                <ProgressLabel completed={isActionInfo}>Form</ProgressLabel>
                <ProgressLabel completed={isIssue}>Issue</ProgressLabel>
                <ProgressLabel completed={isEmailSent}>Email</ProgressLabel>
                <ProgressLabel completed={isPhoneCallMade}>Call</ProgressLabel>
                <ProgressLabel completed={isSocialPosted}>Social</ProgressLabel>
            </div>
        </div>
    )
}