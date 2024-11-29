# Комментарии в JSX

Чтобы закомментировать блок кода, можно использовать два синтаксиса:

* `{/*  */}`

  ```react
  return (
    <div>
      <h1>Список постов:</h1>
      {/*posts.map(p => <PostItem post={p} key={p.id} />)*/}
    </div>
  );
  ```

* `//`

  ```react
  return (
    <div>
      <h1>Список постов:</h1>
      {
        //posts.map(p => <PostItem post={p} key={p.id} />)
      }
    </div>
  );
  ```

