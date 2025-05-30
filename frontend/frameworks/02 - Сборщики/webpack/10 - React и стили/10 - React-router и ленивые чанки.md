# Ленивые чанки

Размер основного бандла нужно держать как можно меньше, чтобы первоначальная загрузка приложения происходила как можно быстрее. Для этих целей отдельные "страницы" приложения извлекаются в отдельные файлы (чанки) и подгружаются только когда они непосредственно нужны (ленивая загрузка).

## Структура проекта

```
project/
  src/
    components/
      About/
        About.lazy.tsx
        About.tsx
        index.tsx
      Shop
        Shop.lazy.tsx
        Shop.tsx
        index.tsx
      App.tsx
    index.tsx
  package.json
  webpack.config.js
  tsconfig.json 
```

## Сборка проекта

После сборки файлы-чанки появятся рядом с бандлом.

# Отдельные страницы

Сделаем несколько отдельных страниц приложения. Потребуется:

* Сделать компонент под страницу.
* Сделать "ленивую" версию компонента с помощью react-функции `lazy`.
  * В tsconfig опцию `module` нужно поставить в значение `ESNext`, чтобы работал динамический импорт.
* Сделать index-файл для удобства импорта компонента и сокрытия его "ленивости".

Функция lazy (см. подробнее в конспекте по реакту) позволяет загружать модуль с компонентом не сразу, а только когда понадобится отрисовать компонент впервые. Мы передаем в lazy лямбду с импортом нужного модуля и когда его надо отрисовать, реакт выполняет эту лямбду. На время, пока происходит импорт и подключается наш компонент, на его месте отображается заглушка. Заглушку мы оформляем с помощью реакт-компонента `<Suspense>`. После первой загрузки реакт кэширует компонент и последующие отображения будут происходить моментально.

## Компонент "О программе"

Компонент:

```react
export default function About() {
  return (
    <h1>О приложении</h1>
  )
}
```

Ленивая версия компонента:

```javascript
import { lazy } from "react";

export const LazyAbout = lazy(() => import("./About"));
```

index для удобства импорта:

```javascript
export { LazyAbout as About } from "./About.lazy";
```

## Компонент "Магазин"

Компонент:

```react
export default function Shop() {
  return (
    <h1>Магазин</h1>
  )
}
```

Ленивая версия компонента:

```javascript
import { lazy } from "react";

export const LazyShop = lazy(() => import("./Shop"));
```

index для удобства импорта:

```javascript
export { LazyShop as Shop } from "./Shop.lazy";
```

# Точка входа и главная страница

## Точка входа

В точке входа в приложение создадим маршрутизатор:

```react
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Suspense } from "react";  // <-- Заглушка, появляется на время подгрузки компонента
import { App } from "./components/App";
// <-- Импорт будет из index.tsx в папке Shop, т.е. импортируется ленивый компонент
import { Shop } from "./components/Shop";
import { About } from "./components/About";

const root = ReactDOM.createRoot(document.getElementById('root'));

const router = createBrowserRouter([  // <-- Создаем маршрутизатор.
  {
    path: "/",
    element: <App/>,
    children: [  // <-- Так компоненты About и Shop будут считаться дочерними для App.
      {
        path: "/about",
        element: <Suspense fallback={"Загрузка..."}><About /></Suspense>
      },
      {
        path: "/shop",
        element: <Suspense fallback={"Загрузка..."}><Shop /></Suspense>
      }
    ]
  }
]);

root.render(
  <RouterProvider router={router} />
);
```

Основная идея оформления компонентов `About` и `Shop` через массив children в том, что так они будут считаться дочерними компонентами для `App` (пути /shop и /about будут как бы не самостоятельными, а разновидностями пути /). В App мы будем использовать реакт-компонент `Outlet`, который как раз предназначен для отображения дочерних компонентов. Если путь `/`, то отрисовывается `App`, а Outlet - пустой. Если `/shop` или `/about`, то отрисовывается App, а в Outlet, соответственно, будет компонент "Магазин" или "О приложении".

## Главная страница

Компонент главной страницы приложения, из которой будем переходить на другие страницы:

```react
import { Outlet } from "react-router-dom";
import { Link } from "react-router-dom";

export const App = () => {
  return (
    <div>
      <Link to={"/about"}>О приложении</Link>
      <br />
      <Link to={"/shop"}>Магазин</Link>
      <br />
      <p>Hello, world!</p>
      <Outlet />
    </div>
  )
}
```

Как объяснялось выше, Outlet будет либо пустой (когда путь просто `/`), либо в него отрисуется компонент Shop или About (когда пути /shop или /about).

# webpack.config.js

В конфиге вебпака нужно добавить опцию `historyApiFallback` для dev-сервера:

```javascript
module.exports = (settings, argv) => {
  return {
    // Остальная часть конфига
    devServer: {
      port: settings.port,
      open: true,
      historyApiFallback: true  // <-- Добавляем опцию для dev-сервера
    }
  }
};
```

Без этой настройки мы не сможем открывать страницы приложения, вводя их в поисковую строку. Переходы будут работать только при щелчке по ссылкам. А с этой настройкой будет работать и так, и так. Все потому что в SPA-приложениях переходы осуществляются за счет JS-кода, а не запросов к серверу. Так что без этой опции на попытку открыть children-страницы мы будет получать GET 404 not found.

