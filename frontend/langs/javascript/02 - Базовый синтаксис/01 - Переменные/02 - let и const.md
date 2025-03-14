# let

Команда `let` позволяет определить переменную:

```javascript
let username;
username = "JohNy";
```

При объявлении можно сразу присвоить значение:

```javascript
let username = "JohNy";
```

Объявление нескольких переменных одной строкой не приветствуется:

```javascript
let username = "JohNy", lang = "Russian", country = "Russia";  // Ухудшает читабельность
```

# const

Этим оператором объявляются переменные, которые нельзя изменить после инициализации. Может использоваться как для хранения непосредственно "констант", вроде цветов, так и для неизменяемых переменных:

```javascript
const COLOR_GREEN = "#00FF21";
const COLOR_RED = "#FF0000";
```

```javascript
const message = "Неизменяемое сообщение.";
```

```javascript
let username = "JohNy", 
    lang = "Russian", 
    country = "Russia";  // Тоже не очень
```

