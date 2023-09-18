# FXML и контроллер

fxml - это язык разметки, позволяющий описать интерфейс в xml-подобном стиле, а потом загрузить его в сцену. Складываются fxml-файлы в папку resources, внутри которой надо создать структуру папок как у пакетов программы. Например, программа оформлена в пакет `com.app.example`, значит аналогичную структуру папок надо создать внутри resources. Кстати, сама папка resources находится на одном уровне с папкой java.

Контроллер - это класс, который можно связать с разметкой, и поместить в него, например, обработчики нажатий кнопок. Проще всего привязать контроллер к разметке прямо внутри fxml.

Пример fxml:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Button?>

<VBox alignment="CENTER"
      xmlns:fx="http://javafx.com/fxml"  // Это используем, чтобы пользоваться атрибутами группы fx:
      fx:controller="com.app.classregistry.HelloController">  // Привязываем контроллер

    <Label fx:id="welcomeText" text="Нажмите на кнопку, чтобы изменить этот текст."/>
    <Button text="Hello!" onAction="#onHelloButtonClick"/>  // Назначаем обработчик, используя #методКонтроллера
</VBox>

```

Сам класс контроллера представляет собой обычный класс:

```java
package com.app.classregistry;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
```

Его поля называются в соответствии с id элементов в fxml. Например, у нас была объявлена метка с `fx:id="welcomeText"`, поэтому в контроллере мы объявили поле с именем welcomeText типа Label. Фреймворк автоматически проведет сопоставление и мы сможем через это поле воздействовать на графический элемент. Например, поменять его текст, что мы и делаем в методе onHelloButtonClick.