import { Nav, Navbar } from "react-bootstrap";
import ProgressNavLink from "./ProgressNavLink";

type Props = {
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
}

export default function DesktopProgressBar({ isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    const linkState = (isComplete: boolean, isActive: boolean) =>
        isComplete ? "complete"
            : isActive ? "active"
                : "disabled"
    return (
        <Navbar variant="dark" className="ps-1">
            <Nav className="d-flex flex-column fs-6">
                <ProgressNavLink href="#introduction" state={"active"} step={1}>
                    Introduction
                </ProgressNavLink>
                <ProgressNavLink href="#send_an_email" state={linkState(isEmailSent, true)} step={2}>
                    Send an Email
                </ProgressNavLink>
                <ProgressNavLink href="#make_a_phone_call" state={linkState(isPhoneCallMade, isEmailSent)} step={3}>
                    Make Phone Call
                </ProgressNavLink>
                <ProgressNavLink href="#post_on_social" state={linkState(isSocialPosted, isPhoneCallMade)} step={4}>
                    Post on Social
                </ProgressNavLink>
            </Nav>
        </Navbar>
    )
}