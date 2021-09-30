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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GeneratorClass {

	private String packageName;
	private Set<String> imports;
	private String modifier;
	private String className;
	private List<GeneratorMethod> methods;
	private File associatedFile;
	private String header = "";
	private String classJavaDoc = "";

	public GeneratorClass() {
		imports = new HashSet<String>();
		methods = new ArrayList<GeneratorMethod>();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeneratorClass) {
			GeneratorClass cmpClass = (GeneratorClass) obj;
			return className.equals(cmpClass.getClassName()) && cmpClass.getMethods().size() == methods.size() && cmpClass.getMethods().stream()
				.allMatch(meth -> methods.contains(meth));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * className.hashCode() * methods.stream().map(Objects::hashCode).reduce(1, (a, b) -> a * b);
	}

	public void addMethod(GeneratorMethod meth) {
		methods.add(meth);
	}

	public void addImport(String imp) {
		imports.add(imp);
	}

	public void addImports(List<String> imports) {
		this.imports.addAll(imports);
	}
	
	public void addHeader(String header) {
		if (header != null) {
			this.header = header;
		}

	}

	public void addClassJavaDoc(String classJavaDoc) {
		if ( classJavaDoc != null) {
			this.classJavaDoc = classJavaDoc;
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Set<String> getImports() {
		return imports;
	}

	public List<GeneratorMethod> getMethods() {
		return methods;
	}

	//	public GeneratorMethod getUseMethod() {
	//		return methods.stream().filter(e -> e.getName().equals("use" + this.className)).findFirst().get();
	//	}

	public File getAssociatedJavaFile() {
		return associatedFile;
	}

	public void setSourceFile(File javaFile) {
		associatedFile = javaFile;
	}

	public String toString() {
		StringBuilder classContent = new StringBuilder(header + "\n" + "package ");
		classContent.append(packageName);
		classContent.append(";\n");
		for (String impo : imports) {
			classContent.append("import ");
			classContent.append(impo);
			classContent.append(";\n");
		}
		classContent.append("\n");
		classContent.append(classJavaDoc);
		classContent.append(modifier + " class " + className + " {\n");

		for (GeneratorMethod genMeth : methods) {
			classContent.append(genMeth);
			classContent.append("\n");
		}

		classContent.append("}");
		return classContent.toString();
	}

}
