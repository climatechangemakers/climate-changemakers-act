import { preComposedTweetAPI, logTweetAPI } from "common/api/ClimateChangemakersAPI";
import Layout from "common/Components/Layout";
import useSessionStorage from "common/hooks/useSessionStorage";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/IssuesResponse";
import type Loadable from "common/lib/Loadable";
import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { Redirect } from "react-router-dom";
import AllDone from "./AllDone/AllDone";
import MakeAPhoneCall from "./MakeAPhoneCall";
import MeetYourReps from "./MeetYourReps/MeetYourReps";
import PostOnSocial from "./PostOnSocial";
import ScrollSpy from "./ScrollSpy/ScrollSpy";
import SendAnEmail from "./SendAnEmail/SendAnEmail";

export default function TakeActionPage() {
    const [isEmailSent, setIsEmailSent] = useState(false);
    const [isPhoneCallMade, setIsPhoneCallMade] = useState(false);
    const [isSocialPosted, setIsSocialPosted] = useState(false);
    const [selectedIssue] = useSessionStorage<Issue | undefined>("selectedIssue");
    const [actionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const [preComposedTweet, setPreComposedTweet] = useState<Loadable<string, string>>({ status: "loading" });

    // `scrollIntoView` doesn't work when the window is backgrounded, so we have some complex logic to enqueue a scroll if the window is invisible.
    const [idToScrollTo, setIdToScrollTo] = useState<undefined | string>();
    useEffect(() => {
        if (!idToScrollTo) {
            return;
        }

        const scroll = () => {
            document.getElementById(idToScrollTo)?.scrollIntoView();
            setIdToScrollTo(undefined);
        };

        if (!document.hidden) {
            scroll();
            return;
        }

        const onVisibilityChange = () => {
            if (document.hidden) {
                return;
            }
            clearScrollTimeout();
            scrollTimeout = setTimeout(() => {
                removeEventListener();
                scroll();
            }, 500);
        };

        let scrollTimeout: undefined | ReturnType<typeof setTimeout>;
        const clearScrollTimeout = () => {
            if (scrollTimeout !== undefined) {
                clearTimeout(scrollTimeout);
            }
        };

        document.addEventListener('visibilitychange', onVisibilityChange, false);
        const removeEventListener = document.removeEventListener.bind(document, 'visibilitychange', onVisibilityChange, false);

        return () => {
            removeEventListener();
            clearScrollTimeout();
        };
    }, [idToScrollTo]);

    useEffect(() => { isEmailSent && setIdToScrollTo("make_a_phone_call") }, [isEmailSent])
    useEffect(() => { isPhoneCallMade && setIdToScrollTo("post_on_social") }, [isPhoneCallMade])
    useEffect(() => { isSocialPosted && setIdToScrollTo("all_done") }, [isEmailSent, isPhoneCallMade, isSocialPosted])

    const selectedIssueId = selectedIssue?.id;
    useEffect(() => {
        if (!selectedIssueId) {
            return;
        }

        setPreComposedTweet({ status: "loading" });

        let isCanceled = false;
        (async () => {
            const response = await preComposedTweetAPI(selectedIssueId);
            if (isCanceled) {
                return;
            }
            if (response.successful && response.data) {
                setPreComposedTweet({
                    status: "loaded",
                    value: response.data.tweet,
                });
            } else {
                setPreComposedTweet({
                    status: "failed",
                    error: response.error ?? "Failed to fetch precomposed tweet",
                });
            }
        })();
        return () => {
            isCanceled = true;
        };
    }, [selectedIssueId]);

    if (!actionInfo)
        return <Redirect to="/" />

    if (!selectedIssue)
        return <Redirect to="/pick-your-issue" />

    return (
        <Layout>
            <Row className="d-flex">
                <Col md="10" xs="12">
                    <h1 className="text-start mb-4 pb-2 pt-4" id="introduction">Time to get started!</h1>
                    <MeetYourReps actionInfo={actionInfo} />
                    <hr id="send_an_email" />
                    <SendAnEmail
                        email={actionInfo.initiatorEmail}
                        selectedIssue={selectedIssue}
                        isEmailSent={isEmailSent}
                        setIsEmailSent={setIsEmailSent} />
                    {isEmailSent &&
                        <>
                            <hr id="make_a_phone_call" />
                            <MakeAPhoneCall isPhoneCallMade={isPhoneCallMade} setIsPhoneCallMade={setIsPhoneCallMade} />
                        </>}
                    {isPhoneCallMade &&
                        <>
                            <hr id="post_on_social" />
                            <PostOnSocial
                                isSocialPosted={isSocialPosted}
                                setIsSocialPosted={setIsSocialPosted}
                                preComposedTweet={preComposedTweet}
                                logTweet={() => {
                                    const { initiatorEmail: email, legislators } = actionInfo;
                                    const bioguideIds = legislators.map(l => l.bioguideId);
                                    logTweetAPI(email, selectedIssue.id, bioguideIds).catch(err => {
                                        // It's okay if this request fails.
                                        console.warn(err);
                                    });
                                }}
                            />
                        </>}
                    {isSocialPosted &&
                        <>
                            <hr id="all_done" />
                            <AllDone />
                        </>}
                </Col>
                <Col md="2" xs="12">
                    <ScrollSpy
                        isEmailSent={isEmailSent}
                        isPhoneCallMade={isPhoneCallMade}
                        isSocialPosted={isSocialPosted}
                    />
                </Col>
            </Row>
        </Layout>
    );
}
