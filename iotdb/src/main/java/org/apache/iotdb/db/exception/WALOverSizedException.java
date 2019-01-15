/**
 * Copyright © 2019 Apache IoTDB(incubating) (dev@iotdb.apache.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.iotdb.db.exception;

import java.io.IOException;

public class WALOverSizedException extends IOException {
    private static final long serialVersionUID = -3145068900134508628L;

    public WALOverSizedException() {
        super();
    }

    public WALOverSizedException(String message) {
        super(message);
    }

    public WALOverSizedException(Throwable cause) {
        super(cause);
    }
}