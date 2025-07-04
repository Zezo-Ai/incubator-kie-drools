/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

public abstract class BaseObjectClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseObjectClassFieldWriter() {

    }

    protected BaseObjectClassFieldWriter(final int index,
                                         final Class< ? > fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseObjectClassFieldWriter(final Class< ? > clazz,
                                      final String fieldName) {
        super( clazz,
               fieldName );
    }

    public abstract void setValue(final Object bean,
                                  final Object value);

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        setValue( bean,
                  value ? Boolean.TRUE : Boolean.FALSE );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setValue( bean, value );
    }

    public void setCharValue(final Object bean,
                             final char value) {
        setValue( bean, value );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setValue( bean, value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setValue( bean, value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setValue( bean, value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setValue( bean, value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setValue( bean, value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setValue",
                                                     Object.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
