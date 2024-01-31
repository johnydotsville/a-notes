# finally и поток выполнения

Блок finally выполняется всегда, что бы ни случилось.

* Сценарий "Нормальный": исключение ловится, обрабатывается и новых исключений не возникает:

  ```java
  try {
      System.out.println("Начали выполнять try");
      throw new Exception();
  }
  catch (Exception ex) {
      System.out.println("Зашли в catch");
  }
  finally {
      System.out.println("Зашли в finally");
  }
  System.out.println("Добрались до последней части программы");
  
  // Вывод
  Начали выполнять try
  Зашли в catch
  Зашли в finally
  Добрались до последней части программы
  ```

    После нормальной обработки исключения программа идет дальше своим ходом.

* Сценарий "Снова здарова": в catch опять возникает исключение, которое не ловится:

  ```java
  try {
      System.out.println("Начали выполнять try");
      throw new RuntimeException("test");
  }
  catch (Exception ex) {
      System.out.println("Зашли в catch");
      if (args.length < 20)
          throw new RuntimeException("test");
  }
  finally {
      System.out.println("Зашли в finally");
  }
  System.out.println("Добрались до последней части программы");
  
  // Вывод
  Начали выполнять try
      Зашли в catch
          Зашли в finally
  ```

    finally отработал, но программа отвалилась, до последней строчки не добрались.

* Сценарий "Друзья помогут": В catch опять возникает исключение, которое ловится в вызывающем коде:

  ```java
  try {
      try {
          System.out.println("Начали выполнять try");
          throw new RuntimeException("test");
      } catch (Exception ex) {
          System.out.println("Зашли в catch");
          if (args.length < 20)
              throw new RuntimeException("test");
      } finally {
          System.out.println("Зашли в finally");
      }
      System.out.println("Добрались до последней части программы");  // X
  } catch (Exception ex) {
      System.out.println("Попали во внешний catch");
  }
  finally {
      System.out.println("Попали во внешний finally");
  }
  System.out.println("Hello, exceptions!");
  
  // Вывод
  Начали выполнять try
  Зашли в catch
  Зашли в finally
  Попали во внешний catch
  Попали во внешний finally
  Hello, exceptions!
  ```

  До строчки *X* не добрались, но важно то, что сначала  выполнился внутренний finally и только потом попали во внешний catch.

* Сценарий "Поехавший": finally есть, а catch нету вообще:

  ```java
  try {
      if (args.length < 20)
          throw new RuntimeException("test");
  }
  finally {
      System.out.println("Зашли в finally");
  }
  System.out.println("До свидания");
  
  // Вывод
  Зашли в finally
  ```

  ```java
  try {
      try {
  	    throw new RuntimeException("test");
      } finally {
      	System.out.println("Зашли в finally");
      }
  }
  finally {
      System.out.println("Попали во внешний finally");
  }
  System.out.println("До свидания");
  
  // Вывод
  Зашли в finally
  Попали во внешний finally
  ```

    Все finally все равно выполняются, но до последней строчки не доходим, программа отваливается.

# finally и return

return в finally перебивает остальные return'ы. И поскольку finally выполняется всегда, то нужно осторожнее пользоваться в нем return'ом:

```java
public String methodA() {
    try {
        return "Думаешь, вернется это?";
    }
    finally {
        return "Ничего подобного! Вернется эта строчка.";
    }
    return "Вот это поворот!";  // Строго говоря, эта строчка недостижима, так что compile error
}

// Вывод
Ничего подобного!
```

