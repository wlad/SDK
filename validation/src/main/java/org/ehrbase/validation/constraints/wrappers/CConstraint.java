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

package org.ehrbase.validation.constraints.wrappers;

import java.util.Map;

/**
 * Abstract class for all constraints
 *
 * @link Created by christian on 8/10/2016.
 */
public abstract class CConstraint {

    protected Map<String, Map<String, String>> localTerminologyLookup;

    protected CConstraint(Map<String, Map<String, String>> localTerminologyLookup) {
        this.localTerminologyLookup = localTerminologyLookup;
    }

}
