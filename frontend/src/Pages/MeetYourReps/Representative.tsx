import { Card } from "react-bootstrap";
import styles from "./Representative.module.css"
import lcvlogo from "./lcv.jpg";
import { Legislator } from "../../models/ActionInfo";

type Props = {
    legislator: Legislator;
}

export default function Representative({ legislator }: Props) {
    return (
        <Card key={legislator.name} className="h-100 d-flex pt-3 mb-2" style={{ width: '14rem' }}>
            <div className={`${styles.imageContainer} m-auto`}>
                <img alt="" className={styles.image} src={legislator.imageUrl} />
            </div>
            <Card.Body className="pb-2">
                <Card.Title className="text-dark fs-5 fw-light mb-1">{legislator.name}</Card.Title>
                <div className="text-dark fs-6 mb-3 fw-bold">
                    <span className="text-capitalize">{legislator.partyAffiliation} </span>
                    - {legislator.area.state}{legislator.area.districtNumber ? `-${legislator.area.districtNumber}` : ""}
                </div>
                {legislator.lcvScores.length > 0 &&
                    <div className={`${styles.lcvScoreContainer} d-flex justify-content-center align-items-center flex-row`}>
                        <img className="h-100 align-baseline me-1" alt="LCV score" src={lcvlogo} />
                        <div className="text-dark fs-6 fw-normal">{legislator.lcvScores[0].score}%</div>
                    </div>}
            </Card.Body>
        </Card>)
}