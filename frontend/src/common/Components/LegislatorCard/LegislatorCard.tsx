import { Legislator } from "common/models/ActionInfo";
import { Badge, Card, Form } from "react-bootstrap";
import lcvlogo from "./lcv.png";
import styles from "./LegislatorCard.module.css";

type Props = {
    legislator: Legislator;
    call?: {
        phoneNumbersCalled: string[];
        isPhoneCallMade: boolean;
        logCall: (phoneNumber: string) => void;
    };
};

export default function LegislatorCard({ legislator, call }: Props) {
    const surname = legislator.area.districtNumber ? "Rep." : "Sen.";
    return (
        <Card
            key={legislator.name}
            bg="light-grey"
            className={`${styles.cardContainer} pt-4 mb-2 pb-3`}
            style={{ width: "12rem" }}
        >
            <div className={`${styles.imageContainer} m-auto`}>
                <img alt="" className={styles.image} src={legislator.imageUrl} />
            </div>
            <Card.Body className="pt-2 pb-2 ps-2 pe-2 d-flex flex-column justify-content-between">
                <Card.Title className="text-dark fs-6 mb-2">
                    {surname} {legislator.name}
                </Card.Title>
                {!call ? (
                    <div>
                        <div className="text-dark fs-6 mb-2 fw-bold d-flex justify-content-center">
                            <Badge bg="purple" className="pt-2 pb-2 me-1 text-capitalize fw-light text-dark">
                                {legislator.partyAffiliation}
                            </Badge>
                            <Badge bg="purple" className="pt-2 pb-2 me-1 text-capitalize fw-light text-dark">
                                {legislator.area.state}
                                {legislator.area.districtNumber ? `-${legislator.area.districtNumber}` : ""}
                            </Badge>
                        </div>
                        <div className="text-purple mb-2 fs-7 fw-bold">@{legislator.twitter}</div>
                        {legislator.lcvScores.length > 0 && (
                            <div
                                className={`${styles.lcvScoreContainer} d-flex justify-content-center align-items-center flex-row`}
                            >
                                <img className="h-100 align-baseline me-1" alt="LCV score" src={lcvlogo} />
                                <div className="text-dark fw-bold me-1">{legislator.lcvScores[0].score}</div>
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="d-flex flex-column align-items-center">
                        {legislator.phoneNumbers.map((n) => {
                            const callMade = call.phoneNumbersCalled.includes(n);
                            return (
                                <Form.Group
                                    key={n}
                                    className={`${styles.checkboxContainer} mb-2 d-flex justify-content-between`}
                                    controlId={`call${n}`}
                                >
                                    <Form.Check
                                        checked={callMade}
                                        disabled={callMade || call.isPhoneCallMade}
                                        onClick={() => call.logCall(n)}
                                        className={styles.formCheckInput}
                                        type="checkbox"
                                    />
                                    {callMade || call.isPhoneCallMade ? (
                                        <div className="text-dark">{n}</div>
                                    ) : (
                                        <a href={`tel:${n.replace("-", "")}`}>{n}</a>
                                    )}
                                </Form.Group>
                            );
                        })}
                    </div>
                )}
            </Card.Body>
        </Card>
    );
}
