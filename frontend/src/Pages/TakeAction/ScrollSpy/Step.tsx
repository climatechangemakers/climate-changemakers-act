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
    return desktop ? (
        <div className="flex-fill d-flex mt-4 flex-row">
            <div className="d-flex align-items-center">
                <div
                    className={cx(
                        "rounded-circle d-flex justify-content-center align-items-center me-4 step-icon-desktop",
                        {
                            "text-pink bg-dark-purple font-weight-800": state === "complete",
                        }
                    )}
                >
                    {state === "complete" && <span>&#10003;</span>}
                </div>
            </div>
            <a
                href={id}
                className={cx("text-decoration-none", styles.disableHover, {
                    "text-white": state === "active",
                    [styles.linkComplete]: state === "complete",
                    [styles.disabled]: state === "disabled",
                })}
            >
                {children}
            </a>
        </div>
    ) : (
        <div className="flex-fill d-flex flex-column">
            <div className="d-flex align-items-center">
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
                <div
                    className={cx("rounded-circle d-flex justify-content-center align-items-center step-icon-mobile", {
                        "border-purple": state !== "disabled",
                        "border-disabled": state === "disabled",
                        "text-white bg-dark-purple font-weight-800": state === "complete",
                    })}
                >
                    {state === "complete" ? (
                        <span>&#10003;</span>
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
            </div>
            <a
                href={id}
                className={cx(
                    styles.stepLabelMobileContainer,
                    "text-decoration-none m-auto text-purple position-relative",
                    styles.disableHover,
                    {
                        [styles.disableAnchor]: state === "disabled",
                    }
                )}
            >
                <div className={cx(styles.stepLabelMobile, "position-absolute")}>{children}</div>
            </a>
        </div>
    );
}
