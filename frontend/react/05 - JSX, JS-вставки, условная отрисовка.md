# Что такое JSX

Любой компонент возвращает JSX-разметку. JSX - это синтаксическое расширение Javascript'а, которое содержит аналоги почти всех HTML-тегов и позволяет удобным образом писать HTML-подобную разметку прямо в компоненте, вместо того чтобы возиться с объектами. Потом реакт преобразует эту разметку в настоящий HTML.

В целом они почти идентичны. Разве что в JSX используется camelCase для атрибутов, т.к. Javascript не позволяет использовать тире в именах переменных и полей, а все атрибуты JSX-"тега" становятся полями объекта. Есть [онлайн-сервисы](https://transform.tools/html-to-jsx) по преобразованию HTML в JSX. Про JSX в [документации](https://react.dev/learn/writing-markup-with-jsx#1-return-a-single-root-element) реакта, дополнительная [информация](https://react.dev/reference/react-dom/components/common).

# JS-вставки в JSX

## Синтаксис вставок, условная отрисовка

### Зачем нужны вставки

JSX позволяет использовать JS-вставки, с помощью которых можно:

* Добавить в разметку динамические данные (значения переменных например).
* Вызвать функции.
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
      {/* <-- Js-вставки на строках ниже между фигурными скобками { } */}
      {flag && <div>{message}</div>}
      <div>{flag ? sayHi() : sayGoodbye()}</div>
      <button onClick={e => setFlag(!flag)}>{flag ? 'Скрыть' : 'Показать'}</button>
    </>
  );
}
```

Где можно использовать вставку:

* В тексте тега, например как тут `<div>{message}</div>`.
* В значении атрибута, например как тут `<button onClick={e => setFlag(!flag)}>`

### Примеры вставок и объяснения

* ```react
  {flag && <div>{message}</div>}
  ```

  Используется, когда надо показать или скрыть компонент на основе условия. Оператор && работает так: если условие слева true, то возвращается правая часть оператора. Если false, то возвращается false. Соответственно, когда true, реакт получает кусок разметки и отрисовывает его, а когда false, то реакт получает false и поймет, что ничего отрисовывать не надо.

  Важно! Не надо использовать цифры в качестве условия. 0 это не false, а 0 и реакт отрисует 0. Надо писать условие с цифрами явно, например `messageCount > 0 && <MessageList />`
  
* ```react
  {flag ? <div>{message}</div> : <span>{report}</span>}
  ```

  Используется, когда надо на основе условия показать один или другой компонент. Если flag = true, то отрисовать div с сообщением внутри, а если flag = false, то span с отчетом. На практике вместо таких простых элементов могут отображаться более сложные компоненты.

* ```react
  <div>{flag ? sayHi() : sayGoodbye()}</div>
  ```

  Вызов одной или другой функции на основе значения флага.

* ```react
  <div>{message} {flag ? "✔" : null}</div>
  ```

  Демонстрация того, что несколько выражений `{}{}` могут идти друг за другом, а также того, что null можно использовать в случаях, когда не нужно ничего отрисовывать.
  
* ```react
  <div style={flag ? {backgroundColor: 'green'} : {backgroundColor: 'red'}}>
    {flag ? message : (
        <del>
          message
        </del>
      ) 
    }
  </div>
  ```

  Когда конструкции в условии располагаются на нескольких строках, могут понадобиться круглые скобки для улучшения читаемости и предотвращения трудноуловимых ошибок.

### JSX и переменные

JSX можно сохранять в переменные и таким образом готовить блоки разметки заранее. Например:

```react
export default function Dyno() {
  const [flag, setFlag] = useState(true);
  let message = "Привет, JSX!";
    
  if (flag) {
    message = (  // <-- Помещаем JSX в переменную
      <span>✔ <del>{message}</del></span>
    );
  }

  return (
    <>
      <div>{message}</div>
      <button onClick={e => setFlag(!flag)}>{flag ? 'Не сделано' : 'Сделано'}</button>
    </>
  );
}
```

Более продвинутый пример:

```react
export default function Gallery() {
  const famousUssrPeople = getPeople()
    .map(p => <li key={p.id}><Profile info={p} /></li>);

  return (
    <section>
      <h1>Известные люди СССР</h1>
      <ul>{famousUssrPeople}</ul>
    </section>
  );
}
```

Здесь, вместо того чтобы выполнять map прямо посреди разметки, мы преобразовали массив данных в массив элементов списка и сохранили полученный JSX в переменную, а затем вставили ее в тело списка. Разметка получилась компактнее.

### Операторы if-else

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

## Объекты во вставках

Когда *внутри* фигурных скобок встречаются другие фигурные скобки, то вложенные означают объект. Например:

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

  

