import { Nav, Navbar } from 'react-bootstrap';
import ProgressNavLink from './ProgressNavLink';

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
                <ProgressNavLink href="#find_your_reps" state={"active"} step={1}>
                    Introduction
                </ProgressNavLink>
                <ProgressNavLink href="#pick_your_issue" state={"active"} step={2}>
                    Write Your Why
                </ProgressNavLink>
                <ProgressNavLink href="#take_action" state={linkState(isSocialPosted, true)} step={3}>
                    Take Action
                </ProgressNavLink>
                <ProgressNavLink href="#send_an_email" state={linkState(isEmailSent, true)}>
                    Send an Email
                </ProgressNavLink>
                <ProgressNavLink href="#make_a_phone_call" state={linkState(isPhoneCallMade, isEmailSent)}>
                    Make Phone Call
                </ProgressNavLink>
                <ProgressNavLink href="#post_on_social" state={linkState(isSocialPosted, isPhoneCallMade)}>
                    Post on Social
                </ProgressNavLink>
            </Nav>
        </Navbar>
    )
}