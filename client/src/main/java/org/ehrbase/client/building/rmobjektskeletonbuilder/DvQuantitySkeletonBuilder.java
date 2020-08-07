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

import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import org.openehr.schemas.v1.CDVQUANTITY;
import org.openehr.schemas.v1.DVQUANTITY;

public class DvQuantitySkeletonBuilder implements RmObjektSkeletonBuilder<CDVQUANTITY, DvQuantity> {
    @Override
    public Class<CDVQUANTITY> getXmlClass() {
        return CDVQUANTITY.class;
    }

    @Override
    public DvQuantity getRmObjekt(CDVQUANTITY xml) {
        DvQuantity dvQuantity = new DvQuantity();
        if (xml.isSetAssumedValue()) {
            DVQUANTITY assumedValue = xml.getAssumedValue();
            dvQuantity.setMagnitude(assumedValue.getMagnitude());
            dvQuantity.setUnits(assumedValue.getUnits());
            dvQuantity.setAccuracy(Double.parseDouble("" + assumedValue.getAccuracy()));
            dvQuantity.setAccuracyIsPercent(assumedValue.getAccuracyIsPercent());
        }
        return dvQuantity;
    }
}
