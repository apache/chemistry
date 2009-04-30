/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis;

/**
 * Join support enumeration.
 */
public enum JoinSupport {

    /**
     * No join support.
     */
    NO("nojoin"),

    /**
     * Support inner join.
     */
    INNER("inneronly"),

    /**
     * Support inner and outer join.
     */
    INNER_AND_OUTER("innerandouter");

    /**
     * Value.
     */
    private final String value;

    /**
     * Create a new instance of this class.
     *
     * @param value value
     */
    private JoinSupport(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
