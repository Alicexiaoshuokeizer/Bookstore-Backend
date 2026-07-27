# Contributing Guidelines

Ground rules for working on this repo as a team. Please read before your first push.

## 1. Branching

- **Never push directly to `main`.** All changes go through a branch + pull request that is reviewed and approved by at least one group member.
- doing this makes sure that the main branch is: 
- **deployable**  (it should always build and pass tests.)



- Branch naming:
  | Prefix | Use for |
  |---|---|
  | `feature/short description` | new functionality |
  | `fix/short description` | bug fixes |
  | `chore/short description` | non functional tasks (config, deps, cleanup) |
  | `debug/short description` | active debugging work |
  | `docs/short description` | documentation only |

  Example: `feature/bookStore-endpointS`

## 2. Commits

- Commit often, with clear messages using the matching prefix:
    - `feature: add product search endpoint`
    - `fix: resolve null pointer on empty cart`
    - `chore: update gitignore`
    - `debug: add logging to trace MySQL connection issue`
    - `docs: update README with setup steps`
- No vague messages like "fixes" or "updates."
- Never commit generated files, `.env` or yaml, credentials, or IDE folders — check `.gitignore` before your first commit. 

## 3. Pull Requests

- No self-merging, at least one other team member reviews before merge.
- Keep PRs small and focused (one feature/fix per PR).
- Include a short description: what changed.

## 4. Merge conflicts

- Pull `main` into your branch regularly to keep conflicts small (this will save you time if you build new branches after pulling from main)
- Never force-push to `main` or any shared branch.
- If two people are touching the same file, flag it in the group chat before you start.

## 5. A note on branch protection

GitHub's free plan only enforces branch protection rules on **public** repos, not private ones. Until/unless we go public, the "no pushing to main" rule is a **team agreement, not a technical block** so it relies on everyone actually following it.
