# tsconfig.json

Настройки компилятора нужно класть в корневую директорию проекта, в файл `tsconfig.json`. 

Пример `tsconfig.json`:

```json
{
  "compilerOptions": {
     "outDir": "./dist/",
     "noImplicitAny": false,
     "sourceMap": true,
     "module": "ESNext",
     "moduleResolution": "node",
     "allowSyntheticDefaultImports": true,
     "experimentalDecorators": true,
     "noUnusedLocals": false,
     "noUnusedParameters": false,
     "target": "es5",
     "typeRoots": [
       "node_modules/@types",
       "typings"
      ]
  },
  "include": [
    "src/**/*.ts"
  ]
}
```

# Ручная компиляция

Скомпилировать проект вручную можно командой `tsc`, находясь в корневой директории проекта, там где лежит tsconfig.json

# Секции tsconfig

## compilerOptions

* `"rootDir": "src"` - папка с исходными файлами.
* `"outDir": "dist"` - папка, куда попадают скомпилированные файлы.
* `"sourceMap": true` - при использовании дебагера будем как будто дебажить исходный ts-код, хотя на самом деле исполняется скомпилированный js-код.
* `"noEmitOnError": true` - не генерировать JS-код в случае нахождения ошибок в TS.
* `"target": "es2016"` - версия JS, в которую нужно компилировать исходный код.
* `"watch": true` - TODO: загуглить, проверить, т.к. в исходном конфиге такой опции вообще нет. TS будет проверять исходные файлы на изменения и перекомпилировать автоматически, когда изменения появляются.
* `"noImplicitAny": false` - когда true, то везде обязательно должен быть указан тип, так что any придется писать явно. Когда false, то можно не писать типы и тогда any будет подразумеваться по умолчанию. Удобно, когда хочется сначала написать обычный JS-код, а потом начать типизировать.
  * Опция автоматически стоит как true, если указана опция `"strict": true`
* `"strictPropertyInitialization": true` - когда true, то полю класса необходимо присвоить значение. Либо указать дефолтное при объявлении, либо присвоить в конструкторе.

## Добавление \ исключение файлов

TODO: что здесь значит включение файлов?

### include

```json
{
  "compilerOptions": {
     // опции компиляции
  },
  "include": [
    "src/**/*.ts"
  ]
}
```



### exclude

```json
{
  "compilerOptions": {
     // опции компиляции
  },
  "exclude": [
    "node_modules",
    "**/*.spec.ts"
  ]
}
```



### files

Используется для прямого включения файлов.

* Если файла нет, будет ошибка.
* Указанные файлы включаются в любом случае, даже попадают под критерии, описанные в секции exclude.

```json
{
  "compilerOptions": {
     // опции компиляции
  },
  "files": [
    "src/main.ts",
    "src/misc/sys.ts",
    "src/core/types.ts"
  ]
}
```



