/*
 * Copyright (C) 2014 Kalin Maldzhanski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.djodjo.json.wrapper;


import org.djodjo.json.JsonElement;
import org.djodjo.json.Validator;
import org.djodjo.json.exception.JsonException;
import org.djodjo.json.schema.Schema;
import org.djodjo.json.schema.fetch.SchemaFetcher;
import org.djodjo.json.schema.fetch.SchemaLocalFetcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;


/**
 * Json element Wrapper to be used to wrap JSON data with its schema content type used to describe the data.
 *
 * It can be extended to define a data model implementing getters for required members.
 *
 * Compared to full POJO mapping it performs much faster.
 *
 */
public abstract class JsonElementWrapper implements Serializable {

    protected transient JsonElement json;
    private String contentType;
    private URI jsonSchemaUri;
    private Schema jsonSchema;
    private transient LinkedHashSet<Validator> validators = new LinkedHashSet<Validator>();
    private transient LinkedHashSet<SchemaFetcher> fetchers = new LinkedHashSet<SchemaFetcher>();


    public <T extends JsonElement> T getJson() {
        return (T)json;
    }

    public String getContentType() {
        return contentType;
    }

    public URI getJsonSchemaUri() {
        return jsonSchemaUri;
    }


    public JsonElementWrapper() {
        fetchers.add(new SchemaLocalFetcher());
    }

    public JsonElementWrapper(JsonElement jsonElement) {
        this.json = jsonElement;
    }

    public JsonElementWrapper(JsonElement jsonElement, String contentType) {
        this(jsonElement);
        this.contentType = contentType;

    }

    public JsonElementWrapper(JsonElement jsonElement, String contentType, URI jsonSchema) {
        this(jsonElement, contentType);
        this.jsonSchemaUri = jsonSchema;
        tryFetchSchema(this.jsonSchemaUri);
    }

    /**
     * Tries to fetch a schema and add the default Schema validator for it
     * @param jsonSchemaUri
     * @return
     */
    private Schema tryFetchSchema(URI jsonSchemaUri) {
        Schema jsonSchema = null;
        if(jsonSchemaUri==null) return null;
        try {
            jsonSchema = doFetchSchema(jsonSchemaUri);
            Validator validator = jsonSchema.getDefaultValidator();
            if(validator !=null) validators.add(validator);
        } catch (Exception ex) {
            return null;
        }

        return jsonSchema;
    }

    private Schema doFetchSchema(URI jsonSchemaUri) {
        Schema currSchema = null;
        Iterator<SchemaFetcher> iterator = fetchers.iterator();
        while (iterator.hasNext() && currSchema==null){
            currSchema = iterator.next().fetch(jsonSchemaUri);
        }
        return currSchema;
    }

    public <T extends JsonElementWrapper> T wrap(JsonElement jsonElement) {
        this.json = jsonElement;
        return (T)this;
    }

    public JsonElementWrapper setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public JsonElementWrapper setJsonSchema(URI uri) {
        this.jsonSchemaUri = uri;
        tryFetchSchema(this.jsonSchemaUri);
        return this;
    }

    public JsonElementWrapper addValidator(Validator validator) {
        this.validators.add(validator);
        return this;
    }

    public boolean isDataValid() {
        Iterator<Validator> iterator = validators.iterator();
        while (iterator.hasNext()){
            if (!iterator.next().isValid(this.getJson()))
                return false;
        }
        return true;
    }

    public String validateData() {
        StringBuilder sb = new StringBuilder();
        Iterator<Validator> iterator = validators.iterator();
        while (iterator.hasNext()){
            iterator.next().validate(this.getJson(), sb);
        }
        return sb.toString();
    }

    public Schema fetchJsonSchema() {
        if(jsonSchema==null)
            tryFetchSchema(this.jsonSchemaUri);
        return jsonSchema;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(json.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JsonException {
        ois.defaultReadObject();
        this.wrap(JsonElement.readFrom((String) ois.readObject()));
    }

}
