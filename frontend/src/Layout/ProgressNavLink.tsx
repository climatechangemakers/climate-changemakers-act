import styles from "./ProgressNavLink.module.css"

type Props = {
    children: string;
    state: "disabled" | "active" | "complete";
    step?: number;
    href: string;
}

export default function ProgressNavLink({ children, state, step, href }: Props) {
    return (
        <div className="d-flex position-relative">
            {state === "complete"
                ? <span className={`${styles.checkmark} d-flex mt-auto mb-auto position-absolute`}>&#10003;</span>
                : step ? <span className={`${styles.numbers} d-flex mt-auto mb-auto position-absolute`}>{step}.</span>
                    : <span className={`${styles.bullets} d-flex mt-auto mb-auto position-absolute`}>&#9679;</span>}
            <a className={`${state === "disabled" ? styles.inactiveLink : "active"} nav-link text-start ms-2`} href={href}>{children}</a>
        </div>
    )
}