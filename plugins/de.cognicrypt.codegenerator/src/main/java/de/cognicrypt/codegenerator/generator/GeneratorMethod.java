package de.cognicrypt.codegenerator.generator;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class GeneratorMethod {

	private String modifier;
	private String returnType;
	private String name;
	private List<Entry<String, String>> parameters;
	private Set<String> exceptions;
	private StringBuilder body;
	private List<Entry<String, String>> variableDeclarations;
	private StringBuilder killStatements;

	public GeneratorMethod() {
		body = new StringBuilder();
		variableDeclarations = new ArrayList<Entry<String, String>>();
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
			variableDeclarations.add(new SimpleEntry<>(varDecl[1], varDecl[0]));
		}
		body.append("\n");
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
		method.append(body);
		method.append("\n}");
		if (killStatements != null) {
			return method.toString().replace("return ", killStatements.toString() + "\n return ");
		} else {
			return method.toString();
		}
	}

}