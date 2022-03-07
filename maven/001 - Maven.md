* Автоматическое создание maven-проекта с нужной структурой
* Что такое артефакты
* Куда класть программу?
* Как скомпилировать и запустить программу с помощью maven

# Что такое Maven

Мэвин - это технология для билда проекта и управления зависимостями. В основе лежит идея "объектной модели проекта" (Project Object Model, POM). Управление проектом происходит в декларативном стиле, т.е. мы указываем, что хотим получить, а не что надо сделать.

# Создание проекта

Создать папку с проектом можно по шаблонам (архетипам), которые предоставляет мэвин. Список мэвин-архетипов можно посмотреть тут https://maven.apache.org/archetypes/index.html

Например, базовый проект создается так (все параметры пишутся в одну строку):

```
mvn
archetype:generate
-DarchetypeGroupId=org.apache.maven.archetypes
-DarchetypeArtifactId=maven-archetype-simple
-DarchetypeVersion=1.4
-DgroupId=home.johnydotsville
-DartifactId=hello-maven-1
-Dversion=7.7.7
```

Три пункта: groupId, artifactId и version указываются как для самого архетипа, так и для собственного приложения.

```
mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-simple -DarchetypeVersion=1.4 -DgroupId=com.samplegroup -DartifactId=some-demo -Dversion=7.7.7
```

# Структура проекта

Каждый архетип формирует свою структуру каталогов. Для сравнения можно посмотреть структуру [archetype-simple](https://maven.apache.org/archetypes/maven-archetype-simple/), [archetype-quickstart](https://maven.apache.org/archetypes/maven-archetype-quickstart/) и [archetype-j2eesimple](https://maven.apache.org/archetypes/maven-archetype-j2ee-simple/)

Описанная выше команда создаст такую структуру simple-проекта:

```
|-- hello-maven-1
    |-- pom.xml
    `-- src
        |-- main
        |   `-- java
        |       `-- home
        |           `-- johnydotsville
        |               `-- App.java
        |-- site
        |   `-- site.xml
        `-- test
            `-- java
                `-- home
        |           `-- johnydotsville
                        `-- AppTest.java
```



# Компиляция

Для компиляции нужно зайти в корень проекта (папка, где лежит pom.xml, на примере выше это hello-maven-1) и набрать команду:

```
mvn compiler:compile
```

При компиляции могут возникнуть такие ошибки (upd при использовании simple архетипа ошибки не было, а только в quickstart):

```
[ERROR] Source option 5 is no longer supported. Use 7 or later.
[ERROR] Target option 5 is no longer supported. Use 7 or later.
```

Фиксятся добавлением в pom раздела properties:

```
<properties>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
</properties>
```

Есть еще команда

```
mvn package
```

Пока не читал особо, что она значит. Вроде компиляция с упаковкой в .jar

# Запуск

Команда запуска тоже запускается из корня проекта

```
mvn exec:java -Dexec.mainClass="home.johny.App"
```

# Зависимости

Задаются в .pom в разделе `dependencies`

```xml
<project ...>
  ...
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
    </dependency>
	<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.28</version>
    </dependency>
  </dependencies>
  ...
```

После добавления зависимости и компиляции проекта, зависимости скачиваются в локальный репозиторий, который находится в директории `C:\Users\%username%\.m2`

# Терминология

► **POM** 

Project Obect Model - Объектная Модель Проекта. pom.xml - это основной файл конфигурации мэвин-проекта, которые содержит всю информацию, необходимую для сборки (build) проекта. Директория, в которой он лежит, является корнем проекта. В этом файле содержится список зависимостей, а также информация о проекте (его название, версия и groupId, пока хз что это такое) и другие настройки.

► **Artifact (Артефакт)**

В документации написано "An artifact is something that is either produced or used by a project. Examples of artifacts produced by Maven for a project include: JARs, source and binary distributions, WARs." "Нечто, что получается из проекта или используется проектом". Пока буду думать об артефакте как о .net-сборке. Потом думаю прояснится.

► **Phase, Goal (фаза, цель)**

мэвин разбивает процесс сборки проекта на "фазы", например вот такая цепочка:

```
validate > compile > test > package > verify > install > deploy (это все фазы)
```

А каждая фаза, в свою очередь, может состоять из нескольких "целей". Цели выполняют конкретные действия, в то время как фазы являются скорее логическими блоками для группировки целей. Вся эта схема образует Build Lifecycle. Есть несколько готовых BL'ов: default, clean, site. Из чего они состоят, можно посмотреть на офф сайте мэвина.