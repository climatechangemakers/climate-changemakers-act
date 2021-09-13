import { useState } from "react";
import { Accordion, Alert, Button, Col, Form, Row } from "react-bootstrap";
import { sendEmailAPI } from "../../api/ClimateChangemakersAPI";
import { Issue } from "../../models/IssuesResponse";
import styles from "./SendAnEmail.module.css";

type Props = {
    email: string;
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
    selectedIssue: Issue;
}

const prompts = ["Where are you from and what do you do?",
    "Why is this climate issue important to you?",
    "What conerns is this issue causing you?",
    "How do you think it could be different?"];

export default function SendAnEmail({ email, isEmailSent, setIsEmailSent, selectedIssue }: Props) {
    const [emailMessage, setEmailMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const hasTalkingPoints = selectedIssue.talkingPoints.length > 0;

    const sendEmail = async () => {
        setErrorMessage("");
        // TODO: Use actual relatedIssueId and contactedBioguideIds
        const response = await sendEmailAPI(email, -1, emailMessage, []);
        if (!response.successful) {
            setErrorMessage(response?.error ?? "Failed to send email");
            return;
        }
        setIsEmailSent(true);
    }

    return (
        <div className="pt-2 pb-3 text-start">
            <h3 className="pb-3">Send An Email</h3>
            <Row>
                <Col md="6">
                    <h4>Instructions</h4>
                    <p className="fs-6">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
                </Col>
                <Col md="6">
                    <h4>Prompts</h4>
                    <div className="fs-6">Here are some prompts to get you started</div>
                    <ul className="fs-6">
                        {prompts.map((m) => <li key={m}>{m}</li>)}
                    </ul>
                </Col>
            </Row>
            <Row className={`${styles.emailRow} mt-3`}>
                {hasTalkingPoints &&
                    <Col md="6" className={`${styles.pointsBodyCol} mb-2`}>
                        <Accordion defaultActiveKey="0">
                            {selectedIssue.talkingPoints.map((point, i) =>
                                <Accordion.Item key={i} eventKey={i.toString()}>
                                    <Accordion.Header>
                                        {point.title}
                                    </Accordion.Header>
                                    <Accordion.Body className={`${styles.pointsBody} p-0 h-100 text-dark fs-6`}>
                                        <div className="py-2 px-3" dangerouslySetInnerHTML={{ __html: point.content }} />
                                    </Accordion.Body>
                                </Accordion.Item>)}
                        </Accordion>
                    </Col>}
                <Col className="mb-2" md={hasTalkingPoints ? "6" : "12"}>
                    <Form.Group className="mb-3 h-100" controlId="emailForm.emailFormTextArea">
                        <Form.Label className="visuallyhidden">Send an email</Form.Label>
                        <Form.Control
                            value={emailMessage}
                            onChange={e => setEmailMessage(e.currentTarget.value)}
                            disabled={isEmailSent}
                            placeholder="Email here..."
                            className="h-100 p-3"
                            as="textarea"
                            rows={7} />
                    </Form.Group>
                </Col>
            </Row>
            <Button
                className="d-flex me-auto mt-3"
                disabled={!email || isEmailSent}
                onClick={sendEmail}>
                {!errorMessage
                    ? "Send Email"
                    : "Try again"}
            </Button>
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