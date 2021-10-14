import { fetcher } from "common/api/ClimateChangemakersAPI";
import Layout from "common/Components/Layout";
import useSessionStorage from "common/hooks/useSessionStorage";
import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { Redirect } from "react-router-dom";
import useSWR from "swr";
import AllDone from "./AllDone/AllDone";
import MakeAPhoneCall from "./MakeACall/MakeACall";
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
    const [formInfo] = useSessionStorage<FormInfo | undefined>("formInfo");

    const scrollToId = (id: string) => document.getElementById(id)?.scrollIntoView();

    useEffect(() => {
        isEmailSent && scrollToId("make_a_phone_call");
    }, [isEmailSent]);
    useEffect(() => {
        isPhoneCallMade && scrollToId("post_on_social");
    }, [isPhoneCallMade]);
    useEffect(() => {
        isSocialPosted && scrollToId("all_done");
    }, [isEmailSent, isPhoneCallMade, isSocialPosted]);

    const selectedIssueId = selectedIssue?.id;
    const { data: preComposedTweetData, error: preComposedTweetError } = useSWR<{ tweet: string }, string>(
        selectedIssueId === undefined || !actionInfo
            ? null
            : `/issues/${selectedIssueId}/precomposed-tweet?${actionInfo.legislators.map(
                  (l) => `&bioguideIds=${l.bioguideId}`
              )}`,
        fetcher
    );

    if (!actionInfo || !formInfo) return <Redirect to="/" />;

    if (!selectedIssue) return <Redirect to="/pick-your-issue" />;

    return (
        <Layout>
            <Row className="d-flex">
                <Col md="10" xs="12">
                    <h1 className="text-start mb-4 pb-2 pt-4" id="introduction">
                        Time to get started!
                    </h1>
                    <MeetYourReps actionInfo={actionInfo} />
                    <hr id="send_an_email" />
                    <SendAnEmail
                        actionInfo={actionInfo}
                        formInfo={formInfo}
                        selectedIssue={selectedIssue}
                        isEmailSent={isEmailSent}
                        setIsEmailSent={setIsEmailSent}
                    />
                    {isEmailSent && (
                        <>
                            <hr id="make_a_phone_call" />
                            <MakeAPhoneCall
                                actionInfo={actionInfo}
                                relatedIssueId={selectedIssue.id}
                                emailAddress={formInfo.email}
                                isPhoneCallMade={isPhoneCallMade}
                                setIsPhoneCallMade={setIsPhoneCallMade}
                            />
                        </>
                    )}
                    {isPhoneCallMade && (
                        <>
                            <hr id="post_on_social" />
                            <PostOnSocial
                                isSocialPosted={isSocialPosted}
                                setIsSocialPosted={setIsSocialPosted}
                                actionInfo={actionInfo}
                                selectedIssue={selectedIssue}
                                preComposedTweet={preComposedTweetData?.tweet}
                                preComposedTweetError={preComposedTweetError}
                            />
                        </>
                    )}
                    {isSocialPosted && (
                        <>
                            <hr id="all_done" />
                            <AllDone />
                        </>
                    )}
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
