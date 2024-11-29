# if-else во вставках

В качестве JS-вставок JSX поддерживает только JS-выражения, т.е. например тернарный оператор, && и т.д. Поэтому использовать if-else непосредственно в JSX нельзя. Однако можно оформить if-else или любую другую объемную логику в виде функции и вызвать ее в нужном месте. Например:

```react
import {useState} from 'react';

function renderMessage(flag, message) {  // <-- Оформляем if-else в отдельную функцию
  if (flag)
    return <div style={{backgroundColor: 'green'}}>{message}</div>;
  else
    return <div style={{backgroundColor: 'red'}}>{message}</div>;
}

export default function Dyno() {
  const [flag, setFlag] = useState(true);
  const message = "Привет, JSX!";

  return (
    <>
      {renderMessage(flag, message)} {/* <-- И вызываем ее в JSX */}
      <button onClick={e => setFlag(!flag)}>{flag ? 'Зеленый' : 'Красный'}</button>
    </>
  );
}
```

Пример синтетический и просто демонстрирует идею, как можно поступить, если по каким-то причинам нужен именно if-else или switch или еще что-то такое, что неудобно реализовывать простыми конструкциями вроде тернарного оператора.

