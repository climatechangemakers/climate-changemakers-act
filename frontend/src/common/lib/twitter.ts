export function getPostTweetUrl(text: string): string {
    const url = new URL("https://twitter.com/intent/tweet");
    url.searchParams.set("text", text);
    return url.toString();
}
