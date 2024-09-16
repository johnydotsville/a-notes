import React from 'react';
import { Suspense } from "react";
import ReactDOM from 'react-dom/client';
import { App } from "./components/App";
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Shop } from "./pages/Shop";
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