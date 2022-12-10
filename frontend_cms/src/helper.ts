export async function fetcher<Data, Payload = undefined>(
    path: string,
    payload?: Payload,
    method?: "GET" | "POST" | "PUT"
) {
    const res = await fetch("/cms/api/" + path, {
        method: method ? method : payload ? "POST" : "GET",
        ...(payload && { body: JSON.stringify(payload) }),
        credentials: "include",
        headers: {
            accept: "application/json",
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        let error = "";
        try {
            error = await res.text();
        } catch {
            throw new Error(`Failed to fetch ${path}: ${res.statusText}`);
        }
        throw new Error(error);
    }

    if (res.status === 204) return undefined;

    return (await res.json()) as Data;
}
