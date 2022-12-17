import { Alert, Button, Form, Modal } from "react-bootstrap";
import { useForm, Controller } from "react-hook-form";
import { ExistingIssue, IssueForm } from "Types/Issue";
import Select from "react-select";
import { useState, useEffect } from "react";
import {
    useBillsQuery,
    useIssuesQuery,
    useCreateIssueMutation,
    useModal,
    useUpdateIssueMutation,
} from "hooks";

type Props = {
    issue?: ExistingIssue;
};

export default function IssuesModal({ issue }: Props) {
    const { data: bills } = useBillsQuery();
    const { refetch } = useIssuesQuery();
    const { mutate: addIssue } = useCreateIssueMutation();
    const { mutate: updateIssue } = useUpdateIssueMutation();
    const { register, handleSubmit, reset, control } = useForm<IssueForm>();
    const { close } = useModal();
    const [error, setError] = useState("");

    useEffect(() => {
        if (issue) reset(issue);
    }, [issue]);

    const handleSuccess = () => {
        reset();
        refetch();
        close();
    };

    const handleError = (error: Error) => setError(error.message);

    const onSubmit = handleSubmit((data) => {
        const { associatedBills, ...issueData } = data;
        if (!issue)
            // TODO: This currently doesn't work, will fix when new create endpoint implemented
            addIssue(issueData, {
                onSuccess: handleSuccess,
                onError: handleError,
            });
        else
            updateIssue(
                { ...issueData, id: issue.id },
                {
                    onSuccess: handleSuccess,
                    onError: handleError,
                }
            );
    });

    return (
        <Modal show onHide={close} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>
                    {!issue ? "Add new issue" : "Update existing issue"}
                </Modal.Title>
            </Modal.Header>
            <Form onSubmit={onSubmit}>
                <Modal.Body>
                    <Form.Group className="mb-3" controlId="title">
                        <Form.Label>Issue Name</Form.Label>
                        <Form.Control required {...register("title")} />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="description">
                        <Form.Label>Issue Description</Form.Label>
                        <Form.Control required {...register("description")} />
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
                    <Form.Group className="mb-3" controlId="formIsFocusIssue">
                        <Form.Check
                            type="checkbox"
                            label="Is Focus Issue"
                            {...register("isFocusIssue")}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="associatedBills">
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
                    {/* TODO: Add talking points */}
                    {error && <Alert variant="danger">{error}</Alert>}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={close}>
                        Close
                    </Button>
                    <Button variant="primary" type="submit">
                        {!issue ? "Add issue" : "Update issue"}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}