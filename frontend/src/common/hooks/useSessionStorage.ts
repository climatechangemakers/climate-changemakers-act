import { useState } from 'react';

export default function useSessionStorage<T>(key: string, initialValue?: string): [T, (item: any) => void] {
    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = window.sessionStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        }
        catch (error) {
            console.log(error);
            return initialValue;
        }
    })
    const setValue = (value: any) => {
        try {
            const valueToStore = value instanceof Function ? value(storedValue) : value;
            setStoredValue(valueToStore);
            window.sessionStorage.setItem(key, JSON.stringify(valueToStore));
        }
        catch (error) {
            console.log(error);
        }
    };
    return [storedValue, setValue];
}