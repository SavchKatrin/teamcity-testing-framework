package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

public class Request {
    /**
     * Request - это класс, описывающий меняющиеся параметры запроса, такие как:
     *  спецификация, эндпоинт (relative URL, model)
     */
    private final RequestSpecification spec;
    private final Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }
}
