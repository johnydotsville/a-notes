# Пакеты

```
npm i react react-dom
```

```
npm i -D @types/react @types/react-dom
```

# tsconfig.json

В конфиг вебпака надо добавить несколько опций:

```yaml
{
  "compilerOptions": {
    # Прочие опции
    "jsx": "react-jsx",
    "allowSyntheticDefaultImports": true,
    "esModuleInterop": true,
  }
}
```

* Опция `jsx`:
  * С ней можно не импортировать React в файлах с исходниками.
  * Можно использовать jsx-синтаксис в исходниках. Без нее ругается на div и прочее.
* Остальные: TODO написать сюда и в конспект по конфигу ts.

# webpack.config.json

Поскольку в react мы используем синтаксис jsx, то все файлы у нас должны быть с расширением `.tsx`. Поэтому в конфиге вебпака нужно изменить расширение у точки входа:

```yaml
module.exports = (settings, argv) => {
  return {
    entry: './src/index.tsx',  # <-- Расширение дб .tsx
    # Остальная часть конфига
  }
}
```

# Исходники

## Расширение файлов

Важно! Файлы должны иметь расширение `.tsx` (react + typescript). ts-loader умеет работать с jsx, поэтому ставить дополнительные лоадеры не надо, если используется связка typescript + react.

## Исходники

### Компонент приложения

Файл `src/components/App.tsx`:

```react
export const App = () => {
  return (
    <div>
      Hello, world!
    </div>
  )
}
```

### Точка входа

Файл `src/index.tsx`:

```typescript
import ReactDOM from 'react-dom/client';
import { App } from "./components/App";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <App />
);
```

