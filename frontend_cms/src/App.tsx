import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { QueryClient, QueryClientProvider } from "react-query";
import Issues from "./Issues";
import { Container } from "react-bootstrap";
import CongressionalBills from "./CongressionalBills";

const queryClient = new QueryClient();

function App() {
    return (
        <div className="App">
            <Container>
                <QueryClientProvider client={queryClient}>
                    <h1 className="mb-4 mt-4 text-white">
                        Climate Changemakers CMS
                    </h1>
                    <Issues />
                    <CongressionalBills />
                </QueryClientProvider>
            </Container>
        </div>
    );
}

export default App;
