package com.example.teamcity.api.generators;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.annotations.Random;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.TestData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TestDataGenerator {

    // Приватный конструктор класса. Это сделано, чтобы запретить создание экземпляров данного класса.
    private TestDataGenerator() {
    }

    /**
     * Основной метод генерации тестовых данных.
     *
     * @param generatedModels - список уже сгенерированных моделей, чтобы не генерировать повторно, если модель уже есть.
     * @param generatorClass - класс, для которого нужно сгенерировать данные (например, какая-то модель).
     * @param parameters - дополнительные параметры, которые могут понадобиться для генерации.
     * @param <T> - тип модели, которая наследуется от BaseModel (базовой модели).
     * @return - возвращает сгенерированный объект.
     *
     * Как работает метод:
     * - Он создает новый объект класса, который передается в generatorClass.
     * - Проверяет все поля этого объекта и, если поле отмечено аннотацией @Optional, то оно пропускается.
     * - Если поле отмечено @Parameterizable и есть параметры, то эти параметры используются для заполнения полей.
     * - Если поле отмечено @Random и это строка (String), то оно заполняется случайными данными (например, случайной строкой).
     * - Если поле является моделью, которая наследуется от BaseModel, то метод рекурсивно вызывает сам себя, чтобы сгенерировать эту модель.
     * - Если поле является списком (List) и его тип - тоже модель, то создается список, содержащий сгенерированную модель.
     */

    /**
     * Основной метод генерации тестовых данных.
     *
     * Если у поля аннотация Optional, оно пропускается, иначе:
     *
     * 1) если у поля аннотация Parameterizable, и в метод были переданы параметры, то поочередно (по мере встречи полей с
     *     этой аннотацией) устанавливаются переданные параметры. То есть, если по ходу генерации было пройдено 4 поля с
     *     аннотацией Parameterizable, но параметров в метод было передано 3, то значения будут установлены только у первых
     *     трех встретившихся элементов в порядке их передачи в метод. Поэтому также важно следить за порядком полей
     *     в @Data классе;
     *
     * 2) иначе, если у поля аннотация Random и это строка, оно заполняется рандомными данными;
     *
     * 3) иначе, если поле - наследник класса BaseModel, то оно генерируется, рекурсивно отправляясь в новый метод generate;
     *
     * 4) иначе, если поле - List, у которого generic type - наследник класса BaseModel, то оно устанавливается списком
     *    из одного элемента, который генерируется, рекурсивно отправляясь в новый метод generate.
     *
     * Параметр generatedModels передается, когда генерируется несколько сущностей в цикле, и содержит в себе
     * сгенерированные на предыдущих шагах сущности. Позволяет при генерации сложной сущности, которая своим полем содержит
     * другую сущность, сгенерированную на предыдущем шаге, установить ее, а не генерировать новую. Данная логика
     * применяется только для пунктов 3 и 4. Например, если был сгенерирован NewProjectDescription, то передав его
     * параметром generatedModels при генерации BuildType, он будет переиспользоваться при установке
     * поля NewProjectDescription project, вместо генерации нового.
     */
    public static <T extends BaseModel> T generate(List<BaseModel> generatedModels, Class<T> generatorClass,
                                                   Object... parameters) {
        try {
            // Создаем новый объект класса, который передается в параметре generatorClass.
            var instance = generatorClass.getDeclaredConstructor().newInstance();

            // Проходим через все поля (переменные) данного объекта.
            for (var field : generatorClass.getDeclaredFields()) {
                field.setAccessible(true); // Делаем поле доступным для изменений.

                // Проверяем, если поле не помечено аннотацией @Optional.
                if (!field.isAnnotationPresent(Optional.class)) {

                    // Пытаемся найти уже сгенерированную модель такого же типа в списке generatedModels.
                    var generatedClass = generatedModels.stream().filter(m
                            -> m.getClass().equals(field.getType())).findFirst();

                    // Если поле помечено аннотацией @Parameterizable и есть переданные параметры, заполняем поле первым параметром.
                    if (field.isAnnotationPresent(Parameterizable.class) && parameters.length > 0) {
                        field.set(instance, parameters[0]);
                        parameters = Arrays.copyOfRange(parameters, 1, parameters.length); // Убираем использованный параметр.

                        // Если поле помечено аннотацией @Random и это строка, генерируем случайную строку и устанавливаем её в поле.
                    } else if (field.isAnnotationPresent(Random.class)) {
                        if (String.class.equals(field.getType())) {
                            field.set(instance, RandomData.getString()); // Генерация случайной строки.
                        }

                        // Если поле является моделью (наследником BaseModel), рекурсивно вызываем метод generate для её создания.
                    } else if (BaseModel.class.isAssignableFrom(field.getType())) {
                        var finalParameters = parameters;
                        field.set(instance, generatedClass.orElseGet(() -> generate(
                                generatedModels, field.getType().asSubclass(BaseModel.class), finalParameters)));

                        // Если поле является списком моделей (List<BaseModel>), генерируем список из одной модели.
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        if (field.getGenericType() instanceof ParameterizedType pt) {
                            var typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                            if (BaseModel.class.isAssignableFrom(typeClass)) {
                                var finalParameters = parameters;
                                field.set(instance, generatedClass.map(List::of).orElseGet(() -> List.of(generate(
                                        generatedModels, typeClass.asSubclass(BaseModel.class), finalParameters))));
                            }
                        }
                    }
                }
                field.setAccessible(false); // Возвращаем изначальный доступ к полю.
            }
            return instance; // Возвращаем сгенерированный объект.
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            // Если произошла ошибка при генерации данных, выводим сообщение об ошибке.
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    public static TestData generate() {
        // Идем по всем полям TestData и для каждого, кто наследник BaseModel вызывыем generate() c передачей уже сгенерированных сущностей
        try {
            var instance = TestData.class.getDeclaredConstructor().newInstance();
            var generatedModels = new ArrayList<BaseModel>();
            for (var field: TestData.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (BaseModel.class.isAssignableFrom(field.getType())) {
                    var generatedModel = generate(generatedModels, field.getType().asSubclass(BaseModel.class));
                    field.set(instance, generatedModel);
                    generatedModels.add(generatedModel);
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    // Перегруженный метод для генерации одной сущности без переданных ранее моделей (generatedModels).
    public static <T extends BaseModel> T generate(Class<T> generatorClass, Object... parameters) {
        return generate(Collections.emptyList(), generatorClass, parameters); // Вызывает основной метод, передавая пустой список.
    }
}