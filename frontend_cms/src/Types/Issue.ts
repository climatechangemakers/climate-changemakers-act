import { EditorState } from "draft-js";

export type IssueInfo = {
    description: string;
    imageUrl: string;
    isFocusIssue: boolean;
    precomposedTweetTemplate: string;
    title: string;
};

export type IssueForm = IssueInfo & {
    associatedBills: number[];
    issueTalkingPoints: TalkingPoints[];
};

export type TalkingPoints = {
    title: string;
    body: EditorState;
}

export type ExistingIssue = IssueInfo & { id: number };
