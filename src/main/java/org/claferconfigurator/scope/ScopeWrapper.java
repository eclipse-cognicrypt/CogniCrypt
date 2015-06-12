package org.claferconfigurator.scope;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.clafer.ast.AstClafer;
import org.clafer.common.Check;
import org.clafer.scope.Scope;

public class ScopeWrapper  {

	private Map<AstClafer,Integer> scopes;
	public org.clafer.scope.Scope getScopeObject(Scope scope,Map<AstClafer, Integer> map) {
		
    	return new Scope(map, 1,scope.getIntLow(),scope.getIntHigh(),scope.getMulLow(),scope.getMulHigh(),1500,scope.getCharLow(),scope.getCharHigh());
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
    

