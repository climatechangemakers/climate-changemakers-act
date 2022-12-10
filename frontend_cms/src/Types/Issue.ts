export type IssueInfo = {
    description: string;
    imageUrl: string;
    isFocusIssue: boolean;
    precomposedTweetTemplate: string;
    title: string;
};

export type IssueForm = IssueInfo & {
    associatedBills: number[];
};

export type ExistingIssue = IssueInfo & { id: number };
