# Что такое JSX

Любой компонент возвращает JSX-разметку. JSX - это синтаксическое расширение Javascript'а, которое содержит аналоги почти всех HTML-тегов и позволяет удобным образом писать HTML-подобную разметку прямо в компоненте, вместо того чтобы возиться с объектами. Потом реакт преобразует эту разметку в настоящий HTML.

В целом они почти идентичны. Разве что в JSX используется camelCase для атрибутов, т.к. Javascript не позволяет использовать тире в именах переменных и полей, а все атрибуты JSX-"тега" становятся полями объекта. Есть [онлайн-сервисы](https://transform.tools/html-to-jsx) по преобразованию HTML в JSX. Про JSX в [документации](https://react.dev/learn/writing-markup-with-jsx#1-return-a-single-root-element) реакта, дополнительная [информация](https://react.dev/reference/react-dom/components/common).

# JS-вставки в JSX

## Синтаксис вставок

JSX позволяет использовать JS-вставки, с помощью которых можно:

* Добавить в разметку динамические данные (значения переменных например), вызвать функции.
* Сделать условную отрисовку. Например, при одном условии отрисовывается один компонент, а при другом - другой.

JS-вставки делаются с использованием фигурных скобок `{}`. Пример:

```react
import {useState} from 'react';

function sayHi() {
  return 'Привет!';
}

const sayGoodbye = () => 'Пока!';

export default function Dyno() {
  const [flag, setFlag] = useState(true);
  const message = "Привет, JSX!";

  return (
    <>
      {flag && <div>{message}</div>}
      {flag ? <div>{message}</div> : null}
      {flag ? <div>{sayHi()}</div> : <div>{sayGoodbye()}</div>}
      <button onClick={e => setFlag(!flag)}>{flag ? 'Скрыть' : 'Показать'}</button>
    </>
  );
}
```

Где можно использовать вставку:

* В тексте тега, например как тут `<div>{message}</div>`.
* В значении атрибута, например как тут `<button onClick={e => setFlag(!flag)}>`

## Условия if-else

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



## Объекты во вставках

Когда внутри фигурных скобок встречаются другие фигурные скобки, то вложенные означают объект. Например:

```react
import {useState} from 'react';

export default function Dyno() {
  const [flag, setFlag] = useState(true);
  const message = "Привет, JSX!";

  return (
    <>
      {<div style={{backgroundColor: 'yellow'}}>Желтый</div>}
      {<div style={flag ? {backgroundColor: 'green'} : {backgroundColor: 'red'}}>{message}</div>}
      <button onClick={e => setFlag(!flag)}>{flag ? 'Зеленый' : 'Красный'}</button>
    </>
  );
}
```

У jsx-элементов есть атрибут style, с помощью которого можно задать inline-стиль для элемента. В этот атрибут нужно поместить объект. Для формирования объекта тоже используются фигурные скобки.

## Комментарии в JSX

Чтобы закомментировать блок кода, можно использовать два синтаксиса:

* `{/*  */}`

  ```react
  return (
    <div>
      <h1>Список постов:</h1>
      {/*posts.map(p => <PostItem post={p} key={p.id} />)*/}
    </div>
  );
  ```

* `//`

  ```react
  return (
    <div>
      <h1>Список постов:</h1>
      {
        //posts.map(p => <PostItem post={p} key={p.id} />)
      }
    </div>
  );
  ```

  

