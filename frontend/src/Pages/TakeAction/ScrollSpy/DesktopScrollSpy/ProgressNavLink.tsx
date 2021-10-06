import styles from "./ProgressNavLink.module.css";

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
                ? <span className={styles.checkmark}>&#10003;</span>
                : <span className={styles.numbers}>{step}.</span>}
            <a className={`${state === "disabled" ? styles.inactiveLink : "active"} nav-link text-start ms-2`} href={href}>{children}</a>
        </div>
    )
}