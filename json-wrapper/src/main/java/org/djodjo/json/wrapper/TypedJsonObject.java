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
import org.djodjo.json.exception.JsonException;
import org.djodjo.json.util.LinkedTreeMap;

import java.util.Iterator;
import java.util.Map;


/**
 * Helper class that can be used for Json Objects containing always the same type;
 *
 * @param <T> The type
 */
public abstract class TypedJsonObject<T> extends JsonObjectWrapper implements Iterable<Map.Entry<String, T>> {

    public T getValue(String key) throws JsonException {
        return get(getJson().get(key), key);
    }


    public T optValue(String key) {
        return get(getJson().opt(key), key);
    }

    public T getValue(int pos) {
        java.util.Collection<JsonElement> var = getJson().valuesSet();
        return get(var.toArray(new JsonElement[var.size()])[pos], getKey(pos));
    }

    public String getKey(int pos) {
        try {
            return getJson().names().getString(pos);
        } catch (JsonException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <O extends TypedJsonObject<T>> O putValue(String key, T value) throws JsonException {
        getJson().put(key, to(value));
        return (O)this;
    }

    public <O extends TypedJsonObject<T>> O  putAll(Map<String, T> map) {
        for(Map.Entry<String, T> entry : map.entrySet()) {
            try {
                putValue(entry.getKey(),entry.getValue());
            } catch (JsonException e) {
                e.printStackTrace();
            }
        }
        return (O)this;
    }

    protected abstract T get(JsonElement jsonElement, String key);
    protected abstract JsonElement to(T value);

    @Override
    public Iterator<Map.Entry<String, T>> iterator() {
        final Iterator<Map.Entry<String, JsonElement>> iterator = getJson().iterator();
        return new Iterator<Map.Entry<String, T>>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map.Entry<String, T> next() {
                return new TypedObjectEntry(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public int length() {
        return getJson().length();
    }

    public Map<String, T> getEntries() {
        Map<String, T> res = new LinkedTreeMap<String, T>();
        for (Map.Entry<String, T> el : this) {
            res.put(el.getKey(), el.getValue());
        }
        return res;
    }

    final class TypedObjectEntry implements Map.Entry<String, T> {
        private final String key;
        private T value;

        public TypedObjectEntry(Map.Entry<String, JsonElement> entry) {
            this.key = entry.getKey();
            this.value = get(entry.getValue(), key);
        }

        public TypedObjectEntry(String key, T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            T old = this.value;
            this.value = value;
            return old;
        }
    }
}
