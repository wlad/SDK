/*
 *
 *  *  Copyright (c) 2020  Stefan Spiska (Vitasystems GmbH) and Hannover Medical School
 *  *  This file is part of Project EHRbase
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.ehrbase.client.classgenerator.examples.testalltypesenv1composition.definition;

import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_QUANTITY")
public class ChoiceDvquantity implements ChoiceChoice {
    @Path("|magnitude")
    private Double choiceMagnitude;

    @Path("|units")
    private String choiceUnits;

    public Double getChoiceMagnitude() {
        return this.choiceMagnitude;
    }

    public void setChoiceMagnitude(Double choiceMagnitude) {
        this.choiceMagnitude = choiceMagnitude;
    }

    public String getChoiceUnits() {
        return this.choiceUnits;
    }

    public void setChoiceUnits(String choiceUnits) {
        this.choiceUnits = choiceUnits;
    }
}
