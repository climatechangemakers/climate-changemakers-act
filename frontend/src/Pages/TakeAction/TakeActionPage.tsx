import Layout from "common/Components/Layout";
import useSessionStorage from "common/hooks/useSessionStorage";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/IssuesResponse";
import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { Redirect } from "react-router-dom";
import AllDone from "./AllDone/AllDone";
import MakeAPhoneCall from "./MakeAPhoneCall";
import PostOnSocial from "./PostOnSocial";
import ScrollSpy from "./ScrollSpy/ScrollSpy";
import SendAnEmail from "./SendAnEmail/SendAnEmail";

export default function TakeActionPage() {
    const [isEmailSent, setIsEmailSent] = useState(false);
    const [isPhoneCallMade, setIsPhoneCallMade] = useState(false);
    const [isSocialPosted, setIsSocialPosted] = useState(false);
    const [selectedIssue] = useSessionStorage<Issue | undefined>("selectedIssue");
    const [actionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");

    const scrollToId = (id: string) =>
        document.getElementById(id)?.scrollIntoView()

    useEffect(() => { isEmailSent && scrollToId("make_a_phone_call") }, [isEmailSent])
    useEffect(() => { isPhoneCallMade && scrollToId("post_on_social") }, [isPhoneCallMade])
    useEffect(() => { isSocialPosted && scrollToId("all_done") }, [isEmailSent, isPhoneCallMade, isSocialPosted])

    if (!actionInfo)
        return <Redirect to="/" />

    if (!selectedIssue)
        return <Redirect to="/pick-your-issue" />

    return (
        <Layout>
            <Row className="d-flex">
                <Col md="10" xs="12">
                    <>
                        <hr id="take_action" />
                        <h2 id="send_an_email">Time to Take Action!</h2>
                        <SendAnEmail
                            email={actionInfo.initiatorEmail}
                            selectedIssue={selectedIssue}
                            isEmailSent={isEmailSent}
                            setIsEmailSent={setIsEmailSent} />
                    </>
                    {isEmailSent &&
                        <>
                            <hr id="make_a_phone_call" />
                            <MakeAPhoneCall isPhoneCallMade={isPhoneCallMade} setIsPhoneCallMade={setIsPhoneCallMade} />
                        </>}
                    {isPhoneCallMade &&
                        <>
                            <hr id="post_on_social" />
                            <PostOnSocial isSocialPosted={isSocialPosted} setIsSocialPosted={setIsSocialPosted} />
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