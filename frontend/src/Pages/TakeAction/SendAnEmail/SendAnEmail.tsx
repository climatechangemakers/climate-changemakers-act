import { ErrorResponse, fetcher, sendEmailAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import HiddenValidationInput from "common/Components/HiddenValidationInput";
import { scrollToId } from "common/lib/scrollToId";
import { ActionInfo } from "common/models/ActionInfo";
import { EmailInfo } from "common/models/EmailInfo";
import { EmailState } from "common/models/EmailState";
import { FormInfo } from "common/models/FormInfo";
import { Issue } from "common/models/Issue";
import { useEffect, useRef, useState } from "react";
import { Accordion, Button, Col, Form, Row } from "react-bootstrap";
import Select from "react-select";
import useSWRImmutable from "swr";
import emailIcon from "./email-icon.svg";
import Prompts from "./Prompts";

type Props = {
    actionInfo: ActionInfo;
    formInfo: FormInfo;
    isEmailDone: boolean;
    setIsEmailDone: (bool: boolean) => void;
    selectedIssue: Issue;
    emailInfo: EmailInfo;
    setEmailInfo: React.Dispatch<React.SetStateAction<EmailInfo>>;
};

const fromScratchEmail = (firstName: string) => `Hello, I’m ${firstName}, a constituent calling from [LOCATION].

As a [INTRODUCE YOURSELF: WHERE DO YOU LIVE? WHAT DO YOU DO? WHAT’S IMPORTANT TO YOU?], I am concerned about the climate crisis because [WHY YOU CARE ABOUT CLIMATE].
I’m calling to urge [Senator/Representative NAME] to take action by [CLEAR ASK].
This policy would [WHY IS THE POLICY IMPORTANT].
Now is the time for [Senator/Representative NAME] to address these pressing issues by [REPEAT CLEAR ASK].
Please pass along my concerns to the [Senator/Representative]. Thanks so much for taking my call, and please thank the [Senator/Representative] for their service and leadership.`;

export default function SendAnEmail({
    actionInfo,
    formInfo,
    isEmailDone,
    setIsEmailDone,
    selectedIssue,
    emailInfo,
    setEmailInfo,
}: Props) {
    const { data: prefixes, error: prefixError } = useSWRImmutable<string[], ErrorResponse>(
        "/values/prefixes",
        fetcher
    );
    const { data: locTopics, error: locTopicsError } = useSWRImmutable<string[], ErrorResponse>(
        "/values/library-of-congress-topics",
        fetcher
    );
    const formRef = useRef<HTMLFormElement>(null);
    const [sendEmailError, setSendEmailError] = useState("");
    const [emailState, setEmailState] = useState<EmailState>("titleing");
    const [isSending, setIsSending] = useState(false);
    const [bioguideIdsToSend, setBioguideIdsToSend] = useState(actionInfo.legislators.map((l) => l.bioguideId));
    const [isEmailSent, setIsEmailSent] = useState(false);

    useEffect(() => {
        emailState === "prompting" && scrollToId("email_prompts");
        emailState === "reviewing" && scrollToId("review_email");
        emailState === "done" && setIsEmailDone(true);
    }, [emailState, setIsEmailDone]);

    const sendEmail = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setSendEmailError("");

        setIsSending(true);
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
            bioguideIdsToSend
        );
        setIsSending(false);

        if (!response.successful) {
            if (typeof response.error === "object") {
                setBioguideIdsToSend(response.error.failedBioguideIds);
                setSendEmailError(
                    `Failed to send ${response.error.failedBioguideIds.length} email${
                        response.error.failedBioguideIds.length > 1 ? "s" : ""
                    }. Click to retry.`
                );
                return;
            }
            setSendEmailError(response?.error ?? "Failed to send email");
            return;
        }
        setIsEmailSent(true);
        setIsEmailDone(true);
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
                Email your reps directly using our form (you can opt to use our prompts for guidance or draft from a
                blank slate). You can always scroll up to pull talking points from the Issue Guide; feel free to
                copy/paste/revise to make your message personalized and unique. Your email is private and will not be
                stored.
            </p>
            {selectedIssue.talkingPoints.length > 0 && (
                <div className="mb-3">
                    <h3 className="h-4">Issue Guide</h3>
                    <Accordion>
                        {selectedIssue.talkingPoints.map((point, i) => (
                            <Accordion.Item key={i} eventKey={i.toString()}>
                                <Accordion.Header>{point.title}</Accordion.Header>
                                <Accordion.Body className="p-0 h-100 text-dark fs-6">
                                    <div
                                        className="pt-4 py-2 px-3 bg-purple-secondary"
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
                                "Include personal details and anecdotes",
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
                                    <Form.Label>Your Prefix</Form.Label>
                                    <Form.Select
                                        value={emailInfo.prefix}
                                        onChange={(e) => setEmailInfo({ ...emailInfo, prefix: e.currentTarget.value })}
                                        disabled={isEmailDone}
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
                                    <Form.Label>Your First Name</Form.Label>
                                    <Form.Control
                                        value={emailInfo.firstName}
                                        onChange={(e) =>
                                            setEmailInfo({ ...emailInfo, firstName: e.currentTarget.value })
                                        }
                                        disabled={isEmailDone}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col lg="5">
                                <Form.Group className="mb-3 h-100" controlId="emailForm.lastName">
                                    <Form.Label>Your Last Name</Form.Label>
                                    <Form.Control
                                        value={emailInfo.lastName}
                                        onChange={(e) =>
                                            setEmailInfo({ ...emailInfo, lastName: e.currentTarget.value })
                                        }
                                        disabled={isEmailDone}
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
                                        disabled={isEmailDone}
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
                                        isDisabled={isEmailDone}
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
                                    <HiddenValidationInput when={!emailInfo.selectedLocTopics.length} />
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
                            Skip to call
                        </Button>
                    </Col>
                    <Col>
                        <Button
                            variant="secondary"
                            className="w-100"
                            disabled={emailState !== "titleing"}
                            onClick={() => {
                                if (formRef.current!.reportValidity()) setEmailState("reviewing");
                                setEmailInfo((info) => ({ ...info, body: fromScratchEmail(info.firstName) }));
                            }}
                        >
                            Draft from scratch
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
                            Draft with prompts
                        </Button>
                    </Col>
                </Row>
                {emailState !== "titleing" && (
                    <Prompts
                        formRef={formRef}
                        firstName={emailInfo.firstName}
                        emailState={emailState}
                        setEmailState={setEmailState}
                        setEmailBody={(body: string) => setEmailInfo((info) => ({ ...info, body }))}
                        isEmailDone={isEmailDone}
                        fromScratchEmail={fromScratchEmail(emailInfo.firstName)}
                    />
                )}
                {(emailState === "reviewing" || emailState === "done") && (
                    <>
                        <hr id="review_email" />
                        <Row className="mt-4">
                            <Form.Group className="mb-3 h-100" controlId="emailForm.body">
                                <Form.Label>
                                    <h3 className="h-4">Your Email</h3>
                                </Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={12}
                                    placeholder="Draft your email"
                                    value={emailInfo.body}
                                    onChange={(e) => setEmailInfo({ ...emailInfo, body: e.currentTarget.value })}
                                    disabled={isEmailDone}
                                    required={emailState === "reviewing"}
                                />
                            </Form.Group>
                        </Row>
                        <Row className="mt-2">
                            <Col>
                                <Button
                                    variant="secondary"
                                    className="w-100"
                                    disabled={isEmailDone}
                                    onClick={() => setIsEmailDone(true)}
                                >
                                    Skip to call
                                </Button>
                            </Col>
                            <Col>
                                <Button type="submit" className="w-100 text-dark" disabled={isEmailDone}>
                                    {isEmailSent
                                        ? "Email sent!"
                                        : isSending
                                        ? "Sending..."
                                        : sendEmailError
                                        ? "Try again"
                                        : "Send email"}
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
