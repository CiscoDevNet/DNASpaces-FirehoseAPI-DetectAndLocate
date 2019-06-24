/*
 * Copyright (c) 2019 Cisco Systems, Inc. and/or its affiliates
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

package com.cisco.dnaspaces.exceptions;

public class FireHoseAPIException extends Exception {
    private int statusCode;

    public FireHoseAPIException() {
        super();
    }

    public FireHoseAPIException(String message) {
        super(message);
    }

    public FireHoseAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public FireHoseAPIException(Throwable cause) {
        super(cause);
    }

    protected FireHoseAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FireHoseAPIException(int statusCode) {
        super();
    }

    public FireHoseAPIException(String message, int statusCode) {
        super(message);
    }

    public FireHoseAPIException(String message, Throwable cause, int statusCode) {
        super(message, cause);
    }

    public FireHoseAPIException(Throwable cause, int statusCode) {
        super(cause);
    }

    protected FireHoseAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int statusCode) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getStatusCode() {
        return statusCode;
    }

}
