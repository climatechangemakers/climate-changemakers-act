import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import logo from './logo.png';
import Layout from './Layout';
import GetStarted from './Pages/InitiateAction';
import { useState } from 'react';
import { ActionInfo } from './models/ActionInfo';
import MeetYourReps from './Pages/MeetYourReps/MeetYourReps';

export default function App() {
    const [actionInfo, setActionInfo] = useState<ActionInfo | undefined>();

    return (
        <Layout>
            <div>
                <img src={logo} className="App-logo" alt="logo" />
                <h1>Take Climate Action</h1>
                <p>
                    Welcome! We want to help you take climate actions whenever you have time for the issues that matter most. In 3 simple steps you can make climate impact:
                </p>
                <GetStarted setActionInfo={setActionInfo} />
                <hr id="meet_your_reps" />
                {actionInfo &&
                    <MeetYourReps actionInfo={actionInfo} />}
            </div>
        </Layout>
    );
}