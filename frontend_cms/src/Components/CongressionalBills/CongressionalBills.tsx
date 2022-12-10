import React from "react";
import { Button, Card, Table } from "react-bootstrap";
import { useBillsQuery, useModal } from "hooks";
import CongressionalBillsModal from "./CongressionalBillsModal";

export default function CongressionalBills() {
    const { data } = useBillsQuery();
    const { open } = useModal();

    return (
        <Card className="p-4 mb-4">
            <h2 className="ms-2 mb-3">Congressional Bills</h2>
            <Table striped bordered hover>
                <tbody>
                    {data?.map((d) => (
                        <tr key={d.id}>
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
