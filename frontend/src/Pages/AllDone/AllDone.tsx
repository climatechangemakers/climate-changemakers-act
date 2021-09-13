import confetti from "canvas-confetti";
import { useEffect } from "react";
import styles from "./AllDone.module.css"

export default function AllDone() {
    useEffect(() => {
        confetti({
            particleCount: 200,
            spread: 75,
            origin: { y: .9 }
        });
    }, [])

    return (
        <div className={`${styles.doneContainer} d-flex align-items-center`}>
            <div className="mb-5 pb-5">
                <h2 className="text-uppercase">Nice Work</h2>
                <p className="fs-5 mb-0">Thank you for taking action to support our planet. Every action in our community is a step towards climate justice.</p>
            </div>
        </div>
    )
}