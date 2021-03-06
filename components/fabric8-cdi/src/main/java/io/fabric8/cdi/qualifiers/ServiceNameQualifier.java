/*
 * Copyright 2005-2014 Red Hat, Inc.                                    
 *                                                                      
 * Red Hat licenses this file to you under the Apache License, version  
 * 2.0 (the "License"); you may not use this file except in compliance  
 * with the License.  You may obtain a copy of the License at           
 *                                                                      
 *    http://www.apache.org/licenses/LICENSE-2.0                        
 *                                                                      
 * Unless required by applicable law or agreed to in writing, software  
 * distributed under the License is distributed on an "AS IS" BASIS,    
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      
 * implied.  See the License for the specific language governing        
 * permissions and limitations under the License.
 */

package io.fabric8.cdi.qualifiers;

import io.fabric8.annotations.ServiceName;

import javax.enterprise.util.AnnotationLiteral;

public class ServiceNameQualifier extends AnnotationLiteral<ServiceName> implements ServiceName {

    private final String id;
    private final String protocol;

    public ServiceNameQualifier(String id, String protocol) {
        this.id = id;
        this.protocol = protocol;
    }

    @Override
    public String value() {
        return id;
    }

}