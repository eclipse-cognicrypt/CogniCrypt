/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import de.cognicrypt.utils.Utils;

public class GeneratorMethod {

	private String modifier;
	private String returnType;
	private String name;
	private List<Entry<String, String>> parameters;
	private Set<String> exceptions;
	private StringBuilder body;
	private List<Entry<String, String>> variableDeclarations;
	private List<Entry<String, String>> postCGVars;
	private StringBuilder killStatements;
	private int templateVariables;
	private List<CodeGenCrySLRule> cryslRules;

	public GeneratorMethod() {
		body = new StringBuilder();
		variableDeclarations = new ArrayList<Entry<String, String>>();
		postCGVars = new ArrayList<Entry<String, String>>();
		parameters = new ArrayList<Entry<String, String>>();
		exceptions = new HashSet<String>();
	}

	@Override
	public boolean equals(Object cmp) {
		if (cmp instanceof GeneratorMethod) {
			GeneratorMethod comparee = (GeneratorMethod) cmp;
			return name.equals(comparee.getName()) && returnType.equals(comparee.getReturnType()) && modifier.equals(comparee.getModifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * name.hashCode() * returnType.hashCode() * modifier.hashCode();
	}

	public void addException(String exception) {
		this.exceptions.add(exception);
	}

	public void addExceptions(Collection<String> exceptions) {
		this.exceptions.addAll(exceptions);
	}

	public void addKillStatement(String statement) {
		if (killStatements == null) {
			killStatements = new StringBuilder();
		}
		killStatements.append(statement);
		killStatements.append("\n");
	}

	public List<Entry<String, String>> getDeclaredVariables() {
		return variableDeclarations;
	}

	public void addStatementToBody(String statement) {
		body.append(statement);
		int index = -1;
		if ((index = statement.indexOf('=')) > 0) {
			String[] varDecl = statement.substring(0, index).split(" ");
			if (varDecl.length == 2) {
				SimpleEntry<String, String> newVar = new SimpleEntry<>(varDecl[1], varDecl[0]);
				if (!variableDeclarations.contains(newVar)) {
					variableDeclarations.add(newVar);
				}
			}
		}
		body.append("\n");
	}
	
	public void addVariablesToBody(List<Entry<String, String>> variables) {
		for (Entry<String, String> var : variables) {
			String type = var.getValue();
			String name = var.getKey();
			try {
				Class.forName(type);
				String simpleType = Utils.retrieveOnlyClassName(type);
				addStatementToBody(simpleType + " " + name + " = null;");
			} catch (ClassNotFoundException e) {
				if(type.matches("\\w+\\[\\]")) {
					addStatementToBody(type + " " + name + " = null;");
				} else {
					addStatementToBody(type + " " + name + " = 0;");
				}
			}
		}
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body.toString();
	}

	public Set<String> getExceptions() {
		return exceptions;
	}

	public void addParameter(Entry<String, String> parameter) {
		parameters.add(parameter);
	}

	public List<Entry<String, String>> getParameters() {
		return parameters;
	}

	public String toString() {
		String signature = modifier + " " + returnType + " " + name + "(";
		StringBuilder method = new StringBuilder(signature);
		for (int i = 0; i < parameters.size(); i++) {
			Entry<String, String> parAtI = parameters.get(i);
			method.append(parAtI.getValue());
			method.append(" ");
			method.append(parAtI.getKey());
			if (i < parameters.size() - 1) {
				method.append(",");
			}
		}
		method.append(")");
		if (exceptions.size() > 0) {
			method.append(" throws ");
			List<String> exAsList = new ArrayList<String>(exceptions);
			for (int i = 0; i < exceptions.size(); i++) {
				method.append(exAsList.get(i));
				if (i < exceptions.size() - 1) {
					method.append(", ");
				}
			}
		}

		method.append("{ \n");
		method.append(body.toString().replaceAll(",\\s+\\)", ")"));
		method.append("\n}");
		if (killStatements != null) {
			return method.toString().replace("return ", killStatements.toString() + "\n return ");
		} else {
			return method.toString();
		}
	}

	public void clearBody() {
		this.body.setLength(0);
	}

	public void setNumberOfVariablesInTemplate(int number) {
		templateVariables = number;
	}

	public int getNumberOfVariablesInTemplate() {
		return templateVariables;
	}

	public void setRules(List<CodeGenCrySLRule> rules) {
		cryslRules = rules;
	}

	public List<CodeGenCrySLRule> getRules() {
		return cryslRules;
	}
	
	public void addPostCGVars(Entry<String, String> postCGVar) {
		postCGVars.add(postCGVar);
	}

	public List<Entry<String, String>> getPostCGVars() {
		return postCGVars;
	}
}
