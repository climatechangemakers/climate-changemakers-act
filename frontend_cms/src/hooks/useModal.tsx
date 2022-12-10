import { useContext, createContext } from "react";

export const ModalContext = createContext({
    open: (_: JSX.Element) => console.log("open modal not implemented"),
    close: () => console.log("close modal not implemented"),
});

export default function useModal() {
    return useContext(ModalContext);
}
