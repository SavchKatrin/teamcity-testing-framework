package com.example.teamcity.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    // Имя файла с конфигурацией, из которого будут загружаться свойства (config.properties).
    private final static String CONFIG_PROPERTIES = "config.properties";

    // Статическое поле для хранения единственного экземпляра класса (singleton).
    private static Config config;

    // Объект Properties, который хранит все загруженные свойства из файла.
    private Properties properties;

    // Приватный конструктор для предотвращения создания новых экземпляров класса (реализация паттерна Singleton).
    private Config() {
        properties = new Properties();  // Создаем новый объект Properties.
        loadProperties(CONFIG_PROPERTIES);  // Загружаем свойства из файла config.properties.
    }

    // Метод для получения единственного экземпляра класса Config (реализация Singleton).
    // Если объект config еще не создан, он будет создан при первом вызове метода.
    private static Config getConfig() {
        if (config == null) {
            config = new Config();  // Создаем новый экземпляр Config, если его еще нет.
        }
        return config;
    }

    // Метод для загрузки свойств из файла.
    // В качестве аргумента передается имя файла, откуда будут загружены свойства.
    public void loadProperties(String fileName) {
        try (InputStream stream = Config.class.getClassLoader().getResourceAsStream(fileName)) {
            // Открываем поток для чтения файла с указанным именем.
            if (stream == null) {
                // Если файл не найден, выводим ошибку в консоль.
                System.err.println("File not found " + fileName);
            }
            // Загружаем свойства из потока.
            properties.load(stream);
        } catch (IOException e) {
            // Если возникла ошибка при чтении файла, выводим сообщение об ошибке и выбрасываем исключение.
            System.err.println("Error during file reading " + fileName);
            throw new RuntimeException(e);
        }
    }

    // Метод для получения значения свойства по его ключу.
    // Использует singleton-экземпляр Config и загруженные свойства.
    public static String getProperty(String key) {
        // Возвращаем значение свойства, соответствующего переданному ключу.
        return getConfig().properties.getProperty(key);
    }
}

///Общее описание:
///
/// 	•	Класс Config используется для загрузки и работы с конфигурационными параметрами из файла config.properties.
/// 	•	Он реализует паттерн Singleton, что позволяет создать только один экземпляр класса Config и использовать его во всей программе.
///
/// Важные моменты:
///
/// 	1.	Паттерн Singleton:
/// 	•	Класс Config используется для управления конфигурацией приложения и загружает данные только один раз при первом вызове метода getConfig(). Все последующие вызовы используют уже созданный объект.
/// 	2.	Загрузка свойств:
/// 	•	Метод loadProperties() читает файл config.properties, который содержит ключи и значения, и загружает их в объект Properties.
/// 	3.	Получение значений свойств:
/// 	•	Метод getProperty(String key) возвращает значение свойства по указанному ключу. Он сначала проверяет, создан ли объект config, и если нет — создает его и загружает свойства.
///
/// Пример работы:
///
/// 	•	Когда в коде вызывается Config.getProperty("host"), программа проверяет, загружены ли свойства. Если нет — загружает их из файла и возвращает значение для ключа host из файла config.properties.
///
/// Это классический способ работы с конфигурационными файлами в Java-приложениях.