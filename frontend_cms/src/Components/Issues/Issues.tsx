import React from "react";
import { Button, Card, Table } from "react-bootstrap";
import { useIssuesQuery, useModal } from "hooks";
import IssuesModal from "./IssuesModal";

export default function Issues() {
    const { data } = useIssuesQuery();
    const { open } = useModal();

    return (
        <Card className="p-4 mb-4">
            <h2 className="ms-2 mb-3">Issues</h2>
            <Table striped bordered hover>
                <tbody>
                    {data?.map((d) => (
                        <tr key={d.id}>
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
