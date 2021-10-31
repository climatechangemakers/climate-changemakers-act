type Props = {
    when: boolean;
    message?: string;
};

export default function HiddenValidationInput({ when, message }: Props) {
    return !when ? (
        <></>
    ) : (
        <input
            className="position-absolute"
            tabIndex={-1}
            autoComplete="off"
            style={{
                opacity: 0,
                height: 0,
                left: 0,
                top: "calc(100% - 6px)",
            }}
            onInvalid={(e: React.ChangeEvent<HTMLInputElement>) => message && e.target.setCustomValidity(message)}
            required
        />
    );
}
