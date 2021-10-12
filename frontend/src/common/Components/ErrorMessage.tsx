import { Alert, Col, Row } from "react-bootstrap";

type Props = {
    message?: string;
};

export default function ErrorMessage({ message }: Props) {
    return (
        !message
            ? <></>
            : <Row>
                <Col>
                    <Alert variant="danger" className="p-1 mt-2 text-center">
                        {message}
                    </Alert>
                </Col>
            </Row>
    );
}
