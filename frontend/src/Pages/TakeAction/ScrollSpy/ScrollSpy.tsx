import cx from "classnames";
import { useEffect, useState } from "react";
import { Card } from "react-bootstrap";
import Step from "./Step";

type Props = {
    isEmailDone: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
    isAmplified: boolean;
    isMember?: boolean;
    desktop?: boolean;
};

const INTRO_SCROLL_BUFFER = 200;

export default function ScrollSpy({
    isEmailDone,
    isPhoneCallMade,
    isSocialPosted,
    isAmplified,
    desktop = false,
}: Props) {
    const [introSectionDistanceFromTop, setIntroSectionDistanceFromTop] = useState(Math.min);
    const [scrolledPastIntro, setScrolledPastIntro] = useState(false);

    useEffect(() => {
        if (introSectionDistanceFromTop - INTRO_SCROLL_BUFFER <= 0) setScrolledPastIntro(true);
    }, [introSectionDistanceFromTop]);

    useEffect(() => {
        const handleResize = () =>
            setIntroSectionDistanceFromTop(
                document.getElementById("send_an_email")?.getBoundingClientRect()?.top ?? Math.min
            );

        handleResize();
        window.addEventListener("scroll", handleResize);
        window.addEventListener("resize", handleResize);
        return () => {
            window.removeEventListener("scroll", handleResize);
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    const linkState = (isComplete: boolean, isActive: boolean) =>
        isComplete ? "complete" : isActive ? "active" : "disabled";

    return (
        <Card
            className={cx({
                "position-fixed border-0 mt-4": desktop,
                "ms-2 me-2": !desktop,
            })}
        >
            <Card.Body
                className={cx("d-flex justify-content-between text-dark pt-3 pb-2 ps-0 pe-0", {
                    "flex-column bg-dark-purple text-white": desktop,
                    "bg-light-grey": !desktop,
                })}
            >
                {desktop && (
                    <h3 className="mb-0">
                        <span className="ms-4 ps-3" />
                        Steps
                    </h3>
                )}
                <Step step={1} id="#introduction" state={linkState(scrolledPastIntro, true)} desktop={desktop}>
                    {desktop ? "Introduction" : "Intro"}
                </Step>
                <Step step={2} id="#send_an_email" state={linkState(isEmailDone, true)} desktop={desktop}>
                    {desktop ? "Send an Email" : "Email"}
                </Step>
                <Step
                    step={3}
                    id="#make_a_phone_call"
                    state={linkState(isPhoneCallMade, isEmailDone)}
                    desktop={desktop}
                >
                    {desktop ? "Make a Call" : "Call"}
                </Step>
                <Step
                    step={4}
                    id="#post_on_social"
                    state={linkState(isSocialPosted, isPhoneCallMade)}
                    desktop={desktop}
                >
                    {desktop ? "Post on Social" : "Post"}
                </Step>
                <Step
                    step={5}
                    id="#join_our_mission"
                    state={linkState(isAmplified, isSocialPosted)}
                    desktop={desktop}
                    last
                >
                    Amplify
                </Step>
            </Card.Body>
        </Card>
    );
}
