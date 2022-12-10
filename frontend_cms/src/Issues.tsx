import React, { useState } from "react";
import { Alert, Button, Card, Form, Modal, Table } from "react-bootstrap";
import { useQuery, useMutation } from "react-query";
import { useForm, Controller } from "react-hook-form";
import { fetcher } from "./helper";
import useBills from "./hooks/useBills";
import Select from "react-select";

type IssueInfo = {
    description: string;
    imageUrl: string;
    isFocusIssue: boolean;
    precomposedTweetTemplate: string;
    title: string;
};

type IssueForm = IssueInfo & {
    associatedBills: number[];
};

type ExistingIssue = IssueInfo & { id: number };

export default function Issues() {
    const { data, refetch } = useQuery("issues", () =>
        fetcher<Array<ExistingIssue>>("issues")
    );
    const { data: bills } = useBills();

    const { mutate: addIssue } = useMutation<
        ExistingIssue | undefined,
        Error,
        IssueInfo,
        unknown
    >((formData: IssueInfo) =>
        fetcher<ExistingIssue | undefined, IssueInfo>("issues", formData)
    );

    const { mutate: updateBillsAssociatedWithIssue } = useMutation<
        unknown,
        Error,
        { issueId: number; billIds: number[] }
    >((formData) =>
        fetcher(`issues/${formData.issueId}/bills`, formData, "PUT")
    );

    const { register, handleSubmit, reset, control } = useForm<IssueForm>();
    const [showModal, setShowModal] = useState(false);
    const [error, setError] = useState("");

    const handleCloseModal = () => {
        setError("");
        setShowModal(false);
    };

    const handleShowModal = () => setShowModal(true);

    const onSubmit = handleSubmit((data) => {
        const { associatedBills, ...issueData } = data;
        addIssue(issueData, {
            onSuccess: (data) => {
                updateBillsAssociatedWithIssue(
                    { issueId: data!.id, billIds: associatedBills },
                    {
                        onSuccess: () => {
                            reset();
                            refetch();
                            handleCloseModal();
                        },
                        onError: async (error: Error) => {
                            setError(error.message);
                        },
                    }
                );
            },
            onError: async (error: Error) => {
                setError(error.message);
            },
        });
        console.log(associatedBills);
    });

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
                <Button onClick={handleShowModal}>+ Add new issue</Button>
            </div>
            <Modal show={showModal} onHide={handleCloseModal} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Add new issue</Modal.Title>
                </Modal.Header>
                <Form onSubmit={onSubmit}>
                    <Modal.Body>
                        <Form.Group className="mb-3" controlId="title">
                            <Form.Label>Issue Name</Form.Label>
                            <Form.Control required {...register("title")} />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="description">
                            <Form.Label>Issue Description</Form.Label>
                            <Form.Control
                                required
                                {...register("description")}
                            />
                        </Form.Group>
                        <Form.Group
                            className="mb-3"
                            controlId="precomposedTweetTemplate"
                        >
                            <Form.Label>Precomposed Tweet Template</Form.Label>
                            <Form.Control
                                required
                                {...register("precomposedTweetTemplate")}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="imageUrl">
                            <Form.Label>Image Url</Form.Label>
                            <Form.Control required {...register("imageUrl")} />
                        </Form.Group>
                        <Form.Group
                            className="mb-3"
                            controlId="formIsFocusIssue"
                        >
                            <Form.Check
                                type="checkbox"
                                label="Is Focus Issue"
                                {...register("isFocusIssue")}
                            />
                        </Form.Group>
                        <Form.Group
                            className="mb-3"
                            controlId="associatedBills"
                        >
                            <Form.Label>Associated Bills</Form.Label>
                            <Controller
                                control={control}
                                name="associatedBills"
                                render={({ field: { onChange } }) => (
                                    <Select
                                        classNamePrefix="addl-class"
                                        options={
                                            bills?.map((b) => ({
                                                value: b.id,
                                                label: b.name,
                                            })) || []
                                        }
                                        onChange={(val) =>
                                            onChange(val.map((v) => v.value))
                                        }
                                        isMulti
                                    />
                                )}
                            />
                        </Form.Group>
                        {error && <Alert variant="danger">{error}</Alert>}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleCloseModal}>
                            Close
                        </Button>
                        <Button variant="primary" type="submit">
                            Add issue
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Card>
    );
}
