## What This PR Does

Briefly describe the change and why it's needed.

Closes #ISSUE_NUMBER

## How I Tested

- [ ] Unit tests added / updated
- [ ] Manual testing completed — describe what was tested
- [ ] Works offline (airplane mode)
- [ ] All three build flavors compile (`github`, `fdroid`, `playstore`)

## Checklist

- [ ] **Module boundaries**: No `feature-*` module depends on another `feature-*` module
- [ ] **No proprietary dependencies**: `fdroid` and `github` flavors have no Firebase, no Google Play Services, no trackers
- [ ] **No hardcoded strings**: All user-facing text uses `strings.xml` entries
- [ ] **No hardcoded secrets**: No API keys, URLs, or secrets in source
- [ ] **Conventional commit**: Commit message follows `type(scope): description` format
- [ ] **Tests**: New behavior is covered by unit tests
- [ ] **ADR alignment**: This does not violate any existing ADR. If it does, a new ADR is proposed in the PR description.
- [ ] **Location-aware tables**: If this PR adds a new location-aware table, it includes a non-null `locationId` FK → `locations.id` (see ADR-016)

## Screenshots (if applicable)

Add screenshots for UI changes.
