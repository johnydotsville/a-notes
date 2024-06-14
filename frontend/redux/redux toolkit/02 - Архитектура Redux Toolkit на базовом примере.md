# Redux Toolkit

Основные концепции такие же, как в редаксе - экшены, экшен-криейторы, редюсеры. Единственно, в тулките есть средства, немного упрощающие оформление всех этих вещей. Особенности тулкита:

* Слайсы. Это дополнительная концепция. Слайс представляет собой объект, в котором мы описываем начальное значение фрагмента состояния и функции, которые над этим фрагментом можно выполнять. Слайс на основании этого автоматически создает экшен-криейторы и редюсеры, и с помощью его свойств мы можем их извлечь. Т.о., немного упрощается оформление этих вещей, нам не приходится придумывать имена действиям и дублировать их в редюсере и экшен-криейторе, т.к. слайс формирует имя автоматически.
* По умолчанию в тулките подключены:
  * redux-thunk
  * redux devtools
  * Библиотека Immer, позволяет писать редюсеры в мутирующем стиле, что гораздо удобнее. Под капотом иммер переделает эти "мутации" в создание нового объекта. Важно! Иммер автоматически работает только внутри createSlice. Но если функция большая, можно оформить ее отдельной функцией и указать в createSlice, тогда иммер все равно будет работать, т.к. ""внешняя" функция все равно как бы находится внутри createSlice.
* Хранилище создается функцией `configureStore`, а не `createStore`. Отличие в том, что новая функция автоматически встраивает мидлвары танк и девтулс. В остальном можно конфигурировать как хочешь.

# Пример

## Базовый

Схема работы такая же как в редаксе, поэтому не буду дублировать. Если забыл - читай конспект по редаксу. Здесь допишу только особенности синтаксиса тулкита.

```react
import { Provider } from 'react-redux';
import { useSelector } from "react-redux";
import { useRef } from 'react';
import { useDispatch } from 'react-redux';
import { configureStore } from "@reduxjs/toolkit";
import { createSlice } from '@reduxjs/toolkit';

// <-- Слайс. Это объект-"контейнер", позволяющий в одном месте описать фрагмент состояния и те экшены,
// которые над ним можно выполнить. Слайс способен автоматически сгенерировать для нас редюсер и экшены.
const slicePerson = createSlice({
  name: 'person',  // <-- Имя "домена". Экшены получат имена в формате 'domain/action', 'person/setFirstname'
  initialState: {  // <-- Объект с начальным значением фрагмента состояния, связанного с редюсером
    firstname: null,
    lastname: null
  },
  reducers: {  // <-- Функции по модификации фрагмента состояния. Из них слайс соберет редюсер.
    setFirstname: (state, action) => {  // <-- Экшен будет называться "person/setFirstname"
      // <-- За счет либы Immer можно "изменять" состояние, а иммер сам все склонирует как надо.
      state.firstname = action.payload;
    },
    setLastname: (state, action) => {  // <-- Этот экшен криейтор будет вызываться так: setLastname(знач);
      state.lastname = action.payload;  // знач попадет в payload, т.е. action.payload == знач
    }
  }
});

// <-- Селекторы. Это функции, достающие из состояния определенный кусок данных. Удобно объявить их рядом с
// описанием состояния, а потом передавать в хук useSelector. Т.о., можно извлекать в нужном месте нужные
// данные и при этом не обязательно помнить структуру состояния.
const selectFirstname = s => s.person.firstname;
const selectLastname = s => s.person.lastname;

// <-- Этот вызов генерирует экшен-криейторы на основе функций, описанных в слайсе.
const { setFirstname, setLastname } = slicePerson.actions;
// <-- Этот вызов формирует редюсер на основе функций, описанных в слайсе.
const reducerPerson = slicePerson.reducer;

const sliceExperience = createSlice({
  name: 'experience',
  initialState: {
    lookingForJob: false,
    workedFor: [] 
  },
  reducers: {
    jobApply: (state, action) => {
      state.workedFor.unshift(action.payload);
    },
    switchLookingForJob : state => {
      state.lookingForJob = !state.lookingForJob;
    }
  }
});

const selectLookingForJob = s => s.experience.lookingForJob;
const selectWorkedFor = s => s.experience.workedFor;

const { jobApply, switchLookingForJob } = sliceExperience.actions;
const reducerExperience = sliceExperience.reducer;

// <-- Одна из вещей, которую можно сделать при конфигурации хранилища - объединить несколько редюсеров
// и т.о. сформировать общее состояние хранилища.
const myStore = configureStore({
  reducer: { 
    person: reducerPerson,  // <-- Фрагмент состояния, связанный с редюсером reducerPerson, попадет в итоговое состояние в поле person
    experience: reducerExperience
  }
});

export default function ReduxToolkitDemo() {
  return (
    <Provider store={myStore}>
      <Person />
      <Experience />
      <Display />
    </Provider>
  );
}

// <-- Компонент, который изменяет состояние.
function Person() {
  const rFirstname = useRef();
  const rLastname = useRef();
  // <-- Получаем функцию для регистрации изменения хранилища.
  const dispatch = useDispatch();

  // <-- Отправляем в хранилище запросы на изменение, путем передачи функции-диспетчеру объекта действия.
  const applyName = () => {
    dispatch(setFirstname(rFirstname.current.value));
    dispatch(setLastname(rLastname.current.value));
  }

  return (
    <div>
      <input ref={rFirstname} placeholder="Введите имя"/>
      <input ref={rLastname}  placeholder="Введите фамилию"/>
      <button onClick={applyName}>Задать</button>
    </div>
  );
}

// <-- Этот компонент тоже изменяет состояние.
function Experience() {
  const rCompany = useRef();
  // <-- Получаем функцию для регистрации изменения хранилища.
  const dispatch = useDispatch();

  // <-- Отправляем в хранилище запросы на изменение.
  const applyJob = () => dispatch(jobApply(rCompany.current.value));
  const lookingForJobSwitch = () => dispatch(switchLookingForJob());

  return (
    <div>
      <input ref={rCompany} placeholder='Куда хотите устроиться'/>
      <button onClick={applyJob}>Устроиться на работу</button>
      <button onClick={lookingForJobSwitch}>Ищет работу</button>
    </div>
  );
}

// <-- Компонент, который читает состояние.
function Display() {
  // <-- Подписываем компонент на уведомления об изменении хранилища, используя хук useSelector
  // и извлекаем данные, передавая в этот хук заготовленные селекторы, обращающийся к нужным нам полям.
  const firstname = useSelector(selectFirstname);
  const lastname  = useSelector(selectLastname);
  const name = firstname && lastname && `${lastname}, ${firstname}`;

  const workedFor = useSelector(selectWorkedFor);
  const isLookingForJob = useSelector(selectLookingForJob);

  return (
    <div>
      <div>{name ?? "Имя не известно."} {isLookingForJob && "(Ищет работу)"}</div>
      {(workedFor.length > 0) ? (
        <div>Работал в этих компаниях: { workedFor.map(c => <div key={c}>{c}</div>) }</div>
      ) : (
        <div>Прошлые места работы не известны.</div>
      )}
    </div>
  );
}
```

## Настройка экшена

Когда мы пишем экшен-криейторы (АС) самостоятельно, то можем задать для функции сколько угодно параметров, и собрать из них payload, например:

```javascript
function setName(firstname, lastname) {
  return {
    type: 'SET_NAME',
    payload: {
      firstname,
      lastname
    }
  }
}
```

Но тулкит создает АС за нас и параметр у АС по умолчанию один - целиковый payload. Однако мы можем кастомизировать АС, чтобы он принимал сколько нам надо параметров, и сами собрать объект payload:

```javascript
const slicePerson = createSlice({
  name: 'person',
  initialState: {
    firstname: null,
    lastname: null
  },
  reducers: {
    setFirstname: (state, action) => {
      state.firstname = action.payload;
    },
    setLastname: (state, action) => {
      state.lastname = action.payload;
    },
    setName: {  // <-- Экшен для одновременной установки и имени, и фамилии
      reducer(state, action) {  // <-- Редюсер
        state.firstname = action.payload.firstname;
        state.lastname = action.payload.lastname;
      },
      prepare(firstname, lastname) {  // <-- Функция по подготовке нагрузки для экшена
        return {  // <-- Должны вернуть объект с полем payload
          payload: {  // <-- Формируем payload как нам надо
            // <-- Тут еще можно было бы, например, сгенерировать id
            firstname,
            lastname
          }
        }
      }
    }
  }
});
```

Такая кастомизация полезна еще тем, что в ней можно делать сайд-эффекты. Например, сгенерировать уникальный id. Делать такое в редюсере концептуально запрещено, а вот при подготовке действия - самое оно.

Теперь можно диспатчить экшен вот так:

```javascript
dispatch(setName(  // <-- Теперь можно передавать в АС два параметра
  rFirstname.current.value,
  rLastname.current.value
));
```

# Структуризация программы

Просто для примера, это не является единственно правильным способом группировки.

## Структура папок

```
src
  components
    Display.js
    Experience.js
    Person.js
  store
    slices
      experience.js
      person.js
    store.js
index.js
```

## Слайсы

### experience

```javascript
// Файл 'src/store/slices/experience.js'
import { createSlice } from "@reduxjs/toolkit";

const experience = createSlice({
  name: 'experience',
  initialState: {
    lookingForJob: false,
    workedFor: [] 
  },
  reducers: {
    jobApply: (state, action) => {
      state.workedFor.unshift(action.payload);
    },
    switchLookingForJob : state => {
      state.lookingForJob = !state.lookingForJob;
    }
  }
});

export const selectLookingForJob = s => s.experience.lookingForJob;
export const selectWorkedFor = s => s.experience.workedFor;

export const { jobApply, switchLookingForJob } = experience.actions;
export const reducerExperience = experience.reducer;
```

### person

```javascript
// Файл 'src/store/slices/person.js'
import { createSlice } from "@reduxjs/toolkit";

const person = createSlice({
  name: 'person',
  initialState: {
    firstname: null,
    lastname: null
  },
  reducers: {
    setFirstname: (state, action) => {
      state.firstname = action.payload;
    },
    setLastname: (state, action) => {
      state.lastname = action.payload;
    }
  }
});

export const selectFirstname = s => s.person.firstname;
export const selectLastname = s => s.person.lastname;

export const { setFirstname, setLastname } = person.actions;
export const reducerPerson = person.reducer;
```

## Хранилище

```javascript
// Файл 'src/store/store.js'
import { configureStore } from "@reduxjs/toolkit";
import { reducerPerson } from "./slices/person";
import { reducerExperience } from "./slices/experience";

export const myStore = configureStore({
  reducer: { 
    person: reducerPerson,
    experience: reducerExperience
  }
});
```

## Компоненты

### Person

```react
// Файл 'src/components/Person.js'
import { useRef } from 'react';
import { useDispatch } from 'react-redux';
import { setFirstname, setLastname } from '../store/slices/person';

export default function Person() {
  const rFirstname = useRef();
  const rLastname = useRef();
  const dispatch = useDispatch();

  const applyName = () => {
    dispatch(setFirstname(rFirstname.current.value));
    dispatch(setLastname(rLastname.current.value));
  }

  return (
    <div>
      <input ref={rFirstname} placeholder="Введите имя"/>
      <input ref={rLastname}  placeholder="Введите фамилию"/>
      <button onClick={applyName}>Задать</button>
    </div>
  );
}
```

### Experience

```react
// Файл 'src/components/Experience.js'
import { useRef } from 'react';
import { useDispatch } from 'react-redux';
import { jobApply, switchLookingForJob } from '../store/slices/experience';

export default function Experience() {
  const rCompany = useRef();
  const dispatch = useDispatch();

  const applyJob = () => dispatch(jobApply(rCompany.current.value));
  const lookingForJobSwitch = () => dispatch(switchLookingForJob());

  return (
    <div>
      <input ref={rCompany} placeholder='Куда хотите устроиться'/>
      <button onClick={applyJob}>Устроиться на работу</button>
      <button onClick={lookingForJobSwitch}>Ищет работу</button>
    </div>
  );
}
```

### Display

```react
// Файл 'src/components/Display.js'
import { useSelector } from "react-redux";
import { selectFirstname, selectLastname } from "../store/slices/person";
import { selectWorkedFor, selectLookingForJob } from "../store/slices/experience";

export default function Display() {
  const firstname = useSelector(selectFirstname);
  const lastname  = useSelector(selectLastname);
  const name = firstname && lastname && `${lastname}, ${firstname}`;

  const workedFor = useSelector(selectWorkedFor);
  const isLookingForJob = useSelector(selectLookingForJob);

  return (
    <div>
      <div>{name ?? "Имя не известно."} {isLookingForJob && "(Ищет работу)"}</div>
      {(workedFor.length > 0) ? (
        <div>Работал в этих компаниях: { workedFor.map(c => <div key={c}>{c}</div>) }</div>
      ) : (
        <div>Прошлые места работы не известны.</div>
      )}
    </div>
  );
}
```

### Корневой компонент

```react
// Файл src/ReduxToolkitDemo.js
import { Provider } from 'react-redux';

import { myStore } from './store/store';
import Person from './components/Person';
import Experience from './components/Experience';
import Display from './components/Display';

export default function ReduxToolkitDemo() {
  return (
    <Provider store={myStore}>
      <Person />
      <Experience />
      <Display />
    </Provider>
  );
}
```

