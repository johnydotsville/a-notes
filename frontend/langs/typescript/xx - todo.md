Синтаксические вещи, которые надо разобрать.

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



# 8 

```typescript
const Status = {
  Active: "Active",
  Inactive: "Inactive",
  Pending: "Pending",
} as const;
```

As const что это?

# 9 

Type branding, flavouring

# 10

```typescript
function employeeTypeChecker<T extends Position>(
  position: T, employee: Director | Seller 
) {
  if (position === Position.Director) {
    return employee as T extends Position.Director ? Director : never
  } else {
    return employee as T extends Position.Seller ? Seller : never;    
  }
}
```

Функция, во-первых, ничего не возвращает судя по заголовку, и в return что-то не особо понятное.



# 11

Конструкция as const в целом.