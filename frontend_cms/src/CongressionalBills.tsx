import React, { useState } from "react";
import { Alert, Button, Card, Form, Modal, Table } from "react-bootstrap";
import { useQuery, useMutation } from "react-query";
import { useForm } from "react-hook-form";
import { fetcher } from "./helper";
import useBills from "./hooks/useBills";
import { Bill } from "./Types/Bill";

const BILL_TYPES = [
    "H.R.",
    "H.Con.Res.",
    "H.J.Res.",
    "H.Res.",
    "S.",
    "S.Con.Res.",
    "S.J.Res.",
    "S.Res.",
];

export default function CongressionalBills() {
    const { data, refetch } = useBills();
    const mutation = useMutation<undefined, Error, Bill>((formData) =>
        fetcher("bills", formData)
    );

    const { register, handleSubmit, reset } = useForm<Bill>();
    const [showModal, setShowModal] = useState(false);
    const [error, setError] = useState("");

    const clear = () => {
        setError("");
        reset();
    };

    const handleCloseModal = () => {
        setError("");
        setShowModal(false);
    };

    const handleShowModal = () => setShowModal(true);

    const onSubmit = handleSubmit((data) => {
        mutation.mutate(data, {
            onSuccess: () => {
                reset();
                refetch();
                handleCloseModal();
            },
            onError: async (error: Error) => {
                setError(error.message);
            },
        });
    });

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
                <Button onClick={handleShowModal}>+ Add new bill</Button>
            </div>
            <Modal show={showModal} onHide={handleCloseModal} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Add new bill</Modal.Title>
                </Modal.Header>
                <Form onSubmit={onSubmit}>
                    <Modal.Body>
                        <Form.Group className="mb-3" controlId="name">
                            <Form.Label>Name</Form.Label>
                            <Form.Control required {...register("name")} />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="type">
                            <Form.Label>Type</Form.Label>
                            <Form.Select required {...register("type")}>
                                {BILL_TYPES.map((t) => (
                                    <option key={t} value={t}>
                                        {t}
                                    </option>
                                ))}
                            </Form.Select>{" "}
                        </Form.Group>
                        <Form.Group
                            className="mb-3"
                            controlId="congressionalSession"
                        >
                            <Form.Label>Congressional Session</Form.Label>
                            <Form.Control
                                type="number"
                                required
                                {...register("congressionalSession")}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="number">
                            <Form.Label>Number</Form.Label>
                            <Form.Control
                                type="number"
                                required
                                {...register("number")}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="url">
                            <Form.Label>Url</Form.Label>
                            <Form.Control required {...register("url")} />
                        </Form.Group>
                        {error && <Alert variant="danger">{error}</Alert>}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleCloseModal}>
                            Close
                        </Button>
                        <Button variant="primary" type="submit">
                            Add bill
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Card>
    );
}
