import { Card } from "react-bootstrap";
import { ActionInfo } from "../../models/ActionInfo";
import styles from "./MeetYourReps.module.css"

type Props = {
    actionInfo: ActionInfo;
}

export default function MeetYourReps({ actionInfo }: Props) {
    return (
        <div>
            <h2 className="d-flex justify-content-md-start justify-content-center mb-4 mt-4">Your reps</h2>
            <div className="mb-3 justify-content-between d-flex flex-md-row flex-column align-items-center">
                {actionInfo.legislators.map(legislator =>
                    <Card className="pt-3 mb-3" style={{ width: '14rem' }}>
                        <div className={`${styles.imageContainer} m-auto`}>
                            <img className={styles.image} src={legislator.imageUrl} />
                        </div>
                        <Card.Body>
                            <Card.Title className="text-dark fs-6 fw-normal">{legislator.name}</Card.Title>
                        </Card.Body>
                    </Card>)}
            </div>
        </div>
    )
}