TODO: здесь про то, как "передать register() в AccountSelector".









Компонент формы:

```react
const schema = z.object({  // <-- Схема данных формы.
  fromAccountId: z.string()
  toAccountId: z.string()  // <-- Имена полей будем брать отсюда.
  amount: z.number()
});

export const MoneyTransfer = () => {
  const {
    control,  // <-- Объект с вещами для связи кастомного компонента с формой.
    register, handleSubmit, watch
  } = useForm<FormData>({
    resolver: zodResolver(schema)
  });
	
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div>
        <AccountSelector label="Откуда" accounts={accounts} 
          name="fromAccountId"  // <-- Имя как в схеме.
          control={control}
        />  
        <AccountSelector label="Куда" accounts={accounts} 
          name="toAccountId"  // <-- Имя как в схеме.
          control={control}
        />  
    </form>
  )
}
```



Компонент выбора счета:

```react
import { useController } from "react-hook-form"

import { AccountSelectorProps } from "./types"

interface RhfProps {
  name: string,
  control: any
}

export const AccountSelector = ({ label, accounts, ...props }: AccountSelectorProps & RhfProps) => {
  const {
    field,
    fieldState: { error }
  } = useController(props);

  return (
    <div>
      <label>{ label }</label>
      <select {...field}>
        <option value="" disabled>Выберите счет...</option>
        {accounts.map(a => 
          <option key={a.id} value={a.id}>
            {a.title}, {a.balance}, {a.currency}
          </option>
        )}
      </select>
    </div>
  )
}
```

