# Пакеты

```
npm install vite-tsconfig-paths --save-dev
```

* Алиасы будем писать в конфиге typescript'а, а пакет с плагином `tsconfig-paths` нужен чтобы vite тянул их автоматически оттуда.

# vite.config.ts

Добавляем `tsconfigPaths()` в плагины:

```typescript
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig({
  plugins: [react(), tsconfigPaths()],
})
```

# tsconfig.app.json

```json
{
  "compilerOptions": {
    /* Остальные настройки компилятора */

    /* alises */
    "baseUrl": "./",
    "paths": {
      "@/*": ["./*"],
      "@src/*": ["src/*"],
      "@data/*": ["data/*"],
      "@components/*": ["src/components/*"],
      "@utils/*": ["src/utils/*"]
    }
  },
  "include": ["src/**/*", "data/**/*"],
}

```

* `"include"`  важный параметр, он влияет на то, в каких директориях ts будет искать файлы. В этом примере он будет искать в папках src и data, которые лежат в корне. Но не в самом корне. Чтобы даже в самом корне искал, надо `"include": ["**/*"]` (так просто, для справки).
* После правок в конфиг надо перезапускать проект, иначе пути могут не работать при импорте.



# Пользуемся алиасами

```javascript
import { Task } from "@components/Task/Task";
```

Теперь можно в импортах пользоваться путями, описанными в конфиге.