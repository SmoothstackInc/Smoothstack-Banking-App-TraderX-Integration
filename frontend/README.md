# Secure Sentinel Bank

# GitFlow Workflow Guide

## Branch Structure

- **main**: Stable release branch.
- **staging**: Pre-release branch.
- **dev**: Active development branch.
- **feature**: Feature-specific branches.

## Workflow Steps

### 1. At the start of each day or work session, begin by updating your dev branch:

- Update `dev` branch:

```
git checkout dev
git pull origin dev
```

- Create new feature branch:

```
git checkout -b feature/name
```

### 2. Switch back to your feature branch and merge or rebase with the updated dev:

```
git checkout feature/your-feature-name
git merge dev
```

### 3. Completing a Feature

- Rebase with `dev` to update your branch:

```
git rebase dev
```

- Resolve any conflicts then continue.

```
git rebase --continue
```

- Or simply merge with dev if not comfortable with rebase:

```
git merge dev
```

- Push feature branch:

```
git push -u origin feature/branch
git push origin feature/branch --force (For rebase)
```

### 4. Code Review and Merging

- Open a Merge Request (MR) in GitLab to `dev`.
- Request code review.
- After approval, merge the MR in GitLab.
- Delete feature branch post-merge.
