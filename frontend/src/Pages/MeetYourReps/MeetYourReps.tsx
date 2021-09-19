import { Card } from "react-bootstrap";
import { ActionInfo } from "../../models/ActionInfo";
import styles from "./MeetYourReps.module.css"
import lcvlogo from "./lcv.jpg";

type Props = {
    actionInfo: ActionInfo;
}

export default function MeetYourReps({ actionInfo }: Props) {
    return (
        <div className="pb-2">
            <h2 className="text-start mb-4 mt-4">Meet Your Reps</h2>
            <div className="mb-2 justify-content-between d-flex flex-md-row flex-column align-items-center">
                {actionInfo.legislators.map(legislator =>
                    <Card key={legislator.name} className="pt-3 mb-3" style={{ width: '14rem' }}>
                        <div className={`${styles.imageContainer} m-auto`}>
                            <img alt="" className={styles.image} src={legislator.imageUrl} />
                        </div>
                        <Card.Body>
                            <Card.Title className="text-dark fs-5 fw-light mb-1">{legislator.name}</Card.Title>
                            <div className="text-dark fs-6 mb-3 fw-bold">
                                <span className="text-capitalize">{legislator.partyAffiliation} </span>
                                - {legislator.area.state}{legislator.area.districtNumber ? `-${legislator.area.districtNumber}` : ""}
                            </div>
                            { legislator.lcvScores.length > 0 && <div className={`${styles.lcvScoreContainer} d-flex justify-content-center align-items-center flex-row`}>
                                <img className="h-100 align-baseline me-1" alt="LCV score" src={lcvlogo} />
                                <div className="text-dark fs-6 fw-normal">{legislator.lcvScores[0].score}%</div>
                            </div> }
                        </Card.Body>
                    </Card>)}
            </div>
            <p className="fs-5">Let’s let them know what we think by calling, emailing, and tweeting them!</p>
        </div>
    )
}
