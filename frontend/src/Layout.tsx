import Container from "react-bootstrap/esm/Container";

type Props = {
    children: any;
}

export default function Layout({ children }: Props) {
    return (
        <main className="App">
            <div className="App-body">
                <Container>
                    {children}
                </Container>
            </div>
        </main>
    )
}