type TalkingPoint = {
    title: string;
    content: string;
}

export type Issue = {
    title: string;
    talkingPoints: TalkingPoint[];
}

export type IssuesResponse = {
    focusIssue: Issue;
    otherIssues: Issue[];
}