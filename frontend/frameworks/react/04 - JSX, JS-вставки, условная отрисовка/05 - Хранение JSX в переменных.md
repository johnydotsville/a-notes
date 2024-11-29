# Хранение JSX в переменных

JSX можно сохранять в переменные и таким образом готовить блоки разметки заранее. Например:

```react
export default function Dyno() {
  const [flag, setFlag] = useState(true);
  let message = "Привет, JSX!";
    
  if (flag) {
    message = (  // <-- Помещаем JSX в переменную
      <span>✔ <del>{message}</del></span>
    );
  }

  return (
    <>
      <div>{message}</div>
      <button onClick={e => setFlag(!flag)}>{flag ? 'Не сделано' : 'Сделано'}</button>
    </>
  );
}
```

Более продвинутый пример:

```react
export default function Gallery() {
  const famousUssrPeople = getPeople()
    .map(p => <li key={p.id}><Profile info={p} /></li>);

  return (
    <section>
      <h1>Известные люди СССР</h1>
      <ul>{famousUssrPeople}</ul>
    </section>
  );
}
```

Здесь, вместо того чтобы выполнять map прямо посреди разметки, мы преобразовали массив данных в массив элементов списка и сохранили полученный JSX в переменную, а затем вставили ее в тело списка. Разметка получилась компактнее.

