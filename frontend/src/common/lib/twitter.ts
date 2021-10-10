export function isTweetValid(text: string): boolean {
    return (text.length <= 1000) && Boolean(text.trim());
}

export function getPostTweetUrl(text: string): string {
    const url = new URL("https://twitter.com/intent/tweet");
    url.searchParams.set("text", text);
    return url.toString();
}
