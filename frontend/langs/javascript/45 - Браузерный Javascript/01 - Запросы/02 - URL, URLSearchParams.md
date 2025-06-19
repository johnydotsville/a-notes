### Мнемоника

```javascript
urlSearchParams.append <--> url.searchParams.append

const urlSearchParams = URLSearchParams()
urlSearchParams.append('foobar', String(foobar));

const url = new URL('someurl');
url.searchParams.append('foobar', String(foobar));
```



### Параметры добавляются непосредственно через url

```javascript
async function fetchData(page, limit) {
  const endpoint = 'https://jsonplaceholder.typicode.com/posts';

  const url = new URL(endpoint);
  url.searchParams.append('_page', String(page));
  url.searchParams.append('_limit', String(limit));

  const response = await fetch(url);
  const data = await response.json();
  data.forEach(p =>console.log(p.title));
}

fetchData(5, 3);
```



### Параметры оформляются через URLSearchParams, а потом целяются к url

```javascript
async function fetchData(page, limit) {
  const params = new URLSearchParams();
  params.append('_page', String(page));
  params.append('_limit', String(limit));

  const endpoint = 'https://jsonplaceholder.typicode.com/posts';
  const url = new URL(endpoint);
  url.search = params.toString();

  const response = await fetch(url);
  const data = await response.json();
  data.forEach(p =>console.log(p.title));
}

fetchData(5, 3);
```

