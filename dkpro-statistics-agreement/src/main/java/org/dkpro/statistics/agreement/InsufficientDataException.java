/*******************************************************************************
 * Copyright 2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
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
 ******************************************************************************/
package org.dkpro.statistics.agreement;

/**
 * Exception type for indicating missing data. The exception is raised for
 * computing agreement for empty annotation studies and for studies with
 * only one annotation category.
 */
public class InsufficientDataException extends RuntimeException {

    public InsufficientDataException() {
        super();
    }

    public InsufficientDataException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InsufficientDataException(final String message) {
        super(message);
    }

    public InsufficientDataException(final Throwable cause) {
        super(cause);
    }

}
