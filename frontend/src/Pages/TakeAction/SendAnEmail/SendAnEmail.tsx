import { ErrorResponse, fetcher, sendEmailAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import { scrollToId } from "common/lib/scrollToId";
import { ActionInfo } from "common/models/ActionInfo";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useEffect, useRef, useState } from "react";
import { Accordion, Button, Col, Form, Row } from "react-bootstrap";
import Select, { MultiValue } from "react-select";
import useSWR from "swr";
import emailIcon from "./email-icon.svg";
import Prompts from "./Prompts";

type Props = {
    actionInfo: ActionInfo;
    formInfo: FormInfo;
    isEmailSent: boolean;
    setIsEmailSent: (bool: boolean) => void;
    selectedIssue: Issue;
    setEmailBody: React.Dispatch<React.SetStateAction<string>>;
};

export default function SendAnEmail({
    actionInfo,
    formInfo,
    isEmailSent,
    setIsEmailSent,
    selectedIssue,
    setEmailBody,
}: Props) {
    const { data: prefixes, error: prefixError } = useSWR<string[], ErrorResponse>("/values/prefixes", fetcher);
    const { data: locTopics, error: locTopicsError } = useSWR<string[], ErrorResponse>(
        "/values/library-of-congress-topics",
        fetcher
    );
    const formRef = useRef<HTMLFormElement>(null);
    const [emailInfo, setEmailInfo] = useState({
        prefix: "",
        firstName: "",
        lastName: "",
        subject: "",
        body: "",
        selectedLocTopics: [] as MultiValue<{ value: string; label: string }>,
    });
    const [sendEmailError, setSendEmailError] = useState("");
    const [emailState, setEmailState] = useState<"titleing" | "prompting" | "reviewing" | "done">("titleing");

    useEffect(() => {
        emailState === "prompting" && scrollToId("email_prompts");
        emailState === "reviewing" && scrollToId("review_email");
        emailState === "done" && setIsEmailSent(true);
    }, [emailState, setIsEmailSent]);

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
        setEmailBody(emailInfo.body);
        setIsEmailSent(true);
    };

    const topicOptions = locTopics?.map((t) => ({ value: t, label: t })) || [];
    const error = prefixError?.message || locTopicsError?.message || sendEmailError;

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
                    <h3 className="h-4">Issue Guide</h3>
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
            <Form ref={formRef} onSubmit={sendEmail}>
                <Row>
                    <Col lg="4">
                        <h3 className="h4">Tips</h3>
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
                    </Col>
                    <Col lg="8">
                        <h3 className="h-4">Draft Your Email</h3>
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
                                <div className="position-relative mb-3">
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
                                        aria-label="Choose a Letter topic"
                                    />
                                    {/* Added this invisible input to make react-select dropdown simulate HTML validation. Open issue at https://github.com/JedWatson/react-select/issues/4327*/}
                                    {!emailInfo.selectedLocTopics.length && (
                                        <input
                                            className="position-absolute"
                                            tabIndex={-1}
                                            autoComplete="off"
                                            style={{
                                                opacity: 0,
                                                height: 0,
                                                top: "calc(100% - 6px)",
                                            }}
                                            required
                                        />
                                    )}
                                </div>
                            </Col>
                        </Row>
                    </Col>
                </Row>
                <Row className="mt-2 mb-4 pb-2">
                    <Col>
                        <Button
                            variant="secondary"
                            className="w-100"
                            disabled={emailState !== "titleing"}
                            onClick={() => setEmailState("done")}
                        >
                            Skip to Call
                        </Button>
                    </Col>
                    <Col>
                        <Button
                            variant="secondary"
                            className="w-100"
                            disabled={emailState !== "titleing"}
                            onClick={() => {
                                if (formRef.current!.reportValidity()) setEmailState("reviewing");
                            }}
                        >
                            Draft from Scratch
                        </Button>
                    </Col>
                    <Col>
                        <Button
                            className="w-100 text-dark"
                            disabled={emailState !== "titleing"}
                            onClick={() => {
                                if (formRef.current!.reportValidity()) setEmailState("prompting");
                            }}
                        >
                            Draft with Prompts
                        </Button>
                    </Col>
                </Row>
                {emailState !== "titleing" && (
                    <Prompts
                        emailState={emailState}
                        setEmailState={setEmailState}
                        setEmailBody={(body: string) => setEmailInfo((info) => ({ ...info, body }))}
                    />
                )}
                {(emailState === "reviewing" || emailState === "done") && (
                    <>
                        <hr id="review_email" />
                        <Row className="mt-4">
                            <Form.Group className="mb-3 h-100" controlId="emailForm.body">
                                <Form.Label>Your Email</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={9}
                                    placeholder="Write your why..."
                                    value={emailInfo.body}
                                    onChange={(e) => setEmailInfo({ ...emailInfo, body: e.currentTarget.value })}
                                    disabled={isEmailSent}
                                    required={emailState === "reviewing"}
                                />
                            </Form.Group>
                        </Row>
                        <Row className="mt-2">
                            <Col>
                                <Button
                                    variant="secondary"
                                    className="w-100"
                                    disabled={isEmailSent}
                                    onClick={() => setIsEmailSent(true)}
                                >
                                    Skip to Call
                                </Button>
                            </Col>
                            <Col>
                                <Button type="submit" className="w-100 text-dark" disabled={isEmailSent}>
                                    {!sendEmailError ? "Send Email" : "Try again"}
                                </Button>
                            </Col>
                        </Row>
                    </>
                )}
            </Form>
            <ErrorMessage message={error} />
        </div>
    );
}
