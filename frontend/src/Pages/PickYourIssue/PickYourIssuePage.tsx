import ErrorMessage from "common/Components/ErrorMessage";
import Layout from "common/Components/Layout";
import useIssues from "common/hooks/useIssues";
import useSessionStorage from "common/hooks/useSessionStorage";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/Issue";
import { useEffect } from "react";
import { Col, Row, Spinner } from "react-bootstrap";
import { Redirect, useHistory } from "react-router-dom";
import IssueCard from "./Issue";

export default function PickYourIssuePage() {
    const { data: issues, error: issuesError } = useIssues();
    const [selectedIssue, setSelectedIssue] = useSessionStorage<Issue | undefined>("selectedIssue");
    const [actionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const history = useHistory();

    useEffect(() => {
        setSelectedIssue(undefined);
    }, [setSelectedIssue]);

    const handleIssueSelect = (issue: Issue) => {
        setSelectedIssue(issue);
        history.push("/take-action");
    };

    if (!actionInfo) return <Redirect to="/" />;

    return (
        <Layout>
            <Row className="pt-4 pb-3">
                <div className="d-flex">
                    <h2 className="text-start pb-4 me-3">Choose an issue</h2>
                    {!issues && (
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                    )}
                </div>
                {issues && (
                    <>
                        <Row className="pb-5">
                            <h3 className="text-start mb-3 h4">Focus Issue</h3>
                            <div>
                                <IssueCard
                                    onClick={() => handleIssueSelect(issues.focusIssue)}
                                    issue={issues.focusIssue}
                                    selectedIssue={selectedIssue}
                                    focusIssue
                                />
                            </div>
                        </Row>
                        <Row>
                            <h3 className="text-start mb-3 h4">Other Issues</h3>
                            {issues.otherIssues.map((issue) => (
                                <Col key={issue.title} className="pb-3 d-flex align-items-center" md="4" sm="6">
                                    <IssueCard
                                        onClick={() => handleIssueSelect(issue)}
                                        issue={issue}
                                        selectedIssue={selectedIssue}
                                    />
                                </Col>
                            ))}
                        </Row>
                    </>
                )}
                <ErrorMessage message={issuesError?.message} />
            </Row>
        </Layout>
    );
}
