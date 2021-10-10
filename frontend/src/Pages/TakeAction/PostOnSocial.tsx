import { useState, useEffect } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import type Loadable from "common/lib/Loadable";
import { isTweetValid, getPostTweetUrl } from "common/lib/twitter";

type Props = {
    isSocialPosted: boolean;
    setIsSocialPosted: (bool: boolean) => void;
    preComposedTweet: Loadable<string, string>;
    logTweet: () => unknown;
}

export default function PostOnSocial({
    isSocialPosted,
    setIsSocialPosted,
    preComposedTweet,
    logTweet,
}: Props) {
    const [tweet, setTweet] = useState("");
    const [hasClickedLink, setHasClickedLink] = useState(false);

    useEffect(() => {
        if (preComposedTweet.status === "loaded") setTweet(preComposedTweet.value);
    }, [preComposedTweet]);

    return (
        <div className="pt-2 pb-3">
            <h3 className="text-start pb-3">Post on Social</h3>
            {preComposedTweet.status === "loading" ? <p>Loading...</p>
             : preComposedTweet.status === "failed" ? <p>Failed to load. Please try refreshing the page.</p>
             : (
                 <Form
                     onSubmit={(event) => {
                         event.preventDefault();
                         event.stopPropagation();

                         if (hasClickedLink) {
                             setIsSocialPosted(true);
                         } else if (window.open(getPostTweetUrl(tweet.trim()))) {
                             setHasClickedLink(true);
                             logTweet();
                         }
                     }}
                 >
                     <Form.Group>
                         <Form.Label htmlFor="draft-tweet-input">
                             Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus!
                         </Form.Label>
                         <InputGroup hasValidation>
                             <Form.Control
                                 as="textarea"
                                 rows={4}
                                 maxLength={1000}
                                 id="draft-tweet-input"
                                 placeholder="Compose your tweet"
                                 isInvalid={!isTweetValid(tweet)}
                                 disabled={isSocialPosted || hasClickedLink}
                                 value={tweet}
                                 onChange={e => setTweet(e.target.value)}
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
                                 onClick={() => {
                                     setIsSocialPosted(true);
                                 }}
                             >
                                 Skip Tweeting
                             </Button>
                         </div>
                         <div className="col d-flex">
                             {hasClickedLink ? (
                                 <Button
                                     type="submit"
                                     className="flex-grow-1 ml-2"
                                     variant="primary"
                                     disabled={isSocialPosted}
                                 >
                                     Done Tweeting
                                 </Button>
                             ) : (
                                 <Button
                                     type="submit"
                                     className="flex-grow-1 ml-2"
                                     variant="primary"
                                     disabled={isSocialPosted || !isTweetValid(tweet)}
                                 >
                                     Send Tweet
                                 </Button>
                             )}
                         </div>
                     </div>
                 </Form>
            )}
        </div>
    )
}
