# Spring Boot

Создать спринг-приложение - по сути означает определить набор бинов и указать взаимосвязи между ними, чтобы спринг их автоматически создал, связал как надо и давал нам в пользование в нужные моменты.

Например, в приложении по работе с БД нам обычно нужны одни и те же классы, вроде EntityMangerFactory, EntityManager, не важно какие еще - главное, что примерно одно и то же всегда. И вот Spring Boot предоставляет нам бины, в которых уже все это настроено и готово к работе.

Ориентируется он по содержимому classpath'а и свойств в application.properties. Т.е. если из них становится понятно, что мы будем пользоваться hibernate, то сконфигурируется на использование именно хибера.