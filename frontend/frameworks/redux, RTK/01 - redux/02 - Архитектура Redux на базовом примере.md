# Зачем нужен Redux

Суть использования редакса - это вынести общее состояние в отдельный объект и предоставить всем компонентам удобный доступ к этому состоянию. Общее состояние - это то, которое может понадобиться компонентам на разных уровнях вложенности - как самым высоким, так и очень глубоко вложенным. Для того, чтобы не пришлось прокидывать это состояние сквозь десятки компонентов через пропсы, и существует редакс.

При этом у компонентов может быть и свое локальное состояние. Если вдруг оно не нужно никому, кроме самого компонента и пары его дочек, то нет смысла выносить такое состояние в общее хранилище.

# Пример

Здесь общий пример, который в следующем разделе будет разбит на отдельные файлы.

## Пример сплошняком

```react
import { useRef } from 'react';
import { createStore } from 'redux';  // <-- Для создания хранилища
import { useDispatch } from 'react-redux';  // <-- Для регистрации изменения хранилища
import { useSelector } from 'react-redux';  // <-- Для подписки на изменение хранилища
import { Provider } from 'react-redux';   // <-- Для предоставления компонентам доступа к хранилищу
import { combineReducers } from 'redux';  // <-- Для объединения множества редюсеров в один объект

// <-- Фрагмент состояния, будем называть его "Person-фрагмент". Содержит имя и фамилию человека.
// Все фрагменты потом соберутся в единое состояние.
const initialStatePerson = {
  firstname: null,
  lastname: null
}

// <-- "Action-creator" - функция, возвращающая объект, который будет понятен редюсерам.
const actionSetFirstname = (firstname) => {
  return {  // <-- Объект, называемый "Action" (Действие)
    // <-- По типу поля редюсеры поймут, кто из них должен обработать это действие.
    type: 'SET_FIRSTNAME',
    // <-- Из payload редюсер извлечет информацию, которую мы хотим ему передать.
    payload: {
      firstname: firstname
    }
  }
}

// <-- Еще одно действие. Это и предыдущее действия ориентированы на работу с Person-фрагментом состояния.
const actionSetLastname = (lastname) => {
  return {
    type: 'SET_LASTNAME',
    payload: {
      lastname: lastname
    }
  }
}

// <-- Редюсер, обрабатывающий действия над Person-фрагментом состояния: установку имени и фамилии.
// Мы связываем редюсер с конкретным фрагментом состояния, указывая нужный фрагмент в качестве
// значения по умолчанию для первого параметра редюсера.
const reducerPerson = (state = initialStatePerson, action) => {
  // <-- Редюсер проверяет тип действия, чтобы понять, должен ли он это действие обработать или нет.
  switch (action.type) {
    // <-- Перечисляем в case все действия, которые должен обработать этот редюсер.
    case 'SET_FIRSTNAME':
      return {  // <-- Результатом обработки является новый объект фрагмента состояния.
        // <-- Из текущего состояния извлекутся поля firstname и lastname и попадут в новый объект.
        ...state,
        // <-- Из пришедшего действия извлечется поле firstname и перекроет старое firstname, а lastname не затронется.
        ...action.payload
      };
    case 'SET_LASTNAME':
      return {
        ...state,
        ...action.payload
      };
    // <-- Если редюсер не обработал действие, то должен вернуть текущее значение своего фрагмента состояния.
    default:
      return state;
  }
};

// <-- Следующий фрагмент состояния (Experience-фрагмент), для описания опыта работы человека.
const initialStateExperience = {
  lookingForJob: false,  // <-- Ищет ли работу
  workedFor: []  // <-- Где работал
};
// <-- Для упрощения извлечения нужных кусков данных можно написать селекторы.
// Для этого надо заранее решить, под каким именем мы будем класть фрагмент в итоговое состояние.
const selectLookingForJob = s => s.experience.lookingForJob;
const selectWorkedFor = s => s.experience.workedFor;

// <-- Действия для работы с Experience-фрагментом состояния
const actionJobApply = (company) => {
  return {
    type: 'JOB_APPLY',
    payload: {
      current: company
    }
  }
}

const actionSwitchLookingForJob = () => {
  return {
    type: 'LOOKING_FOR_JOB',
    payload: { }
  }
}

// <-- И редюсер для Experience-фрагмента
const reducerExperience = (state = initialStateExperience, action) => {
  switch (action.type) {
    case 'JOB_APPLY':
      return {
        ...state,
        workedFor: [action.payload.current, ...state.workedFor]
      };
    case 'LOOKING_FOR_JOB':
      return {
        ...state,
        lookingForJob: !state.lookingForJob
      }
    default:
      return state;
  }
}

// <-- Объединяем все редюсеры в единый редюсер. Потом, при инициализации хранилища, редакс 
// объединит фрагменты состояний, с которыми работает каждый из этих редюсеров, в единый объект состояния.
const rootReducer = combineReducers({
  person: reducerPerson,  // <-- "Person-фрагмент" попадет в итоговое состояние в поле person
  experience: reducerExperience  // <-- "Experience-фрагмент" попадет в поле experience итогового состояния
});

// <-- Создаем хранилище и указываем корневой редюсер.
const myStore = createStore(rootReducer);

// <-- Корневой компонент нашего приложения
export default function ReduxBasicDemo() {
  return (
    <Provider store={myStore}>  {/* <-- Оборачиваем все приложение в провайдер и указываем хранилище */}
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
    dispatch(actionSetFirstname(rFirstname.current.value));
    dispatch(actionSetLastname(rLastname.current.value));
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
  const applyJob = () => dispatch(actionJobApply(rCompany.current.value));
  const switchLookingForJob = () => dispatch(actionSwitchLookingForJob());

  return (
    <div>
      <input ref={rCompany} placeholder='Куда хотите устроиться'/>
      <button onClick={applyJob}>Устроиться на работу</button>
      <button onClick={switchLookingForJob}>Ищет работу</button>
    </div>
  );
}

// <-- Компонент, который читает состояние.
function Display() {
  // <-- Подписываем компонент на уведомления об изменении хранилища, используя хук useSelector
  // и извлекаем данные, передавая в этот хук колбэк, обращающийся к нужным нам полям.
  const firstname = useSelector(state => state.person.firstname);
  const lastname  = useSelector(state => state.person.lastname);
  const name = firstname && lastname && `${lastname}, ${firstname}`;

  // <-- Или передаем в хук селектор
  const workedFor = useSelector(selectWorkedFor)
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

## Пример фрагментированный

Мб так легче будет воспринимать:

```javascript
import { useRef } from 'react';
import { createStore } from 'redux';  // <-- Для создания хранилища
import { useDispatch } from 'react-redux';  // <-- Для регистрации изменения хранилища
import { useSelector } from 'react-redux';  // <-- Для подписки на изменение хранилища
import { Provider } from 'react-redux';   // <-- Для предоставления компонентам доступа к хранилищу
import { combineReducers } from 'redux';  // <-- Для объединения множества редюсеров в один объект
```

```javascript
// <-- Фрагмент состояния, будем называть его "Person-фрагмент". Содержит имя и фамилию человека.
// Все фрагменты потом соберутся в единое состояние.
const initialStatePerson = {
  firstname: null,
  lastname: null
}
```

```javascript
// <-- "Action-creator" - функция, возвращающая объект, который будет понятен редюсерам.
const actionSetFirstname = (firstname) => {
  return {  // <-- Объект, называемый "Action" (Действие)
    // <-- По типу поля редюсеры поймут, кто из них должен обработать это действие.
    type: 'SET_FIRSTNAME',
    // <-- Из payload редюсер извлечет информацию, которую мы хотим ему передать.
    payload: {
      firstname: firstname
    }
  }
}
```

```javascript
// <-- Еще одно действие. Это и предыдущее действия ориентированы на работу с Person-фрагментом состояния.
const actionSetLastname = (lastname) => {
  return {
    type: 'SET_LASTNAME',
    payload: {
      lastname: lastname
    }
  }
}
```

```javascript
// <-- Редюсер, обрабатывающий действия над Person-фрагментом состояния: установку имени и фамилии.
// Мы связываем редюсер с конкретным фрагментом состояния, указывая нужный фрагмент в качестве
// значения по умолчанию для первого параметра редюсера.
const reducerPerson = (state = initialStatePerson, action) => {
  // <-- Редюсер проверяет тип действия, чтобы понять, должен ли он это действие обработать или нет.
  switch (action.type) {
    // <-- Перечисляем в case все действия, которые должен обработать этот редюсер.
    case 'SET_FIRSTNAME':
      return {  // <-- Результатом обработки является новый объект фрагмента состояния.
        // <-- Из текущего состояния извлекутся поля firstname и lastname и попадут в новый объект.
        ...state,
        // <-- Из пришедшего действия извлечется поле firstname и перекроет старое firstname, а lastname не затронется.
        ...action.payload
      };
    case 'SET_LASTNAME':
      return {
        ...state,
        ...action.payload
      };
    // <-- Если редюсер не обработал действие, то должен вернуть текущее значение своего фрагмента состояния.
    default:
      return state;
  }
};
```

```javascript
// <-- Следующий фрагмент состояния (Experience-фрагмент), для описания опыта работы человека.
const initialStateExperience = {
  lookingForJob: false,  // <-- Ищет ли работу
  workedFor: []  // <-- Где работал
};
// <-- Для упрощения извлечения нужных кусков данных можно написать селекторы.
// Для этого надо заранее решить, под каким именем мы будем класть фрагмент в итоговое состояние.
const selectLookingForJob = s => s.experience.lookingForJob;
const selectWorkedFor = s => s.experience.workedFor;
```

```javascript
// <-- Действия для работы с Experience-фрагментом состояния
const actionJobApply = (company) => {
  return {
    type: 'JOB_APPLY',
    payload: {
      current: company
    }
  }
}
```

```javascript
const actionSwitchLookingForJob = () => {
  return {
    type: 'LOOKING_FOR_JOB',
    payload: { }
  }
}
```

```javascript
// <-- И редюсер для Experience-фрагмента
const reducerExperience = (state = initialStateExperience, action) => {
  switch (action.type) {
    case 'JOB_APPLY':
      return {
        ...state,
        workedFor: [action.payload.current, ...state.workedFor]
      };
    case 'LOOKING_FOR_JOB':
      return {
        ...state,
        lookingForJob: !state.lookingForJob
      }
    default:
      return state;
  }
}
```

```javascript
// <-- Объединяем все редюсеры в единый редюсер. Потом, при инициализации хранилища, редакс 
// объединит фрагменты состояний, с которыми работает каждый из этих редюсеров, в единый объект состояния.
const rootReducer = combineReducers({
  person: reducerPerson,  // <-- "Person-фрагмент" попадет в итоговое состояние в поле person
  experience: reducerExperience  // <-- "Experience-фрагмент" попадет в поле experience итогового состояния
});
```

```javascript
// <-- Создаем хранилище и указываем корневой редюсер.
const myStore = createStore(rootReducer);
```

```react
// <-- Корневой компонент нашего приложения
export default function ReduxBasicDemo() {
  return (
    <Provider store={myStore}>  {/* <-- Оборачиваем все приложение в провайдер и указываем хранилище */}
      <Person />
      <Experience />
      <Display />
    </Provider>
  );
}
```

```react
// <-- Компонент, который изменяет состояние.
function Person() {
  const rFirstname = useRef();
  const rLastname = useRef();
  // <-- Получаем функцию для регистрации изменения хранилища.
  const dispatch = useDispatch();

  // <-- Отправляем в хранилище запросы на изменение, путем передачи функции-диспетчеру объекта действия.
  const applyName = () => {
    dispatch(actionSetFirstname(rFirstname.current.value));
    dispatch(actionSetLastname(rLastname.current.value));
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

```react
// <-- Этот компонент тоже изменяет состояние.
function Experience() {
  const rCompany = useRef();
  // <-- Получаем функцию для регистрации изменения хранилища.
  const dispatch = useDispatch();

  // <-- Отправляем в хранилище запросы на изменение.
  const applyJob = () => dispatch(actionJobApply(rCompany.current.value));
  const switchLookingForJob = () => dispatch(actionSwitchLookingForJob());

  return (
    <div>
      <input ref={rCompany} placeholder='Куда хотите устроиться'/>
      <button onClick={applyJob}>Устроиться на работу</button>
      <button onClick={switchLookingForJob}>Ищет работу</button>
    </div>
  );
}
```

```react
// <-- Компонент, который читает состояние.
function Display() {
  // <-- Подписываем компонент на уведомления об изменении хранилища, используя хук useSelector
  // и извлекаем данные, передавая в этот хук колбэк, обращающийся к нужным нам полям.
  const firstname = useSelector(state => state.person.firstname);
  const lastname  = useSelector(state => state.person.lastname);
  const name = firstname && lastname && `${lastname}, ${firstname}`;

  // <-- Или передаем в хук селектор
  const workedFor = useSelector(selectWorkedFor)
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



# Общая схема работы редакса

## Схема работы

Буду пользоваться в объяснении объектами из предыдущего примера. Тут я попробовал описать процесс работы с редаксом с максимально логичной отправной точки, как мне на данный момент кажется.

* Логически, общее состояние формируется из `фрагментов`. Например, Person-фрагмент и Experience-фрагмент из примера выше (объекты в переменных initialStatePerson и initialStateExperience). Несколько фрагментов объединяются вместе и получается то, что мы называем состоянием. Состояние хранится в `хранилище`.

* Для обработки каждого такого фрагмента мы придумываем `действия`. С каждым действием связаны какие-то данные. Например, чтобы задать фамилию человека, надо эту фамилию куда-то передать. А также у каждого действия есть *тип* (обычная строка), чтобы отличать одно действие от другого. Получается, действие "Задать человеку фамилию Петров" можно выразить например таким объектом:

  ```
  {
    type: 'SET_LASTNAME',
    payload: {
      lastname: 'Петров'
    }
  }
  ```

* Как создаются действия?

  * Для создания действий мы пишем action-creator'ы. В базовом случае это обычные функции, которые возвращают объект действия. Также в редаксе есть собственные функции для создания действий. Кроме того, action creator может содержать дополнительную логику, например, отправку запроса на сервер для получения \ загрузки данных. Такие creator'ы с сайд эффектами возвращают не объект действия, а оформленную особым образом функцию и являются отдельной темой, обычно связанной с асинхронностью, например redux-thunk.
  * Нужно различать концепцию action и action creator, это упростит понимание асинхронного редакса redux-thunk.
  
* Действие мы передаем редаксу, а он уже отправляет это действие в *редюсеры* (о них дальше). Как именно мы можем передать действие редаксу?

  * Мы импортируем хук `useDispatch` из пакета `react-redux`. По пакету видно, что этот хук предназначен для интеграции редакса с реактом.
  * Вызов этого хука возвращает нам специальную функцию-`диспетчер`.
  * Мы формируем объект действия и передаем его диспетчеру. Вот так действие и попадает в редакс.

* Непосредственно логику обработки действий мы описываем в `редюсерах`.

  * Редюсер - это обычная функция с двумя параметрами. Первый параметр - это фрагмент состояния, за обработку которого будет отвечать этот редюсер, а второй параметр - это пришедшее в редюсер действие.

    * Чтобы связать редюсер с конкретным фрагментом состояниями, мы указываем этот фрагмент в качестве значения по умолчанию для первого параметра (state) редюсера.
  * Каким образом редюсеры обрабатывают действия?

    * Внутри редюсера мы проверяем тип пришедшего действия. Поскольку обычно редюсеров несколько, то каждый отвечает за обработку только некоторого набора действий. Т.о. мы решаем, что например редюсер Person будет отвечать за обработку действий по установке имени и фамилии, т.е. SET_FIRSTNAME и SET_LASTNAME. Стало быть, если в этот редюсер придет действие JOB_APPLY, то он не будет его обрабатывать.

    * В любом случае, результатом работы редюсера является возврат фрагмента состояния.

      * Если редюсер обрабатывает действие, то он возвращает новый объект фрагмента состояния. Поскольку изменять (мутировать) состояние нельзя, то редюсер формирует новый объект на основе текущего состояния и заменяет в этом новом объекте ту часть данных, которую необходимо обновить, пришедшими в действии данными.

      * Если редюсер не обрабатывает действие, то он возвращает переданный ему фрагмент состояния как есть, без изменений.
  * Кто вызывает редюсеры и передает им состояние и действие?

    * Это делает редакс. Мы передаем ему редюсеры при создании хранилища. Об этом, и о том, как редакс собирает фрагменты в единое состояние при первом запуске, чуть позже. Пока же надо понять, что когда мы передаем редаксу действие, то он начинает передавать это действие всем известным ему редюсерам по очереди. Кроме действия, редакс передает редюсеру и тот фрагмент состояния, за обработку которого отвечает этот редюсер (именно фрагмент состояния, а не состояние целиком!). В ответ на это каждый из редюсеров возвращает редаксу обновленный или нетронутый фрагмент состояния, из которых редакс собирает итоговое состояние.
  * Как мы передаем редюсеры редаксу?
    * Мы импортируем функцию `combineReducers` из пакета redux и передаем ей все редюсеры. Она формирует единый объект с редюсерами (иногда его называют "корневой редюсер").
    * Далее мы создаем `хранилище`. Для этого мы импортируем функцию `createStore` из пакета redux и передаем ей корневой редюсер.
      * Редакс при инициализации хранилища вызывает все полученные редюсеры с фейковым действием. Поскольку ни один редюсер это действие обработать не может, то в качестве фрагмента состояния каждый из них возвращает объект, который мы указали по умолчанию для параметра state редюсера. Благодаря этому редакс понимает, какой редюсер за какой фрагмент отвечает и впоследствии сможет передавать редюсеру именно нужный фрагмент на обработку. В итоге у редакса оказываются все фрагменты, из которых он собирает начальное общее состояние. 
  * На этом настройка хранилища завершена и начинается работа с редаксом. Как регистрировать действия и изменять т.о. состояние мы уже рассмотрели. Теперь рассмотрим как состояние читать.
    * Когда изменяется состояние, то реакт перерисовывает компоненты, которые подписаны на эти изменения.
      * Подписка компонента на изменение состояния осуществляется с помощью хука `useSelector(s => s.somePartOfState`) из пакета react-redux. Сам факт использования этого хука в компоненте подписывает компонент на изменение состояния. Причем обычно не на изменение состояния целиком, а какой-то его части - той, что возвращается из переданного колбэка. В данном случае - somePartOfState. P.S. Если в селекторе вернуть состояние целиком, в консоли даже выведется предупреждение, что скорее всего вы ошиблись, подписываться на изменение целого состояния - это неправильно.

Схема работы графически:

![redux-workflow.drawio](img/redux-workflow.drawio.svg)

## Концепции

Резюмируем. В редаксе можно выделить следующие концепции:

* Действие (экшен, action) - это объект, имеющий тип ("имя") и полезную нагрузку в виде каких-то данных.
* Action creator (экшен криейтор) - это функция, основная задача которой - создать экшен и вернуть его нам.
* Редюсер (reducer) - это функция, которая принимает состояние и действие. На основе типа действия она принимает решение, как надо модифицировать текущее состояние. Для модификации она использует данные из действия и возвращает новое состояние.

# Структуризация приложения

Есть разные способы организовать структуру redux-приложения. Все файлы, относящиеся к редаксу, будем размещать в директории `src/store`. Пока что для себя я решил, что удобнее будет складывать редюсер и действия, с ними связанные, в одном файле. Получается так:

```
src/
  components/
    Display.js
    Experience.js
    Person.js
  store/
    reducers/
      experience.js
      person.js
      rootReducer.js
    store.js
index.js
```

P.S. Некоторые переменные я переименовал. Поскольку в предыдущем примере все было в одном файле, я делал префиксы action, reducer и т.д. Тут я их убрал.

## Редюсеры

### experience

```javascript
// Файл: src/store/reducers/experience.js
const initial = {
  lookingForJob: false,
  workedFor: []
};

export const selectLookingForJob = s => s.experience.lookingForJob;
export const selectWorkedFor = s => s.experience.workedFor;

export function jobApply(company) {
  return {
    type: 'JOB_APPLY',
    payload: {
      current: company
    }
  }
}

export function switchLookingForJob() {
  return {
    type: 'LOOKING_FOR_JOB',
    payload: { }
  }
}

export function experience(state = initial, action) {
  switch (action.type) {
    case 'JOB_APPLY':
      return {
        ...state,
        workedFor: [action.payload.current, ...state.workedFor]
      };
    case 'LOOKING_FOR_JOB':
      return {
        ...state,
        lookingForJob: !state.lookingForJob
      }
    default:
      return state;
  }
}
```

Чтобы связать фрагмент состояния с редюсером, мы первому параметру редюсера (state) присваиваем фрагмент в качестве значения по умолчанию.

### person

```javascript
// Файл src/store/reducers/person.js
const initial = {
  firstname: null,
  lastname: null
}

export function setFirstname(firstname) {
  return {
    type: 'SET_FIRSTNAME',
    payload: {
      firstname: firstname
    }
  }
}

export function setLastname(lastname) {
  return {
    type: 'SET_LASTNAME',
    payload: {
      lastname: lastname
    }
  }
}

export function person(state = initial, action) {
  switch (action.type) {
    case 'SET_FIRSTNAME':
      return {
        ...state,
        ...action.payload
      };
    case 'SET_LASTNAME':
      return {
        ...state,
        ...action.payload
      };
    default:
      return state;
  }
};
```

### Собираем редюсеры вместе, rootReducer

```react
// Файл src/store/rootReducer.js
import { combineReducers } from 'redux';

import { person } from './person';
import { experience } from './experience';

export const rootReducer = combineReducers({
  person: person,
  experience: experience
});
```

Не забываем, что фрагменты попадут в итоговое состояние в те поля, которые мы укажем тут.

## Создаем хранилище

```react
// Файл src/store/store.js
import { createStore } from 'redux';

import { rootReducer } from './reducers/rootReducer';

export const myStore = createStore(rootReducer);
```

## Корневой компонент

В корневом компоненте нам понадобятся компоненты, которые мы хотим снабдить доступом к хранилищу, само хранилище и провайдер хранилища:

```react
// Файл src/ReduxBasicDemo.js
import { Provider } from 'react-redux';

import { myStore } from './store/store';
import Person from './components/Person';
import Experience from './components/Experience';
import Display from './components/Display';

export default function ReduxBasicDemo() {
  return (
    <Provider store={myStore}>
      <Person />
      <Experience />
      <Display />
    </Provider>
  );
}
```

## Компоненты, изменяющий состояние

### Experience

```react
// Файл src/components/Experience.js
import { useRef } from 'react';
import { useDispatch } from 'react-redux';

import { jobApply, switchLookingForJob } from '../store/reducers/experience';

export default function Experience() {
  const rCompany = useRef();
  const dispatch = useDispatch();

  const apply = () => dispatch(jobApply(rCompany.current.value));
  const switchLooking = () => dispatch(switchLookingForJob());

  return (
    <div>
      <input ref={rCompany} placeholder='Куда хотите устроиться'/>
      <button onClick={apply}>Устроиться на работу</button>
      <button onClick={switchLooking}>Ищет работу</button>
    </div>
  );
}
```

С помощью хука `useDispatch` получаем функцию-диспетчер. Затем формируем объект действия и передаем его диспетчеру. Дальше реакт прокидывает это действие сквозь все редюсеры и передает каждому редюсеру его фрагмент. Если редюсер обрабатывает действие, то возвращает новую версию фрагмента, а если не обрабатывает - то возвращает фрагмент как есть. Из полученных фрагментов реакт собирает обновленное общее состояние.

### Person

```react
// Файл src/components/Person.js
import { useRef } from 'react';
import { useDispatch } from 'react-redux';

import { setFirstname, setLastname } from '../store/reducers/person';

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

## Компонент, читающий состояние

### Display

```react
// Файл /src/components/Display.js
import { useSelector } from 'react-redux';
import { selectLookingForJob } from '../store/reducers/experience';
import { selectWorkedFor } from '../store/reducers/experience';

export default function Display() {
  const firstname = useSelector(state => state.person.firstname);
  const lastname  = useSelector(state => state.person.lastname);
  const name = firstname && lastname && `${lastname}, ${firstname}`;

  const workedFor = useSelector(selectWorkedFor)
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

Подписываться на изменения всего состояния не рекомендуется. Если написать `state => state`, то в консоли браузера даже выведется сообщение:

> Selectors that return the entire state are almost certainly a mistake, as they will cause a rerender whenever *anything* in state changes.

Поэтому выделяем из состояния только нужный нам фрагмент и пользуемся им в компоненте. А как только эта часть изменится, то реакт об этом узнает и перерисует компонент.