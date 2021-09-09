import { useState } from "react";
import { Accordion, Button, Col, Form, Row } from "react-bootstrap";
import { Issue } from "../../models/IssuesResponse";
import styles from "./SendAnEmail.module.css";

type Props = {
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
    selectedIssue: Issue;
}

const prompts = ["Where are you from and what do you do?",
    "Why is this climate issue important to you?",
    "What conerns is this issue causing you?",
    "How do you think it could be different?"];

export default function SendAnEmail({ isEmailSent, setIsEmailSent, selectedIssue }: Props) {
    const [email, setEmail] = useState("");
    const hasTalkingPoints = selectedIssue.talkingPoints.length > 0;

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
                        {prompts.map((m) => <li>{m}</li>)}
                    </ul>
                </Col>
            </Row>
            <Row className={`${styles.emailRow} mt-3`}>
                {hasTalkingPoints &&
                    <Col md="6" className={`${styles.pointsBodyCol} mb-2`}>
                        <Accordion defaultActiveKey="0">
                            {selectedIssue.talkingPoints.map((point, i) =>
                                <Accordion.Item eventKey={i.toString()}>
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
                            value={email}
                            onChange={e => setEmail(e.currentTarget.value)}
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
                onClick={() => setIsEmailSent(true)}>
                Send Email
            </Button>
        </div>
    )
}