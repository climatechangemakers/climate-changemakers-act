export type Issue = {
    id: number;
    title: string;
    talkingPoints: {
        title: string;
        content: string;
    }[];
}