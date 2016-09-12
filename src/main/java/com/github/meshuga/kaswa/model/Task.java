package com.github.meshuga.kaswa.model;

import io.katharsis.resource.annotations.JsonApiId;
import io.katharsis.resource.annotations.JsonApiResource;

@JsonApiResource(type = "tasks")
public class Task {

    @JsonApiId
    public Long id;
    public String title;

    public Task(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
