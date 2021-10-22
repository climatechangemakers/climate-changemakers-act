export type Issue = {
    id: number;
    title: string;
    imageUrl: string;
    description: string;
    talkingPoints: {
        title: string;
        content: string;
    }[];
};
