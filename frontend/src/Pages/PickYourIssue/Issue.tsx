import cx from "classnames";
import { Issue } from "common/models/Issue";
import { Button, Card } from "react-bootstrap";
import styles from "./Issue.module.css";

type Props = {
    onClick: () => void;
    issue: Issue;
    selectedIssue: Issue | undefined;
    focusIssue?: boolean;
};

export default function IssueCard({ onClick, issue, selectedIssue, focusIssue }: Props) {
    return (
        <Button
            onClick={onClick}
            className={cx(styles.issueCard, "border-0 d-flex card bg-light-grey text-start h-100 w-100", {
                [styles.issueCardSelected]: selectedIssue?.title === issue.title,
                "align-items-md-center flex-sm-row px-4 py-4": focusIssue,
                "px-3 py-3": !focusIssue,
            })}
        >
            <div
                className={cx("d-flex", {
                    [styles.focusImageContainer]: focusIssue,
                })}
            >
                <img className={styles.issueImage} alt="" src={issue.imageUrl} />
            </div>
            <div
                className={cx("flex-grow-1 d-flex align-items-between flex-column h-100 mt-3", {
                    "ms-sm-3 mt-sm-0": focusIssue,
                })}
            >
                <div>
                    <Card.Title className={styles.issueText}>{issue.title}</Card.Title>
                    <p className={`${styles.issueText} fs-6 mt-2`}>{issue.description}</p>
                </div>
                <p className={`${styles.learnMore} fw-bold fs-7 mt-auto mb-0`}>Choose issue &#10230;</p>
            </div>
        </Button>
    );
}
