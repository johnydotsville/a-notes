# var

Устаревшее ключевое слово для объявления переменных. Выделено в отдельный раздел, чтобы удобнее показать его особенности.

## Область видимости

У var нет блочной области видимости:

```javascript
{
  var tom = "Tom";
  let huck = "Huck";
}
console.log(tom);   // Tom, var-переменная видна
console.log(huck);  // Ошибка: huck is not defined.
```

Это касается не только таких блоков, но и любых других - условия, циклы.

var может дать только ограничение области видимости внутри функции:

```javascript
function demo() {
  var tom = "Tom";
  console.log("demo: " + tom);
}

demo();  // demo: Tom
console.log("wtf: " + tom);  // tom is not defined
```

Впрочем, var лучше, чем объявление переменной вообще без ключевого слова:

```javascript
function demo() {
  tom = "Tom";  // Локальная переменная функции будет видна даже вне ее
  console.log("demo: " + tom);
}

demo();  // demo: Tom
console.log("wtf: " + tom);  // wtf: Tom
```

## Повторные объявления

var позволяет объявить переменную несколько раз. Это никак не влияет на ее значение, но само по себе выглядит странно:

```javascript
var name = "Tom";
console.log(name);  // Tom
var name;
console.log(name);  // Tom, повторное объявление не затерло значение
var name = "Huck";
console.log(name);  // Huck
```

