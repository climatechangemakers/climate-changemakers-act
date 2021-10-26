import { useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";

type Props = {
    emailState: "prompting" | "reviewing" | "done";
    setEmailState: React.Dispatch<React.SetStateAction<"titleing" | "prompting" | "reviewing" | "done">>;
    setEmailBody: (body: string) => void;
}

export default function Prompts({ emailState, setEmailState, setEmailBody }: Props) {
    const [emailPrompts, setEmailPrompts] = useState({
        salutation: "",
        policyAsk: "",
        whyItMatters: "",
        whyYouCare: "",
        reiteratePolicyAsk: "",
    });

    const addPromptsToEmail = () => {
        setEmailBody(Object.values(emailPrompts)
            .filter((o) => !!o)
            .join("\n\n"));

        setEmailState("reviewing");
    }

    return (
        <>
            <hr id="email_prompts" />
            <Row>
                <Col lg="4">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.salutation">
                        <Form.Label>Salutation</Form.Label>
                        <Form.Control
                            value={emailPrompts.salutation}
                            onChange={(e) =>
                                setEmailPrompts({ ...emailPrompts, salutation: e.currentTarget.value })
                            }
                            disabled={emailState !== "prompting"}
                            placeholder="Dear..."
                        />
                    </Form.Group>
                </Col>
            </Row>
            <Row>
                <Col xs="12">
                    <Form.Group className="mb-3 h-100" controlId="emailForm.policyAsk">
                        <Form.Label>Policy Ask</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={emailPrompts.policyAsk}
                            onChange={(e) =>
                                setEmailPrompts({ ...emailPrompts, policyAsk: e.currentTarget.value })
                            }
                            disabled={emailState !== "prompting"}
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
                            onChange={(e) =>
                                setEmailPrompts({ ...emailPrompts, whyItMatters: e.currentTarget.value })
                            }
                            disabled={emailState !== "prompting"}
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
                            onChange={(e) =>
                                setEmailPrompts({ ...emailPrompts, whyYouCare: e.currentTarget.value })
                            }
                            disabled={emailState !== "prompting"}
                            placeholder="As a constituent I am concerned because..."
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
                            disabled={emailState !== "prompting"}
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
                        disabled={emailState !== "prompting"}
                        onClick={() => setEmailState("reviewing")}
                    >
                        Draft from Scratch
                    </Button>
                </Col>
                <Col>
                    <Button
                        className="w-100 text-dark"
                        disabled={emailState !== "prompting"}
                        onClick={addPromptsToEmail}
                    >
                        Review Email
                    </Button>
                </Col>
            </Row>
        </>)
}