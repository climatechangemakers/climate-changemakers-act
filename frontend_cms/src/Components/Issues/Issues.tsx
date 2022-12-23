import React, { MouseEvent, useState } from "react";
import { Alert, Button, Card, Spinner, Table } from "react-bootstrap";
import { useDeleteIssueMutation, useIssuesQuery, useModal } from "hooks";
import IssuesModal from "./IssuesModal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashCan } from "@fortawesome/free-solid-svg-icons";

export default function Issues() {
    const { data, isLoading, error: issuesError, refetch } = useIssuesQuery();
    const { mutate: deleteIssue } = useDeleteIssueMutation();
    const { open } = useModal();
    const [deleteError, setDeleteError] = useState("");

    const error = issuesError?.message || deleteError;

    const handleDelete = (
        e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
        id: number
    ) => {
        e.stopPropagation();
        deleteIssue(id, {
            onSuccess: () => refetch(),
            onError: (error: Error) => setDeleteError(error.message),
        });
    };

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
                            <td className="d-flex align-items-center justify-content-between p-1 table-row">
                                <div className="p-2">{d.title}</div>
                                <Button
                                    className="delete-icon"
                                    onClick={(e) => handleDelete(e, d.id)}
                                >
                                    <FontAwesomeIcon icon={faTrashCan} />
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            {error && <Alert variant="danger">{error}</Alert>}
            <div className="d-flex justify-content-end">
                <Button onClick={() => open(<IssuesModal />)}>
                    + Add new issue
                </Button>
            </div>
        </Card>
    );
}
