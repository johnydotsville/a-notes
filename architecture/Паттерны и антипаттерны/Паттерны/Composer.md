# Composer

TODO: Этот пример похож на то что надо, но нужно проверить, когда вернусь к теме детальнее.

UPD. Это не композер.

Валидация продукта. Пусть у нас есть простой класс продукта и какое-нибудь начальное требование к его валидности. Например, чтобы цена была больше нуля:

```java
@Getter @Setter
public class Product {
    private String name;
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public boolean isValid() {
        return price > 0;
    }
}
```

```java
var product = new Product("Cake", 350);
boolean valid = product.isValid();
System.out.println(valid);
```

Размещая код валидации в самом классе продукта, мы нарушаем SRP, потому что у нас появляется две причины для изменения класса:

1. Изменение структуры продукта - добавление или изменение полей.
2. Изменение условий валидации. Например, "подарочный" продукт должен иметь цену 0. Или наоборот цена не должна быть меньше какого-то значения. К тому же, если валидация станет сложнее, например, добавятся разные требования к проверке названия, то метод может стать к тому же и очень сложным.

В итоге получается, что выгоднее отделить валидацию от продукта.

```java
@Getter @Setter
public class Product {
    private String name;
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    // Убрали метод валидации
}
```

Определяем интерфейс для валидаторов и пишем несколько реализаций:

```java
public interface ProductValidator {
    boolean isValid(Product product);
}
```

```java
public class DefaultProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return product.getPrice() > 0;
    }
}
```

```java
public class GiftProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return product.getPrice() == 0;
    }
}
```

```java
public class TitleProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return Character.isUpperCase(product.getName().charAt(0));
    }
}
```

Для удобного комбинирования валидаторов воспользуемся шаблоном Composite и создадим валидатор, образующий цепочку из нескольких валидаторов.

```java
public interface ProductValidationChain {
    boolean isValid(Product product);
    void addValidator(ProductValidator validator);
    void removeValidator(ProductValidator validator);
}
```

```java
public class SimpleProductValidationChain implements ProductValidationChain {
    private final Set<ProductValidator> chain;

    public SimpleProductValidationChain() {
        chain = new LinkedHashSet<>();
    }

    public SimpleProductValidationChain(Collection<ProductValidator> validators) {
        chain = new LinkedHashSet<>(validators.size());
        chain.addAll(validators);
    }

    public void addValidator(ProductValidator validator) {
        chain.add(validator);
    }

    public void removeValidator(ProductValidator validator) {
        chain.remove(validator);
    }

    @Override
    public boolean isValid(Product product) {
        System.out.println(chain.size());
        return chain.stream().allMatch(v -> v.isValid(product));
    }
}
```

Проведем валидацию продукта новым способом:

```java
var product = new Product("Cake", 350);

var giftValidator = new GiftProductValidator();
var titleValidator = new TitleProductValidator();
// ProductValidator validationChain = new SimpleProductValidationChain(
//     List.of(giftValidator, titleValidator));
ProductValidationChain chain = new SimpleProductValidationChain();
chain.addValidator(giftValidator);
chain.addValidator(titleValidator);
chain.removeValidator(giftValidator);
boolean valid = chain.isValid(product);
```

Можно было бы улучшить, чтобы возвращался список проблем, но это уже другая история. Важно то, что теперь функциональность разнесена и изменение условий валидации не затрагивают класс Product.