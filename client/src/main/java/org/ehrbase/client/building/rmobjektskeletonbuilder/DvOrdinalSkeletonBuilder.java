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
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.support.identification.TerminologyId;
import org.openehr.schemas.v1.CDVORDINAL;
import org.openehr.schemas.v1.DVORDINAL;

public class DvOrdinalSkeletonBuilder implements RmObjektSkeletonBuilder<CDVORDINAL, DvOrdinal> {
    @Override
    public Class<CDVORDINAL> getXmlClass() {
        return CDVORDINAL.class;
    }

    @Override
    public DvOrdinal getRmObjekt(CDVORDINAL xml) {
        DvOrdinal dvOrdinal = new DvOrdinal();
        if (xml.isSetAssumedValue()) {
            DVORDINAL assumedValue = xml.getAssumedValue();
            dvOrdinal.setValue(Long.valueOf("" + assumedValue.getValue()));
            dvOrdinal.setSymbol(
                    new DvCodedText(
                            assumedValue.getSymbol().getValue(),
                            new CodePhrase(
                                    new TerminologyId(assumedValue.getSymbol().getDefiningCode().getTerminologyId().getValue()),
                                    assumedValue.getSymbol().getDefiningCode().getCodeString()
                            )
                    )
            );
        }

        return dvOrdinal;
    }
}
