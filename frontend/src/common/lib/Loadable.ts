type Loadable<ValueT, ErrorT> =
  | { status: "loading" }
  | { status: "loaded"; value: ValueT }
  | { status: "failed"; error: ErrorT };

export default Loadable;
