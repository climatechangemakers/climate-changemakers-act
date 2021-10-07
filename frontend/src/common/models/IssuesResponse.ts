type TalkingPoint = {
    title: string;
    content: string;
}

export type Issue = {
    id: number;
    title: string;
    talkingPoints: TalkingPoint[];
}

export type IssuesResponse = {
    focusIssue: Issue;
    otherIssues: Issue[];
}
