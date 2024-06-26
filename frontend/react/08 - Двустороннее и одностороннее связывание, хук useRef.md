# Двустороннее связывание

Двустороннее связывание - это когда изменение значения элемента приводит к изменению состояния компонента, а изменение состояния компонента ведет к изменению значения элемента.

## Простой

Пример компонента, состояние которого связано с элементом ввода:

```react
import {useState} from 'react';

const Message = () => {
  const [message, setMessage] = useState("Привер, мир!");

  return (
    <div>
      <h1>{message}</h1>
      <input type="text" value={message} onChange={event => setMessage(event.target.value)} />
    </div>
  );
};

export default Message;
```

* Объявляем состояние в виде простой строки.
* Привязываем это состояние к значению элемента (в данном случае input, используя пропс value).
* В событии изменения элемента вызываем функцию изменения состояния.
* Теперь при изменении элемента меняется состояние, а при изменении состояния меняется значение в элементе. Вот и получилось двустороннее связывание.

## Вложенный элемент

Теперь пример посложнее - элемент будет вложен в отдельный компонент и нужно прокинуть атрибуты из компонента MyInput до внутреннего элемента input:

```react
import {useState} from 'react';

const Message = () => {
  const [message, setMessage] = useState("Привер, мир!");

  return (
    <div>
      <h1>{message}</h1>
      <MyInput type="text" value={message} onChange={event => setMessage(event.target.value)} />
    </div>
  );
};

export default Message;
```

Все эти атрибуты нужно прокинуть до элемента, лежащего внутри компонента MyInput. Это можно сделать с помощью оператора расширения `...`, разбив объект пропсов на отдельные свойства. Таким образом все атрибуты, которые мы указали для MyInput, окажутся и в обычном input:

```react
const MyInput = (props) => {
  return (
    <input {...props} />
  );
};

export default MyInput;
```

# Одностороннее связывание

## Хук useRef

При одностороннем связывании мы не связываем элемент с состоянием, а просто получаем значение из элемента. Для этого понадобится хук `useRef`:

```react
import {useRef} from 'react';
```

В паре с атрибутом `ref` компонента этот хук позволит нам получить ссылку на компонент, а стало быть и ко всем его свойствам, вроде значения. 

## Простой

```react
import {useRef} from 'react';

const Message = () => {
  
  const messageRef = useRef();  // <-- 1. С помощью хука создаем некий объект

  const log = () => {
    console.log(messageRef.current.value);  // <-- 3. Получаем значение элемента
  }

  return (
    <div>
      <input type="text" ref={messageRef} />  {/* <-- 2. Связываем этот объект с элементом */}
      <button onClick={log}>Вывести в консоль</button>
    </div>
  );
};

export default Message;
```

* хук useRef возвращает нам объект со свойством `current`. Можно передать в хук какое-нибудь значение и он вернет нам объект с этим значением, доступным через свойство current.
* В данном примере мы передаем полученный из хука объект в атрибут `ref` элемента ввода и таким образом в объекте оказывается ссылка на этот DOM-элемент.
* Используя эту ссылку, мы получаем значение из поля ввода и выводим в консоль.

## Вложенный элемент

Чтобы заработало, обычно требуется перезагрузка страницы с программой.

```react
import {useRef} from 'react';
import InputCustom from './InputCustom';

const Message = () => {
  
  const messageRef = useRef();

  const log = () => {
    console.log(messageRef.current.value);
  }

  return (
    <div>
      <InputCustom type="text" ref={messageRef} />
      <button onClick={log}>Вывести в консоль</button>
    </div>
  );
};

export default Message;
```

Когда используется не непосредственный элемент, а произвольный, то нужно обернуть его в функцию `React.forwardRef`:

```react
import React from 'react';

const InputCustom = React.forwardRef((props, ref) => {
  return (
    <input ref={ref} {...props} />
  );
});

export default InputCustom;
```

TODO: подробнее с этим разобраться. Наверное ref это как key, не передается через пропсы явно, поэтому требуется оборачивать.