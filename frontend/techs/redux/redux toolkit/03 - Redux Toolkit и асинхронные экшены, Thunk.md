# Асинхронность в Redux Toolkit

## createAsyncThunk

### Синтаксис

Функция `createSlice` сама по себе не умеет работать с асинхронными действиями (танками). Поэтому танк нужно написать отдельно. Обычно его пишут в одном файле со слайсом.

Танк создается с помощью функции `createAsyncThunk`. Она принимает:

* Имя действия (можно поставить произвольное, но лучше составить из комбинации имени слайса и выполняемой функции для наглядности).
* Код, который нужно выполнить. Обычно это тот самый асинхронный код с сайд-эффектами, вроде загрузки \ отправки данных.

createAsyncThunk возвращает нам экшен криейтор, который мы диспатчим как обычно:

```javascript
const thunkDownloadPerson = createAsyncThunk(  // <-- Создаем танк,
  'person/downloadPerson',  // <-- указав имя действия
  async ({param1, paramN}, {dispatch, getState}) => {  // <-- и функцию для выполнения
    // Здесь выполняем загрузку \ отправку данных или прочую асинхронщину
  }
);

dispatch(thunkDownloadPerson({param1: 'hello', param2: 'world'}));  // <-- Диспатчим действие-танк
```

### Параметры для колбэка

В танк-экшен мы можем передавать параметры. При передаче мы должны оформить их в единый объект:

```javascript
dispatch(thunkDownloadPerson(
  {param1: 'hello', param2: 'world'}  // <-- Передаем в танк-экшен параметры
));
```

Этот объект тулкит передаст в колбэк. Мы можем его деструктурировать и получить переданные данные:

```javascript
const thunkDownloadPerson = createAsyncThunk(
  'person/downloadPerson',
  async (
    {param1, paramN}, // <-- Дестрачим первый параметр-объект и получаем наши параметры
    {dispatch, getState}  // <-- Дефолтные параметры, получаемые от тулкита
  ) => {
    console.log(param1);  // <-- Пользуемся параметрами
  }
);
```

Кроме наших параметров, тулкит еще передает в колбэк стандартные параметры вроде функций `dispatch` и `getState`, чтобы мы могли внутри своего экшена получать доступ к хранилищу и диспачить другие действия, если надо. Это не все стандартные параметры, есть еще и другие, о них можно почитать тут:  https://redux-toolkit.js.org/api/createAsyncThunk и https://redux.js.org/tutorials/essentials/part-6-performance-normalization#thunk-arguments

## Автоэкшены

Обычно при выполнении асинхронной работы к состоянию добавляют дополнительные поля вроде `status` и `error`. Например, у нас есть состояние с массивом людей, который мы хотим загрузить с сервера. Тогда мы бы могли при отправке запроса поставить `status = loading`, при успешной загрузке success, при ошибке - fail, а сам текст ошибки поместить в поле error. Поскольку это типичный паттерн, то createAsyncThunk это реализует из коробки. Он автоматически генерирует экшены для этих ситуаций и автоматически их диспатчит. Экшены можно получить так:

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

// <-- Создаем танк из функции загрузки людей. Имя произвольное, но лучше по имени слайса + функции.
const thunkDownloadPerson = createAsyncThunk(
  'person/downloadPerson', 
  async ({url, foobar}, {dispatch, getState}) => {  // <-- url получим из параметра
    const transform = (person) => person.map(p => ({
      name:  p.name,
      email: p.email
    }));
    console.log(foobar);
    
    const data = await fetch(url)
      .then(response => { 
        if (response.status != 200)
          throw new Error('Не удалось загрузить данные.');
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
);

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
  // <-- Более подробное объяснение про обработку автоэкшенов - в след разделе
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