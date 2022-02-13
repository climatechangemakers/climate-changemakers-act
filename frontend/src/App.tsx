import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Layout from "./common/Components/Layout";
import PickYourIssue from "./Pages/PickYourIssue/PickYourIssuePage";
import TakeAction from "./Pages/TakeAction/TakeActionPage";
import Welcome from "./Pages/Welcome/WelcomePage";

export default function App() {
    return (
        <Layout>
            <BrowserRouter>
                <Routes>
                    <Route path="/take-action" element={<TakeAction />} />
                    <Route path="/pick-your-issue" element={<PickYourIssue />} />
                    <Route path="/" element={<Welcome />} />
                </Routes>
            </BrowserRouter>
        </Layout>
    );
}
