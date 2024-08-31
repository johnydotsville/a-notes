https://medium.com/@shruti.latthe/understanding-react-outlet-a-comprehensive-guide-b122b1e5e7ff

https://reactrouter.com/en/main/components/outlet



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

Можно посмотреть в конспекте про вебпак "React-router и ленивые чанки", там есть пример. Мб его адапритровать и перенести сюда?