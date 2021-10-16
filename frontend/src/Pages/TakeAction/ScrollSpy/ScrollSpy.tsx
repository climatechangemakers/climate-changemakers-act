import cx from "classnames";
import { useEffect, useState } from "react";
import { Card } from "react-bootstrap";
import Step from "./Step";

type Props = {
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
    desktop?: boolean;
};

export default function ScrollSpy({ isEmailSent, isPhoneCallMade, isSocialPosted, desktop = false }: Props) {
    const [scrollY, setScrollY] = useState(0);
    const [scrolledPastTop, setScrolledPastTop] = useState(false);

    useEffect(() => {
        if (scrollY > 0) setScrolledPastTop(true);
    }, [scrollY]);

    useEffect(() => {
        const handleScroll = () =>
            setScrollY(window.scrollY);

        handleScroll();
        window.addEventListener("scroll", handleScroll);
        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    const linkState = (isComplete: boolean, isActive: boolean) =>
        isComplete ? "complete" : isActive ? "active" : "disabled";

    return (
        <Card
            className={cx({
                "position-fixed border-0 mt-4": desktop,
                "ms-3 me-3": !desktop,
            })}
        >
            <Card.Body
                className={cx("d-flex justify-content-between text-dark pt-3 pb-2 ps-0 pe-0", {
                    "flex-column bg-dark-purple text-white": desktop,
                })}
            >
                {desktop && (
                    <h3 className="mb-0">
                        <span className="ms-4 ps-3" />
                        Steps
                    </h3>
                )}
                <Step step={1} id="#introduction" state={linkState(scrolledPastTop, true)} desktop={desktop}>
                    {desktop ? "Introduction" : "Intro"}
                </Step>
                <Step step={2} id="#send_an_email" state={linkState(isEmailSent, true)} desktop={desktop}>
                    {desktop ? "Send an Email" : "Email"}
                </Step>
                <Step
                    step={3}
                    id="#make_a_phone_call"
                    state={linkState(isPhoneCallMade, isEmailSent)}
                    desktop={desktop}
                >
                    {desktop ? "Make a Call" : "Call"}
                </Step>
                <Step
                    step={4}
                    id="#post_on_social"
                    state={linkState(isSocialPosted, isPhoneCallMade)}
                    last
                    desktop={desktop}
                >
                    {desktop ? "Post on Social" : "Post"}
                </Step>
            </Card.Body>
        </Card>
    );
}
