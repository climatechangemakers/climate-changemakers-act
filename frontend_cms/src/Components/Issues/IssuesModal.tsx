import { Alert, Button, Form, Modal, Accordion } from "react-bootstrap";
import { useForm, Controller } from "react-hook-form";
import {
    ExistingIssue,
    IssueForm,
    IssueInfo,
    TalkingPointsForm,
} from "Types/Issue";
import Select from "react-select";
import { useState, useEffect } from "react";
import {
    useBillsQuery,
    useIssuesQuery,
    useCreateIssueMutation,
    useModal,
    useUpdateIssueMutation,
} from "hooks";
import {
    ContentBlock,
    ContentState,
    convertFromHTML,
    convertToRaw,
    EditorState,
} from "draft-js";
import { Editor } from "react-draft-wysiwyg";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import draftToHtml from "draftjs-to-html";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowUp } from "@fortawesome/free-solid-svg-icons";
import { faArrowDown } from "@fortawesome/free-solid-svg-icons";
import TalkingPoint from "./TalkingPoint";

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
        if (issue)
            reset({
                ...issue,
                talkingPoints: issue.talkingPoints
                    .sort(
                        (a, b) =>
                            a.relativeOrderPosition - b.relativeOrderPosition
                    )
                    .map((t) => ({
                        id: crypto.randomUUID(),
                        title: t.title,
                        content: EditorState.createWithContent(
                            ContentState.createFromBlockArray(
                                convertFromHTML(
                                    t.content
                                ) as unknown as ContentBlock[]
                            )
                        ),
                        relativeOrderPosition: t.relativeOrderPosition,
                    })),
            });
    }, [issue]);

    const handleSuccess = () => {
        reset();
        refetch();
        close();
    };

    const handleError = (error: Error) => setError(error.message);

    const onSubmit = handleSubmit((data) => {
        const issues: IssueInfo = {
            ...data,
            relatedBillIds: data.relatedBillIds ?? [],
            talkingPoints: data.talkingPoints.map((t, i) => ({
                title: t.title,
                content: draftToHtml(
                    convertToRaw(t?.content?.getCurrentContent() ?? "")
                ),
                relativeOrderPosition: i,
            })),
        };

        if (!issue)
            addIssue(issues, {
                onSuccess: handleSuccess,
                onError: handleError,
            });
        else
            updateIssue(
                { ...issues, id: issue.id },
                {
                    onSuccess: handleSuccess,
                    onError: handleError,
                }
            );
    });

    const updateTalkingPoint = (
        talkingPoints: TalkingPointsForm[],
        updatedTalkingPoint: TalkingPointsForm,
        onChange: (...event: any[]) => void
    ) => {
        onChange(
            talkingPoints.map((t) =>
                t.id !== updatedTalkingPoint.id ? t : updatedTalkingPoint
            )
        );
    };

    const swap = (arr: any[], from: number, to: number) => {
        let temp = arr;
        [temp[to], temp[from]] = [temp[from], temp[to]];
        return temp;
    };

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
                    <Form.Group className="mb-3" controlId="relatedBillIds">
                        <Form.Label>Associated Bills</Form.Label>
                        <Controller
                            control={control}
                            name="relatedBillIds"
                            render={({ field: { onChange, value } }) => (
                                <Select
                                    options={
                                        bills?.map((b) => ({
                                            value: b.id,
                                            label: b.name,
                                        })) || []
                                    }
                                    value={bills
                                        ?.filter((b) => value?.includes(b.id))
                                        ?.map((b) => ({
                                            value: b.id,
                                            label: b.name,
                                        }))}
                                    onChange={(val) =>
                                        onChange(val.map((v) => v.value))
                                    }
                                    isMulti
                                />
                            )}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="talkingPoints">
                        <Controller
                            control={control}
                            name="talkingPoints"
                            render={({ field: { onChange, value } }) => (
                                <>
                                    <Form.Label>
                                        Issue Talking Points
                                        <Button
                                            className="ms-2"
                                            size="sm"
                                            onClick={() =>
                                                onChange([
                                                    ...(value ?? []),
                                                    {
                                                        id: crypto.randomUUID(),
                                                        title: `Talking Point ${
                                                            (value?.length ??
                                                                0) + 1
                                                        }`,
                                                        content: "",
                                                    },
                                                ])
                                            }
                                        >
                                            +
                                        </Button>
                                    </Form.Label>
                                    <Accordion className="ps-4 pe-5 position-relative">
                                        {value?.map((talkingPoint, i) => (
                                            <TalkingPoint
                                                index={i}
                                                talkingPoint={talkingPoint}
                                                update={(newTalkingPoint) =>
                                                    updateTalkingPoint(
                                                        value,
                                                        newTalkingPoint,
                                                        onChange
                                                    )
                                                }
                                                deleteTalkingPoint={(tp) =>
                                                    onChange(
                                                        value.filter(
                                                            (t) =>
                                                                t.id !== tp.id
                                                        )
                                                    )
                                                }
                                                moveUp={() =>
                                                    onChange(
                                                        swap(value, i, i - 1)
                                                    )
                                                }
                                                moveDown={() =>
                                                    onChange(
                                                        swap(value, i, i + 1)
                                                    )
                                                }
                                                isLast={i === value.length - 1}
                                            />
                                        ))}
                                    </Accordion>
                                </>
                            )}
                        />
                    </Form.Group>
                    {error && <Alert variant="danger">{error}</Alert>}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={close}>
                        Cancel
                    </Button>
                    <Button variant="primary" type="submit">
                        {!issue ? "Add issue" : "Update issue"}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}
