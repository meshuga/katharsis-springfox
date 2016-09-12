package com.github.meshuga.kaswa.jsonapi;

import io.katharsis.response.LinksInformation;
import io.katharsis.response.MetaInformation;

import java.util.Collection;
import java.util.List;

public class ManyTopJsonApi<T> {
    private List<T> data;
    private MetaInformation meta;
    private LinksInformation links;
    private Collection<JsonApiResource> included;

    public List<T> getData() {
        return data;
    }

    public ManyTopJsonApi setData(List<T> data) {
        this.data = data;
        return this;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public ManyTopJsonApi setMeta(MetaInformation meta) {
        this.meta = meta;
        return this;
    }

    public LinksInformation getLinks() {
        return links;
    }

    public ManyTopJsonApi setLinks(LinksInformation links) {
        this.links = links;
        return this;
    }

    public Collection<JsonApiResource> getIncluded() {
        return included;
    }

    public ManyTopJsonApi setIncluded(Collection<JsonApiResource> included) {
        this.included = included;
        return this;
    }
}
