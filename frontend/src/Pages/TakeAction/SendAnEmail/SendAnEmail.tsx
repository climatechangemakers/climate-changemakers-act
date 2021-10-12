import { fetcher, sendEmailAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useState } from "react";
import { Accordion, Alert, Button, Col, Form, Row } from "react-bootstrap";
import Select, { MultiValue } from "react-select";
import useSWR from "swr";
import emailIcon from "./email-icon.svg";

type Props = {
    actionInfo: ActionInfo;
    formInfo: FormInfo;
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
    selectedIssue: Issue;
};

export default function SendAnEmail({ actionInfo, formInfo, isEmailSent, setIsEmailSent, selectedIssue }: Props) {
    const [emailInfo, setEmailInfo] = useState({
        prefix: "",
        firstName: "",
        lastName: "",
        subject: "",
        body: "",
        selectedLocTopics: [] as MultiValue<{ value: string; label: string }>,
    });
    const [sendEmailError, setSendEmailError] = useState("");
    const { data: prefixes, error: prefixError } = useSWR<string[], string>("/values/prefixes", fetcher);
    const { data: locTopics, error: locTopicsError } = useSWR<string[], string>(
        "/values/library-of-congress-topics",
        fetcher
    );

    const sendEmail = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setSendEmailError("");
        const response = await sendEmailAPI(
            formInfo.email,
            emailInfo.prefix,
            emailInfo.firstName,
            emailInfo.lastName,
            formInfo.streetAddress,
            formInfo.city,
            formInfo.state,
            formInfo.postalCode,
            emailInfo.selectedLocTopics.map((t) => t.value),
            emailInfo.subject,
            emailInfo.body,
            selectedIssue.id,
            actionInfo.legislators.map((l) => l.bioguideId)
        );
        if (!response.successful) {
            setSendEmailError(response?.error ?? "Failed to send email");
            return;
        }
        setIsEmailSent(true);
    };

    const topicOptions = locTopics?.map((t) => ({ value: t, label: t })) || [];
    const error = prefixError || locTopicsError || sendEmailError;

    return (
        <div className="pt-2 pb-3 text-start">
            <div className="d-flex">
                <img src={emailIcon} alt="" height="40" width="40" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Send an Email</h2>
            </div>
            <p>
                Fill out the form below to open up an email to your elected representatives. The email template includes
                plenty of ‘fill-in-the-blank’ spaces, so you should weave in your freshly-drafted ‘why’ to make your
                message stand out.
            </p>
            {selectedIssue.talkingPoints.length > 0 && (
                <div className="mb-3">
                    <h4>Issue Guide</h4>
                    <Accordion defaultActiveKey="0">
                        {selectedIssue.talkingPoints.map((point, i) => (
                            <Accordion.Item key={i} eventKey={i.toString()}>
                                <Accordion.Header>{point.title}</Accordion.Header>
                                <Accordion.Body className="p-0 h-100 text-dark fs-6">
                                    <div
                                        className="py-2 px-3 bg-purple-secondary"
                                        dangerouslySetInnerHTML={{ __html: point.content }}
                                    />
                                </Accordion.Body>
                            </Accordion.Item>
                        ))}
                    </Accordion>
                </div>
            )}
            <Form onSubmit={sendEmail}>
                <Row>
                    <Col lg="4">
                        <h4>Tips</h4>
                        <ul>
                            {[
                                "Identify yourself as a constituent",
                                "State any relevant expertise",
                                "Mention how the issue impacts your state/district",
                                "Stick to one issue",
                                "Be respectful and brief",
                                "Make a direct ask",
                            ].map((m) => (
                                <li key={m}>{m}</li>
                            ))}
                        </ul>
                        <h4>Prompts</h4>
                        <ul className="fs-6">
                            {[
                                "Where are you from and what do you do?",
                                "Why is this climate issue important to you?",
                                "What conerns is this issue causing you?",
                                "How do you think it could be different?",
                            ].map((m) => (
                                <li key={m}>{m}</li>
                            ))}
                        </ul>
                    </Col>
                    <Col className="mt-auto" lg="8">
                        <h4>Draft Your Email</h4>
                        <Row>
                            <Col lg="3">
                                <Form.Group className="mb-3 h-100" controlId="emailForm.prefix">
                                    <Form.Label>Prefix</Form.Label>
                                    <Form.Select
                                        value={emailInfo.prefix}
                                        onChange={(e) => setEmailInfo({ ...emailInfo, prefix: e.currentTarget.value })}
                                        disabled={isEmailSent}
                                        required
                                    >
                                        <option value="">--</option>
                                        {prefixes?.map((p) => (
                                            <option key={p} value={p}>
                                                {p}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            <Col lg="4">
                                <Form.Group className="mb-3 h-100" controlId="emailForm.firstName">
                                    <Form.Label>First Name</Form.Label>
                                    <Form.Control
                                        value={emailInfo.firstName}
                                        onChange={(e) =>
                                            setEmailInfo({ ...emailInfo, firstName: e.currentTarget.value })
                                        }
                                        disabled={isEmailSent}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col lg="5">
                                <Form.Group className="mb-3 h-100" controlId="emailForm.lastName">
                                    <Form.Label>Last Name</Form.Label>
                                    <Form.Control
                                        value={emailInfo.lastName}
                                        onChange={(e) =>
                                            setEmailInfo({ ...emailInfo, lastName: e.currentTarget.value })
                                        }
                                        disabled={isEmailSent}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Form.Group className="mb-3 h-100" controlId="emailForm.subject">
                                    <Form.Label>Subject Line</Form.Label>
                                    <Form.Control
                                        value={emailInfo.subject}
                                        onChange={(e) => setEmailInfo({ ...emailInfo, subject: e.currentTarget.value })}
                                        disabled={isEmailSent}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Form.Label>Letter Topic</Form.Label>
                                <Select
                                    defaultValue={emailInfo.selectedLocTopics}
                                    onChange={(e) => setEmailInfo({ ...emailInfo, selectedLocTopics: e })}
                                    options={topicOptions}
                                    isDisabled={isEmailSent}
                                    styles={{
                                        option: (provided) => ({
                                            ...provided,
                                            color: "black",
                                        }),
                                    }}
                                    isMulti
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
                <Row className="mt-1">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.body">
                        <Form.Label>Body</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={6}
                            placeholder="Write your why..."
                            value={emailInfo.body}
                            onChange={(e) => setEmailInfo({ ...emailInfo, body: e.currentTarget.value })}
                            disabled={isEmailSent}
                            required
                        />
                    </Form.Group>
                </Row>
                <Row>
                    <Col md="6">
                        <Button variant="secondary" className="w-100" disabled={isEmailSent} onClick={() => setIsEmailSent(true)}>
                            Skip to Call
                        </Button>
                    </Col>
                    <Col md="6">
                        <Button type="submit" className="w-100 text-dark" disabled={isEmailSent}>
                            {!sendEmailError ? "Send Email" : "Try again"}
                        </Button>
                    </Col>
                </Row>
            </Form>
            <ErrorMessage message={error} />
        </div>
    );
}
