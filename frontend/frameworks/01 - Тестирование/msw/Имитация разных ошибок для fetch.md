# Таймаут

```javascript
it('Таймаут', async() => {
  const url = new URL('http://localhost:3099/tasks');
    const mockApi = (result) => {
      server.use(http.get(url.toString(), () => {
        return new Promise(() => { });  // <-- Имитация зависания сервера.
      }));
    }
  
  try {
    mockApi(new Promise(() => { }));
  
    const promise = fetchData<Task[]>(url.toString());
    vi.advanceTimersByTime(6000);

    const error = await promise.catch(err => err);

    expect(error).toBeInstanceOf(Error);
    expect(error).toMatchObject({ message: 'Попробуйте еще раз позже.' });
  } finally {
    vi.useRealTimers();
  }
});
```

При таких ситуациях нужно пользоваться средствами тест-фреймворка для имитации таймеров, чтобы сам тест не сваливался из-за задержки. Надо перемотать фейковый таймер, чтобы fetch свалился сразу.

# Ошибка сети

Это:

* CORS.
* Несуществующий url.
* Таймаут.
* Ошибка парсинга тела ответа.

```javascript
it('Ошибка сети', async() => {
  const url = new URL('http://lolosoft.com/tasks');
  server.use(http.get(url.toString(), () => {
    return Response.error();  // <-- Имитация сетевой ошибки.
  }));
```



# Ошибка парсинга JSON

```typescript
it.only('Ошибка парсинга json ответа', async() => {
  const url = new URL('http://lolosoft.com/tasks');
  server.use(http.get(url.toString(), () => {
    return new HttpResponse('Not a JSON', {  // <-- В ответе не JSON.
      status: 200,
      headers: { 'Content-Type': 'application/json' },
    });
  }));
```



# Ошибки HTTP

```javascript
server.use(http.get(url.toString(), () => {
  return new HttpResponse('Not a JSON', {
    status: httpStatus,  // <-- Ставим статус-код, который надо.
    headers: { 'Content-Type': 'application/json' },
  });
}));
```

