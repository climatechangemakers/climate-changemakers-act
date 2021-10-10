import {isTweetValid, getPostTweetUrl } from "./twitter";

describe("Twitter utilities", () => {
    describe("isTweetValid", () => {
        it("returns false for blank strings", () => {
            expect(isTweetValid("")).toBe(false);
            expect(isTweetValid("\t")).toBe(false);
            expect(isTweetValid("  ")).toBe(false);
        });

        it("returns false for very long strings", () => {
            expect(isTweetValid("x".repeat(1001))).toBe(false);
        });

        it("returns true for strings of normal length", () => {
            expect(isTweetValid("x".repeat(123))).toBe(true);
            expect(isTweetValid("x".repeat(280))).toBe(true);
            expect(isTweetValid("x".repeat(300))).toBe(true);
        });
    })

    describe("getPostTweetUrl", () => {
        it("returns the web intent URL", () => {
            const actual = getPostTweetUrl("Hello world 🌴");
            const expected = "https://twitter.com/intent/tweet?text=Hello+world+%F0%9F%8C%B4";
            expect(actual).toEqual(expected);
        });
    });
});
