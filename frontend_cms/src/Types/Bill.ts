export type Bill = {
    congressionalSession: number;
    type: string;
    number: number;
    name: string;
    url: string;
};

export type ExisitingBill = Bill & { id: number };