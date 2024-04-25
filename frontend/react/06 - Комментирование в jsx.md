# Комментарии

Чтобы закомментировать блок кода, можно использовать два синтаксиса:

```react
return (
  <div className="App">
    <h1>Список постов:</h1>
      {/*posts.map(p => <PostItem post={p} key={p.id} />)*/}
  </div>
);
```

```react
return (
  <div className="App">
    <h1>Список постов:</h1>
      {
        //posts.map(p => <PostItem post={p} key={p.id} />)
      }
  </div>
);
```

