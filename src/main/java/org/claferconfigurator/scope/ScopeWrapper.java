package org.claferconfigurator.scope;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.clafer.ast.AstClafer;
import org.clafer.common.Check;
import org.clafer.scope.Scope;

public class ScopeWrapper  {

	private Map<AstClafer,Integer> scopes;
	public ScopeWrapper() {
		
	}

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
    	return scopes;
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

	public void displayScope(Scope scope) {
		System.out.println("scopes");
		for (AstClafer ast: scopes.keySet()){
			System.out.println(ast.getName()+" "+scopes.get(ast));
		}
		System.out.println("DefaultScope "+scope.getDefaultScope());
		System.out.println("Charhigh "+scope.getCharHigh());
		System.out.println("CharLow "+scope.getCharLow());
		System.out.println("IntHigh "+scope.getIntHigh());
		System.out.println("IntLow "+scope.getIntLow());
		System.out.println("MulHigh "+scope.getMulHigh());
		System.out.println("MulLow "+scope.getMulLow());
		System.out.println("StringLength "+scope.getStringLength());
		
		}

	
	}
    

