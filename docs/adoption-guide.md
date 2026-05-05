# The Pyramid Architecture Adoption

## Adoption Check List

- □ Does your system have clear business domain boundaries?
- □ Do you anticipate needing to scale modules independently?
- □ Is data sovereignty or tenant isolation a requirement?
- □ Does your team value compile-time architectural enforcement?
- □ Are you building systems that may integrate AI agents?

If ≥3 boxes checked: Pyramid Architecture is likely a strong fit.

## Adoption Checklist by Face

| Face        | Question to Ask                                                            | If No, Consider…                                                          |
|-------------|----------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| Structural  | Are module boundaries clear and enforced at compile time?                  | Introduce sub-project isolation; define facade contracts                        |
| Connective  | Can infrastructure choices be swapped without code changes?                | Introduce extensions/drivers; refactor cross-cutting logic into interceptors    |
| Elastic     | Could any module be extracted to its own process tomorrow?                 | Ensure service-api is the only inter-module dependency; add remote client slots |
| Convergence | Could an AI agent reliably discover and invoke your system's capabilities? | Generate tool catalogs from facades; expose lifecycle events via interceptors   |



## Migration Path Table
For teams adopting incrementally:

| Current State        | First Step                                     | Next Milestone                        | Target        |
|----------------------|------------------------------------------------|---------------------------------------|---------------|
| Legacy monolith      | Extract one module with facade + dal-api       | Add interceptor for cross-module sync | M/1 modulith  |
| Ad-hoc microservices | Define facade interfaces for existing services | Introduce manifest-driven assembly    | M/P hybrid    |
| Greenfield project   | Start with module anatomy + generator          | Add extensions/drivers as needed      | Full Pyramid  |

