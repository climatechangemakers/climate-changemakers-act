import { useBillsQuery, useCreateBillMutation, useModal } from "hooks";
import React, { useState } from "react";
import { Alert, Button, Form, Modal } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { Bill } from "Types/Bill";

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

export default function CongressionalBillsModal() {
    const { refetch } = useBillsQuery();
    const mutation = useCreateBillMutation();
    const { register, handleSubmit, reset } = useForm<Bill>();
    const [error, setError] = useState("");
    const { close } = useModal();

    const onSubmit = handleSubmit((data) => {
        mutation.mutate(data, {
            onSuccess: () => {
                reset();
                refetch();
                close();
            },
            onError: async (error: Error) => {
                setError(error.message);
            },
        });
    });

    return (
        <Modal show onHide={close} size="lg">
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
                    <Button variant="secondary" onClick={close}>
                        Close
                    </Button>
                    <Button variant="primary" type="submit">
                        Add bill
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}
