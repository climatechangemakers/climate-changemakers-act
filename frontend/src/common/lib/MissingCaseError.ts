export default class MissingCaseError extends Error {
    constructor(shouldNeverHappen: never) {
        super(`Got an unexpected case: ${String(shouldNeverHappen)}`);
    }
}
