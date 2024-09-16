import React from "react";
import * as classes from "./App.module.scss";
import { Outlet, Link } from "react-router-dom";

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