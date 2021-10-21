import { fetcher, initiateActionAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import useSessionStorage from "common/hooks/useSessionStorage";
import logo from "common/logo.png";
import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useState } from "react";
import { Alert, Badge, Button, Col, Form, Row } from "react-bootstrap";
import { useHistory } from "react-router-dom";
import useSWR from "swr";
import styles from "./WelcomePage.module.css";

export default function WelcomePage() {
    const [formInfo, setFormInfo] = useSessionStorage<FormInfo>("formInfo", {
        streetAddress: "",
        city: "",
        state: "",
        postalCode: "",
        email: "",
        hasTrackingConsent: false,
        hasEmailingConsent: false,
    });
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [, setActionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const { data: areas, error: areasError } = useSWR<{ shortName: string; fullName: string }[]>(
        "/values/areas",
        fetcher
    );
    useSWR<{ focusIssue: Issue; otherIssues: Issue[] }, string>(
        "/issues",
        fetcher
    );
    const history = useHistory();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setErrorMessage("");
        setIsLoading(true);
        const response = await initiateActionAPI(formInfo);
        setIsLoading(false);

        if (!response.successful) {
            setErrorMessage(response?.error ?? "Failed to initiate action");
            return;
        }

        setActionInfo(response.data!);
        history.push("/pick-your-issue");
    };

    return (
        <div className={`${styles.welcomePageContainer} text-center m-auto`}>
            <img src={logo} className="App-logo" alt="logo" />
            <h1 id="find_your_reps">Climate Action in 3 Steps</h1>
            <p>Advocate to your elected reprepresentatives on issues that matter to you.</p>
            <Row className="d-flex mb-3 mb-md-4">
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>
                        1
                    </Badge>
                    <span>Choose an issue</span>
                </Col>
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>
                        2
                    </Badge>
                    <span>Write your why</span>
                </Col>
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>
                        3
                    </Badge>
                    <span>Use your voice</span>
                </Col>
            </Row>
            <div className={`${styles.formContainer} d-flex m-auto`}>
                <Form onSubmit={handleSubmit}>
                    <Form.Group
                        className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row"
                        controlId="formGridEmail"
                    >
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Email</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.email}
                            onChange={(e) => setFormInfo({ ...formInfo, email: e.currentTarget.value })}
                            type="email"
                            placeholder="ilovetheplanet@example.com"
                            required
                        />
                    </Form.Group>
                    <Form.Group
                        className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row"
                        controlId="formGridAddress"
                    >
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Street</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.streetAddress}
                            onChange={(e) => setFormInfo({ ...formInfo, streetAddress: e.currentTarget.value })}
                            placeholder="1234 Make An Impact St"
                            required
                        />
                    </Form.Group>
                    <Form.Group
                        className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row"
                        controlId="formGridCity"
                    >
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>City</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.city}
                            onChange={(e) => setFormInfo({ ...formInfo, city: e.currentTarget.value })}
                            required
                        />
                    </Form.Group>
                    <Form.Group
                        className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row"
                        controlId="formGridState"
                    >
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>State</Form.Label>
                        <Form.Select
                            className={styles.formControl}
                            value={formInfo.state}
                            onChange={(e) => setFormInfo({ ...formInfo, state: e.currentTarget.value })}
                            required
                        >
                            <option value="">Select</option>
                            {areas?.map((area) => (
                                <option key={area.shortName} value={area.shortName}>
                                    {area.fullName}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>
                    <Form.Group
                        className="mb-3 d-flex align-items-start align-items-md-center flex-column flex-md-row"
                        controlId="formGridZip"
                    >
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Zip Code</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.postalCode}
                            onChange={(e) => setFormInfo({ ...formInfo, postalCode: e.currentTarget.value })}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formConsentCheckbox">
                        <Form.Check
                            className="text-start"
                            checked={formInfo.hasTrackingConsent}
                            onChange={() =>
                                setFormInfo({ ...formInfo, hasTrackingConsent: !formInfo.hasTrackingConsent })
                            }
                            type="checkbox"
                            label="I consent to allow Climate Changemakers to store my email address and the policymakers I contact to track our collective impact. (required)"
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formInformationalCheckbox">
                        <Form.Check
                            className="text-start"
                            checked={formInfo.hasEmailingConsent}
                            onChange={() =>
                                setFormInfo({ ...formInfo, hasEmailingConsent: !formInfo.hasEmailingConsent })
                            }
                            type="checkbox"
                            label="Yes, I would like to receive occasional information emails from Climate Changemakers! (optional)"
                        />
                    </Form.Group>
                    <Button className="w-100 text-dark fs-5" variant="primary" type="submit">
                        {errorMessage ? "Try again" : isLoading ? "Loading..." : "Start Advocating"}
                    </Button>
                    <p className="mt-2 text-start fs-7">
                        Complete street address needed to identify your elected representatives. Climate Changemakers
                        will not save your address or use it for any other purpose.
                    </p>
                </Form>
            </div>
            <ErrorMessage message={areasError || errorMessage} />
        </div>
    );
}
