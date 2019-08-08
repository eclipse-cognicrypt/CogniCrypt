package de.cognicrypt.codegenerator.generator;

import crypto.rules.CryptSLObject;

public class CodeGenCrySLObject extends CryptSLObject{

	private final String method;
	private final int position;
	
	public CodeGenCrySLObject(String name, String type, String method, int position) {
		super(name, type);
		this.method = method;
		this.position = position;
	}
	
	public CodeGenCrySLObject(CryptSLObject obj, String method, int position) {
		this(obj.getVarName(), obj.getJavaType(), method, position);
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public String getMethod() {
		return this.method;
	}

}
