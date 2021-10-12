import LegislatorCard from "common/Components/LegislatorCard/LegislatorCard";
import { ActionInfo } from "common/models/ActionInfo";
import { Col, Row } from "react-bootstrap";

type Props = {
    actionInfo: ActionInfo;
};

export default function MeetYourReps({ actionInfo }: Props) {
    return (
        <div className="pb-2">
            <h2 className="text-pink fw-bold mb-4">Meet Your Reps</h2>
            <Row className="mb-2 d-flex flex-md-row flex-column text-center">
                {actionInfo.legislators.map((legislator, i) => (
                    <Col className="d-flex justify-content-center" xs="12" md="4" key={i}>
                        <LegislatorCard legislator={legislator} />
                    </Col>
                ))}
            </Row>
        </div>
    );
}
