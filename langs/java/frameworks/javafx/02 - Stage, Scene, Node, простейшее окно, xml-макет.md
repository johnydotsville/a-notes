# Словарь

* Stage - суть окно. Может быть несколько окон, но как минимум одно.
* Scene - суть содержимое окна. Одно окно может содержать несколько сцен и между ними можно переключаться.
* Node - непосредственно какой-то компонент сцены. Сцена может содержать только один компонент, но он может быть макетом (layout) и значит содержать множество других компонентов.

# Главный класс приложения

Главный класс приложения должен расширять класс `Application`:

```java
public class App extends Application {
    @Override
    public void start(Stage stage) {
        ...
    }

    public static void main(String[] args) {
        launch();
    }
}
```

У него есть стандартный метод main() с запуском приложения, а также переопределенный метод start(), в котором мы наполняем главное окно контентом и показываем его.

# Описание сцены в fxml

## Зависимости и модуль

JavaFx'ный xml называется fxml. Для работы с ним нам понадобятся специальные классы, поэтому нужно подключить зависимость:

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>19</version>
</dependency>
```

Кроме того, в папке java должен быть файл *module-info.java*, в него тоже нужно кое-что добавить:

```java
module johny.dotsville {
    requires javafx.fxml;  // <-- Вот это
    requires javafx.controls;
    exports johny.dotsville;
}
```

## Сам fxml

Размещать fxml удобно в папке *resources* (ее мы создаем в src/main, на одном уровне с папкой java). Назовем fxml-файл, например, *mainScene.fxml*:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import java.lang.*?>
<?import java.util.*?>
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox>
    <Label text="Hello world!"/>
    <Label text="This is a simple demo application."/>
    <Button text="Click me!"/>
</VBox>
```

## Загружаем из fxml

Теперь загрузим сцену. Для этого в методе start нашего класса приложения загрузим fxml и положим контент в главное окно:

```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

@Override
public void start(Stage stage) {
    FXMLLoader loader = new FXMLLoader();
    URL xmlUrl = getClass().getResource("/mainScene.fxml");
    loader.setLocation(xmlUrl);

    Parent root = null;
    try {
        root = loader.load();
    } catch (IOException ioex) {

    }

    stage.setScene(new Scene(root));
    stage.show();
}
```

Компилируем-запускаем, должны увидеть окно с двумя метками и кнопкой.