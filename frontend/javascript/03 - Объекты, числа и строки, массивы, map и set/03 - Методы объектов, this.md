# Методы объектов

Номинально, методы объектов - это свойства, значениями которых являются функции. Есть несколько способов описать функции объекта. Вот наиболее похожий на типичный стиль в ООП-языках:

```javascript
let user = {
  name: "Tom",
  age: 14,  // <-- Не забываем запятую

  hello() {  // <-- Т.н. "сокращенная форма"
    console.log("Hello!");
  }  // <-- Если несколько методов, то здесь будет нужна запятая
};

user.hello();
```

## Альтернативные способы описания

```javascript
let user = {
  name: "Tom",
  age: 14,

  hello: function() {  // <-- Обычное свойство, значением которого явл. функция
    console.log("Hello!");
  }
};

user.hello();
```

```javascript
let user = {
  name: "Tom",
  age: 14
};

user.hello = function() {  // На ходу добавили свойство и поместили в него новую функцию.
  console.log("Hello!");
}

user.hello();
```

```javascript
let user = {
  name: "Tom",
  age: 14
};

user.hello = hello;  // Добавили свойство и положили в него уже существующую функцию.

user.hello();

function hello() {
  console.log("Hello, I'm function declaration!");
}
```

TODO: способы не равнозначны. Отличия проявляются при наследовании. Вернуться и дописать эти отличия, когда разберусь в наследовании.

# this

`this` в js вычисляется в момент вызова функции. Когда функция вызывается на объекте, то в this помещается ссылка на этот объект:

```javascript
let tom = { name: "Tom" };
let huck = { name: "Huck" };

let hello = function() {
  console.log("Hello! My name is " + this.name);
}

tom.h = hello;  // Закинем функцию в оба объекта,
huck.h = hello; // у каждого из которых есть св-во name.

tom.h();   // Hello! My name is Tom
huck.h();  // Hello! My name is Huck
```

Если попробовать вызвать функцию hello без объекта:

```javascript
hello();
```

То здесь два варианта развития событий:

* В строгом режиме this получит значение undefined, и попытка обратиться к свойству name на undefined приведет к ошибке "Cannot read properties of undefined (reading 'name') at hello".
* В обычном режиме this получит значение глобального объекта `window` и ошибки не будет.

## this и лямбды

TODO: сейчас известно только то, что лямбды заимствуют this из области видимости выше. Подробнее - позже.