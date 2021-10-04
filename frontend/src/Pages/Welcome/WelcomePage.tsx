import { initiateActionAPI } from "common/api/ClimateChangemakersAPI";
import useSessionStorage from "common/hooks/useSessionStorage";
import logo from "common/logo.png";
import { ActionInfo } from "common/models/ActionInfo";
import { useState } from "react";
import { Alert, Badge, Button, Col, Form, Row } from "react-bootstrap";
import { useHistory } from "react-router-dom";
import styles from "./WelcomePage.module.css";

export default function WelcomePage() {
    const [formInfo, setFormInfo] = useState(
        {
            streetAddress: "",
            city: "",
            state: "",
            postalCode: "",
            email: "",
            hasTrackingConsent: false,
            hasEmailingConsent: false
        });

    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [, setActionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
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
    }

    return (
        <div className={`${styles.welcomePageContainer} m-auto`}>
            <img src={logo} className="App-logo" alt="logo" />
            <h1 id="find_your_reps">Climate Action in 3 Steps</h1>
            <p>Advocate to your elected reprepresentatives on issues that matter to you.</p>
            <Row className="d-flex mb-3 mb-md-4">
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>1</Badge>
                    <span>Choose an issue</span>
                </Col>
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>2</Badge>
                    <span>Write your why</span>
                </Col>
                <Col md="4" className="fs-5 mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2 text-dark" pill>3</Badge>
                    <span>Use your voice</span>
                </Col>
            </Row>
            <div className={`${styles.formContainer} d-flex m-auto`}>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row" controlId="formGridEmail">
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Email</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.email}
                            onChange={e => setFormInfo({ ...formInfo, email: e.currentTarget.value })}
                            type="email"
                            placeholder="ilovetheplanet@example.com"
                            required />
                    </Form.Group>
                    <Form.Group className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row" controlId="formGridAddress">
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Street</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.streetAddress}
                            onChange={e => setFormInfo({ ...formInfo, streetAddress: e.currentTarget.value })}
                            placeholder="1234 Make An Impact St"
                            required />
                    </Form.Group>
                    <Form.Group className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row" controlId="formGridCity">
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>City</Form.Label>
                        <Form.Control
                            className={styles.formControl}
                            value={formInfo.city}
                            onChange={e => setFormInfo({ ...formInfo, city: e.currentTarget.value })}
                            required />
                    </Form.Group>
                    <Form.Group className="mb-2 d-flex align-items-start align-items-md-center flex-column flex-md-row" controlId="formGridState">
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>State</Form.Label>
                        <Form.Select className={styles.formControl} value={formInfo.state} onChange={e => setFormInfo({ ...formInfo, state: e.currentTarget.value })} required>
                            <option value="">Select</option>
                            <option value="AL">Alabama</option>
                            <option value="AK">Alaska</option>
                            <option value="AS">American Samoa</option>
                            <option value="AZ">Arizona</option>
                            <option value="AR">Arkansas</option>
                            <option value="CA">California</option>
                            <option value="CO">Colorado</option>
                            <option value="CT">Connecticut</option>
                            <option value="DE">Delaware</option>
                            <option value="DC">District Of Columbia</option>
                            <option value="FL">Florida</option>
                            <option value="GA">Georgia</option>
                            <option value="GU">Guam</option>
                            <option value="HI">Hawaii</option>
                            <option value="ID">Idaho</option>
                            <option value="IL">Illinois</option>
                            <option value="IN">Indiana</option>
                            <option value="IA">Iowa</option>
                            <option value="KS">Kansas</option>
                            <option value="KY">Kentucky</option>
                            <option value="LA">Louisiana</option>
                            <option value="ME">Maine</option>
                            <option value="MD">Maryland</option>
                            <option value="MA">Massachusetts</option>
                            <option value="MI">Michigan</option>
                            <option value="MN">Minnesota</option>
                            <option value="MS">Mississippi</option>
                            <option value="MO">Missouri</option>
                            <option value="MT">Montana</option>
                            <option value="NE">Nebraska</option>
                            <option value="NV">Nevada</option>
                            <option value="NH">New Hampshire</option>
                            <option value="NJ">New Jersey</option>
                            <option value="NM">New Mexico</option>
                            <option value="NY">New York</option>
                            <option value="NC">North Carolina</option>
                            <option value="ND">North Dakota</option>
                            <option value="MP">Northern Mariana Islands</option>
                            <option value="OH">Ohio</option>
                            <option value="OK">Oklahoma</option>
                            <option value="OR">Oregon</option>
                            <option value="PA">Pennsylvania</option>
                            <option value="PR">Puerto Rico</option>
                            <option value="RI">Rhode Island</option>
                            <option value="SC">South Carolina</option>
                            <option value="SD">South Dakota</option>
                            <option value="TN">Tennessee</option>
                            <option value="TX">Texas</option>
                            <option value="UT">Utah</option>
                            <option value="VT">Vermont</option>
                            <option value="VI">Virgin Islands</option>
                            <option value="VA">Virginia</option>
                            <option value="WA">Washington</option>
                            <option value="WV">West Virginia</option>
                            <option value="WI">Wisconsin</option>
                            <option value="WY">Wyoming</option>
                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3 d-flex align-items-start align-items-md-center flex-column flex-md-row" controlId="formGridZip">
                        <Form.Label className={`${styles.formLabel} text-start mb-md-0 mb-1`}>Zip Code</Form.Label>
                        <Form.Control className={styles.formControl} value={formInfo.postalCode} onChange={e => setFormInfo({ ...formInfo, postalCode: e.currentTarget.value })} required />
                    </Form.Group>
                    <Form.Group
                        className="mb-3"
                        controlId="formConsentCheckbox">
                        <Form.Check
                            className="text-start"
                            checked={formInfo.hasTrackingConsent}
                            onChange={() => setFormInfo({ ...formInfo, hasTrackingConsent: !formInfo.hasTrackingConsent })}
                            type="checkbox"
                            label="I consent to allow Climate Changemakers to store my email address and the policymakers I contact to track our collective impact. (required)"
                            required
                        />
                    </Form.Group>
                    <Form.Group
                        className="mb-3"
                        controlId="formInformationalCheckbox">
                        <Form.Check
                            className="text-start"
                            checked={formInfo.hasEmailingConsent}
                            onChange={() => setFormInfo({ ...formInfo, hasEmailingConsent: !formInfo.hasEmailingConsent })}
                            type="checkbox"
                            label="Yes, I would like to receive occasional information emails from Climate Changemakers! (optional)"
                        />
                    </Form.Group>
                    <Button className="w-100 text-dark fs-5" variant="primary" type="submit">
                        {errorMessage
                            ? "Try again"
                            : isLoading
                                ? "Loading..."
                                : "Start Advocating"}
                    </Button>
                    <p className="mt-2 text-start smallText">Complete street address needed to identify your elected representatives. Climate Changemakers will not save your address or use it for any other purpose.</p>
                </Form>
            </div>
            {errorMessage &&
                <Row>
                    <Col>
                        <Alert variant="danger" className="p-1 mt-2">
                            {errorMessage}
                        </Alert>
                    </Col>
                </Row>}
        </div>
    )
}