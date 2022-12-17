import React, { MouseEvent } from "react";
import { Alert, Button, Card, Spinner, Table } from "react-bootstrap";
import { useBillsQuery, useModal } from "hooks";
import CongressionalBillsModal from "./CongressionalBillsModal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashCan } from "@fortawesome/free-solid-svg-icons";

export default function CongressionalBills() {
    const { data, isLoading, error } = useBillsQuery();
    const { open } = useModal();

    const handleDelete = (
        e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
        id: number
    ) => {
        e.stopPropagation();
        // TODO: Add deleting endpoint
        console.log("Deleting bill " + id);
    };

    return (
        <Card className="p-4 mb-4">
            <div className="d-flex">
                <h2 className="ms-2 mb-3 me-2">Congressional Bills</h2>
                {isLoading && <Spinner animation="border" />}
            </div>
            <Table striped bordered hover>
                <tbody>
                    {data?.map((d) => (
                        <tr
                            onClick={() =>
                                open(<CongressionalBillsModal bill={d} />)
                            }
                            key={d.id}
                        >
                            <td className="d-flex align-items-center justify-content-between p-1 table-row">
                                <div className="p-2">
                                    {d.id} - {d.name}
                                </div>
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
            {error && <Alert variant="danger">{error.message}</Alert>}
            <div className="d-flex justify-content-end">
                <Button onClick={() => open(<CongressionalBillsModal />)}>
                    + Add new bill
                </Button>
            </div>
        </Card>
    );
}
