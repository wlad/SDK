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

package org.ehrbase.client.building.rmobjektskeletonbuilder;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.support.identification.TerminologyId;
import org.openehr.schemas.v1.CCODEPHRASE;
import org.openehr.schemas.v1.CODEPHRASE;

public class CodePhraseSkeletonBuilder implements RmObjektSkeletonBuilder<CCODEPHRASE, CodePhrase> {
    @Override
    public Class<CCODEPHRASE> getXmlClass() {
        return CCODEPHRASE.class;
    }

    @Override
    public CodePhrase getRmObjekt(CCODEPHRASE xml) {
        CodePhrase codePhrase = new CodePhrase();
        if (xml.isSetAssumedValue()) {
            CODEPHRASE assumedValue = xml.getAssumedValue();
            codePhrase.setCodeString(assumedValue.getCodeString());
            codePhrase.setTerminologyId(new TerminologyId(assumedValue.getTerminologyId().getValue()));
        }

        return codePhrase;
    }
}
