# AGENTS.md — shorter-url-quarkus

Umbrella workspace for solo-coding projects. Spec-driven development (SDD) is
available here and is explicit-opt-in.

## Communication style

Use the `caveman` skill voice in every session in this workspace. Terse, no
fluff, all technical facts preserved. Applies until the user says "stop caveman"
or "normal mode".

## Repo map

| Path | What | Git |
| --- | --- | --- |
| `specs/` | Constitution, feature specs, plans, tasks | this repo |
| `.opencode/` | SDD commands and the read-only spec reviewer agent | this repo |
| `docs/sdd.md` | The workflow explained for humans | this repo |
| `shorter-url-quarkus/` | Quarkus 3.40 API, Java 25, MongoDB | its own nested repo, ignored here |
| `shorter-url-web/` | Next.js 16 + TypeScript frontend (not scaffolded yet) | this repo |

The backend is the source of truth for the HTTP contract. The frontend consumes
it through generated types. Features may span both repos; the spec names the
affected repos.

## SDD workflow — hard gates

0. **Spec workflow is explicit-opt-in.** Create `spec.md`, `plan.md`, or
   `tasks.md` only when the user invokes `/specify`, `/plan-feature`, `/tasks`,
   or explicitly asks for that artifact. A general "yes", agreement to
   proceed, or a direct implementation request is not consent to start SDD.
   Direct requests proceed directly; offer `/specify` only when the user asks
   for SDD.
1. **Specify.** Write `specs/NNNN-slug/spec.md` in `draft`. Resolve open
   questions with the user. Only the user moves it to `approved`.
2. **Plan.** Draft `plan.md` against the spec; it maps every acceptance
   criterion to a component and a test strategy. A provisional plan may be
   prepared for review while the spec is `draft`, but it is not executable until
   the spec is `approved`. Resolve open questions with the user first, asking them
   one at a time.
3. **Tasks.** Draft `tasks.md` against the plan; it is executable only when the
   spec and plan are `approved`. Every task cites the acceptance criteria it
   satisfies and the files it touches.
4. **Implement.** One task at a time. Test first where practical. Tick the box,
   update `specs/README.md`, keep the spec truthful.
5. **Verify.** Map each acceptance criterion to passing evidence (test, command,
   or manual check). Only then set `status: done`.

Commands: `/specify`, `/review`, `/approve`, `/plan-feature`, `/tasks`,
`/implement`, `/verify`.

Plan and task approval is recorded in their frontmatter with `approved_by` and
`approved_on`. `/approve` requires explicit user confirmation and approves one
artifact per run.

## Non-negotiable rules

- Never create a git commit unless the user explicitly asks for one. Never
  stage or prepare commit changes unless the user explicitly asks for staging
  or committing. Leave implementation changes in the working tree.
- Never invent, widen, or silently change acceptance criteria. Propose the
  change, get explicit approval, and append it to the spec's change log.
- **Ask, don't assume; one step at a time.** During `/specify`, `/plan-feature`,
  `/tasks`, and at every approval gate, put each open decision to the user as an
  explicit question and wait for the answer. One question — or one step — per
  turn. Never batch approvals, never treat a recommendation as consent, and never
  start the next step until the current one is confirmed.
- Never create spec artifacts unless `/specify`, `/plan-feature`, `/tasks`, or
  an explicit request for that artifact was given. A "yes" to a question is not
  an SDD invocation.
- When SDD is active, never implement code while a spec is `draft` or
  `needs-clarification`.
- OpenAPI is the contract. Frontend types are generated from it, never
  hand-copied or hand-edited.
- The browser talks only to the Next.js server (BFF). Basic credentials never
  reach the browser; the session is an httpOnly cookie.
- No secrets in the repo. `.env.example` is the only committed env file.
- Runtime-tunable values (for example limits, timeouts, URLs, and feature flags)
  must use environment-backed configuration with safe defaults, not source-code
  constants. Document environment variable names and defaults in the relevant
  repo guidance.
- **Ask before installing anything.** That includes system packages, npm
  packages, Maven dependencies, container images, and project scaffolds.
  Wait for explicit confirmation before running the install command.
- Do not modify `shorter-url-quarkus/` unless a spec names it as affected.
- Do not read `archive/` or any file outside the workspace root. Archived specs
  are out of scope for id allocation, review, and reference; work only from the
  live tree.
- Test methods follow the existing project style: `should<Behavior>` or
  `should<Behavior>When<Condition>`. A brief comment names the acceptance
  criterion the test proves, e.g. `// AC-3.2`. Do not embed the AC id in the
  method name.

## Commands

### Backend — `shorter-url-quarkus/`

```bash
./mvnw test                 # unit tests, no container
./mvnw verify               # unit + integration (Testcontainers)
./mvnw quarkus:dev          # dev mode on :8080 (needs MongoDB)
podman run --name mongodb -d docker.io/library/mongo:4.2-bionic
```

See `shorter-url-quarkus/AGENTS.md` for Podman/Testcontainers setup and
deployment notes.

### Web — `shorter-url-web/` (after scaffolding)

```bash
npm run dev                 # Next.js on :3000
npm run lint
npm run typecheck
npm test
npm run build
npm run api:types           # regenerate lib/api/schema.d.ts from the backend contract
```

### Workspace

```bash
git status                  # root repo only tracks specifications and docs
```

## Spec conventions

- Folder: `specs/NNNN-kebab-slug/` with a zero-padded four-digit id.
- Files: `spec.md` (WHAT/WHY), `plan.md` (HOW), `tasks.md` (ORDER),
   `evidence.md` (PROOF).
- Frontmatter: `id`, `slug`, `title`, `status`, `approved_by`, `approved_on`,
   `projects`, `repos`, `owners`, `created`, `updated`.
- Statuses: `draft` → `approved` → `implementing` → `done`, plus
   `needs-clarification`, `on-hold`, `superseded`.
- Acceptance criteria: `AC-<story>.<n>` written as Given/When/Then and testable
   on their own. IDs are scoped to the spec.
- Traceability chain: user story → AC → plan component → task → test.
- Every spec lists its **out of scope** items and an **open questions** section.
   An open question that blocks implementation keeps the spec in `draft`.

## Definition of Done

See `specs/constitution.md`, Article 8. In short: every AC has passing
evidence, all gates for every affected repo pass, the spec/plan/tasks match
what was built, and the index is up to date.