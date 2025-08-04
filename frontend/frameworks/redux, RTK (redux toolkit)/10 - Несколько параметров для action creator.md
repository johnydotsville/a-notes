RTK создает action creator за нас и параметр у него по умолчанию один - целиковый payload. Однако мы можем кастомизировать action creator, чтобы он принимал сколько нам надо параметров, и сами собрать объект payload:

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
  'John',
  'Doe'
));
```

