/*
 *  Copyright (c) 2019  Stefan Spiska (Vitasystems GmbH) and Hannover Medical School
 *  This file is part of Project EHRbase
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ehrbase.client.introspect.config;

import com.nedap.archie.rm.composition.IsmTransition;
import org.ehrbase.client.terminology.TerminologyProvider;
import org.ehrbase.client.terminology.ValueSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ehrbase.client.terminology.TerminologyProvider.OPENEHR;

public class IsmTransitionIntrospectConfig implements RmIntrospectConfig {

    private static final Set<String> FIELDS = Stream.of("currentState", "transition").collect(Collectors.toSet());

    @Override
    public Class getRMClass() {
        return IsmTransition.class;
    }

    @Override
    public Set<String> getNonTemplateFields() {
        return FIELDS;
    }

    @Override
    public ValueSet findExternalValueSet(String fieldName) {
        switch (fieldName) {
            case "currentState":
                return TerminologyProvider.findOpenEhrValueSet(OPENEHR, "instruction states");
            case "transition":
                return TerminologyProvider.findOpenEhrValueSet(OPENEHR, "instruction transitions");
            default:
                return ValueSet.EMPTY_VALUE_SET;
        }
    }
}
