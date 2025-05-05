# Структура папок

TODO: про фичеориентированную структуру.





# Типизация

## Типизация пропсов компонентов

Формула `interface ИмяКомпонентаProps`:

```typescript
interface AccountSelectorProps {
  accounts: ReadonlyArray<Account>;
}
```

```react
const AccountSelector = ({ accounts }: AccountSelectorProps) => {
  // ...
}
```

## Типизация хуков

Формула `interface ИмяХукаResult`:

```typescript
interface UseAccountsResult {
  accounts: ReadonlyArray<Account>;
  isLoading: boolean;
  error: string | null;
}
```

```typescript
export const useAccounts = (): UseAccountsResult => {
  // ...
  return { accounts, isLoading, error };
}
```

