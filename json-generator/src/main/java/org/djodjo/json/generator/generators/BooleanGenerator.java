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

package org.djodjo.json.generator.generators;

import org.djodjo.json.JsonBoolean;
import org.djodjo.json.JsonElement;
import org.djodjo.json.generator.Generator;
import org.djodjo.json.generator.GeneratorConfig;
import org.djodjo.json.schema.Schema;

public class BooleanGenerator extends Generator {

    public BooleanGenerator(Schema schema, GeneratorConfig configuration) {
        super(schema, configuration);
    }

    public BooleanGenerator(Schema schema, GeneratorConfig configuration, String propertyName) {
        super(schema, configuration, propertyName);
    }

    @Override
    public JsonElement generate() {
        return new JsonBoolean(rnd.nextBoolean());
    }
}
