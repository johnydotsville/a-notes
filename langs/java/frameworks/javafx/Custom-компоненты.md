# Компоненты с произвольной структурой

Бывает нужно создать какой-нибудь произвольный компонент, состоящий из нескольких простых. Например, мы хотим сделать список классов с кнопкой для открытия страницы с документацией по классу и с кнопкой для открытия страницы с исходным кодом класса . В этом случае каждый элемент списка представляет собой метку + две кнопки.

Решение заключается в следующем:

* Создать fxml для будущего компонента;
* Создать для него отдельный класс, который в том числе будет выполнять и роль контроллера;
* Правильно связать этот класс с fxml'ем;
* Создать экземпляр компонента и поместить его куда нужно.

## Создаем fxml

Создаем в ресурсах fxml-файл с разметкой под наш компонент. Назовем его `entry-view`:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<fx:root type="javafx.scene.layout.HBox"
         xmlns="http://javafx.com/javafx"
         xmlns:fx="http://javafx.com/fxml"
         style="-fx-background-color: #A0A0A0">

    <Label fx:id="name" text="SampleClassName"/>
    <Button fx:id="sourceLink" text="src"/>
    <Button fx:id="docLink" text="doc"/>
</fx:root>

```

Здесь есть несколько тонкостей:

* Корневым элементом компонента является `<fx:root>`, а не какой-нибудь `HBox` например.

  Это связано с технической организацией произвольных компонентов: мы создадим класс-потомок одного из уже существующих компонентов, загрузим fxml и для полученного объекта установим в качестве корня этот класс-потомок. Поэтому, если в fxml будет указан, например, HBox то получится HBox, завернутый в HBox, т.е. дублирование. Одним словом, когда мы делаем fxml для произвольного компонента, то должны содержимое помещать именно в `<fx:root>`, а не во что-то еще.

* Для кнопок не указываются обработчики.

  Это связано с тем, что мы здесь в разметке не указываем для компонента контроллер. Мы будем устанавливать его программно - внутри класса компонента. И поскольку в разметке не указан контроллер, то IDE не сможет распознать методы-обработчики. Их мы тоже установим программно.

## Создаем контроллер

Создаем класс компонента, который одновременно будет являться и контроллером компонента:

```java
package com.app.classregistry.components;

import com.app.classregistry.ClassRegistryApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Entry extends HBox {

    @FXML
    private Label name;
    @FXML
    private Button sourceLink;
    @FXML
    private Button docLink;

    protected void sourceLinkClick() {
        name.setText("Посмотреть исходники");
    }

    protected void docLinkClick() {
        name.setText("Посмотреть документацию");
    }

    private Entry() {

    }

    public static Entry create() throws IOException {
        var entry = new Entry();

        var loader = new FXMLLoader(ClassRegistryApp.class.getResource("entry-view.fxml"));
        loader.setRoot(entry);
        loader.setController(entry);
        loader.load();

        return entry;
    }

    @FXML
    public void initialize() {
        sourceLink.setOnAction(e -> sourceLinkClick());
        docLink.setOnAction(e -> docLinkClick());
    }
}

```

Здесь тоже есть несколько тонкостей:

* Я объявил конструктор приватным исключительно потому, что передавать this из конструктора куда-то в сторонние объекты - это плохая практика (называется "ускользание this", "this escaping"), а в исходных примерах именно так и делалось. Возможно, что приватный конструктор без параметров необходим в каких-то других сценариях, но пока оставлю так.
* Создание компонента выглядит специфично:
  * Мы создаем экземпляр нашего произвольного компонента;
  * С помощью загрузчика мы из fxml генерируем компонент и устанавливаем ему в качестве корня и контроллера наш экземпляр.
  * Возвращаем наш экземпляр как готовый к использованию произвольный компонент.
* Для загрузки fxml используется класс ClassRegistryApp - главный класс приложения, потому что он после компиляции оказывается в корневой директории вместе с файлами ресурсов.
* Установка обработчиков к кнопкам должна осуществляться в методе `initialize`, потому что только к этому моменту фреймворк привяжет реальные кнопки к полям контроллера.

## Добавляем произвольный компонент в контейнер

Пусть у нас такое главное окно:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.layout.VBox?>

<?import javafx.scene.control.Button?>

<VBox alignment="CENTER" spacing="20.0"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.app.classregistry.MainController">

    <Button text="Добавить" onAction="#addEntry"/>

    <VBox fx:id="entries"  <!-- Добавлять будем сюда -->
          style="-fx-background-color: green"/>
</VBox>
```

По id находим контейнер, в который хотим добавить наш компонент, и добавляем.

```java
public class MainController {
    @FXML
    private VBox entries;

    @FXML
    protected void addEntry() throws IOException {
        entries.getChildren().add(Entry.create());
    }
}
```

