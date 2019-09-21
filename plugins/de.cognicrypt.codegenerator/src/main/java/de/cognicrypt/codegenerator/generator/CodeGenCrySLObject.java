package de.cognicrypt.codegenerator.generator;

import crypto.rules.CryptSLObject;

public class CodeGenCrySLObject extends CryptSLObject{

	private final String crySLVariable;
	private String method = "";
	private int pos = -1;
	
	public CodeGenCrySLObject(String name, String type, String crySLVariable) {
		super(name, type);
		this.crySLVariable = crySLVariable;
	}
	
	public CodeGenCrySLObject(CryptSLObject obj, String crySLVariable) {
		this(obj.getVarName(), obj.getJavaType(), crySLVariable);
	}
	
	public String getMethod() {
		return this.method;
	}
	
	public int getPosition() {
		return this.pos;
	}

	public void setMethod(String methodName, int position) {
		this.method = methodName;
		this.pos = position;
	}

	public String getCrySLVariable() {
		return this.crySLVariable;
	}

}