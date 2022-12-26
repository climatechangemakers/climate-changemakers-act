import { EditorState } from "draft-js";

type Issue = {
    description: string;
    imageUrl: string;
    isFocusIssue: boolean;
    precomposedTweetTemplate: string;
    title: string;
    relatedBillIds: number[];
}
export type IssueInfo = Issue & {
    talkingPoints: Array<{
        id?: string;
        title: string;
        content: string;
        relativeOrderPosition: number;
    }>
};

export type IssueForm = Issue & {
    talkingPoints: TalkingPointsForm[];
};

export type TalkingPointsForm = {
    id: string;
    title: string;
    content: EditorState;
}

export type ExistingIssue = IssueInfo & { id: number };
