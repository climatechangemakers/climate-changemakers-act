import Layout from "common/Components/Layout";
import useAreas from "common/hooks/useAreas";
import useSessionStorage from "common/hooks/useSessionStorage";
import { scrollToId } from "common/lib/scrollToId";
import { ActionInfo } from "common/models/ActionInfo";
import { EmailInfo } from "common/models/EmailInfo";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { Navigate } from "react-router-dom";
import { MultiValue } from "react-select";
import usePrecomposedTweetData from "../../common/hooks/usePrecomposedTweetData";
import AllDone from "./AllDone/AllDone";
import Amplify from "./Amplify/Amplify";
import MakeAPhoneCall from "./MakeACall/MakeACall";
import MeetYourReps from "./MeetYourReps/MeetYourReps";
import PostOnSocial from "./PostOnSocial/PostOnSocial";
import ScrollSpy from "./ScrollSpy/ScrollSpy";
import SendAnEmail from "./SendAnEmail/SendAnEmail";

export default function TakeActionPage() {
    const [isEmailDone, setIsEmailDone] = useState(false);
    const [isPhoneCallMade, setIsPhoneCallMade] = useState(false);
    const [isSocialPosted, setIsSocialPosted] = useState(false);
    const [isAmplified, setIsAmplified] = useState(false);
    const [selectedIssue] = useSessionStorage<Issue | undefined>("selectedIssue");
    const [actionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const [formInfo] = useSessionStorage<FormInfo | undefined>("formInfo");
    const [emailInfo, setEmailInfo] = useState<EmailInfo>({
        prefix: "",
        firstName: "",
        lastName: "",
        subject: "",
        body: "",
        selectedLocTopics: [] as MultiValue<{ value: string; label: string }>,
    });
    const selectedIssueId = selectedIssue?.id;
    const { data: preComposedTweetData, error: preComposedTweetError } = usePrecomposedTweetData(
        selectedIssueId,
        actionInfo
    );
    const { data: areas, error: areasError } = useAreas();

    useEffect(() => {
        isEmailDone && scrollToId("make_a_phone_call");
    }, [isEmailDone]);
    useEffect(() => {
        isPhoneCallMade && scrollToId("post_on_social");
    }, [isPhoneCallMade]);
    useEffect(() => {
        isSocialPosted && scrollToId("amplify");
    }, [isSocialPosted]);
    useEffect(() => {
        isAmplified && scrollToId("all_done");
    }, [isAmplified]);

    if (!actionInfo || !formInfo) return <Navigate to="/" />;

    if (!selectedIssue) return <Navigate to="/pick-your-issue" />;

    return (
        <Layout>
            <Row className="d-flex flex-column flex-lg-row">
                <Col lg="10" xs="12">
                    <h1 className="text-start mb-4 pb-2 pt-4" id="introduction">
                        Time to get started!
                    </h1>
                    <MeetYourReps actionInfo={actionInfo} />
                    <hr id="send_an_email" />
                    <SendAnEmail
                        actionInfo={actionInfo}
                        formInfo={formInfo}
                        selectedIssue={selectedIssue}
                        isEmailDone={isEmailDone}
                        setIsEmailDone={setIsEmailDone}
                        emailInfo={emailInfo}
                        setEmailInfo={setEmailInfo}
                    />
                    {isEmailDone && (
                        <>
                            <hr id="make_a_phone_call" />
                            <MakeAPhoneCall
                                actionInfo={actionInfo}
                                relatedIssueId={selectedIssue.id}
                                emailAddress={formInfo.email}
                                isPhoneCallMade={isPhoneCallMade}
                                setIsPhoneCallMade={setIsPhoneCallMade}
                                emailBody={emailInfo.body}
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
                                preComposedTweetError={preComposedTweetError?.message}
                            />
                        </>
                    )}
                    {isSocialPosted && (
                        <>
                            <hr id="amplify" />
                            <Amplify isAmplified={isAmplified} setIsAmplified={setIsAmplified} />
                        </>
                    )}
                    {isAmplified && (
                        <>
                            <hr id="all_done" />
                            <AllDone />
                        </>
                    )}
                </Col>
                <Col xs="12" md="2" className="d-none d-lg-block">
                    <ScrollSpy
                        isEmailDone={isEmailDone}
                        isPhoneCallMade={isPhoneCallMade}
                        isSocialPosted={isSocialPosted}
                        isAmplified={isAmplified}
                        desktop
                    />
                </Col>
                <div className="d-block d-lg-none w-100 position-fixed mobileScrollSpy">
                    <ScrollSpy
                        isEmailDone={isEmailDone}
                        isPhoneCallMade={isPhoneCallMade}
                        isSocialPosted={isSocialPosted}
                        isAmplified={isAmplified}
                    />
                </div>
            </Row>
        </Layout>
    );
}
