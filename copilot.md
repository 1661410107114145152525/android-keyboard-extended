# Copilot Development Guidelines

## Testing Requirements
- Tests should **always be run and fixed immediately** after making any code changes
- Run tests before committing changes to ensure nothing is broken
- If tests fail, fix them before proceeding with additional changes

## Code Change Philosophy
- Changes should be made in a way that **code not coming from this fork is changed as little as possible**
- If upstream code must be changed, make changes that are **update-safe** - changes that won't break when pulling updates from upstream
- Prefer adding new files over modifying existing upstream files
- When modifying existing files, make minimal and localized changes
- Document any necessary changes to upstream code clearly
