package com.example.teamcity.api.requests.checked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

@SuppressWarnings("unchecked")
public final class CheckedBase<T extends BaseModel> extends Request implements CrudInterface {

    // Поле для хранения экземпляра класса UncheckedBase,
    // который выполняет основную работу с HTTP-запросами без проверок статусов
    private final UncheckedBase uncheckedBase;

    // Конструктор инициализирует класс CheckedBase и создает экземпляр UncheckedBase для работы с API.
    // Он принимает спецификацию HTTP-запроса и конечную точку API (endpoint)
    public CheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint); // Вызов конструктора базового класса Request
        this.uncheckedBase = new UncheckedBase(spec, endpoint); // Инициализация UncheckedBase для работы с API-запросами
    }

    // Метод для создания сущности (объекта).
    // Использует UncheckedBase для отправки запроса на создание и проверяет, что статус ответа — 200 OK.
    @Override
    public T create(BaseModel model) {
        var createdModel = (T) uncheckedBase
                .create(model)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(endpoint.getModelClass());

        TestDataStorage.getStorage().addCreatedEntity(endpoint, createdModel);
        return createdModel;
    }

    // Метод для получения сущности по её идентификатору.
    // Проверяет, что запрос завершился успешно (200 OK).
    @Override
    public T read(String id) {
        return (T) uncheckedBase
                .read(id)  // Вызов метода read из UncheckedBase, который отправляет запрос на чтение
                .then().assertThat().statusCode(HttpStatus.SC_OK)  // Проверка успешного статуса (200 OK)
                .extract().as(endpoint.getModelClass());  // Преобразование ответа в объект нужного типа
    }

    // Метод для обновления сущности по идентификатору.
    // Проверяет успешность операции через статус ответа.
    @Override
    public T update(String id, BaseModel model) {
        return (T) uncheckedBase
                .update(id, model)  // Вызов метода update из UncheckedBase для обновления сущности
                .then().assertThat().statusCode(HttpStatus.SC_OK)  // Проверка, что запрос завершился со статусом 200 OK
                .extract().as(endpoint.getModelClass());  // Преобразование ответа в объект нужного типа
    }

    // Метод для удаления сущности по идентификатору.
    // Проверяет успешность операции через статус 200 OK и возвращает результат в виде строки.
    @Override
    public Object delete(String id) {
        return uncheckedBase
                .delete(id)  // Вызов метода delete из UncheckedBase для удаления сущности
                .then().assertThat().statusCode(HttpStatus.SC_OK)  // Проверка успешного завершения операции (200 OK)
                .extract().asString();  // Извлечение ответа в виде строки
    }
}

/// Общее описание:
///
/// 	•	Основной класс CheckedBase — это шаблонный класс, наследуемый от Request, который реализует интерфейс CrudInterface для выполнения операций CRUD (Create, Read, Update, Delete) с проверкой статуса ответа от сервера.
/// 	•	UncheckedBase — это класс, который фактически отправляет запросы к API, но не проверяет статусы ответов.
/// 	•	CheckedBase использует методы из UncheckedBase, но добавляет проверку статуса HTTP (200 OK) после каждого запроса. Это гарантирует, что запрос был успешным.
/// 	•	Ответы сервера преобразуются в нужный тип модели, который определяется на основе Endpoint.
///
/// Этот код позволяет безопаснее работать с запросами к API, так как проверяется статус ответа, и если запрос не прошел успешно, это можно будет легко выявить.