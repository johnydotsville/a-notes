# Момент срабатывания хука

В зависимости от синтаксиса, эффект будет срабатывать в разные моменты.

## Каждую реальную перерисовку

Если нужно, чтобы эффект срабатывал при каждой отрисовке компонента, передаем в хук только колбэк:

```react
useEffect(() => {
  console.log('Эффект работает на каждую реальную перерисовку компонента.');
});  // <-- Потому что вообще не передали массив зависимостей
```

Пример: хук будет срабатывать:

* Когда мы показываем \ скрываем компонент. Скрытие приводит к размонтированию, показ - к монтированию, а при монтировании срабатывает первичная отрисовка.

* Когда мы изменяем значение в поле ввода и применяем его. Потому что при этом изменяется состояние компонента и стало быть реакт должен его перерисовать.

  Важное замечание: эффект, срабатывает только при реальной необходимости перерисовки. Если ввести что-то в поле ввода, и несколько раз нажать на кнопку "Задать", то хук не сработает повторно. Реакт видит, что реальных изменений вносить в DOM не надо и не перерисовывает.

```react
import { useEffect } from 'react';
import { useState } from 'react';
import { useRef } from 'react';

export default function EffectsDemo() {
  const [showA, setShowA] = useState(true);
  
  return (
    <>
      <button onClick={() => setShowA(!showA)}>Скрыть \ показать</button>
      { showA && <A /> }
    </>
  );
}

// <-- Компонент А
function A() {
  const [message, setMessage] = useState(null);
  const msg = useRef();

  useEffect(() => {  // <-- Эффект
    console.log('Эффект сработал на Рендеринг A.');
  });

  return (
    <div style={{ border: '2px solid red' }}>
      <div>{message ?? 'Сообщение не задано.'}</div>
      <input ref={msg} />
      <button onClick={() => setMessage(msg.current.value)}>Задать</button>
    </div>
  );
}
```

## Монтирование

Чтобы хук сработал на этапе монтирования, мы передаем в него колбэк и *пустой* массив зависимостей:

```react
useEffect(() => {
  console.log('Эффект работает только при монтировании компонента.');
}, []);  // <-- Потому что массив зависимостей пуст
```

Пример: эффект сработает только при показе компонента. Скрытие приводит к размонтированию, показ - к монтированию. При изменении значения в поле ввода эффект работать не будет.

```react
import { useEffect } from 'react';
import { useState } from 'react';
import { useRef } from 'react';

export default function EffectsDemo() {
  const [showA, setShowA] = useState(true);
  
  return (
    <>
      <button onClick={() => setShowA(!showA)}>Показать \ скрыть</button>
      { showA && <A /> }
    </>
  );
}

// <-- Компонент А
function A() {
  const [message, setMessage] = useState(null);
  const msg = useRef();

  useEffect(() => {  // <-- Эффект
    console.log('Эффект сработал на Монтирование A.');
  }, []);

  return (
    <div style={{ border: '2px solid red' }}>
      <div>{message ?? 'Сообщение не задано.'}</div>
      <input ref={msg} />
      <button onClick={() => setMessage(msg.current.value)}>Задать</button>
    </div>
  );
}
```

## Размонтирование, функция очистки

Если нужно "почистить" за компонентом (например, закрыть сетевые соединения, сбросить анимации и т.д.), тогда мы возвращаем из эффекта функцию, в которой и пишем требуемые для очистки действия. Она выполнится в следующих случаях:

* При размонтировании компонента.
* Каждый раз перед *повторным* выполнением колбэка.

```react
useEffect(() => {
  console.log('Эффект работает только при монтировании компонента.');
  return () => {  // <-- Эта функция выполнится при размонтировании компонента
    // <-- или перед повторным выполнением хука
    console.log('Сработала функция очистки.');
  }
});
```

Пример: функция очистки сработает

* При скрытии компонента, потому что происходит размонтирование.
* При изменении сообщения с помощью кнопки. Т.к. в хуке не указан массив зависимостей, значит хук будет выполнять колбэк при каждой перерисовке. А функция очистки срабатывает, как написано выше, перед каждым повторным выполнением колбэка.

```react
import { useEffect } from 'react';
import { useState } from 'react';
import { useRef } from 'react';

export default function EffectsDemo() {
  const [showA, setShowA] = useState(true);
  
  return (
    <>
      <button onClick={() => setShowA(!showA)}>Показать \ скрыть</button>
      { showA && <A /> }
    </>
  );
}

// <-- Компонент А
function A() {
  const [message, setMessage] = useState(null);
  const msg = useRef();

  useEffect(() => {  // <-- Эффект
    console.log('Эффект сработал на Монтирование A.');
    return () => {
      console.log('Сработала функция очистки A.');
    }
  });

  return (
    <div style={{ border: '2px solid red' }}>
      <div>{message ?? 'Сообщение не задано.'}</div>
      <input ref={msg} />
      <button onClick={() => setMessage(msg.current.value)}>Задать</button>
    </div>
  );
}
```

## Зависимости

Если передать зависимости, то хук будет работать при изменении *любой* из них. Обычно в зависимостях эффектов указываются те вещи, на которые опирается работа кода эффекта.

```react
useEffect({() => {
  console.log('Эффект срабатывает при монтировании и изменении любой из зависимостей');
}, [dep1, dep2]);  // <-- Массив зависимостей
```

Пример: эффект будет срабатывать при монтировании и при изменении зависимости:

```react
import { useEffect } from 'react';
import { useState } from 'react';
import { useRef } from 'react';

export default function EffectsDemo() {
  const [showA, setShowA] = useState(true);
  
  return (
    <>
      <button onClick={() => setShowA(!showA)}>Показать \ скрыть</button>
      { showA && <A /> }
    </>
  );
}

// <-- Компонент А
function A() {
  const [message, setMessage] = useState(null);
  const [flag, setFlag] = useState(true);
  const msg = useRef();

  useEffect(() => {  // <-- Эффект
    console.log(`Эффект срабатывает при монтировании и изменении зависимости flag: ${flag}.`);
  }, [flag]);

  return (
    <div style={{ border: '2px solid red' }}>
      <div>{message ?? 'Сообщение не задано.'}</div>
      <input ref={msg} />
      <button onClick={() => setMessage(msg.current.value)}>Задать</button>
      <button onClick={() => setFlag(!flag)}>Флаг</button>
    </div>
  );
}
```

