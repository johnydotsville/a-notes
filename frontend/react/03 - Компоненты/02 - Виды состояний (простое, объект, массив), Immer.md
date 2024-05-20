# Виды состояний

## Состояние-примитив

Было рассмотрено при объяснении хука useState.

## Состояние-объект

Когда состояние представлено объектом, мы придерживаемся правила иммутабельности - сам объект не изменяем, а предоставляем сеттеру новый объект:

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

Когда объект большой, в нем много полей и если они к тому же вложены друг в друга, то переписывать целиком такой объект не удобно. В этих случаях нам поможет оператор расширения `...` (см. конспект по Javascript, если не понятно как он работает и как именно помогает в данном случае):

```react
const [info, setInfo] = useState({  // <-- Объект со множеством полей
  firstName: 'Alan',
  lastName: 'Wake',
  profession: 'writer'
});

const changeReality = () => {
  setInfo({
    ...info,
    profession: 'crazy man with a gun'
  });
}
```

```react
const [info, setInfo] = useState({  // <-- Объект со вложенными полями
  name: {
    firstName: 'Alan',
    lastName: 'Walker'
  },
  profession: 'writer'
});

const changeReality = () => {
  setInfo({
    ...info,
    name: {
      ...info.name,
      lastName: 'Wake'
    }
  });
}
```

В целом же стоит избегать глубоких вложений в состоянии и проектировать его так, чтобы оно было как можно более плоским.

## Состояние-массив

Когда состояние является массивом, мы должны сохранять иммутабельность: любая операция изменения состава массива (удаление, добавление) выполняется через создание нового массива, копирования в него элементов из старого, и установки нового массива в качестве состояния.

### Добавление в конец \ начало массива

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'}
  ]);
  const [wordId, setWordId] = useState(words.length);
  const [word, setWord] = useState('');

  const handleAddNameClick = () => {
    setWords([...words, {id: wordId, word: word}]);  // <-- Добавление в конец массива
    setWordId(wordId + 1);
  }

  return (
    <div>
      <input onChange={e => setWord(e.target.value)} />
      <button onClick={handleAddNameClick}>Добавить</button>
      <ul>
        {words.map(w => (<li key={w.id}>{w.word}</li>))}
      </ul>
    </div>
  )
}
```

Добавление **в начало** можно сделать так: `setWords([{id: wordId, word: word}, ...words])`

### Удаление из массива

Делается через фильтрацию методом `filter` массива:

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'}
  ]);
  const [wordId, setWordId] = useState(words.length);
  const [word, setWord] = useState('');

  const handleAddNameClick = () => {
    setWords([...words, {id: wordId, word: word}]);
    setWordId(wordId + 1);
  }

  const handleDeleteWordClick = (id) => {
    setWords(words.filter(w => w.id != id));  // <-- Возвращаем новый массив без удаляемого элемента
  }

  return (
    <div>
      <input onChange={e => setWord(e.target.value)} />
      <button onClick={handleAddNameClick}>Добавить</button>
      <ul>
        {words.map(w => (
          <li key={w.id}>
            {w.word}
            <button onClick={() => handleDeleteWordClick(w.id)}>Удалить</button>
          </li>))
        }
      </ul>
    </div>
  )
}
```

### Изменение всех элементов

Делается методом `.map()` массива:

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'},
    {id: 3, word: 'Весна'},
    {id: 4, word: 'Победа'}
  ]);

  const handleTransformClick = () => {
    setWords(words.map((w, i) => i > 1 ? {...w, word: w.word.toUpperCase()} : w));  // <--
  }

  return (
    <>
      <ul>
        {words.map(w => <li key={w.id}>{w.word}</li>)}
      </ul>
      <button onClick={handleTransformClick}>Преобразовать</button>
    </>
  )
}
```

### Замена конкретного элемента

Тоже делается методом `.map()` массива. Просто пишем условие так, чтобы заменился только один элемент:

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'},
    {id: 3, word: 'Весна'},
    {id: 4, word: 'Победа'}
  ]);
  const wordToSwap = 'Весна';
  const swapTo = 'Лето';

  const handleTransformClick = () => {
    setWords(words.map(w => !w.word.localeCompare(wordToSwap) ?  // <--
      {...w, word: swapTo} : w));
  }

  return (
    <>
      <ul>
        {words.map(w => <li key={w.id}>{w.word}</li>)}
      </ul>
      <button onClick={handleTransformClick}>Преобразовать</button>
    </>
  )
}
```

### Вставка элемента в середину массива

Делается комбинацией метода `.slice()` массива и оператора расширения. Методом slice возвращаем новый массив от начала до места вставки, расширяем полученный массив, потом ставим новый элемент, и опять методом slice возвращаем новый массив от места вставки до конца, расширяем его и все это вместе заворачиваем в новый массив:

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'},
    {id: 3, word: 'Весна'},
    {id: 4, word: 'Победа'}
  ]);
  const [wordId, setWordId] = useState(words.length);
  const [word, setWord] = useState('');
  const middle = words.length / 2;  // <-- Вставлять будем в середину массива

  const handleAddNameClick = () => {
    setWords([
      ...words.slice(0, middle),  // <-- Выбираем подмассив до места вставки
      {id: wordId, word: word},   // <-- Вставляем новый элемент
      ...words.slice(middle)      // <-- Выбираем подмассив после места вставки
    ]);  // <-- И из всех этих кусочков собираем новый массив
    setWordId(wordId + 1);
  }

  return (
    <div>
      <input onChange={e => setWord(e.target.value)} />
      <button onClick={handleAddNameClick}>Добавить</button>
      <ul>
        {words.map(w => (<li key={w.id}>{w.word}</li>))}
      </ul>
    </div>
  )
}
```

### Сортировка, инверсия массива

Методы `.sort()` и `.reverse()` мутируют массив. Поэтому сначала делаем копию существующего массива, затем эту копию сортируем \ инвертируем и устанавливаем в качестве нового состояния. Стоит помнить, что копия *поверхностная*, т.е. хоть массив новый, но он содержит ссылки на исходные элементы, поэтому менять их нельзя, т.к. иначе получится что мы изменим и исходное состояние.

```react
import {useState} from 'react';

export default function Words() {
  const [words, setWords] = useState([
    {id: 0, word: 'Мир'},
    {id: 1, word: 'Труд'},
    {id: 2, word: 'Май'},
    {id: 3, word: 'Весна'},
    {id: 4, word: 'Победа'}
  ]);

  const handleSortClick = () => {
    const copy = [...words];  // <-- Делаем копию
    copy.sort((a, b) => a.word.localeCompare(b.word));  // <-- Сортируем копию
    setWords(copy);  // <-- Устанавливаем копию как новое состояние
  };
  const handleReverseClick = () => {
    const copy = [...words];  // <-- Делаем копию
    copy.reverse();  // <-- Инвертируем копию
    setWords(copy);  // <-- Устанавливаем копию как новое состояние
  };

  return (
    <div>
      <ul>
        {words.map(w => (<li key={w.id}>{w.word}</li>))}
      </ul>
      <button onClick={handleSortClick}>Сортировать</button>
      <button onClick={handleReverseClick}>Инвертировать</button>
    </div>
  )
}
```

В примере разбито на три строчки для наглядности, а кратко делаем так: `setWords([...words].sort((a, b) => a.word.localeCompare(b.word)));`

## Библиотека Immer

Чтобы сделать работу с иммутабельным состоянием удобнее и нагляднее, существует библиотека `Immer`. Если я о ней и напишу, то это будет отдельный конспект.