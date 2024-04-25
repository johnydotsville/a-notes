# Компонент

## Функциональный компонент

Функциональный компонент - это функция, которая возвращает jsx. Особенности:

* Функция может быть как обычной, так и лямбдой.
* Название компонента принято писать с большой буквы.
* Компонент надо размещать в отдельном файле с расширением `.jsx`.
* Файл должен называться так же, как и компонент.

Например:

```react
const Hello = () => {
  return (
    <h1>Привет, мир!</h1>
  );
};

export default Hello;
```

```react
const Hello = function() {
  return (
    <h1>Привет, мир!</h1>
  );
};

export default Hello;
```

```react
function Hello() {
  return (
    <h1>Привет, мир!</h1>
  );
};

export default Hello;
```

## Классовый компонент

Описание компонентов через классы является устаревшим подходом и рекомендуется пользоваться функциональными компонентами. Особенности классовых компонентов:

* Класс должен наследоваться от класса `React.Component`.
* Конструктор должен принимать параметр props и передавать его в родительский конструктор.
* Класс должен иметь метод `render()`, который возвращает jsx.

```react
import React from 'react';

class Hello extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {
    return (
      <h1>Привет, мир!</h1>
    );
  }

}

export default Hello;
```

## Общие правила

TODO: некоторые личные замечания по компонентам:

* Компонент представляет собой единый блок, поэтому в return мы должны возвращать, грубо говоря, единый div в котором находится вся начинка.
* В файле может быть несколько компонентов, но только один можно экспортировать.
* Несколько компонентов в одном файле может понадобиться, чтобы собрать единый компонент из нескольких небольших.

# Состояние компонента

## Функциональный компонент

### Хук useState

```react
import {useState} from 'react';
```

Работа с состоянием компонента начинается с вызова функции-хука `useState(начальное_состояние)`. Мы передаем в нее значение \ объект, который хотим использовать в качестве начального состояния, а она нам возвращает это состояние, плюс функцию для его изменения.

```react
const [состояние, функцияДляИзмененияСостояния] = useState(5);
```

Теперь, чтобы сказать реакту, что компонент изменился, мы вызываем хук, передав в него новое значение состояния. Само по себе состояние мы обычно не изменяем, а в хук передаем именно новое значение \ новый объект и если надо, формируем его на основе текущего состояния.

Пример:

```react
import {useState} from 'react';

const Counter = () => {
  const [count, setCount] = useState(5);  // <-- Создали состояние и получили ф-ию для его измения

  function increment() {
    setCount(count + 1);  // <-- Сообщаем реакту об изменении состояния
    // Но саму переменную count мы не меняем
  }

  function decrement() {
    setCount(count - 1);
  }

  return (
    <div>
      <h1>{count}</h1>
      <button onClick={increment}>Увеличить</button>
      <button onClick={decrement}>Уменьшить</button>
    </div>
  );
}

export default Counter;
```

Вот пример для состояния, представленного объектом:

```react
import {useState} from 'react';

const Counter = () => {
  const [data, setData] = useState({x: 5, y: 10});

  function increment() {
    setData({
      x: data.x + 1,
      y: data.y + 1
    });
  }

  function decrement() {
    setData({
      x: data.x - 1,
      y: data.y - 1
    });
  }

  return (
    <div>
      <h1>x: {data.x}, y: {data.y}</h1>
      <button onClick={increment}>Увеличить</button>
      <button onClick={decrement}>Уменьшить</button>
    </div>
  );
}

export default Counter;
```

### Управляемый компонент

Пример компонента, состояние которого связано с элементом ввода ("двустороннее связывание"). Пока не знаю, отдельная это тема или нет, пусть полежит здесь:

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

## Классовый компонент

* В классовом компоненте состояние объявляется в конструкторе.
* Под состояние существует свойство `state`.
  * Для изменения состояния - метод `setState`.
* Все методы класса необходимо биндить, потому что теряется контекст. TODO: можно кстати почитать, почему так происходит.

```react
import React from 'react';

class Counter extends React.Component {
  
  constructor(props) {
    super(props);
    this.state = {  // <-- Объявление состояния
      count: 5
    };
    this.increment = this.increment.bind(this);  // <-- Надо биндить методы
    this.decrement = this.decrement.bind(this);  // <-- Надо биндить методы
  }

  increment() {
    this.setState({
      count: this.state.count + 1
    });
  }

  decrement() {
    this.setState({
      count: this.state.count - 1
    });
  }

  render() {
    return (
      <div>
        <h1>{this.state.count}</h1>
        <button onClick={this.increment}>Увеличить</button>
        <button onClick={this.decrement}>Уменьшить</button>
      </div>
    );
  }

}

export default Counter;
```

