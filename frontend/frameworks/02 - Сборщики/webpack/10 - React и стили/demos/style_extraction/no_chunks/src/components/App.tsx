import "./App.css";
import Info from "./Info";

export default function App() {
  return (
    <div className="card cardSpace">
      <h1 className="underlined">Hello, webpack!</h1>
      <p>Пример на использование модульных стилей.</p>
      <Info />
    </div>
  )
}