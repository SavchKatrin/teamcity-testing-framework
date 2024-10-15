package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class Specifications {
    // Поле для хранения единственного экземпляра класса Specifications (реализация паттерна Singleton)
    private static Specifications spec;

    // Приватный конструктор (паттерн Singleton) для предотвращения создания экземпляров извне
    private Specifications() {}

    // Метод для получения экземпляра класса Specifications.
    // Если экземпляра ещё нет (он равен null), создаётся новый экземпляр. Иначе возвращается уже существующий экземпляр.
    public static Specifications getSpec() {
        if (spec == null) {
            spec = new Specifications(); // Ленивая инициализация (создание объекта по требованию)
        }
        return spec;  // Возврат единственного экземпляра класса Specifications
    }

    // Приватный метод для создания и настройки объекта RequestSpecBuilder, который используется для конфигурации HTTP-запросов
    private static RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();  // Создание нового объекта RequestSpecBuilder
        requestBuilder.addFilter(new RequestLoggingFilter());  // Добавление фильтра для логирования запросов (в консоль выводится информация о каждом запросе)
        requestBuilder.addFilter(new ResponseLoggingFilter());  // Добавление фильтра для логирования ответов (в консоль выводится информация о каждом ответе)
        requestBuilder.setContentType(ContentType.JSON);  // Установка типа содержимого для запросов как JSON
        requestBuilder.setAccept(ContentType.JSON);  // Установка типа принимаемых ответов как JSON
        return requestBuilder;  // Возврат настроенного объекта RequestSpecBuilder
    }

    public static RequestSpecification superUserAuthSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s/httpAuth".formatted("", Config.getProperty("superUserToken"), Config.getProperty("host")));
        return requestBuilder.build();
    }

    // Метод для получения спецификации HTTP-запроса без авторизации (анонимный запрос)
    public static RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();  // Вызов метода для создания базовой спецификации запроса
        return requestBuilder.build();  // Построение и возврат объекта RequestSpecification без авторизации
    }

    // Метод для получения спецификации HTTP-запроса с авторизацией пользователя
    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();  // Вызов метода для создания базовой спецификации запроса
        // Установка базового URI с авторизацией через username и password пользователя
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Config.getProperty("host")));
        return requestBuilder.build();  // Построение и возврат объекта RequestSpecification с авторизацией
    }
}

/// Общее описание:
///
/// 	•	Класс Specifications — это Singleton, который создает и возвращает объект спецификации для HTTP-запросов. Паттерн Singleton используется для того, чтобы иметь только один экземпляр класса на все приложение, что экономит память и упрощает управление конфигурацией.
/// 	•	RequestSpecBuilder — это объект из библиотеки RestAssured, который используется для конфигурации HTTP-запросов. Он позволяет добавлять фильтры для логирования, устанавливать тип контента (например, JSON) и другие параметры запроса.
///
/// Методы:
///
/// 	1.	getSpec() — предоставляет единственный экземпляр класса Specifications. Если он ещё не создан, создается новый экземпляр.
/// 	2.	reqBuilder() — приватный метод для создания базовой конфигурации запросов, включая логирование запросов и ответов, и установку формата данных (JSON).
/// 	3.	unauthSpec() — возвращает спецификацию для анонимных запросов (без авторизации).
/// 	4.	authSpec(User user) — возвращает спецификацию для запросов с авторизацией, используя учетные данные (логин и пароль) пользователя. Формат авторизации вставляется в URI запроса.
///
/// Этот код упрощает процесс создания и повторного использования спецификаций для различных типов запросов в API: как с авторизацией, так и без нее.