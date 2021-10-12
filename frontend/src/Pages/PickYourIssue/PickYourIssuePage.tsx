import { fetcher } from "common/api/ClimateChangemakersAPI";
import useSessionStorage from "common/hooks/useSessionStorage";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/Issue";
import { Alert, Col, Row } from "react-bootstrap";
import { Redirect, useHistory } from "react-router-dom";
import useSWR from "swr";
import IssueCard from "./Issue";

export default function PickYourIssuePage() {
    const { data: issues, error: issuesError } = useSWR<{ focusIssue: Issue; otherIssues: Issue[]; }, string>("/issues", fetcher);
    const [selectedIssue, setSelectedIssue] = useSessionStorage<Issue | undefined>("selectedIssue");
    const [actionInfo] = useSessionStorage<ActionInfo | undefined>("actionInfo");
    const history = useHistory();

    const handleIssueSelect = (issue: Issue) => {
        setSelectedIssue(issue);
        history.push("/take-action");
    }

    if (!actionInfo)
        return <Redirect to="/" />

    return (
        <div className="pt-2 pb-3">
            <h2 className="text-start pb-4">Which issue do you care most about?</h2>
            {!!issues &&
                <>
                    <Row className="pb-4">
                        <Col md="6">
                            <h3 className="text-start">Featured</h3>
                            <div>
                                <IssueCard
                                    onClick={() => handleIssueSelect(issues.focusIssue)}
                                    title={issues.focusIssue.title}
                                    selectedIssue={selectedIssue} />
                            </div>
                        </Col>
                    </Row>
                    <Row>
                        <h3 className="text-start">Top issues</h3>
                        {issues.otherIssues.map(issue =>
                            <Col key={issue.title} className="pb-3 d-flex align-items-center" md="4" sm="6">
                                <IssueCard
                                    onClick={() => handleIssueSelect(issue)}
                                    title={issue.title}
                                    selectedIssue={selectedIssue} />
                            </Col>)}
                    </Row>
                </>}
            {issuesError &&
                <Row>
                    <Col>
                        <Alert variant="danger" className="p-1 mt-2">
                            {issuesError}
                        </Alert>
                    </Col>
                </Row>}
        </div>
    )
}