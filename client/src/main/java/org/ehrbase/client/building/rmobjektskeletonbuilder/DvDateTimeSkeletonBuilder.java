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

import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.schemas.v1.CDATETIME;

public class DvDateTimeSkeletonBuilder implements RmObjektSkeletonBuilder<CDATETIME, DvDateTime> {
    @Override
    public Class<CDATETIME> getXmlClass() {
        return CDATETIME.class;
    }

    @Override
    public DvDateTime getRmObjekt(CDATETIME xml) {
        DvDateTime dvDateTime;
        if (xml.isSetAssumedValue()) {
            dvDateTime = new DvDateTime(xml.getAssumedValue());
        } else {
            dvDateTime = new DvDateTime();
        }
        return dvDateTime;
    }
}
