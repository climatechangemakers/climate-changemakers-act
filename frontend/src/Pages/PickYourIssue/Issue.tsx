import { Button, Card } from "react-bootstrap"
import { Issue } from "../../models/IssuesResponse"
import styles from "./Issue.module.css"

type Props = {
    onClick: () => void;
    title: string;
    selectedIssue: Issue | undefined;
}

export default function IssueCard({ onClick, title, selectedIssue }: Props) {
    return (
        <Button
            onClick={onClick}
            className={`${styles.issueCard} ${selectedIssue?.title === title ? styles.issueCardSelected : ""} d-flex justify-content-around align-items-start card h-100 w-100 px-4 py-4`}
            variant="white"
            disabled={!!selectedIssue}>
            <Card.Title className={`${styles.issueTitle} text-start`}>{title}</Card.Title>
            <p className={`${styles.learnMore} mb-0 fw-bold fs-6`}>Learn more</p>
        </Button>
    )
}