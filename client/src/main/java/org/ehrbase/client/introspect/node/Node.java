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

package org.ehrbase.client.introspect.node;

import static org.ehrbase.client.introspect.TemplateIntrospect.TERM_DIVIDER;

public interface Node {

    String getName();


    default String getFirstName() {
        if (getName() == null) {
            return null;
        }
        String[] split = getName().split(TERM_DIVIDER);
        return split[split.length - 1];
    }
}
