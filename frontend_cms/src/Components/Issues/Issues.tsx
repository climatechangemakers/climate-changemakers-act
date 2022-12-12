import React from "react";
import { Button, Card, Spinner, Table } from "react-bootstrap";
import { useIssuesQuery, useModal } from "hooks";
import IssuesModal from "./IssuesModal";

export default function Issues() {
    const { data, isLoading } = useIssuesQuery();
    const { open } = useModal();

    return (
        <Card className="p-4 mb-4">
            <div className="d-flex">
                <h2 className="ms-2 mb-3 me-2">Issues</h2>
                {isLoading && <Spinner animation="border" />}
            </div>
            <Table striped bordered hover>
                <tbody>
                    {data?.map((d) => (
                        <tr
                            onClick={() => open(<IssuesModal issue={d} />)}
                            key={d.id}
                        >
                            <td>{d.title}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <div className="d-flex justify-content-end">
                <Button onClick={() => open(<IssuesModal />)}>
                    + Add new issue
                </Button>
            </div>
        </Card>
    );
}
