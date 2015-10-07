/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * @author Ram Kamath
 *
 */
package org.claferconfigurator.scope;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.clafer.ast.AstClafer;
import org.clafer.common.Check;
import org.clafer.scope.Scope;


public class ScopeWrapper  {

	private Map<AstClafer,Integer> scopes;
	public Scope getScopeObject(Scope scope,Map<AstClafer, Integer> map) {
		Scope scoped;
    	scoped= new Scope(map, 1,scope.getIntLow(),600,scope.getMulLow(),scope.getMulHigh(),20,scope.getCharLow(),scope.getCharHigh());
    	return scoped;
	}
    public void setScopes(Set<AstClafer> set,Scope scope){
    	this.scopes=new HashMap<AstClafer, Integer>();
    	for (AstClafer ast: set){
    		scopes.put(ast,scope.getScope(ast));
    	}
    }
    public Map<AstClafer, Integer> getScope(){
    	return Check.notNull(this.scopes);
    }
    
    public void alterScope(AstClafer clafer,Integer scope){
    	for (AstClafer ast: scopes.keySet()){
    		if(ast.getName().equals(clafer.getName())){
    			scopes.put(ast,Check.notNull(scope));
    			break;
    		}
    	} 
    }

	public Integer getScope(AstClafer clafer) {
		for (AstClafer ast: scopes.keySet()){
		if(ast.getName().equals(clafer.getName())){
			return scopes.get(ast);
		}
	}    	
	return null;
	}
}
    

