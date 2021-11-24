import { logTweetAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import { getPostTweetUrl } from "common/lib/twitter";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/Issue";
import { useState } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import postOnSocialIcon from "./post-on-social-icon.svg";

type Props = {
    isSocialPosted: boolean;
    setIsSocialPosted: (bool: boolean) => void;
    preComposedTweet: undefined | string;
    preComposedTweetError: undefined | string;
    actionInfo: ActionInfo;
    selectedIssue: Issue;
};

export const isTweetValid = (text: string) => text.length <= 1000 && text.trim().length > 0;

export default function PostOnSocial({
    isSocialPosted,
    setIsSocialPosted,
    preComposedTweet,
    preComposedTweetError,
    actionInfo,
    selectedIssue,
}: Props) {
    const [tweet, setTweet] = useState(preComposedTweet);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        e.stopPropagation();

        if (window.open(getPostTweetUrl(tweet!.trim()))) {
            const response = await logTweetAPI(
                actionInfo.initiatorEmail,
                selectedIssue.id,
                actionInfo.legislators.map((l) => l.bioguideId)
            );
            if (!response.successful) {
                console.warn(response.error ?? "Failed to log tweet");
                return;
            }
            setIsSocialPosted(true);
        }
    };

    return (
        <div className="pt-2 pb-3">
            <div className="d-flex">
                <img src={postOnSocialIcon} alt="" height="40" width="40" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Post on Social Media</h2>
            </div>
            {tweet && (
                <Form onSubmit={handleSubmit}>
                    <Form.Group>
                        <Form.Label htmlFor="draft-tweet-input">
                            Social media can be very effective at grabbing the attention of policymakers and amplifying
                            your message to a wider audience. Use the sample Tweet below as a starting point and don’t
                            forget to tag your members of Congress!
                        </Form.Label>
                        <InputGroup hasValidation>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                maxLength={1000}
                                id="draft-tweet-input"
                                placeholder="Compose your tweet"
                                className="mt-2"
                                isInvalid={!isTweetValid(tweet)}
                                disabled={isSocialPosted}
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
                                disabled={isSocialPosted}
                                onClick={() => setIsSocialPosted(true)}
                            >
                                Skip Tweeting
                            </Button>
                        </div>
                        <div className="col d-flex">
                            <Button
                                type="submit"
                                className="flex-grow-1 ml-2 text-dark"
                                variant="primary"
                                disabled={isSocialPosted || !isTweetValid(tweet)}
                            >
                                Send Tweet
                            </Button>
                        </div>
                    </div>
                </Form>
            )}
            <ErrorMessage message={preComposedTweetError && "Failed to load precomposed tweet"} />
        </div>
    );
}
