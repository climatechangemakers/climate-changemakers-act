import React from "react";
import { Button, Card, Spinner, Table } from "react-bootstrap";
import { useBillsQuery, useModal } from "hooks";
import CongressionalBillsModal from "./CongressionalBillsModal";

export default function CongressionalBills() {
    const { data, isLoading } = useBillsQuery();
    const { open } = useModal();

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
                            <td>{d.name}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <div className="d-flex justify-content-end">
                <Button onClick={() => open(<CongressionalBillsModal />)}>
                    + Add new bill
                </Button>
            </div>
        </Card>
    );
}
