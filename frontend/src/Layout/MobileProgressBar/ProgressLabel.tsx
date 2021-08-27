import styles from "./ProgressLabel.module.css"

type Props = {
    children: string;
    completed: boolean;
}

export default function ProgressLabel({ children, completed }: Props) {
    return (
        <div className={`${styles.step} ${completed ? "text-light" : "text-dark"}`}>{children}</div>
    )
}