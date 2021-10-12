import { Container } from "react-bootstrap";
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
        <div className="flex-grow-1 d-flex justify-content-center">
            <div className="position-fixed d-none d-lg-block">
                <DesktopProgressBar
                    isEmailSent={isEmailSent}
                    isPhoneCallMade={isPhoneCallMade}
                    isSocialPosted={isSocialPosted}
                />
            </div>
            <div className={`d-block d-lg-none w-100 position-fixed ${styles.mobileProgress}`}>
                <MobileProgressBar
                    isEmailSent={isEmailSent}
                    isPhoneCallMade={isPhoneCallMade}
                    isSocialPosted={isSocialPosted}
                />
            </div>
        </div>
    );
}
