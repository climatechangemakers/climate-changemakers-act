import { logTweetAPI } from "common/api/ClimateChangemakersAPI";
import ErrorMessage from "common/Components/ErrorMessage";
import TwitterForm from "common/Components/TwitterForm";
import { getPostTweetUrl } from "common/lib/twitter";
import { ActionInfo } from "common/models/ActionInfo";
import { Issue } from "common/models/Issue";
import { useState } from "react";
import twitterIcon from "./twitter-icon.svg";

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
    const [hasOpenedTwitter, setHasOpenedTwitter] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>, tweet: string) => {
        e.preventDefault();
        e.stopPropagation();

        if (hasOpenedTwitter) {
            setIsSocialPosted(true);
        } else {
            const openedWindow = window.open(getPostTweetUrl(tweet!.trim()));
            if (openedWindow) {
                setHasOpenedTwitter(true);
                const response = await logTweetAPI(
                    actionInfo.initiatorEmail,
                    selectedIssue.id,
                    actionInfo.legislators.map((l) => l.bioguideId)
                );
                if (!response.successful) console.warn(response.error ?? "Failed to log tweet");
            }
        }
    };

    return (
        <div className="pt-2 pb-3">
            <div className="d-flex">
                <img src={twitterIcon} alt="" height="40" width="40" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Post on Social Media</h2>
            </div>
            {preComposedTweet && (
                <TwitterForm
                    preComposedTweet={preComposedTweet}
                    description="Social media can be very effective for grabbing the attention of policymakers and amplifying your message to a wider audience. Use the (editable) sample Tweet below as a starting point, revise it to make your message unique, and don’t forget to tag your members of Congress!"
                    skipTweetLabel="Skip this Tweet"
                    id="post-on-social-input"
                    hasOpenedTwitter={hasOpenedTwitter}
                    isComplete={isSocialPosted}
                    setIsComplete={setIsSocialPosted}
                    onSendTweet={handleSubmit}
                />
            )}
            <ErrorMessage message={preComposedTweetError || "Failed to load precomposed tweet"} />
        </div>
    );
}
