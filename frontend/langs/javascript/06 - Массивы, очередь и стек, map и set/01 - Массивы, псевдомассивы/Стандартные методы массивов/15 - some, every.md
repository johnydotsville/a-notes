# some

```javascript
myarr.some((i, ind, arr) => bool)
```

* true -> хотя бы один элемент прошел условие.
* Для пустого массива дает true.

# every

```javascript
myarr.every((i, ind, arr) => bool)
```

* true -> все элементы должны пройти условие.
* Для пустого массива дает false.



# Общее

Оба метода обходят массив пока не поймут свой результат, и сразу останавливаются. Так что не обязательно переберут все элементы.



# Задачи

## Пример 1

В массиве статей найти:

* Статьи, в которых есть все указанные тэги: work, finance.
* Статьи, в которых есть любой из указанных тэгов: nature, cinema.

```javascript
const articles = [
  {
    id: 1,
    tags: [ 'work', 'education', 'finance' ]
  },
  {
    id: 2,
    tags: [ 'education', 'nature', 'food' ]
  },
  {
    id: 3,
    tags: [ 'cinema', 'art' ]
  }
];

// Статьи, в которых есть все указанные тэги.
let searchTags = [ 'work', 'finance' ];  // 1
let filtered = articles.filter(article => searchTags.every(tag => article.tags.includes(tag)));
console.log(filtered);

// Статьи, в которых есть любой из указанных тэгов.
searchTags = [ 'nature', 'cinema' ];  // 2, 3
filtered = articles.filter(article => searchTags.some(tag => article.tags.includes(tag)));
console.log(filtered);
```

Улучшения решения: теги каждой статьи добавляем в Set. Это ускоряет поиск, когда тэгов много:

```javascript
// Найти статьи, в которых есть все указанные тэги.
let searchTags = [ 'work', 'finance' ];  // 1
let filtered = articles.filter(a => {
  const tagsSet = new Set(a.tags);
  return searchTags.every(tag => tagsSet.has(tag));
});
console.log(filtered);

// Найти статьи, в которых есть любой из указанных тэгов.
searchTags = [ 'nature', 'cinema' ];  // 2, 3
filtered = articles.filter(a => {
  const tagsSet = new Set(a.tags);
  return searchTags.some(tag => tagsSet.has(tag));
});
console.log(filtered);
```

Улучшение второе: если проверки множественные, можно создать Set один раз и добавить в объект, а потом пользоваться этим "закэшированным" сетом:

```javascript
articles.forEach(a => a.tagsSet = new Set(a.tags));

// Найти статьи, в которых есть все указанные тэги.
let searchTags = [ 'work', 'finance' ];  // 1
let filtered = articles.filter(a => searchTags.every(t => a.tagsSet.has(t)));
console.log(filtered);

// Найти статьи, в которых есть любой из указанных тэгов.
searchTags = [ 'nature', 'cinema' ];  // 2, 3
filtered = articles.filter(a => searchTags.some(t => a.tagsSet.has(t)));
console.log(filtered);
```

