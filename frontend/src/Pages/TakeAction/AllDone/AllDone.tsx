import confetti from "canvas-confetti";
import { useEffect } from "react";
import { Button } from "react-bootstrap";
import { useHistory } from "react-router-dom";
import styles from "./AllDone.module.css";

export default function AllDone() {
    const history = useHistory();

    useEffect(() => {
        const velocity = Math.max(50, window.innerWidth / 24);
        confetti({
            particleCount: 200,
            spread: 60,
            origin: { x: 0, y: 0.75 },
            angle: 60,
            startVelocity: velocity,
            disableForReducedMotion: true,
        });

        confetti({
            particleCount: 200,
            spread: 60,
            origin: { x: 1, y: 0.75 },
            angle: 120,
            startVelocity: velocity,
            disableForReducedMotion: true,
        });
    }, []);

    return (
        <div className={`${styles.doneContainer} d-flex mt-3 pt-4 m-auto`}>
            <div className="mb-5 pb-3 text-center">
                <h2 className="text-uppercase">Nice Work!</h2>
                <p className="fs-5 mb-0">
                    Thank you for taking meaningful climate action. Research shows that using our own voices and our own
                    words is the most impactful and effective way to influence our policymakers. Every action counts as
                    we press our leaders to prioritize immediate and ambitious climate action!
                </p>
                <div className="d-flex m-auto justify-content-center w-100 mt-3">
                    <div className={styles.buttonWidth}>
                        <Button
                            type="submit"
                            className="w-100 text-dark"
                            variant="primary"
                            onClick={() => history.push("/pick-your-issue")}
                        >
                            Choose Another Issue
                        </Button>
                        <Button className="w-100 mt-2" variant="secondary" onClick={() => window.close()}>
                            End Session
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
}
