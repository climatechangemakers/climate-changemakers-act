import styles from "./Step.module.css";

type Props = {
    children: string;
    step: number;
    state: "disabled" | "active" | "complete";
    desktop: boolean;
    last?: boolean;
};

export default function Step({ children, step, state, desktop, last = false }: Props) {
    return (
        <div className={`flex-fill d-flex align-items-center ${desktop ? "flex-row" : "flex-column"}`}>
            <div className={`${desktop ? "" : "w-100"} d-flex align-items-center`}>
                {!desktop &&
                    <div className="flex-fill">
                        {step !== 1 && <hr className={`${styles.scrollSpyLine} m-0`} />}
                    </div>}
                <div className={`${styles.stepIcon} ${state === "complete" ? "bg-dark-purple text-white font-weight-very-bold" : "text-dark font-weight-normal"} ${desktop ? "" : "border-purple"} rounded-circle d-flex justify-content-center align-items-center`}>{state === "complete" ? <span>&#10003;</span> : desktop ? <></> : <span>{step}</span>}</div>
                {!desktop && <div className="flex-fill">
                    {!last && <hr className={`${styles.scrollSpyLine} m-0`} />}
                </div>}
            </div>
            <div>{children}</div>
        </div>
    )
}
