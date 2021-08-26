import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import logo from './logo.png';
import Layout from './Layout/Layout';
import GetStarted from './Pages/InitiateAction';
import { useEffect, useState } from 'react';
import { ActionInfo } from './models/ActionInfo';
import MeetYourReps from './Pages/MeetYourReps/MeetYourReps';
import PickYourIssue from './Pages/PickYourIssue';
import SendAnEmail from './Pages/SendAnEmail';
import MakeAPhoneCall from './Pages/MakeAPhoneCall';
import PostOnSocial from './Pages/PostOnSocial';
import AllDone from './Pages/AllDone';

export default function App() {
    const [actionInfo, setActionInfo] = useState<ActionInfo | undefined>();
    const [issue, setIssue] = useState<string | undefined>();
    const [isEmailSent, setIsEmailSent] = useState(false);
    const [isPhoneCallMade, setIsPhoneCallMade] = useState(false);
    const [isSocialPosted, setIsSocialPosted] = useState(false);

    const scrollToId = (id: string) =>
        document.getElementById(id)?.scrollIntoView()

    useEffect(() => { actionInfo && scrollToId("pick_your_issue") }, [actionInfo])
    useEffect(() => { issue && scrollToId("take_action") }, [issue])
    useEffect(() => { isEmailSent && scrollToId("make_a_phone_call") }, [isEmailSent])
    useEffect(() => { isPhoneCallMade && scrollToId("post_on_social") }, [isPhoneCallMade])
    useEffect(() => { isEmailSent && isPhoneCallMade && isSocialPosted && scrollToId("all_done") }, [isEmailSent, isPhoneCallMade, isSocialPosted])

    return (
        <Layout
            isActionInfo={!!actionInfo}
            isIssue={!!issue}
            isEmailSent={isEmailSent}
            isPhoneCallMade={isPhoneCallMade}
            isSocialPosted={isSocialPosted}
        >
            <div>
                <img src={logo} className="App-logo" alt="logo" />
                <h1 id="find_your_reps">Take Climate Action</h1>
                <p>
                    Welcome! We want to help you take climate actions whenever you have time for the issues that matter most. In 3 simple steps you can make climate impact:
                </p>
                <GetStarted actionInfo={actionInfo} setActionInfo={setActionInfo} />
                {actionInfo &&
                    <>
                        <hr id="pick_your_issue" />
                        <MeetYourReps actionInfo={actionInfo} />
                        <hr />
                        <PickYourIssue issue={issue} setIssue={setIssue} />
                    </>}
                {issue &&
                    <>
                        <hr id="take_action" />
                        <h2 id="send_an_email">Time to Take Action!</h2>
                        <SendAnEmail isEmailSent={isEmailSent} setIsEmailSent={setIsEmailSent} />
                    </>}
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
                {isEmailSent && isPhoneCallMade && isSocialPosted &&
                    <>
                        <hr id="all_done" />
                        <AllDone />
                    </>}
            </div>
        </Layout >
    );
}