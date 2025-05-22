* Про типизацию итераторов и генераторов почитать отдельно

x Остановился на стр. 105, писал в конспект по дженерикам.
  Остановился, потому что почувствовал необходимость почитать
  раздел про типы, т.к. не понял что такое unknown тип.



* Record, Tuple





- as const + typeof

- [x] ```
  function getProperty<T>(obj: T, key: keyof T): T[keyof T] {
  ```

  что тут значит T[keyof T]

- ```
  type StringKeys<T> = { [K in keyof T]: T[K] extends string ? K : never }[keyof T];
  ```