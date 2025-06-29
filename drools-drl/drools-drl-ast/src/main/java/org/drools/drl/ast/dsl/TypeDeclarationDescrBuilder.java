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
package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.TypeDeclarationDescr;

/**
 *  A descriptor builder for declared types
 */
public interface TypeDeclarationDescrBuilder
    extends
    AnnotatedDescrBuilder<TypeDeclarationDescrBuilder>,
    AbstractClassTypeDeclarationBuilder<TypeDeclarationDescr> {


    /**
     * Defines the type name
     *
     * @param type the type name
     *
     * @return itself
     */
    public TypeDeclarationDescrBuilder name( String type );

    /**
     * Defines the super type of this type. For POJOs, this is the
     * super class it will extend
     * 
     * @param type the super type for this type
     * 
     * @return itself
     */
    public TypeDeclarationDescrBuilder superType( String type );

    public TypeDeclarationDescrBuilder setTrait( boolean trait );
}
