import { ErrorResponse, signUpAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import { EmailInfo } from "common/models/EmailInfo";
import { FormInfo } from "common/models/FormInfo";
import { useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";

type Props = {
    formInfo: FormInfo;
    emailInfo: EmailInfo;
    areas: { shortName: string; fullName: string }[] | undefined;
    areasError: ErrorResponse | undefined;
    isJoinedMission: boolean;
    setIsJoinedMission: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function JoinMission({
    formInfo,
    emailInfo,
    areas,
    areasError,
    isJoinedMission,
    setIsJoinedMission,
}: Props) {
    const [joinInfo, setJoinInfo] = useState({
        email: formInfo.email,
        firstName: emailInfo.firstName,
        lastName: emailInfo.lastName,
        postalCode: formInfo.postalCode,
        state: formInfo.state,
        priorExperience: false,
    });
    const [isSending, setIsSending] = useState(false);
    const [signUpError, setSignUpError] = useState("");

    const signUp = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setSignUpError("");
        setIsSending(true);
        const response = await signUpAPI(
            joinInfo.email,
            joinInfo.firstName,
            joinInfo.lastName,
            joinInfo.state,
            joinInfo.postalCode,
            joinInfo.priorExperience
        );
        setIsSending(false);
        if (!response.successful) {
            setSignUpError(response?.error ?? "Failed to sign up");
            return;
        }
        setIsJoinedMission(true);
    };

    return (
        <div className="pb-2">
            <h2 className="text-pink fw-bold mb-3">Join Our Mission</h2>
            <p>Sign up to be a changemaker to make productive, political action a habit!</p>
            {areas && (
                <Form onSubmit={signUp}>
                    <Row>
                        <Col lg="6">
                            <Form.Group className="mb-3 h-100" controlId="signUp.firstName">
                                <Form.Label>First Name</Form.Label>
                                <Form.Control
                                    value={joinInfo.firstName}
                                    onChange={(e) => setJoinInfo({ ...joinInfo, firstName: e.currentTarget.value })}
                                    disabled={isJoinedMission}
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
                                    disabled={isJoinedMission}
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
                                    disabled={isJoinedMission}
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
                                    disabled={isJoinedMission}
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
                                    disabled={isJoinedMission}
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
                        <Form.Group className="mb-3 mt-1" controlId="signUp.priorExperience">
                            <Form.Check
                                className="text-start"
                                checked={joinInfo.priorExperience}
                                onChange={() =>
                                    setJoinInfo({ ...joinInfo, priorExperience: !joinInfo.priorExperience })
                                }
                                type="checkbox"
                                label="Do you have any prior political action or organizing experience?"
                                disabled={isJoinedMission}
                            />
                            <Form.Text className="text-white">
                                No experience, no problem! Over half of our community members are doing this for the
                                first time.
                            </Form.Text>
                        </Form.Group>
                    </Row>
                    <Row className="mt-2">
                        <Col>
                            <Button
                                variant="secondary"
                                className="w-100"
                                disabled={isJoinedMission}
                                onClick={() => setIsJoinedMission(true)}
                            >
                                No Thanks
                            </Button>
                        </Col>
                        <Col>
                            <Button type="submit" className="w-100 text-dark" disabled={isJoinedMission}>
                                {!signUpError ? "Become a Changemaker" : "Try again"}
                            </Button>
                        </Col>
                    </Row>
                </Form>
            )}
            <ErrorMessage message={signUpError || areasError?.message} />
        </div>
    );
}
