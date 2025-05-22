# Pick

```typescript
Pick<Foobar, 'foo' | 'bar' | 'baz'>
```

* `Pick` создает новый тип, в котором есть только указанные поля из переданного типа.

# Omit

```typescript
Omit<Foobar, 'foo' | 'bar' | 'baz'>
```

* `Omit` создает новый тип, в котором есть все поля из переданного типа, кроме указанных.



# Пара замечаний

* keyof для избежания несуществующих полей.
  * keyof как бы "встроен" в Pick и Omit, поэтому мы не сможем в них указать имена полей, которых нет в исходном типе.
* Принцип использования:
  * Pick - когда из множества полей нужно чуть-чуть, то есть например "пикнуть" 3 проще чем исключить 17.
  * Omit - когда из множества полей нужно много, то есть исключить 5 проще, чем пикать 15ы.

# Область использования

## Создание "облегченной" версии типа

Например, есть тип для товара, в котором много полей:

```typescript
type Product = {
  id: number;
  title: string;
  price: number;
  description: string;
  category: string;
  rating: {
    rate: number;
    count: number;
  };
  images: string[];
};
```

Нам нужна "облегченная" версия этого типа для карточки товара. Например, только название, цена, фотографии и рейтинг:

```typescript
type ProductCard = Pick<Product, 'title' | 'price'> 
    & Pick<Product['rating'], 'rate'>;

function showProductCard(card: ProductCard) {
  console.log(card);
}

showProductCard({
  title: 'Машинка',
  price: 735,
  rate: 4.7
})
```

```typescript
const ProductCard: React.FC<ProductCardProps> = ({
  title,
  price,
  images,
  rating,
}) => {
  // some code
}
```

P.S. Если не понятна конструкция `Product['rating']` см. понятие *Lookup types* (оно же *Indexed Access Types*). Про `&` если не понятно, то см *union type*.

Пример чтобы сохранить вложенность рейтинга:

```typescript
type ProductCard = Pick<Product, 'title' | 'price'> 
    & { rating: Pick<Product['rating'], 'rate'>};

function showProductCard(card: ProductCard) {
  console.log(card);
}

showProductCard({
  title: 'Машинка',
  price: 735,
  rating: {
    rate: 4.7
  }
})
```

## Исключение чувствительных данных

Когда в типе товара есть данные не для широкого круга, например, закупочная цена, поставщик и т.д. и нужно их исключить в функциях отправки в другой сервис.



