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

package org.ehrbase.client.exception;

import java.util.Arrays;

public class WrongStatusCodeException extends ClientException {

    private final int actualStatusCode;
    private final int[] expectedStatusCode;

    public WrongStatusCodeException(String message, int actualStatusCode, int... expectedStatusCode) {

        super(String.format("Wrong Status code. Expected: %s. Got: %s. Error message: %s", Arrays.toString(expectedStatusCode), actualStatusCode, message));
        this.actualStatusCode = actualStatusCode;
        this.expectedStatusCode = expectedStatusCode;
    }

    public int getActualStatusCode() {
        return actualStatusCode;
    }

    public int[] getExpectedStatusCode() {
        return expectedStatusCode;
    }
}
