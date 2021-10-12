import { Container } from "react-bootstrap";

type Props = {
    children: any;
};

export default function Layout({ children }: Props) {
    return (
        <main className="App">
            <div className="d-flex justify-content-between flex-column flex-lg-row align-items-center align-items-lg-start">
                <div className="App-body w-100 flex-1 pb-5 d-flex m-auto">
                    <Container>{children}</Container>
                </div>
            </div>
        </main>
    );
}
