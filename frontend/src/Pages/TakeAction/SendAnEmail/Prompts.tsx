import HiddenValidationInput from "common/Components/HiddenValidationInput";
import { EmailState } from "common/models/EmailState";
import { useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";

type Props = {
    formRef: React.RefObject<HTMLFormElement>;
    firstName: string;
    emailState: EmailState;
    setEmailState: React.Dispatch<React.SetStateAction<EmailState>>;
    setEmailBody: (body: string) => void;
    isEmailDone: boolean;
};

const salutation = "To whom it may concern,";

export default function Prompts({ formRef, firstName, emailState, setEmailState, setEmailBody, isEmailDone }: Props) {
    const [emailPrompts, setEmailPrompts] = useState({
        policyAsk: "",
        whyItMatters: "",
        whyYouCare: "",
        reiteratePolicyAsk: "",
    });
    const [addedPrompts, setAddedPrompts] = useState(false);

    const addPromptsToEmail = () => {
        if (emailState === "reviewing" || formRef.current!.reportValidity()) {
            setEmailBody(
                [salutation, ...Object.values(emailPrompts).filter((o) => !!o), `Sincerely,\n${firstName}`].join("\n\n")
            );
            setAddedPrompts(true);
            setEmailState("reviewing");
        }
    };

    return (
        <>
            <hr id="email_prompts" />
            <h3 className="h-4 mb-3 mt-4">Email Prompts</h3>
            <Row>
                <Col xs="12">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.policyAsk">
                        <Form.Label>Policy Ask</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={emailPrompts.policyAsk}
                            onChange={(e) => setEmailPrompts({ ...emailPrompts, policyAsk: e.currentTarget.value })}
                            disabled={addedPrompts || isEmailDone}
                            placeholder="I am writing to urge you to..."
                        />
                    </Form.Group>
                </Col>
            </Row>
            <Row>
                <Col xs="12">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.whyItMatters">
                        <Form.Label>Why It Matters</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={emailPrompts.whyItMatters}
                            onChange={(e) => setEmailPrompts({ ...emailPrompts, whyItMatters: e.currentTarget.value })}
                            disabled={addedPrompts || isEmailDone}
                            placeholder="This policy would..."
                        />
                    </Form.Group>
                </Col>
            </Row>
            <Row>
                <Col xs="12">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.whyYouCare">
                        <Form.Label>Why You Care</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={emailPrompts.whyYouCare}
                            onChange={(e) => setEmailPrompts({ ...emailPrompts, whyYouCare: e.currentTarget.value })}
                            disabled={addedPrompts || isEmailDone}
                            placeholder="As your constituent I am concerned because..."
                        />
                    </Form.Group>
                </Col>
            </Row>
            <Row>
                <Col xs="12">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.reiteratePolicyAsk">
                        <Form.Label>Reiterate Policy Ask</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={emailPrompts.reiteratePolicyAsk}
                            onChange={(e) =>
                                setEmailPrompts({
                                    ...emailPrompts,
                                    reiteratePolicyAsk: e.currentTarget.value,
                                })
                            }
                            disabled={addedPrompts || isEmailDone}
                            placeholder="As my elected representative, I urge you to take action by..."
                        />
                    </Form.Group>
                </Col>
            </Row>
            <Row className="mt-2 mb-4 pb-2">
                <Col>
                    <Button
                        variant="secondary"
                        className="w-100"
                        disabled={addedPrompts || isEmailDone}
                        onClick={() => setEmailState("reviewing")}
                    >
                        Draft from scratch
                    </Button>
                </Col>
                <Col className="position-relative">
                    <Button
                        className="w-100 text-dark"
                        disabled={addedPrompts || isEmailDone}
                        onClick={addPromptsToEmail}
                    >
                        Review email
                    </Button>
                    <HiddenValidationInput
                        when={emailState === "prompting" && Object.values(emailPrompts).every((p) => !p)}
                        message="Please fill out at least one prompt"
                    />
                </Col>
            </Row>
        </>
    );
}
