import { useEffect, useState } from "react";
import { Alert, Col, Row } from "react-bootstrap";
import { issueAPI } from "../../api/ClimateChangemakersAPI";
import { Issue, IssuesResponse } from "../../models/IssuesResponse";
import IssueCard from "./Issue";

type Props = {
    issues: IssuesResponse | undefined;
    setIssues: (issues: IssuesResponse) => void;
    selectedIssue: Issue | undefined;
    setSelectedIssue: (issue: Issue) => void;
}

export default function PickYourIssue({ issues, setIssues, selectedIssue, setSelectedIssue }: Props) {
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        const fetchIssues = async () => {
            const response = await issueAPI();
            if (typeof response === "string") {
                setErrorMessage(response);
                return;
            }
            setIssues(response);
        }
        fetchIssues();
    }, [setIssues])

    return (
        <div className="pt-2 pb-3">
            <h2 className="text-start pb-4">Which issue do you care most about?</h2>
            {!!issues &&
                <>
                    <Row className="pb-4">
                        <Col md="6">
                            <h3 className="text-start">Featured</h3>
                            <IssueCard
                                onClick={() => setSelectedIssue(issues.focusIssue)}
                                title={issues.focusIssue.title}
                                selectedIssue={selectedIssue} />
                        </Col>
                    </Row>
                    <Row>
                        <h3 className="text-start">Top issues</h3>
                        {issues.otherIssues.map(issue =>
                            <Col key={issue.title} className="pb-3 d-flex align-items-center" md="4" sm="6">
                                <IssueCard
                                    onClick={() => setSelectedIssue(issue)}
                                    title={issue.title}
                                    selectedIssue={selectedIssue} />
                            </Col>)}
                    </Row>
                </>}
            {errorMessage &&
                <Row>
                    <Col>
                        <Alert variant="danger" className="p-1 mt-2">
                            {errorMessage}
                        </Alert>
                    </Col>
                </Row>}
        </div>
    )
}