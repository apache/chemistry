/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.opencmis.commons.impl.dataobjects;

import java.math.BigDecimal;

import org.apache.opencmis.commons.api.PropertyDecimalDefinition;
import org.apache.opencmis.commons.enums.DecimalPrecision;

/**
 * Decimal property definition data implementation.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class PropertyDecimalDefinitionImpl extends AbstractPropertyDefinition<BigDecimal>
    implements PropertyDecimalDefinition {

  private static final long serialVersionUID = 1L;

  private BigDecimal fMinValue;
  private BigDecimal fMaxValue;
  private DecimalPrecision fPrecision;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.PropertyDecimalDefinitionData#getMinValue()
   */
  public BigDecimal getMinValue() {
    return fMinValue;
  }

  public void setMinValue(BigDecimal minValue) {
    fMinValue = minValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.PropertyDecimalDefinitionData#getMaxValue()
   */
  public BigDecimal getMaxValue() {
    return fMaxValue;
  }

  public void setMaxValue(BigDecimal maxValue) {
    fMaxValue = maxValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.provider.PropertyDecimalDefinitionData#getPrecision()
   */
  public DecimalPrecision getPrecision() {
    return fPrecision;
  }

  public void setPrecision(DecimalPrecision precision) {
    fPrecision = precision;
  }
}
