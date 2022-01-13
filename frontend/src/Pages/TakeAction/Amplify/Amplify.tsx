import TwitterForm from "common/Components/TwitterForm";
import { getPostTweetUrl } from "common/lib/twitter";
import { useState } from "react";
import socialIcon from "../social-icon.svg";

type Props = {
    isAmplified: boolean;
    setIsAmplified: (bool: boolean) => void;
};

export const isTweetValid = (text: string) => text.length <= 1000 && text.trim().length > 0;

export default function Amplify({ isAmplified, setIsAmplified }: Props) {
    const [hasOpenedTwitter, setHasOpenedTwitter] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>, tweet: string) => {
        e.preventDefault();
        e.stopPropagation();

        if (hasOpenedTwitter) {
            setIsAmplified(true);
            return;
        }

        if (window.open(getPostTweetUrl(tweet!.trim()))) setHasOpenedTwitter(true);
    };

    return (
        <div className="pt-2 pb-3">
            <div className="d-flex">
                <img src={socialIcon} alt="" height="40" width="40" />
                <h2 className="text-pink fw-bold mb-3 ms-3">Amplify Your Voice</h2>
            </div>
            <TwitterForm
                preComposedTweet="Just used the personalized outreach tool developed by Climate Changemakers @theclimatevote to contact Congress. Check it out for yourself at https://act.climatechangemakers.org!"
                description=" Encourage friends and family in your network to take action too. Share this tool to amplify your impact."
                skipTweetLabel="Skip this tweet"
                id="amplify-input"
                hasOpenedTwitter={hasOpenedTwitter}
                isComplete={isAmplified}
                setIsComplete={setIsAmplified}
                onSendTweet={handleSubmit}
            />
        </div>
    );
}
