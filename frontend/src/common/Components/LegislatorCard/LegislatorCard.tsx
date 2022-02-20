import cx from "classnames";
import { Legislator } from "common/models/ActionInfo";
import { useRef, useState } from "react";
import { Badge, Button, Card, OverlayTrigger, Tooltip } from "react-bootstrap";
import { faCircleUser } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import lcvlogo from "./lcv.png";
import styles from "./LegislatorCard.module.css";

type Props = {
    legislator: Legislator;
    call?: {
        bioguideIdsCalled: string[];
        isPhoneCallMade: boolean;
        logCall: () => void;
    };
};

export default function LegislatorCard({ legislator, call }: Props) {
    const surname = legislator.area.districtNumber ? "Rep." : "Sen.";
    const legislatorCalled = call?.bioguideIdsCalled.includes(legislator.bioguideId);
    const legislatorContainerRef = useRef(null);
    const [imageError, setImageError] = useState(false);

    return (
        <Card
            key={legislator.name}
            bg="light-grey"
            className={`${styles.cardContainer} pt-4 mb-2 pb-3`}
            style={{ width: "12rem" }}
        >
            <div className={`${styles.imageContainer} position-relative m-auto`}>
                {!imageError
                    ? <img alt="" className={styles.image} src={legislator.imageUrl} onError={() => setImageError(true)} />
                    : <FontAwesomeIcon size="5x" color="rgb(24, 49, 83)" icon={faCircleUser} />}
            </div>
            <Card.Body className="pt-2 pb-1 ps-2 pe-2 d-flex flex-column justify-content-between">
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
                        <div className="text-purple mb-2 fs-7 fw-bold" ref={legislatorContainerRef}>
                            @{legislator.twitter}
                        </div>
                        {legislator.lcvScores.length > 0 && (
                            <OverlayTrigger
                                placement="bottom"
                                overlay={
                                    <Tooltip className="position-absolute" id={`lcv-${legislator.bioguideId}`}>
                                        The League of Conservation Voters (LCV) calculates a score of 0-100 for
                                        favorable environmental votes in Congress. Shown above is the{" "}
                                        <b>
                                            {legislator.lcvScores[0].scoreType.type === "lifetime"
                                                ? "lifetime"
                                                : legislator.lcvScores[0].scoreType.year}
                                        </b>{" "}
                                        score.
                                    </Tooltip>
                                }
                            >
                                <Button
                                    className={`${styles.lcvScoreContainer} d-flex justify-content-center align-items-center flex-row bg-transparent border-0 w-100 d-block`}
                                >
                                    <img className="h-100 align-baseline me-1" alt="LCV score" src={lcvlogo} />
                                    <div className="text-dark fw-bold me-1">{legislator.lcvScores[0].score}</div>
                                </Button>
                            </OverlayTrigger>
                        )}
                    </div>
                ) : (
                    <div className={cx(styles.callContainer, "ms-auto me-auto")}>
                        {legislator.phoneNumbers.map((n) => (
                            <div key={n} className="mb-2 d-flex justify-content-center text-center">
                                {call.isPhoneCallMade || legislatorCalled ? (
                                    <div className="text-dark">{n}</div>
                                ) : (
                                    <a href={`tel:${n.replace("-", "")}`}>{n}</a>
                                )}
                            </div>
                        ))}
                        <Button
                            className={cx("text-dark mt-1 w-100", {
                                "fw-bold": !call.isPhoneCallMade && !legislatorCalled,
                            })}
                            onClick={() => call.logCall()}
                            disabled={call.isPhoneCallMade || legislatorCalled}
                        >
                            I called!
                        </Button>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
}
