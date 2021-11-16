import { MultiValue } from "react-select/dist/declarations/src/types";

export type EmailInfo = {
    prefix: string;
    firstName: string;
    lastName: string;
    subject: string;
    body: string;
    selectedLocTopics: MultiValue<{
        value: string;
        label: string;
    }>;
};
