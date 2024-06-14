# Асинхронность в Redux Toolkit

## createAsyncThunk

TODO: про createAsyncThunk потом подробнее почитать тут https://redux-toolkit.js.org/api/createAsyncThunk и законспектировать детали. Еще вот тут https://redux.js.org/tutorials/essentials/part-6-performance-normalization#thunk-arguments

Функция `createSlice` сама по себе не умеет работать с асинхронными действиями (танками). Поэтому танк нужно написать отдельно. Обычно его пишут в одном файле со слайсом.

Танк создается с помощью функции `createAsyncThunk`. Она принимает имя действия (можно поставить произвольное, но лучше составить из комбинации имени слайса и выполняемой функции, для наглядности) и код, который нужно выполнить. Обычно это тот самый асинхронный код с сайд-эффектами, вроде загрузки \ отправки данных. Например:

```javascript
async function downloadPerson() { // <-- Код может быть большой, поэтому можно оформить отдельной функцией/
  // Здесь выполняем загрузку \ отправку данных или прочую асинхронщину
}
// <-- Создаем танк, указав имя действия, и колбэк, который будет выполняться в танке
const thunkDownloadPerson = createAsyncThunk('person/downloadPerson', downloadPerson);

// <-- А диспатчить будем так
dispatch(thunkDownloadPerson());  // <-- Диспатчим действие-танк
```

Возвращает эта функция нам экшен криейтор, который мы диспатчим как обычно.

Важный момент. Обычно при выполнении асинхронной работы к состоянию добавляют дополнительные поля вроде `status` и `error`. Например, у нас есть состояние с массивом людей, который мы хотим загрузить с сервера. Тогда мы бы могли при отправке запроса поставить `status = loading`, при успешной загрузке success, при ошибке - fail, а сам текст ошибки поместить в поле error. Поскольку это типичный паттерн, то createAsyncThunk это реализует из коробки. Он автоматически генерирует экшены для этих ситуаций и автоматически их диспатчит. Экшены можно получить так:

```javascript
thunkDownloadPerson.pending  // <-- экшен для ситуации "запрос отправлен"
thunkDownloadPerson.fulfilled  // <-- для ситуации "запрос выполнен успешно"
thunkDownloadPerson.rejected  // <-- для ситуации "запрос выполнен НЕ успешно"
```

Как ими пользоваться и как писать для них обработчики - показано дальше в примере и прокомментировано отдельно. А здесь просто хотелось обратить внимание, что есть такая штука.

# Пример

## Полный код

```react
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { createSlice } from "@reduxjs/toolkit";
import { useSelector } from "react-redux";
import { useDispatch } from 'react-redux';

// <-- Написал функцию загрузки людей отдельно просто чтобы не загромождать второй параметр createAsyncThunk
async function downloadPerson() {
  const transform = (person) => person.map(p => ({
    name:  p.name,
    email: p.email
  }));
  
  const data = await fetch('https://jsonplaceholder.typicode.com/users')
    .then(response => {  // <-- Заморочился с проверками, т.к. тулкит рейтит статусы выполняемой работы
      if (response.status != 200)
        throw new Error('Не удалось загрузить данные.');  // <-- На каждый потенциальный косяк пишу его детали
      return response.json(); 
    })
    .then(json => {
      try {
        return transform(json);
      } catch (err) {
        throw new Error('Не удалось обработать загруженные данные.');
      }
    });

  return data;
}
// <-- Создаем танк из функции загрузки людей. Имя действия произвольное, но лучше по имени слайса + функции.
const thunkDownloadPerson = createAsyncThunk('person/downloadPerson', downloadPerson);

const slicePerson = createSlice({
  name: 'person',
  initialState: {
    personList: [
      {
        name:  'Gabe Newell',
        email: 'gaben@valvesoftware.com'
      }
    ],
    status: 'idle',
    error: null
  },
  reducers: {
    addPersonMany: (state, action) => {
      state.push(action.payload);
    }
  },
  // <-- Более подробное объяснение чуть дальше
  extraReducers(builder) {  // <-- Дописываем дополнительные функции по модификации фрагмента
    builder
      .addCase(thunkDownloadPerson.pending, (state) => {  // <-- Указываем действие, на которое должна
        state.error = null;                               // реагировать функция
        state.status = 'loading';
      })
      .addCase(thunkDownloadPerson.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.error = null;
        state.personList = state.personList.concat(action.payload);
      })
      .addCase(thunkDownloadPerson.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
  }
});

// <-- На самом деле в этом примере этот экшен криейтор не понадобится. Просто для массовки.
const { addPersonMany } = slicePerson.actions;
const personReducer = slicePerson.reducer;

const myStore = configureStore({
  reducer: {
    person: personReducer
  }
});

export default function ReduxToolkitAsyncDemo() {
  return (
    <Provider store={myStore}>
      <Control />
      <Person />
    </Provider>
  );
}

function Control() {
  const dispatch = useDispatch();
  const download = () => {
    dispatch(thunkDownloadPerson());  // <-- Диспатчим танк
  }

  return (
    <button onClick={download}>Загрузить асинхронно</button>
  );
}

function Person() {
  const persons = useSelector(s => s.person.personList);
  const status = useSelector(s => s.person.status);
  const error = useSelector(s => s.person.error);

  return (
    <>{
      (status === 'loading') ?
        <div>Данные загружаются...</div> :
          (status === 'failed') ?
            (<div>"Ошибка:" {error}</div>) :
                <PersonList persons={persons} />
    }</>
  );
}

function PersonList({persons}) {
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

## Обработка авто-экшенов

Как я писал в начале, тулкит автоматически создает экшены на `отправку`, `успех` и `провал` запроса. Когда мы диспатчим танк, то автоматически диспатчится экшен отправки. Поскольку танк внутри себя подразумевает промис, то когда этот промис завершается успешно, автоматически диспатчится успех. Если промис отклоняется, то автоматически диспатчится провал.

Поэтому мы должны написать обработчики для этих экшенов. Это делается в слайсе, отдельным параметром идет функция с объектом строителя `extraReducers(builder)`:

```javascript
// <-- Создаем танк
const thunkDownloadPerson = createAsyncThunk('person/downloadPerson', downloadPerson);
// Имена экшенов теперь можно получить через эти свойства:
//     thunkDownloadPerson.pending
//     thunkDownloadPerson.fulfilled
//     thunkDownloadPerson.rejected
...
const slicePerson = createSlice({
  name: 'person',
  initialState: {
    personList: [
      {
        name:  'Gabe Newell',
        email: 'gaben@valvesoftware.com'
      }
    ],
    status: 'idle',
    error: null
  },
  reducers: {
    addPersonMany: (state, action) => {
      state.push(action.payload);
    }
  },
  // <-- Тут мы можем обработать авто-экшены
  extraReducers(builder) {  // <-- Дописываем дополнительные функции по модификации фрагмента состояния
    builder
      .addCase(thunkDownloadPerson.pending, (state) => {  // <-- Указываем действие, на которое должна
        state.error = null;                               // реагировать функция
        state.status = 'loading';
      })
      .addCase(thunkDownloadPerson.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.error = null;
        state.personList = state.personList.concat(action.payload);
      })
      .addCase(thunkDownloadPerson.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
  }
});
```



TODO: Дополнительные методы добавления обработчиков: addMatcher, addDefaultCase, разобраться позже.