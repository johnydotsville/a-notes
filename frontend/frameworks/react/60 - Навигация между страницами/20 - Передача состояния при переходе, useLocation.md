# Передача состояния при переходе

При переходе можно передать данные с текущей страницы на следующую. Это можно сделать как через компонент Link, так и через хук useNavigation. У обоих для этого есть свойство `state`, в который надо положить передаваемый объект. Для извлечения переданных данных, в целевом компоненте пользуемся хуком useLocation.

Под капотом реакт использует History API, например метод pushState, чтобы реализовать передачу.

Базовый пример:

```react
import { BrowserRouter, useLocation } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<SourceComponent />} />
        <Route path='/destination' element={<DestinationComponent />} />
      </Routes>
    </BrowserRouter>
  );
}

function SourceComponent() {
  const person = {
    name: "Tom Sawyer",
    from: "Missouri"
  }

  const navigate = useNavigate();
  // <-- Заполняем св-во state что тут, что в Link
  const send = () => navigate("/destination", { state: person });

  return <>
    <button onClick={() => send()}>Перейти на другую страницу через useNavigate</button>
    <br />
    <Link to="/destination" state={person}>Перейти на другую страницу через Link</Link>
  </>
}

function DestinationComponent() {
  const location = useLocation();  // <-- Используем хук
  const data = location.state;  // <-- Извлекаем данные из свойства state

  return <div>Имя: {data.name}, Откуда: {data.from}</div>
}
```

