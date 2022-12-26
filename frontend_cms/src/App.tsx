import "App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { QueryClient, QueryClientProvider } from "react-query";
import Issues from "Components/Issues/Issues";
import { Container } from "react-bootstrap";
import CongressionalBills from "Components/CongressionalBills/CongressionalBills";
import { useState } from "react";
import { ModalContext } from "hooks/useModal";

const queryClient = new QueryClient();

function App() {
    const [modal, setModal] = useState<JSX.Element | undefined>(undefined);
    return (
        <div className="App">
            <QueryClientProvider client={queryClient}>
                <ModalContext.Provider
                    value={{
                        open: (modal: JSX.Element) => setModal(modal),
                        close: () => setModal(undefined),
                    }}
                >
                    <Container>
                        <h1 className="mb-4 mt-4 text-white">
                            Climate Changemakers CMS
                        </h1>
                        <Issues />
                        <CongressionalBills />
                    </Container>
                    {modal}
                </ModalContext.Provider>
            </QueryClientProvider>
        </div>
    );
}

export default App;
