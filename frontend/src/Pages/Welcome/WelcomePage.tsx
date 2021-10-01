import { initiateActionAPI } from "common/api/ClimateChangemakersAPI";
import useSessionStorage from "common/hooks/useSessionStorage";
import logo from "common/logo.png";
import { ActionInfo } from "common/models/ActionInfo";
import { useState } from "react";
import { Alert, Badge, Button, Col, Form, Row } from "react-bootstrap";
import { useHistory } from "react-router-dom";

export default function WelcomePage() {
    const [streetAddress, setStreetAddress] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");
    const [postalCode, setPostalCode] = useState("");
    const [email, setEmail] = useState("");
    const [hasTrackConsent, setHasTrackConsent] = useState(false);
    const [hasEmailingConsent, setHasEmailingConsent] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [, setActionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const history = useHistory();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setErrorMessage("");
        setIsLoading(true);
        const response = await initiateActionAPI(email, streetAddress, city, state, postalCode, hasTrackConsent, hasEmailingConsent);
        setIsLoading(false);

        if (!response.successful) {
            setErrorMessage(response?.error ?? "Failed to initiate action");
            return;
        }

        setActionInfo(response.data!);
        history.push("/pick-your-issue");
    }

    return (
        <>
            <img src={logo} className="App-logo" alt="logo" />
            <h1 id="find_your_reps">Take Climate Action</h1>
            <p>
                Welcome! We want to help you take climate actions whenever you have time for the issues that matter most. In 3 simple steps you can make climate impact:
            </p>
            <Row className="d-flex mb-3 mb-md-4">
                <Col md="4" className="mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2" pill>1</Badge>
                    <span>Choose an issue</span>
                </Col>
                <Col md="4" className="mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2" pill>2</Badge>
                    <span>Write your why</span>
                </Col>
                <Col md="4" className="mb-2 mb-md-0 d-flex align-items-center justify-content-md-center">
                    <Badge className="me-2" pill>3</Badge>
                    <span>Use your voice</span>
                </Col>
            </Row>
            <Form className="pb-3" onSubmit={handleSubmit}>
                <Row>
                    <Form.Group as={Col} md="6" className="mb-3 d-flex align-items-start flex-column" controlId="formGridAddress">
                        <Form.Label>Address</Form.Label>
                        <Form.Control
                            value={streetAddress}
                            onChange={e => setStreetAddress(e.currentTarget.value)}
                            placeholder="1234 Make An Impact St"
                            required />
                    </Form.Group>
                    <Form.Group as={Col} md="3" className="mb-3 col-12 d-flex align-items-start flex-column" controlId="formGridCity">
                        <Form.Label>City</Form.Label>
                        <Form.Control
                            value={city}
                            onChange={e => setCity(e.currentTarget.value)}
                            required />
                    </Form.Group>
                    <Form.Group as={Col} md="3" className="mb-3 col-12 d-flex align-items-start flex-column" controlId="formGridState">
                        <Form.Label>State</Form.Label>
                        <Form.Select value={state} onChange={e => setState(e.currentTarget.value)} required>
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
                    <Form.Group as={Col} md="3" className="mb-3 d-flex align-items-start flex-column" controlId="formGridZip">
                        <Form.Label>Zip</Form.Label>
                        <Form.Control value={postalCode} onChange={e => setPostalCode(e.currentTarget.value)} required />
                    </Form.Group>
                    <Form.Group lg as={Col} md="6" className="mb-3 d-flex align-items-start flex-column" controlId="formGridEmail">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            value={email}
                            onChange={e => setEmail(e.currentTarget.value)}
                            type="email"
                            placeholder="ilovetheplanet@example.com"
                            required />
                    </Form.Group>
                    <Form.Group
                        as={Col}
                        md="12"
                        className="mb-3 fs-5"
                        controlId="formConsentCheckbox">
                        <Form.Check
                            className="text-start"
                            checked={hasTrackConsent}
                            onChange={() => setHasTrackConsent(!hasTrackConsent)}
                            type="checkbox"
                            label="I consent to allow Climate Changemakers to store my email address and the policymakers I contact to track our collective impact. (required)"
                            required
                        />
                    </Form.Group>
                    <Form.Group
                        as={Col}
                        md="12"
                        className="mb-3"
                        controlId="formInformationalCheckbox">
                        <Form.Check
                            className="text-start fs-5"
                            checked={hasEmailingConsent}
                            onChange={() => setHasEmailingConsent(!hasEmailingConsent)}
                            type="checkbox"
                            label="Yes, I would like to receive occasional information emails from Climate Changemakers! (optional)"
                        />
                    </Form.Group>
                    <Col sm="12" md="3" className="mt-3 mb-3 d-flex align-items-end justify-content-center">
                        <Button className="w-100" variant="primary" type="submit">
                            {errorMessage
                                ? "Try again"
                                : isLoading
                                    ? "Loading..."
                                    : "Find Your Reps!"}
                        </Button>
                    </Col>
                </Row>
            </Form>
            {errorMessage &&
                <Row>
                    <Col>
                        <Alert variant="danger" className="p-1 mt-2">
                            {errorMessage}
                        </Alert>
                    </Col>
                </Row>}
        </>
    )
}