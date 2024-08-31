# Установка

```
npm install dexie
npm install dexie-react-hooks
```





# Удаление

```javascript
const dbs = await window.indexedDB.databases()
dbs.forEach(db => { window.indexedDB.deleteDatabase(db.name) })
```





# Посмотреть содержимое БД в браузере

F12 > Application > меню слева > IndexedDB