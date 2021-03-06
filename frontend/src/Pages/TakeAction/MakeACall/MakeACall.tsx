import { logCallAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import LegislatorCard from "common/Components/LegislatorCard/LegislatorCard";
import { ActionInfo } from "common/models/ActionInfo";
import { useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";
import callIcon from "./call-icon.svg";

type Props = {
    actionInfo: ActionInfo;
    relatedIssueId: number;
    emailAddress: string;
    isPhoneCallMade: boolean;
    setIsPhoneCallMade: (bool: boolean) => void;
    emailBody: string;
};

const skipEmailText = `Hello, I’m [NAME], a constituent calling from [LOCATION].
As a [INTRODUCE YOURSELF: WHERE DO YOU LIVE? WHAT DO YOU DO? WHAT’S IMPORTANT TO YOU?], I am concerned about the climate crisis because [WHY YOU CARE ABOUT CLIMATE].
I’m calling to urge [Senator/Representative NAME] to take action by [CLEAR ASK].
This policy would [WHY IS THE POLICY IMPORTANT].
Now is the time for [Senator/Representative NAME] to address these pressing issues by [REPEAT CLEAR ASK].
Please pass along my concerns to the [Senator/Representative]. Thanks so much for taking my call, and please thank the [Senator/Representative] for their service and leadership.`;

export default function MakeACall({
    actionInfo,
    relatedIssueId,
    emailAddress,
    isPhoneCallMade,
    setIsPhoneCallMade,
    emailBody,
}: Props) {
    const [bioguideIdsCalled, setBuiguideIdsCalled] = useState<string[]>([]);
    const [error, setError] = useState("");
    const [script, setScript] = useState(emailBody || skipEmailText);

    const logCall = async (contactedBioguideId: string) => {
        const response = await logCallAPI(emailAddress, relatedIssueId, contactedBioguideId);
        if (!response.successful) {
            setError(response?.error ?? "Failed to log phone number");
            return;
        }
        setBuiguideIdsCalled((n) => [...n, contactedBioguideId]);
    };

    return (
        <div className="pt-2 pb-3">
            <div className="d-flex">
                <img src={callIcon} alt="" height="40" width="38" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Make a Call</h2>
            </div>
            <p>
                Plan your personalized script, then dial away! Click “I called!” when you’re finished calling each
                member of Congress.
            </p>
            <h3 className="h4">Tips</h3>
            <ul>
                {[
                    "Call both the DC and district offices",
                    "Repurpose your email for your call script",
                    "Make edits in the working space below",
                ].map((m) => (
                    <li key={m}>{m}</li>
                ))}
            </ul>
            <h4>Script</h4>
            <Form.Group className="mb-3">
                <Form.Label htmlFor="callForm" className="visually-hidden" />
                <Form.Control
                    as="textarea"
                    rows={9}
                    id="callForm"
                    disabled={isPhoneCallMade}
                    value={script}
                    onChange={(e) => setScript(e.target.value)}
                />
            </Form.Group>
            <Row className="legislator-max-width m-auto mb-2 d-flex flex-md-row flex-column justify-content-center text-center">
                {actionInfo.legislators.map((legislator, i) => (
                    <Col className="d-flex justify-content-center" xs="12" md="4" key={i}>
                        <LegislatorCard
                            legislator={legislator}
                            call={{
                                bioguideIdsCalled,
                                isPhoneCallMade,
                                logCall: () => logCall(legislator.bioguideId),
                            }}
                        />
                    </Col>
                ))}
            </Row>
            <div className="row mt-3">
                <div className="col d-flex">
                    <Button
                        className="flex-grow-1 mr-2"
                        variant="secondary"
                        disabled={isPhoneCallMade}
                        onClick={() => setIsPhoneCallMade(true)}
                    >
                        Skip to Tweet
                    </Button>
                </div>
                <div className="col d-flex">
                    <Button
                        type="submit"
                        className="flex-grow-1 ml-2 text-dark"
                        variant="primary"
                        disabled={!bioguideIdsCalled.length || isPhoneCallMade}
                        onClick={() => setIsPhoneCallMade(true)}
                    >
                        Done calling
                    </Button>
                </div>
            </div>
            <ErrorMessage message={error} />
        </div>
    );
}
