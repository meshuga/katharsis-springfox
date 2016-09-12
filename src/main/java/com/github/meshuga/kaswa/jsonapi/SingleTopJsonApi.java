package com.github.meshuga.kaswa.jsonapi;

import io.katharsis.response.LinksInformation;
import io.katharsis.response.MetaInformation;

import java.util.Collection;

public class SingleTopJsonApi<T> {
    private T data;
    private MetaInformation meta;
    private LinksInformation links;
    private Collection<JsonApiResource> included;

    public T getData() {
        return data;
    }

    public SingleTopJsonApi setData(T data) {
        this.data = data;
        return this;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public SingleTopJsonApi setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    public LinksInformation getLinks() {
        return links;
    }

    public SingleTopJsonApi setLinks(LinksInformation links) {
        this.links = links;
        return this;
    }

    public Collection<JsonApiResource> getIncluded() {
        return included;
    }

    public SingleTopJsonApi setIncluded(Collection<JsonApiResource> included) {
        this.included = included;
        return this;
    }
}
