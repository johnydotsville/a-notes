# Асинхронность в redux

## В чем суть

TODO: погуглить и дописать: мб когда дело касается танков и асинхронных запросов, хорошим тоном является добавление статуса выполнения, типа "запрос отправлен", "запрос выполнился успешно", "запрос вернулся с ошибкой". Потому что в тулките даже автоматически эти штуки генерируются. Мб и тут положено их писать, только вручную?

Здесь будет про библиотеку `redux-thunk`. Она крошечная и вся ее суть сводится к тому, что мы с ее помощью можем диспатчить не только объекты, но и функции. Для чего это нужно?

В базовом случае из action creator мы возвращаем объект действия и передаем его в dispatch. В этом случае дальнейшая работа происходит *синхронно*, т.е. действие сразу отправляется в редюсеры. Пример: ввели в поле ввода имя и добавляем его в хранилище.

Однако некоторые сценарии требуют дополнительной работы. Например, отправить это имя на сервер для сохранения. Или наоборот, загрузить имя \ массив имен с сервера. В этом случае в обработку действия вступает асинхронность и возникает вопрос, куда ее запихнуть. В редюсерах сайд эффектов быть не должно.

Поэтому существует паттерн для оформления таких ситуаций. Для такого действия action creator должен возвращать не объект, а функцию с параметрами. Пример:

```javascript
function actionCreatorFoobar(someData) {
  return async (dispatch, getState) => {  // <-- Возвращаем не объект, а функцию (назовем ее F)
    try {
      const result = await someAsyncAPI.call(someData); // Делаем свои асинхронные дела
      dispatch(result);
    } catch (err) {
      // Обрабатываем ошибку
    }
  }
}

dispatch(actionCreatorFoobar('Hello, redux-thunk!'));
```

Основная идея такова: когда мы будем диспатчить полученную функцию `F`, то редакс передаст ей первым параметром функцию-диспетчер, а вторым параметром - функцию для получения состояния. Т.о., мы сможем оформить внутри такой функции любые сайд-эффекты, а когда наконец получим результат, то с помощью `dispatch` отдать редаксу результат. Также, если нам понадобится в F зачем-то прочитать состояние, например чтобы принять какие-то решения, то мы сможем это сделать с помощью функции `getState`.

В результате такого подхода мы сможем примешивать к действиям сайд-эффекты, но при этом снаружи такой action creator не будет отличаться от обычного, который возвращает простой объект действия.

## redux-thunk

> thunk - это концепция программирования, про которую простыми словами можно сказать, что это "функция, откладывающая выполнение другой функции".

`redux-thunk` - это один из способов добавить асинхронности действиям. Есть и другие библиотеки, например, `redux-saga`.

### Установка пакета

```
npm i redux-thunk
```

### Пример

```react
import { Provider } from "react-redux";
import { createStore } from 'redux';
import { combineReducers } from 'redux';
import { useSelector } from "react-redux";
import { useDispatch } from 'react-redux';
import { applyMiddleware } from 'redux';  // <-- Чтобы включить thunk в цепочку обработки действия
import { composeWithDevTools } from 'redux-devtools-extension';
import thunk from 'redux-thunk';

const initPerson = [
  { 
    name:  'Gabe Newell',
    email: 'gaben@valvesoftware.com'
  }
];

// <-- Синхронный action-creator, возвращает объект действия
function addPersonMany(persons) {
  return {
    type: 'ADD_PERSON_MANY',
    payload: persons
  }
}

// <-- Этот action-creator возвращает функцию, а не объект.
function downloadPerson() {
  const transform = (person) => person.map(p => ({
      name:  p.name,
      email: p.email
  }));

  // <-- В итоге будем диспатчить эту функцию
  return async (dispatch, getState) => {
    try {  // <-- В этой функции можем пользоваться сайд-эффектами
      const data = await fetch('https://jsonplaceholder.typicode.com/users')
        .then(response => response.json())
        .then(json => transform(json));
      dispatch(addPersonMany(data));  // <-- Получив результат, диспатчим обычное действие
    } catch (error) {
      console.log('Ошибка при загрузке данных:' + error);
    }
  }
}

// <-- Обычный редюсер
function personReducer(state = initPerson, action) {
  switch (action.type) {
    case 'ADD_PERSON_MANY': 
      return [...state, ...action.payload];
    default:
      return state;
  }
}

// <-- Обычное объединение нескольких редюсеров в один
const rootReducer = combineReducers({
  person: personReducer
});

// <-- Подключаем middleware с танком (devtools не нужен, просто чтобы показать как
// подключить и то, и другое вместе)
const myStore = createStore(rootReducer, composeWithDevTools(applyMiddleware(thunk)));

export default function ReduxAsyncDemo() {
  return (
    <Provider store={myStore}>
      <Control />
      <Person />
    </Provider>
  );
}

// <-- Компонент с кнопкой загрузки данных.
function Control() {
  const dispatch = useDispatch();
  // <-- Диспатчим "волшебное действие", которое представлено функцией, а не объектом.
  // <-- Но со стороны этого не заметно, в том и прикол.
  const download = () => {
    // <-- Вызываем action creator, чтобы он вернул нам функцию.
    const actionWithSideEffects = downloadPerson();
    dispatch(actionWithSideEffects);  // <-- Диспатчим эту функцию.
  }

  return (
    <button onClick={download}>Загрузить асинхронно</button>
  );
}

function Person() {
  const persons = useSelector(s => s.person);
  return (
    <div>{
      (persons.length > 0) ? (
        persons.map(p => <div key={p.email}>{p.name}, {p.email}</div>)
      ) : (
        <div>Нет информации о людях.</div>
      )
    }</div>
  );
}
```

Вся суть собрана вот в этой функции:

```javascript
function downloadPerson() {  // <-- По сути, это действие-обертка.
  const transform = (person) => person.map(p => ({
      name:  p.name,
      email: p.email
  }));

  return async (dispatch, getState) => {
    try {
      const data = await fetch('https://jsonplaceholder.typicode.com/users')
        .then(response => response.json())
        .then(json => transform(json));
      dispatch(addPersonMany(data));  // <-- Получив результат, диспатчим обычное действие
    } catch (error) {
      console.log('Ошибка при загрузке данных:' + error);
    }
  }
}
```

Это action creator. Хотя он возвращает не объект действия, а функцию, результат его вызова можно диспатчить. Поэтому внешне работа с таким AC ничем не отличается от работы с любым другим AC. Именно внедрение thunk-миддлвари позволяет передавать в dispatch не только объект, но и функцию. В случае ошибки сервера, мы могли бы задиспатчить другое действие, какое-нибудь DOWNLOAD_PERSON_ERROR, и даже при начале загрузки DOWNLOAD_PERSON_START, чтобы как-то отразить начало загрузки.

Без thunk мы бы могли сделать как-то так:

```react
function Control() {
  const dispatch = useDispatch();
  const download = () => downloadPerson(dispatch);
  return (
    <button onClick={download}>Загрузить асинхронно</button>
  );
}

function downloadPerson(dispatch) {
  fetch('https://jsonplaceholder.typicode.com/users')  // или через try-await
    .then(response => response.json())
    .then(json => dispatch(addPersonMany(transform(json))));  // <-- Диспатчим обычное действие
}
```

Плюсы такого подхода:

* Не требуется подключать и настраивать дополнительную библиотеку (thunk).
* На первый взгляд код более наглядный.

Минусы:

* Все дополнительные манипуляции, связанные с действием (загрузка \ отправка на сервер) оторваны от самого действия и выглядят как отдельные функции. Т.о., если например при добавлении имени есть требование сохранить его на сервер, а не только добавить в локальное хранилище, то нам придется не забывать об этом и самостоятельно комбинировать отправку с последующим сохранением в локальное хранилище. В то время как с танком мы можем все эти манипуляции инкапсулировать в действии-обертке.



