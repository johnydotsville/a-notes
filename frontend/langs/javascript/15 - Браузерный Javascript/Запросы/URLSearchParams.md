

```javascript
const searchParams = new URLSearchParams();
searchParams.append("priority", priority);
tags.forEach(t => searchParams.append("tags", t));
```

Приделываем к запросу:

```javascript
fetch("/tasks" + "?" + searchParams);
```

