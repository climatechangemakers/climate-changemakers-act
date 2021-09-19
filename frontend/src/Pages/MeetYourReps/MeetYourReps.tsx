import { Col, Row } from "react-bootstrap";
import { ActionInfo } from "../../models/ActionInfo";
import Representative from "./Representative";

type Props = {
    actionInfo: ActionInfo;
}

export default function MeetYourReps({ actionInfo }: Props) {
    return (
        <div className="pb-2">
            <h2 className="text-start mb-4 mt-4">Meet Your Reps</h2>
            <Row className="mb-4 justify-content-between d-flex flex-md-row flex-column">
                {actionInfo.legislators.map((legislator, i) =>
                    <Col className="d-flex justify-content-center" xs="12" md="4" key={i}>
                        <Representative legislator={legislator} />
                    </Col>)}
            </Row>
            <p className="fs-5">Let’s let them know what we think by calling, emailing, and tweeting them!</p>
        </div>
    )
}
