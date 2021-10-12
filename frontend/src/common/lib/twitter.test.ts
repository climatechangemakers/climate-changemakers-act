import { getPostTweetUrl } from "./twitter";

describe("Twitter utilities", () => {
    describe("getPostTweetUrl", () => {
        it("returns the web intent URL", () => {
            const actual = getPostTweetUrl("Hello world 🌴");
            const expected = "https://twitter.com/intent/tweet?text=Hello+world+%F0%9F%8C%B4";
            expect(actual).toEqual(expected);
        });
    });
});
