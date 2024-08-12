# Размер бандла

Размер основного бандла нужно держать как можно меньше, чтобы первоначальная загрузка приложения происходила как можно быстрее. Для этих целей отдельные "страницы" приложения извлекаются в отдельные файлы, чанки и подгружаются только когда они непосредственно нужны (ленивая загрузка).



# Пакет реакт-роутера

```
npm i react-router-dom
```



# Главная страница приложения

Поменяем точку входа так, чтобы был роутинг:

```react
import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from "./components/App";
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));

const router = createBrowserRouter([
  {
    path: "/",
    element: <App/>,
    children: [
      {
        path: "/about",
        element: <h1>О приложении</h1>
      },
      {
        path: "/shop",
        element: <h1>Магазин</h1>
      }
    ]
  }
]);

root.render(
  <RouterProvider router={router} />
);
```

И надо добавить одну настройку для дев-сервера в конфиг вебпака ([Документация](https://webpack.js.org/configuration/dev-server/#devserverhistoryapifallback)): TODO: поподробнее загуглить, зачем оно нужно

```typescript
import type { Configuration as DevServerConfiguration } from "webpack-dev-server";
import { BuildOptions } from "./types/types";

export function buildDevServer(options: BuildOptions): DevServerConfiguration {
  return {
    port: options.port,
    open: true,
    historyApiFallback: true  // <-- Вот она.
  }
}
```

P.S. Это связано с чем-то вроде того, что в SPA переход по страницам осуществляется за счет JS, а не за счет запроса реальной страницы с сервера. Поэтому надо дев-сервер сконфигурировать с учетом этой вещи.

Поменяем главную страницу приложения, добавим `<Outlet />` - в него будет загружаться страница. TODO: загуглить, что это за аутлет, не видел такого еще:

```react
import React from "react";
import * as classes from "./App.module.scss";
import { Outlet } from "react-router-dom";

export const App = () => {
  return (
    <div>
      <Link to={"/about"}>О приложении</Link>
      <br />
      <Link to={"/shop"}>Магазин</Link>
      <br />
      <p className={classes.nicep}>Hello, world!</p>
      <Outlet />
    </div>
  )
}
```

Можно запустить, чтобы убедиться, что переход по страницам работает.

# Отдельные страницы

Сделаем страницы приложения отдельными компонентами. Понадобится:

* Компонент.
* И его "ленивая" версия (TODO: lazy в react загуглить).
* Дополнительный файл для сокрытия "ленивости" (опционально).

Компонент "О приложении", кладем в `/src/pages/About/About.tsx`:

```react
import React from "react";

const About = () => {
  return (
    <h1>
      О приложении
    </h1>
  )
}

export default About;
```

Ленивая версия, кладем в `/src/pages/about/About.lazy.tsx`:

```react
import { lazy } from "react";

export const LazyAbout = lazy(() => import("./About"));
```

Файл для сокрытия ленивости, кладем в `/src/pages/About/index.tsx`:

```typescript
export { LazyShop as Shop } from "./Shop.lazy";
```

Аналогично для магазина. Из особенностей:

* Под каждую страницу создаем отдельную папку, например `/src/pages/About` и `/src/pages/Shop`
* Ленивую версию называем так же, как обычную, но с суффиксом lazy в названии файла, например, `About.lazy.tsx`
* Файл для сокрытия ленивости добавляем по желанию. Нужен исключительно для удобства, чтобы при использовании компонентов не акцентировать внимание на том, что компонент ленивый.

В роутере задаем эти компоненты:

```react
import React from 'react';
import { Suspense } from "react";  // <-- Заглушка, появляется на время подгрузки компонента
import ReactDOM from 'react-dom/client';
import { App } from "./components/App";
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Shop } from "./pages/Shop";  // <-- Импорт будет из index.tsx в папке Shop, т.е. имп-ся ленивый компонент
import { About } from "./pages/About";

const root = ReactDOM.createRoot(document.getElementById('root'));

const router = createBrowserRouter([
  {
    path: "/",
    element: <App/>,
    children: [
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

Оборачиваем компоненты в роуете в `<Suspence>`, чтобы он показывал нам заглушку на время, пока подгружается чанк. Заглушка покажется только при первой подгрузке, а дальше компонент будет уже в памяти и будет показываться мгновенно.

# Сборка

Если собрать проект, то рядом с главным бандлом будут файлы с чанками.