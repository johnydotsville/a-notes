TODO: Почти по каждому пункту есть нюансы, вроде обработки успеха \ неуспеха сохранения и т.д. В каждом разделе оставлю TODO на моменты, которые заметил и которые надо потом доработать, когда время появится.

# Документация

| Источник    | Ссылка                                                       |
| ----------- | ------------------------------------------------------------ |
| MDN         | https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API |
| Илья Кантор | https://learn.javascript.ru/indexeddb                        |

# Что такое

IndexedDB - это:

* Объектная БД (Object-oriented database).
* Работает на основе транзакций.
* Поддерживает индексы.
* Предназначена для хранения больших объемов данных (включая файлы и BLOB).



# Операции над БД

## Асинхронность операций с БД

Все операции с IndexedDb асинхронные и работа с ними опирается на события. Например, команда "открыть БД" возвращает не саму БД, а объект "запроса на открытие". У этого запроса есть события, например "успешно", "неуспешно", и другие. Мы на эти события должны повесить обработчик и когда запрос выполнится, то он вызовет наш обработчик и передаст ему объект события, из которого мы сможем достать, например, уже объект самой БД.

Этот принцип применим абсолютно ко всем операциям над БД. Поэтому удобно оборачивать все эти операции в промисы и в событиях резолвить или отклонять промис.

## Открытие или создание БД

БД создается и открывается одним и тем же методом - `.open()`. У него два параметра:

* Имя БД.
* Версия БД.

Метод возвращает запрос на создание \ открытие БД. У запроса три события (мб больше, но пока опишу только актуальные для себя):

* `onupgradeneeded` - срабатывает в двух случаях:
  * Когда БД еще не существует.
  * Когда в open указана версия старше, чем текущая версия БД.
* `onsuccess` - срабатывает, когда БД открылась успешно.
* `onerror` - срабатывает, когда при открытии БД произошла ошибка.

onupgradeneeded и onsuccess срабатывают последовательно. Например, если БД не существует или версия увеличилась, то сначала сработает onupgradeneeded, а потом onsuccess.

Саму БД можно получить из запроса через свойство `.result`:

```javascript
new Promise((resolve, reject) => {
  const request = indexedDB.open("localdb", 5);  // <-- Делаем запрос на открытие.

  request.onupgradeneeded = (event) => {
    console.log("БД еще не существует, либо версия увеличилась.");
    const db = request.result;  // <-- Получаем объект БД из запроса.
  }

  request.onsuccess = (event) => {
    console.log("БД открыта успешно!");
    const db = request.result;  // <-- Получаем объект БД из запроса.
    resolve(db);
  }

  request.onerror = (event) => {
    console.log("Ошибка при открытии БД: " + request.error);
    reject(request.error);  // <-- Получаем объект БД из запроса.
  }
});
```

Что делать с БД после успешного открытия - наше дело. Можно например сразу создать хранилища, а можно вернуть объект БД из промиса.

# Работа с хранилищами

## Создать хранилище

Важно! Создавать хранилище можно только при открытии БД в событии `onupgradeneeded`. Иначе будет ошибка `Failed to execute 'createObjectStore' on 'IDBDatabase': The database is not running a version change transaction`.

Хранилище создается методом `.createObjectStore()` на объекте БД ([документация](https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase/createObjectStore)):

```javascript
function createStorage(database) {
  const name = "users";  // <-- Имя для хранилища.
  const options = {  // <-- Опции для хранилища, связанные с ключами.
    // keyPath: "id",
    // autoIncrement: true
  };
  database.createObjectStore(name, options);  // <-- Создаем хранилище в БД.
}
```

У метода два параметра:

* Имя хранилища.

* Опции для хранилища. Эти опции связаны с ключом, поскольку у каждого объекта, который мы добавляем в хранилище, должен быть ключ. Этот параметр не обязательный, но тогда придется указывать ключ вручную.

  * `keyPath` - позволяет указать поле объекта, которое нужно использовать в качестве ключа.
  * `autoIncrement` - если true, тогда хранилище будет использовать генератор ключей и при сохранении объект получит очередной ключ автоматически. Сам генератор тоже создается автоматически.

  Можно разом использовать обе опции. Тогда работать будет так: если у объекта есть поле, указанное в keyPath, то оно возьмется в качестве ключа. Если поля нет, тогда будет использован генератор. Примеры, показывающие влияние этих опций на сохранение объета, будут ниже.

### Опции для хранилища

Примеры с демонстрацией, как будет выглядеть добавление объекта в хранилище при разных способах создания хранилища.

#### autoIncrement: true

Предоставляем хранилищу возможность автоматически установить ключ для объекта:

```javascript
function save(database) {
  const data = {
    firstname: "Tom",
    lastname: "Sawyer"
  };

  const storageName = "users";
  const tx = database.transaction(storageName, "readwrite");
  const storage = tx.objectStore(storageName);
  storage.add(data);  // <-- Просто сохраняем объект, ключ он получит автоматически.
}
```

#### keyPath: "поле"

Указываем, какое поле сохраняемого объекта следует использовать в качестве ключа:

```javascript
function save(database) {
  const data = {
    id: 10,  // <-- Укажем, что это поле надо взять в качестве ключа.
    firstname: "Tom",
    lastname: "Sawyer"
  };

  const storageName = "users";
  const tx = database.transaction(storageName, "readwrite");
  const storage = tx.objectStore(storageName);
  storage.add(data);  // <-- В качестве ключа будет взято значение 10, из поля id объекта data.
}
```

#### Нет опций

Когда нет опций, то нужно вручную указать ключ для объекта:

```javascript
function save(database) {
  const data = {
    firstname: "Tom",
    lastname: "Sawyer"
  };

  const storageName = "users";
  const tx = database.transaction(storageName, "readwrite");
  const storage = tx.objectStore(storageName);
  storage.add(data, 100);  // <-- При сохранении указываем ключ самостоятельно.
}
```

## Проверить существование хранилища

Если попытаться создать хранилище, которое уже существует, будет ошибка `Failed to execute 'createObjectStore' on 'IDBDatabase': An object store with the specified name already exists.`. Проверить, существует ли хранилище, можно вот так:

```javascript
if (!database.objectStoreNames.contains(name)) {
  // Если хранилище с таким именем не существует
}
```

## Сохранить в хранилище

| Метод                | Документация                                                 |
| -------------------- | ------------------------------------------------------------ |
| database.transaction | https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase/transaction |
| tx.objectStore       | https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction/objectStore |
| storage.add          | https://developer.mozilla.org/en-US/docs/Web/API/IDBObjectStore/add |

TODO: метод add возвращает запрос, у которого есть события успеха \ неуспеха. См. документацию, и потом расширить пример обработкой этих событий.

Сохранение в хранилище делается с помощью транзакции. Создается транзакция, затем через нее получаем хранилище и потом уже сохраняем:

```javascript
function save(database, data) {
  const storageName = "users";
  const tx = database.transaction(storageName, "readwrite");  // <-- Создаем транзакцию.
  const storage = tx.objectStore(storageName);  // <-- Получаем хранилище.
  storage.add(data);  // <-- Сохраняем объект.
}
```

При создании транзакции можно использовать два режима (три, но см. документацию по третьему):

* `readonly` - режим по умолчанию, для ситуаций, когда надо только читать.
* `readwrite` - для ситуаций, когда надо не только читать, но и писать в хранилище.

TODO: Если при сохранении окажется, что такой ключ уже есть, будет ошибка. Но программа не свалится, а просто сработает событие onerror.

## Прочитать из хранилища



## Специфичное

TODO: сюда написать про то как скипнуть часть записей, как считать пачку и т.д.

## Курсоры

TODO: подумать, мб это вообще отдельные level-1 раздел?





























# Черновик

Чтобы не потерять рабочие фрагменты кода:

```javascript

export class Database {
  currentVersion: number;
  dbname: string;
  storages: any;

  // TODO: потом более продвинутую структуру можно сделать, чтобы таблицы во вложенном объекте были
  constructor(config) {
    this.currentVersion = config.currentVersion;
    this.dbname = config.dbname;
    this.storages = config.storages;
  }

  initDb() {
    return new Promise((resolve, reject) => {
      console.log('Выполняем indexedDB.open ...');
      const request = indexedDB.open(this.dbname, this.currentVersion);  // Это асинхронная или синхронная команда? UPD. Асинхронная конечно
      // Попытка открыть несуществующую БД приведет к созданию БД
      console.log('indexedDB.open выполнено!');

      request.onupgradeneeded = (e) => {
        console.log('dbase upgrade needed событие сработало');
        const db = request.result;
        console.log(`oldVersion: ${e.oldVersion}, currentVersion: ${e.newVersion}`);
        this.storages.forEach(s => {
          if (!db.objectStoreNames.contains(s.name)) {
            db.createObjectStore(s.name, s.settings);
          }
        });
        resolve(true);
      };
      
      request.onsuccess = () => {
        console.log('dbase success событие сработало');
        const db = request.result;
        const version = db.version;
        console.log('Версия БД: ' + version);
        resolve(true);
      };

      request.onerror = () => {
        reject('В функции initDb ошибка: ' + request.error);
      };
    });
  }

  // Считывает из указанного хранилища все записи
  // TODO как сделать так, чтобы не пришлось явно указывать имя хранилища, из которого читать?
  // Вероятно, сделать обертку над голой БД и сделать говорящие методы.
  // Но пока пусть будет так.
  read(storeName, key) {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbname);
      
      request.onsuccess = () => {
        try {
          const db = request.result;
          const tx = db.transaction(storeName, 'readonly');
          const store = tx.objectStore(storeName);
          const res = key ? store.get(key) : store.getAll();
          res.onsuccess = () => {
            resolve(res.result);  // TODO: В result всегда массив?
          }
        } catch (err) {
          reject(err);
        }
      };

      request.onerror = () => {
        const error = request.error?.message
        if (error) {
          reject(error);
        } else {
          reject('Unknown error');
        }
      };
    });
  }


  // Возвращает последнюю сохраненную запись из указанного хранилища
  readLast(storeName) {
    return new Promise((resolve, reject) => {
      const ropen = indexedDB.open(this.dbname);
      
      ropen.onsuccess = (e: any) => {
        try {
          const db = e.target.result;
          const tx = db.transaction(storeName, 'readonly');
          const store = tx.objectStore(storeName);
          const rcursor = store.openCursor(null, "prev");
          
          rcursor.onsuccess = (e) => {
            const cursor = e.target.result;
            if (cursor) {
              resolve(cursor.value);
            } else {
              resolve(null);
            }
          }
        } catch (err) {
          reject(err);
        }
      };
    });
  }


  // Считывает из указанного хранилища пачку записей
  // TODO: Возможно, есть смысл считывать вообще все записи в память и страничить уже из памяти.
  readPage(storeName, pageNum, pageSize) {
    return new Promise((resolve, reject) => {
      const ropen = indexedDB.open(this.dbname);
      
      ropen.onsuccess = (e: any) => {
        try {
          const matches = [];
          let skip = true;
          const skipSize = (pageNum - 1) * pageSize;
          
          const db = e.target.result;
          const tx = db.transaction(storeName, 'readonly');
          const store = tx.objectStore(storeName);
          const rcursor = store.openCursor(null, "prev");
          
          rcursor.onsuccess = (e) => {
            // Это получается "шаг цикла", обход курсора
            const cursor = e.target.result;
            
            if (skip && skipSize > 0) {
              skip = false;
              cursor.advance(skipSize);
              return;  // Это прервать текущий шаг и перейти на некст запись курсора
            }

            if (cursor) {
              matches.push(cursor.value);
              if (matches.length < pageSize) {
                cursor.continue();  // Похоже, приводит к возникновению onsuccess
              } else {
                resolve(matches);
              }
            }
          }
        } catch (err) {
          reject(err);
        }
      };
    });
  }


  save(storeName, data) {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbname);

      request.onsuccess = () => {
        try {
          const db = request.result;
          const tx = db.transaction(storeName, 'readwrite');
          const store = tx.objectStore(storeName);
          store.add(data);  // TODO: Почему, когда тут возникает ошибка, она не ловится, а крашит? UPD. Потому, что ошибка происходит не синхронно, как бы "вне" промиса
          resolve(data);
        } catch (err) {
          reject(err);
        }
      };

      request.onerror = () => {
        const error = request.error?.message
        if (error) {
          reject(error);
        } else {
          reject('Unknown error');
        }
      };
    })
  }


  saveMany(storeName, data) {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbname);

      request.onsuccess = () => {
        try {
          const db = request.result;
          const tx = db.transaction(storeName, 'readwrite');
          const store = tx.objectStore(storeName);
          data.forEach(d => store.add(d));
          resolve(data);
        } catch (err) {
          reject(err);
        }
      };

      request.onerror = () => {
        const error = request.error?.message
        if (error) {
          reject(error);
        } else {
          reject('Unknown error');
        }
      };
    })
  }


  clear(storeName) {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbname);

      request.onsuccess = () => {
        try {
          const db = request.result;
          console.log('Попытка очистить хранилище...');
          const tx = db.transaction(storeName, 'readwrite');
          const store = tx.objectStore(storeName);
          const osr = store.clear();

          osr.onsuccess = () => {
            console.log('Хранилище очищено успешно.');
            resolve(true);
          }
          
          osr.onerror = () => {
            console.log('Не удалось очистить хранилище: ' + osr.error?.message);
            resolve(false);
          }
        } catch (err) {
          reject(err);
        }
      };

      request.onerror = () => {
        const error = request.error?.message
        if (error) {
          reject(error);
        } else {
          reject('Unknown error');
        }
      };
    })
  }

}
```

