import {
    faArrowDown,
    faArrowUp,
    faTrashCan,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Accordion, Button, Form } from "react-bootstrap";
import { Editor } from "react-draft-wysiwyg";
import { TalkingPointsForm } from "Types/Issue";

type Props = {
    index: number;
    talkingPoint: TalkingPointsForm;
    update: (newTalkingPoint: TalkingPointsForm) => void;
    deleteTalkingPoint: (talkingPoint: TalkingPointsForm) => void;
    moveUp: () => void;
    moveDown: () => void;
    isLast: boolean;
};

export default function TalkingPoint({
    index,
    talkingPoint,
    update,
    deleteTalkingPoint,
    moveUp,
    moveDown,
    isLast,
}: Props) {
    return (
        <Accordion.Item eventKey={index.toString()} key={talkingPoint.id}>
            <div className="position-relative">
                <div className="d-flex m-auto flex-column pe-2 accordion-move-buttons">
                    <Button
                        variant="secondary"
                        className="order-arrow"
                        size="sm"
                        disabled={index === 0}
                        onClick={moveUp}
                    >
                        <FontAwesomeIcon icon={faArrowUp} />
                    </Button>
                    <Button
                        variant="secondary"
                        className="order-arrow"
                        size="sm"
                        disabled={isLast}
                        onClick={moveDown}
                    >
                        <FontAwesomeIcon icon={faArrowDown} />
                    </Button>
                </div>
                <Accordion.Header className="w-100">
                    {talkingPoint.title}
                </Accordion.Header>
                <div className="d-flex accordion-delete-button">
                    <Button onClick={() => deleteTalkingPoint(talkingPoint)}>
                        <FontAwesomeIcon icon={faTrashCan} />
                    </Button>
                </div>
            </div>
            <Accordion.Body className="ms-3">
                <Form.Group
                    className="mb-3"
                    controlId={`talkingPointTitle-${talkingPoint.id}`}
                >
                    <Form.Label>Title</Form.Label>
                    <Form.Control
                        required
                        value={talkingPoint.title}
                        onChange={(e) =>
                            update({
                                ...talkingPoint,
                                title: e.currentTarget.value,
                            })
                        }
                    />
                </Form.Group>
                <Form.Group
                    className="mb-3"
                    controlId={`talkingPointContent-${talkingPoint.id}`}
                >
                    <Form.Label>Content</Form.Label>
                    <div className="border">
                        <Editor
                            editorClassName="p-2"
                            editorState={talkingPoint.content}
                            onEditorStateChange={(e) =>
                                update({
                                    ...talkingPoint,
                                    content: e,
                                })
                            }
                        />
                    </div>
                </Form.Group>
            </Accordion.Body>
        </Accordion.Item>
    );
}
