import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Layout from "./common/Components/Layout";
import PickYourIssue from "./Pages/PickYourIssue/PickYourIssuePage";
import TakeAction from "./Pages/TakeAction/TakeActionPage";
import Welcome from "./Pages/Welcome/WelcomePage";

export default function App() {
    return (
        <Layout>
            <Router>
                <Switch>
                    <Route path="/take-action">
                        <TakeAction />
                    </Route>
                    <Route path="/pick-your-issue">
                        <PickYourIssue />
                    </Route>
                    <Route path="/">
                        <Welcome />
                    </Route>
                </Switch>
            </Router>
        </Layout>
    );
}
