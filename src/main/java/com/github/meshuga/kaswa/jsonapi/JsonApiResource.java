package com.github.meshuga.kaswa.jsonapi;

public class JsonApiResource<T> {

    private String type;
    private String id;
    private T attributes;
    private T relationships;

    public String getType() {
        return type;
    }

    public JsonApiResource setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public JsonApiResource setId(String id) {
        this.id = id;
        return this;
    }

    public T getAttributes() {
        return attributes;
    }

    public JsonApiResource setAttributes(T attributes) {
        this.attributes = attributes;
        return this;
    }

    public T getRelationships() {
        return relationships;
    }

    public JsonApiResource setRelationships(T relationships) {
        this.relationships = relationships;
        return this;
    }
}
