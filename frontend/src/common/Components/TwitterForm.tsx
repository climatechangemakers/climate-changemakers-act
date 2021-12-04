import { useState } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";

const isTweetValid = (text: string) => text.length <= 1000 && text.trim().length > 0;

type Props = {
    preComposedTweet: string;
    description: string;
    skipTweetLabel: string;
    id: string;
    hasOpenedTwitter: boolean;
    isComplete: boolean;
    setIsComplete: (bool: boolean) => void;
    onSendTweet: (e: React.FormEvent<HTMLFormElement>, tweet: string) => void;
};

export default function TwitterForm({
    preComposedTweet,
    description,
    skipTweetLabel,
    id,
    hasOpenedTwitter,
    isComplete,
    setIsComplete,
    onSendTweet,
}: Props) {
    const [tweet, setTweet] = useState(preComposedTweet);

    return (
        <Form onSubmit={(e) => onSendTweet(e, tweet)}>
            <Form.Group>
                <Form.Label htmlFor={id}>{description}</Form.Label>
                <InputGroup hasValidation>
                    <Form.Control
                        as="textarea"
                        rows={5}
                        maxLength={1000}
                        id={id}
                        placeholder="Compose your tweet"
                        className="mt-2"
                        isInvalid={!isTweetValid(tweet)}
                        disabled={isComplete || hasOpenedTwitter}
                        value={tweet}
                        onChange={(e) => setTweet(e.target.value)}
                    />
                    <Form.Control.Feedback type="invalid" tooltip>
                        {tweet.trim() ? "Your tweet is invalid. Is it too long?" : "You must enter a tweet."}
                    </Form.Control.Feedback>
                </InputGroup>
            </Form.Group>
            <div className="row mt-3">
                <div className="col d-flex">
                    <Button
                        className="flex-grow-1 mr-2"
                        variant="secondary"
                        disabled={isComplete}
                        onClick={() => setIsComplete(true)}
                    >
                        {skipTweetLabel}
                    </Button>
                </div>
                <div className="col d-flex">
                    {!hasOpenedTwitter ? (
                        <Button
                            type="submit"
                            className="flex-grow-1 ml-2 text-dark"
                            variant="primary"
                            disabled={isComplete || !isTweetValid(tweet)}
                        >
                            Send Tweet
                        </Button>
                    ) : (
                        <Button
                            type="submit"
                            className="flex-grow-1 ml-2 text-dark"
                            variant="primary"
                            disabled={isComplete}
                        >
                            Done Tweeting
                        </Button>
                    )}
                </div>
            </div>
        </Form>
    );
}
