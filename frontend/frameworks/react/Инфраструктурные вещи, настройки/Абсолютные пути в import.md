# Пути в import

Когда мы импортируем компонент, то пишем что-то вроде этого:

```react
import Gallery from './components/Gallery';
import Foobar from '../../ui/Foobar';
```

Путь отсчитывается от текущей директории и по умолчанию нет способа использовать абсолютный путь, например, от папки src или от корневой папки проекта. Это настраивается и конфигурируется на уровне проекта, зависит от скрипта, которым создается проект.

Для исправления ситуации есть, например, утилита `Craco`.

# Абсолютные пути в import

## craco

* Останавливаем приложение, если оно запущено.

* Переходим в корень приложения и устанавливаем craco: `npm install @craco/craco --save`

* В корне приложения (рядом с `package.json`) создаем файл конфигурации крако `craco.config.js`

* Внутри этого файла настраиваем пути, например:

  ```yaml
  const path = require("path");
  
  module.exports = {
    webpack: {
      alias: {
        src: path.resolve(__dirname, "src"),
    	  components: path.resolve(__dirname, "src/components")
      },
    },
  };
  ```

* Меняем в `package.json` скрипты приложения, добавляя craco-скрипт вместо дефолтного реактовского. Например:

  ```yaml
  ## Было
  "scripts": {
      "start": "react-scripts start",
      "build": "react-scripts build",
      "test": "react-scripts test",
      "eject": "react-scripts eject"
    },
  
  ## Стало
  "scripts": {
      "start": "craco start",
      "build": "craco build",
      "test": "craco test",
    },
  ```

  Или в случае с дополнительными настройками, вот так:

  ```yaml
  "scripts": {
      "start": "set \"PORT=3005\" && set \"BROWSER=none\" && start chrome http://localhost:3005 && craco start",
  ```

* Запускаем приложение.

* Теперь можно пользоваться абсолютными путями, отталкиваясь от директорий, обозначенных в конфиге крако. Например:

  ```react
  import CButton from 'src/components/ui/button/CButton';
  import CButton from 'components/ui/button/CButton';
  ```

  Относительные пути тоже продолжают работать как и раньше:

  ```react
  import CButton from '../button/CButton';
  ```

P.S. Этот способ работал у меня для приложения, созданного скриптом `create-react-app`. Возможно что при других способах создания приложения работать не будет. Но не суть, главное было показано, что абсолютные пути - это отдельно настраиваемая вещь, а не что-то имеющееся по умолчанию.