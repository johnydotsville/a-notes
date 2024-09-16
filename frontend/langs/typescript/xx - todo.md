Синтаксические вещи, которые надо разобрать.

# 1

```typescript
export interface IAppShell {
  readonly moveItemToTrash: (path: string) => Promise<void>
```

Что тут означает конструкция `=>`

P.S. Похоже то, что в поле moveItemToTrash можно положить лямбду, которая возвращает Promise void

# 2

```typescript
readonly repositories: ReadonlyArray<Repository | CloningRepository>
```

То, что дженерик можно закрыть любым из этих типов?

# 3

```typescript
export function isMergeConflictState(
  conflictStatus: ConflictState
): conflictStatus is MergeConflictState {
  return conflictStatus.kind === 'merge'
}
```

`is` тут что значит?

# 4

```typescript
export function findContributionTargetDefaultBranch(
  repository: Repository,
  { defaultBranch, upstreamDefaultBranch }: IBranchesState
): Branch | null {
  return isRepositoryWithGitHubRepository(repository)
    ? upstreamDefaultBranch ?? defaultBranch
    : defaultBranch
}
```

Что это за конструкция `{ defaultBranch, upstreamDefaultBranch }` частичная деструктуризация что ли какая-то?

# 5

```typescript
export class Account {
  
  public constructor(
    public readonly login: string,
    public readonly endpoint: string,
    public readonly token: string,
```

Что означает `public readonly` примененные к параметру конструктора?

# 6

ReadonlyArray

# 7

```typescript
export type ForkedGitHubRepository = GitHubRepository & {
  readonly parent: GitHubRepository
  readonly fork: true
}
```

`&` здесь что значит?