import logo from './logo.png';
import Badge from 'react-bootstrap/badge';
import Row from 'react-bootstrap/esm/Row';
import Form from 'react-bootstrap/esm/Form';
import Col from 'react-bootstrap/esm/Col';
import { Button } from 'react-bootstrap';
import { useState } from 'react';

export default function HomePage() {
    const [address, setAddress] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");
    const [zip, setZip] = useState("");
    const [email, setEmail] = useState("");

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log("Submitted");
    }

    return (
        <div>
            <img src={logo} className="App-logo" alt="logo" />
            <h1>Take Climate Action</h1>
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
            <Form onSubmit={handleSubmit}>
                <Row>
                    <Form.Group as={Col} md="6" className="mb-2 mb-md-3 d-flex align-items-start flex-column" controlId="formGridAddress">
                        <Form.Label>Address</Form.Label>
                        <Form.Control
                            value={address}
                            onChange={e => setAddress(e.currentTarget.value)}
                            placeholder="1234 Make An Impact St"
                            required />
                    </Form.Group>
                    <Form.Group as={Col} md="3" className="mb-2 mb-md-3 col-12 d-flex align-items-start flex-column" controlId="formGridCity">
                        <Form.Label>City</Form.Label>
                        <Form.Control
                            value={city}
                            onChange={e => setCity(e.currentTarget.value)}
                            required />
                    </Form.Group>
                    <Form.Group as={Col} md="3" className="mb-2 mb-md-3 col-12 d-flex align-items-start flex-column" controlId="formGridState">
                        <Form.Label>State</Form.Label>
                        <Form.Select value={state} onChange={e => setState(e.currentTarget.value)} required>
                            <option value="">Select</option>
                            <option value="AL">Alabama</option>
                            <option value="AK">Alaska</option>
                            <option value="AZ">Arizona</option>
                            <option value="AR">Arkansas</option>
                            <option value="CA">California</option>
                            <option value="CO">Colorado</option>
                            <option value="CT">Connecticut</option>
                            <option value="DE">Delaware</option>
                            <option value="DC">District Of Columbia</option>
                            <option value="FL">Florida</option>
                            <option value="GA">Georgia</option>
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
                            <option value="OH">Ohio</option>
                            <option value="OK">Oklahoma</option>
                            <option value="OR">Oregon</option>
                            <option value="PA">Pennsylvania</option>
                            <option value="RI">Rhode Island</option>
                            <option value="SC">South Carolina</option>
                            <option value="SD">South Dakota</option>
                            <option value="TN">Tennessee</option>
                            <option value="TX">Texas</option>
                            <option value="UT">Utah</option>
                            <option value="VT">Vermont</option>
                            <option value="VA">Virginia</option>
                            <option value="WA">Washington</option>
                            <option value="WV">West Virginia</option>
                            <option value="WI">Wisconsin</option>
                            <option value="WY">Wyoming</option>
                        </Form.Select>
                    </Form.Group>
                    <Form.Group as={Col} md="3" className="mb-2 mb-md-3 d-flex align-items-start flex-column" controlId="formGridZip">
                        <Form.Label>Zip</Form.Label>
                        <Form.Control value={zip} onChange={e => setZip(e.currentTarget.value)} required />
                    </Form.Group>
                    <Form.Group lg as={Col} md="6" className="mb-2 mb-md-3 d-flex align-items-start flex-column" controlId="formGridEmail">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            value={email}
                            onChange={e => setEmail(e.currentTarget.value)}
                            type="email"
                            placeholder="ilovetheplanet@example.com"
                            required />
                    </Form.Group>
                    <Col md="3" className="mt-3 mb-2 mb-md-3 d-flex align-items-end">
                        <Button className="w-100" variant="primary" type="submit">
                            Let's Go!
                        </Button>
                    </Col>
                </Row>
            </Form>
        </div>
    )
}