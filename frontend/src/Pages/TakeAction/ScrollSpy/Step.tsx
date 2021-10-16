import cx from "classnames";
import styles from "./Step.module.css";

type Props = {
    children: string;
    step: number;
    id: string;
    state: "disabled" | "active" | "complete";
    desktop: boolean;
    last?: boolean;
};

export default function Step({ children, step, id, state, desktop, last = false }: Props) {
    return (
        <div
            className={cx("flex-fill d-flex", {
                "mt-4 flex-row": desktop,
                "flex-column": !desktop,
            })}
        >
            <div className="d-flex align-items-center">
                {!desktop && (
                    <div className="flex-fill">
                        {step !== 1 && (
                            <hr
                                className={cx("m-0", styles.scrollSpyLine, {
                                    [styles.activeLine]: state !== "disabled",
                                    [styles.disabled]: state === "disabled",
                                })}
                            />
                        )}
                    </div>
                )}
                <div
                    className={cx("rounded-circle d-flex justify-content-center align-items-center", {
                        "step-icon-mobile": !desktop,
                        "border-purple": !desktop && state !== "disabled",
                        "border-disabled": !desktop && state === "disabled",
                        "text-white": !desktop && state === "complete",
                        "me-4 step-icon-desktop": desktop,
                        "text-pink": desktop && state === "complete",
                        "bg-dark-purple font-weight-800": state === "complete",
                    })}
                >
                    {state === "complete" ? (
                        <span>&#10003;</span>
                    ) : desktop ? (
                        <></>
                    ) : (
                        <span
                            className={cx({
                                [styles.disabled]: state === "disabled",
                            })}
                        >
                            {step}
                        </span>
                    )}
                </div>
                {!desktop && (
                    <div className="flex-fill">
                        {!last && (
                            <hr
                                className={cx("m-0", styles.scrollSpyLine, {
                                    [styles.activeLine]: state === "complete" || step === 1,
                                    [styles.disabled]: state !== "complete" && step !== 1,
                                })}
                            />
                        )}
                    </div>
                )}
            </div>
            <a
                href={id}
                className={cx("text-decoration-none", styles.disableHover, {
                    "m-auto text-purple": !desktop,
                    [styles.disableAnchor]: !desktop || state === "disabled",
                    "text-white": desktop && state === "active",
                    [styles.linkComplete]: desktop && state === "complete",
                    [styles.disabled]: desktop && state === "disabled",
                })}
            >
                {children}
            </a>
        </div>
    );
}
