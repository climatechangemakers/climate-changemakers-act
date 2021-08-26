import { Nav, Navbar } from "react-bootstrap";
import ProgressNavLink from "./ProgressNavLink";

type Props = {
    isActionInfo: boolean;
    isIssue: boolean;
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
}

export default function DesktopProgressBar({ isActionInfo, isIssue, isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    const linkState = (isComplete: boolean, isActive: boolean) =>
        isComplete ? "complete"
            : isActive ? "active"
                : "disabled"
    return (
        <Navbar variant="dark" className="ps-1">
            <Nav className="d-flex flex-column fs-6">
                <ProgressNavLink href="#find_your_reps" state={linkState(isActionInfo, !isIssue)} step={1}>
                    Find Your Reps
                </ProgressNavLink>
                <ProgressNavLink href="#pick_your_issue" state={linkState(isIssue, isActionInfo)} step={2}>
                    Pick an Issue
                </ProgressNavLink>
                <ProgressNavLink href="#take_action" state={linkState(isEmailSent && isPhoneCallMade && isSocialPosted, isIssue)} step={3}>
                    Take Action
                </ProgressNavLink>
                <Nav className="d-flex flex-column ps-4">
                    <ProgressNavLink href="#send_an_email" state={linkState(isEmailSent, isIssue)}>
                        Send an Email
                    </ProgressNavLink>
                    <ProgressNavLink href="#make_a_phone_call" state={linkState(isPhoneCallMade, isIssue && isEmailSent)}>
                        Make Phone Call
                    </ProgressNavLink>
                    <ProgressNavLink href="#post_on_social" state={linkState(isSocialPosted, isIssue && isEmailSent && isPhoneCallMade)}>
                        Post on Social
                    </ProgressNavLink>
                </Nav>
            </Nav>
        </Navbar>
    )
}