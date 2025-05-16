

# react-hook-form (RHF) + zod

`react-hook-form` - библиотека для удобной работы с формами. Берет на себя организацию рутинных операций, вроде управления состоянием формы, отправки, хранения ошибок и т.д. Работает через хук. Например, без библиотеки нам бы пришлось писать самостоятельно код, который при вводе данных в input сохраняет введенное значение в состояние компонента; писать код, который бы во время отправки формы поддерживал статус isSubmitting и т.д. С библиотекой множество таких вещей становится делать проще.

`zod` - это библиотека для описания схемы данных формы. Также позволяет гибко описать правила валидации этих данных. Вместе с react-hook-form позволяет работать с формами еще удобнее. Хорошо поддерживает Typescript.

# Концепция RHF в двух словах

* Получаем от хука useForm разные средства для работы с формой.
* С помощью этих средств связываем конечные элементы формы (кнопки, поля ввода, селекты и т.д.) с логикой хука.
* Готово. Теперь форма управляется хуком, многие рутинные вещи реализованы, остается только правильно ими воспользоваться.

Дополнительные возможности:

* Можно использовать библиотеки вроде zod для описания схемы данных формы и правил их валидации. Эту схему затем можно интегрировать с хуком useForm.

# Как реализовать?

Использовать zod мне показалось удобным. Поэтому я разделил конспект на две части:

* Сначала почитай конспект про zod. Там написано как описать схему данных формы и составить валидацию. Он идет первым, потому что создание формы логично начать с понимания, какие данные в ней будут.
* Потом читай конспект по RHF. Там написано как связать zod-схему с хуком useForm и как интегрировать его с конкретными элементами формы.

#  Большой пример

TODO: мне не нравится использование большого примера. Обычно в нем все равно ничего не понятно. С другой стороны, без большого примера бывает не понятно как связать отдельные куски вместе. Подумать, что делать.

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
```


