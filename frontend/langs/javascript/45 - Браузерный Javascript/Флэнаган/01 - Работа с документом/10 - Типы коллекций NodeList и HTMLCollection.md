

# NodeList и HTMLCollection

Оба этих типа представляют собой коллекции элементов. Разные методы, которые возвращают несколько элементов, пользуются этими типами для хранения результата своей работы. Также разные свойства, которые позволяют получать несколько элементов, тоже имеют один из этих типов. В последующих конспектах упоминаются эти типы коллекций, поэтому нужно понимать, в чем их сходство и отличия.

Сходства:

* Обе коллекции являются псевдомассивами.
* У обоих есть свойство `.length`, по которому можно понять количество элементов в коллекции.
* Обе коллекции следует использовать исключительно для чтения.

Особенности NodeList:

* Могут содержать элементы любых типов (теги, текст, комментарии, атрибуты и т.д.).
* Коллекция может быть как статической, так и живой. Некоторые методы возвращают статическую, а некоторые - живую. Так что для каждого метода надо смотреть отдельно.
* Коллекция позволяет обращаться к своим элементам только через индекс.

Особенности HtmlCollection:

* Может содержать только элементы-теги.
* Коллекция всегда живая.
* Коллекция позволяет обращаться к своим элементам по индексу, по имени и по id. Т.е. если у элемента, находящегося в коллекции, был задан атрибут name или id, то их можно использовать для доступа (см в примере дальше).

Пример для наблюдения сходств и отличий коллекций этих типов: с помощью разных методов получаем три коллекции, каждого вида. Нажатием на кнопку добавляем элемент и в консоль выводятся размеры коллекций, видим какие изменяются, а какие - нет. Ну и на других кнопках попытки обратиться к элементу разными способами в одном случае приведут к ошибке, а в другом - будут успешны:

```html
<body>
  <p id="p1" name="frag">Предвижу все: вас оскорбит</p>
  <p id="p2" name="frag">Печальной тайны объясненье.</p>
  <p id="p3" name="frag">Какое горькое презренье</p>
  <div id="d4" name="special">Ваш гордый взгляд изобразит!</div>
  <button onclick="add()">Добавить</button>
  <button onclick="readNL()">NodeList</button>
  <button onclick="readHC()">HTMLCollection</button>
</body>
```

```javascript
const nlStatic = document.querySelectorAll("p");     // NodeList статический
const nlDynam = document.getElementsByName("frag");  // NodeList живой
const hcDynam = document.getElementsByTagName("p");  // HTMLCollection живая
console.log(`Начальный размер коллекции параграфов: ${nlStatic.length}`);

function add() {
  const p = document.createElement("p");
  p.innerText = "Этот параграф добавлен программно.";
  p.setAttribute("name", "frag");
  document.body.append(p);
  console.log(`Размер NodeList static: ${nlStatic.length}`);  // <-- Не будет меняться.
  console.log(`Размер NodeList live:   ${nlDynam.length}`);   // <-- Изменится.
  console.log(`Размер HTMLCollection static: ${hcDynam.length}`);  // <-- Изменится.
}

function readNL() {
  const coll = document.querySelectorAll("div");
  console.log("Обращаемся разными способами к элементам в NodeList.");
  console.log(coll[0].innerText);       // <-- Ok
  console.log(coll.d4.innerText);       // <-- Ошибка
  console.log(coll.special.innerText);  // <-- Ошибка
}

function readHC() {
  const coll = document.getElementsByTagName("div");
  console.log("Обращаемся разными способами к элементам в HTMLCollection.");
  console.log(coll[0].innerText);       // <-- Ok
  console.log(coll.d4.innerText);       // <-- Ok
  console.log(coll.special.innerText);  // <-- Ok
}
```

