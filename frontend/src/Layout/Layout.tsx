import { Container } from "react-bootstrap"
import DesktopScrollspy from "./DesktopProgressBar"

type Props = {
    children: any;
    isActionInfo: boolean;
    isIssue: boolean;
    isEmailSent: boolean;
    isPhoneCallMade: boolean;
    isSocialPosted: boolean;
}

export default function Layout({ children, isActionInfo, isIssue, isEmailSent, isPhoneCallMade, isSocialPosted }: Props) {
    return (
        <main className="App">
            <div className="d-flex justify-content-between">
                <div className="flex-grow-1" />
                <div className="App-body w-100 flex-1 justify-content-center">
                    <Container>
                        {children}
                    </Container>
                </div>
                <div className="flex-grow-1 d-flex justify-content-center">
                    <div className="position-fixed d-none d-lg-block">
                        <DesktopScrollspy
                            isActionInfo={isActionInfo}
                            isIssue={isIssue}
                            isEmailSent={isEmailSent}
                            isPhoneCallMade={isPhoneCallMade}
                            isSocialPosted={isSocialPosted}
                        />
                    </div>
                </div>
            </div>
        </main>
    )
}