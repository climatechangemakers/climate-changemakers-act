import { logCallAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import LegislatorCard from "common/Components/LegislatorCard/LegislatorCard";
import { ActionInfo } from "common/models/ActionInfo";
import { useState } from "react";
import { Button, Card, Col, Row } from "react-bootstrap";
import callIcon from "./call-icon.svg";

type Props = {
    actionInfo: ActionInfo;
    relatedIssueId: number;
    emailAddress: string;
    isPhoneCallMade: boolean;
    setIsPhoneCallMade: (bool: boolean) => void;
    emailBody: string;
};

export default function MakeACall({
    actionInfo,
    relatedIssueId,
    emailAddress,
    isPhoneCallMade,
    setIsPhoneCallMade,
    emailBody,
}: Props) {
    const [phoneNumbersCalled, setPhoneNumbersCalled] = useState<string[]>([]);
    const [error, setError] = useState("");

    const logCall = async (phoneNumber: string, contactedBioguideId: string) => {
        const response = await logCallAPI(emailAddress, relatedIssueId, phoneNumber, contactedBioguideId);
        if (!response.successful) {
            setError(response?.error ?? "Failed to log phone number");
            return;
        }
        setPhoneNumbersCalled((n) => [...n, phoneNumber]);
    };

    return (
        <div className="pt-2 pb-3">
            <div className="d-flex">
                <img src={callIcon} alt="" height="40" width="40" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Make a Call</h2>
            </div>
            <p>
                Fill out the form below to open up an email to your elected representatives. The email template includes
                plenty of ‘fill-in-the-blank’ spaces, so you should weave in your freshly-drafted ‘why’ to make your
                message stand out.
            </p>
            {emailBody && (
                <>
                    <h4>Script</h4>
                    <Card className="mb-4">
                        <Card.Body className="text-dark">{emailBody}</Card.Body>
                    </Card>
                </>
            )}
            <Row className="legislator-max-width m-auto mb-2 d-flex flex-md-row flex-column justify-content-center text-center">
                {actionInfo.legislators.map((legislator, i) => (
                    <Col className="d-flex justify-content-center" xs="12" md="4" key={i}>
                        <LegislatorCard
                            legislator={legislator}
                            call={{
                                phoneNumbersCalled,
                                isPhoneCallMade,
                                logCall: (phoneNumber: string) => logCall(phoneNumber, legislator.bioguideId),
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
                        disabled={!phoneNumbersCalled.length || isPhoneCallMade}
                        onClick={() => setIsPhoneCallMade(true)}
                    >
                        Done Calling
                    </Button>
                </div>
            </div>
            <ErrorMessage message={error} />
        </div>
    );
}
