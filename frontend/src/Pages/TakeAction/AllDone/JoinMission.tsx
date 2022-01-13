import { ErrorResponse, signUpAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import { EmailInfo } from "common/models/EmailInfo";
import { FormInfo } from "common/models/FormInfo";
import { useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";

type Props = {
    formInfo: FormInfo;
    emailInfo: EmailInfo;
    areas: { shortName: string; fullName: string }[] | undefined;
    areasError: ErrorResponse | undefined;
};

export default function JoinMission({ formInfo, emailInfo, areas, areasError }: Props) {
    const [joinInfo, setJoinInfo] = useState({
        email: formInfo.email,
        firstName: emailInfo.firstName,
        lastName: emailInfo.lastName,
        postalCode: formInfo.postalCode,
        state: formInfo.state,
        referral: "",
        actionReason: "",
        socialVerification: "",
        priorExperience: false,
    });
    const [signUpSuccess, setSignUpSuccess] = useState(false);
    const [signUpError, setSignUpError] = useState("");

    const signUp = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setSignUpError("");
        const response = await signUpAPI(
            joinInfo.email,
            joinInfo.firstName,
            joinInfo.lastName,
            joinInfo.state,
            joinInfo.postalCode,
            joinInfo.referral,
            joinInfo.actionReason,
            joinInfo.socialVerification,
            joinInfo.priorExperience
        );
        if (!response.successful) {
            setSignUpError(response?.error ?? "Failed to sign up");
            return;
        }
        setSignUpSuccess(true);
    };

    return (
        <div className="pb-2">
            <h2 className="text-center">Join our community!</h2>
            <p className="fs-5 mb-4 text-center">
                Sign up to be a changemaker to make productive, political action a habit.
            </p>
            {areas && (
                <Form onSubmit={signUp}>
                    <Row>
                        <Col lg="6">
                            <Form.Group className="mb-3 h-100" controlId="signUp.firstName">
                                <Form.Label>First Name</Form.Label>
                                <Form.Control
                                    value={joinInfo.firstName}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, firstName: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col lg="6">
                            <Form.Group className="mb-3 h-100" controlId="signUp.lastName">
                                <Form.Label>Last Name</Form.Label>
                                <Form.Control
                                    value={joinInfo.lastName}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, lastName: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    required
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row>
                        <Col lg="5">
                            <Form.Group className="mb-3 h-100" controlId="signUp.email">
                                <Form.Label>Email</Form.Label>
                                <Form.Control
                                    value={joinInfo.email}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, email: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col lg="3">
                            <Form.Group className="mb-3 h-100" controlId="signUp.postalCode">
                                <Form.Label>Zip Code</Form.Label>
                                <Form.Control
                                    value={joinInfo.postalCode}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, postalCode: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col lg="4">
                            <Form.Group className="mb-2" controlId="signUp.state">
                                <Form.Label>State</Form.Label>
                                <Form.Select
                                    value={formInfo.state}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, state: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    required
                                >
                                    <option value="">Select</option>
                                    {areas?.map((area) => (
                                        <option key={area.shortName} value={area.shortName}>
                                            {area.fullName}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Form.Group className="mb-3 h-100" controlId="signUp.referral">
                                <Form.Label>How did you hear about us?</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={4}
                                    value={joinInfo.referral}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, referral: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    placeholder="Social media, a friend..."
                                    required
                                />
                                <Form.Text className="text-white">
                                    If a current changemaker referred you, please tell us who. We like to give
                                    high-fives!
                                </Form.Text>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Form.Group className="mb-3 h-100" controlId="signUp.actionReason">
                                <Form.Label>Why did you choose to take action?</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={4}
                                    value={joinInfo.actionReason}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, actionReason: e.currentTarget.value })}
                                    disabled={signUpSuccess}
                                    placeholder="I choose to take action for my kids"
                                    required
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Form.Group className="mb-3 h-100" controlId="signUp.socialVerification">
                                <Form.Label>LinkedIn, Twitter, Instagram, or other social profile?</Form.Label>
                                <Form.Control
                                    value={joinInfo.socialVerification}
                                    onChange={(e) =>
                                        setJoinInfo({ ...joinInfo, socialVerification: e.currentTarget.value })
                                    }
                                    disabled={signUpSuccess}
                                    placeholder="@"
                                    required
                                />
                                <Form.Text className="text-white">So we can verify you’re human!</Form.Text>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row>
                        <Form.Group className="mb-3 mt-1" controlId="signUp.priorExperience">
                            <Form.Check
                                className="text-start"
                                checked={joinInfo.priorExperience}
                                onChange={() =>
                                    setJoinInfo({ ...joinInfo, priorExperience: !joinInfo.priorExperience })
                                }
                                type="checkbox"
                                label="Do you have any prior political action or organizing experience?"
                                disabled={signUpSuccess}
                            />
                            <Form.Text className="text-white">
                                No experience, no problem! Over half of our community members are doing this for the
                                first time.
                            </Form.Text>
                        </Form.Group>
                    </Row>
                    <Row className="mt-2">
                        <Col>
                            <Button type="submit" className="w-100 text-dark" disabled={signUpSuccess}>
                                {!signUpError ? "Become a Changemaker" : "Try again"}
                            </Button>
                        </Col>
                    </Row>
                </Form>
            )}
            <ErrorMessage message={signUpError || areasError?.message} />
            {signUpSuccess && (
                <Row className="mt-2">
                    <Col>
                        <Alert variant="success" className="p-1 mt-2 text-center">
                            Sign Up Successful!
                        </Alert>
                    </Col>
                </Row>
            )}
        </div>
    );
}
