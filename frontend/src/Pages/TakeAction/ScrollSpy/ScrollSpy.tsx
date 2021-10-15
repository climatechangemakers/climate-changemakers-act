import { Col, Container } from "react-bootstrap";
import DesktopProgressBar from "./DesktopScrollSpy/DesktopScrollSpy";
import MobileProgressBar from "./MobileScrollSpy/MobileScrollSpy";
import styles from "./ScrollSpy.module.css";

type Props = {
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
};

export default function ScrollSpy({ isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    return (
        <>
            <Col xs="12" md="2" className="d-none d-lg-block">
                <MobileProgressBar
                    isEmailSent={isEmailSent}
                    isPhoneCallMade={isPhoneCallMade}
                    isSocialPosted={isSocialPosted}
                    desktop
                />
            </Col>
            <div className={`d-block d-lg-none w-100 position-fixed ${styles.mobileProgress}`}>
                <MobileProgressBar
                    isEmailSent={isEmailSent}
                    isPhoneCallMade={isPhoneCallMade}
                    isSocialPosted={isSocialPosted}
                />
            </div>
        </>
    );
}
