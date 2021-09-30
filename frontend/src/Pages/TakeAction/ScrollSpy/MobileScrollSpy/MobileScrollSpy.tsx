import { ProgressBar } from "react-bootstrap";
import styles from "./MobileScrollSpy.module.css";
import ProgressLabel from "./ProgressLabel";

type Props = {
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
}

export default function MobileScrollSpy({ isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    const percentDone = (+isEmailSent + +isPhoneCallMade + +isSocialPosted) * 20;
    return (
        <div className="position-relative ms-2 me-2">
            <ProgressBar variant="success" now={percentDone} />
            <div className={`${styles.stepsContainer} d-flex justify-content-between fs-6`}>
                <ProgressLabel completed={true}>Introduction</ProgressLabel>
                <ProgressLabel completed={true}>Write Your Why</ProgressLabel>
                <ProgressLabel completed={isEmailSent}>Email</ProgressLabel>
                <ProgressLabel completed={isPhoneCallMade}>Call</ProgressLabel>
                <ProgressLabel completed={isSocialPosted}>Social</ProgressLabel>
            </div>
        </div>
    )
}