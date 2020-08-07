/*
 * Modifications copyright (C) 2019 Christian Chevalley, Vitasystems GmbH and Hannover Medical School

 * This file is part of Project EHRbase

 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.serialisation.dbencoding.wrappers.json.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.support.identification.TerminologyId;
import org.ehrbase.serialisation.dbencoding.wrappers.json.I_DvTypeAdapter;
import org.ehrbase.serialisation.util.ObjectSnakeCase;

import java.io.IOException;

/**
 * GSON adapter for DvDateTime
 * Required since JSON does not support natively a DateTime data type
 */
public class CodePhraseAdapter extends DvTypeAdapter<CodePhrase> {

    private Gson gson;

    public CodePhraseAdapter(I_DvTypeAdapter.AdapterType adapterType) {
        super(adapterType);
        gson = new GsonBuilder()
                .registerTypeAdapter(TerminologyId.class, new TerminologyIDAdapter(adapterType))
                .setPrettyPrinting()
                .create();
    }

    public CodePhraseAdapter() {
    }

    @Override
    public CodePhrase read(JsonReader arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void write(JsonWriter writer, CodePhrase codePhrase) throws IOException {
        if (codePhrase == null) {
            writer.nullValue();
            return;
        }

        if (adapterType == I_DvTypeAdapter.AdapterType.PG_JSONB) {
            writer.beginObject();
            writer.name("codeString").value(codePhrase.getCodeString());
            writer.name("terminologyId");
            writer.beginObject();
            writer.name("name").value(codePhrase.getTerminologyId().getValue());
            writer.name("value").value(codePhrase.getTerminologyId().getValue());
            writer.endObject();
            writer.endObject();
        } else if (adapterType == I_DvTypeAdapter.AdapterType.RAW_JSON) {
            writer.beginObject(); //{
            writer.name(I_DvTypeAdapter.TAG_CLASS_RAW_JSON).value(new ObjectSnakeCase(codePhrase).camelToUpperSnake());
            writer.name("code_string").value(codePhrase.getCodeString());
            writer.name("terminology_id").value(gson.toJson(codePhrase.getTerminologyId()));
            writer.endObject(); //}
        }

    }

}
