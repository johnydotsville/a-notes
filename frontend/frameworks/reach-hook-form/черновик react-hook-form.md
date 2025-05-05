

 #  Большой пример

```react
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { AccountSelector } from "../AccountSelector/AccountSelector"
import { useAccounts } from "../../hooks/useAccounts"


const schema = z.object({
  fromAccountId: z.string(),
  toAccountId: z.string(),
  amount: z.number()
    .positive("Сумма перевода должна быть больше нуля.")
}).refine(
    (data) => data.toAccountId !== data.fromAccountId,
    {
      message: "Счета не должны совпадать",
      path: ["fromAccountId"]
    }
);

type FormData = z.infer<typeof schema>;


export const MoneyTransfer = () => {
  const { accounts, isLoading, error } = useAccounts();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting }
  } = useForm<FormData>({
    resolver: zodResolver(schema)
  });

  if (isLoading) {
    return <div>Загрузка счетов...</div>
  }
  if (error) {
    return <div>{error}</div>
  }

  const onSubmit = async (data: FormData) => {
    console.log("Сейчас начнется перевод: ", data);
    await new Promise(resolve => {
      setTimeout(() => { resolve(null) }, 1500);
    });
    console.log("Перевод завершен");
  }

  // const {fromAccountId, toAccountId} = watch(["fromAccountId", "toAccountId"]);
  // console.log("fromAccountId: " + fromAccountId);
  const fromAccountId = watch("fromAccountId");
  const toAccountId = watch("toAccountId");
  // console.log("toAccountId: " + toAccountId);
  const fromAccounts = accounts.filter(a => a.id !== toAccountId);
  const toAccounts = accounts.filter(a => a.id !== fromAccountId);
  console.log(fromAccounts);
  console.log(toAccounts);

  const selectAccountFrom = (
    <div>
      <label>Откуда</label>
      <select {...register("fromAccountId")}>
        <option value="">Выберите счёт</option>
        {fromAccounts.map(a => (
          <option key={a.id} value={a.id}>
            {a.title}, {a.balance}, {a.currency}
          </option>
        ))}
      </select>
      {errors.fromAccountId && (
        <p style={{ color: "red" }}>{errors.fromAccountId.message}</p>
      )}
    </div>
  );

  const selectAccountTo = (
    <div>
      <label>Куда</label>
      <select {...register("toAccountId")}>
        <option value="">Выберите счёт</option>
        {toAccounts.map(a => (
          <option key={a.id} value={a.id}>
            {a.title}, {a.balance}, {a.currency}
          </option>
        ))}
      </select>
      {errors.toAccountId && (
        <p style={{ color: "red" }}>{errors.toAccountId.message}</p>
      )}
    </div>
  );

  const inputAmount = (
    <div>
      <label>Сумма</label>
      <input type="number" disabled={!fromAccountId} {...register("amount", { valueAsNumber: true})} />
      {errors.amount && (
        <p style={{ color: "red" }}>{errors.amount.message}</p>
      )}
    </div>
  )

  const buttonTransfer = (
    <button type="submit" disabled={isSubmitting}>
      {isSubmitting ? "Отправка..." : "Перевести"}
    </button>
  )

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      { selectAccountFrom }
      { selectAccountTo }
      { inputAmount }
      { buttonTransfer }
    </form>
  )
}




/*
<div>
      <AccountSelector label={"Откуда"} accounts={accountsFrom} {...register("fromAccountId")} />
      <AccountSelector label={"Куда"} accounts={accountsTo} {...register("toAccountId")} />
    </div>
    */
```





# Схема

В схеме мы описываем данные, которые будут использоваться в форме.

Кроме того, можем задать правила валидации как отдельных полей, так и формы в целом.

TODO: refine на отдельных полях и на схеме в целом.

```typescript
const schema = z.object({
  fromAccountId: z.string(),
  toAccountId: z.string(),
  amount: z.number()
    .positive("Сумма перевода должна быть больше нуля.")
}).refine(
    (data) => data.toAccountId !== data.fromAccountId,
    {
      message: "Счета не должны совпадать",
      path: ["fromAccountId"]
    }
);
```



# хук useForm



TODO: defaultValues

```typescript
const { control, watch, setValue } = useForm({
    defaultValues: {
      select1: "",
      select2: "",
    },
  });
```



# Функции

```typescript
const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting }
  } = useForm<FormData>({
    resolver: zodResolver(schema)
  });
```

## register

Принимает название поля формы (как в схеме) и возвращает объект (наверное) со вспопогательными вещами. Среди них - имя, ссылка, обработчик изменения значения, обработчик получения фокуса и мб что-то еще.

Все эти вещи мы прицепляем к непосредственным элементам формы, что избавляет нас от необходимости писать обработчики для изменения значения и т.д. Т.о. библиотека дает нам состояние формы - бери и пользуйся.

## watch

Это функция, которая позволяем нам подписаться на изменения значения формы и вызвать повторный рендер. Передаем ей название поля формы (как в схеме), а она нам возвращает значение (мы используем его для чтения). Если в форме произойдет изменение этого значения (например в поле ввода введут что-то новое), то watch приведет к перезагрузке формы и вернет нам обновленное значение.

```typescript
const acc = watch("account");
```

```typescript
const [accFrom, accTo] = watch(["accountFrom", "accountTo"]);
```



TODO: useWatch

TODO: со значениями по умолчанию

```typescript
const watchedFields = watch({
  firstName: "", // начальное значение (опционально)
  lastName: "",
});
```



## formState

TODO