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
package crossing.e1.featuremodel.clafer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstModel;
import org.clafer.ast.AstUtil;
import org.clafer.collection.Triple;
import org.clafer.common.Check;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class ClaferModel {

	private String modelName;
	Triple<AstModel, Scope, Objective[]> triple;
	private Map<String, AstConcreteClafer> constraintClafers;
	//private ParseClafer pClafer = new ParseClafer();
	ArrayList<AstConcreteClafer> properties;

	public ClaferModel(String path) {
		loadModel(path);
		properties = new ArrayList<AstConcreteClafer>();
	}

	public Scope getScope(){
		return triple.getSnd();
	}

	private void loadModel(String path) {
		try {
			File filename = new File(path);

			triple = Javascript.readModel(filename, Javascript.newEngine());

			this.setModelName("Cyrptography Task Configurator");
			setTaskList(triple.getFst());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<AstConcreteClafer> getClafersByType(String type) {
		return triple.getFst().getChildren().stream()
				.filter(child -> child.getSuperClafer().getName().equals(type))
				.collect(Collectors.toList());
	}

	public List<AstConstraint> getConstraints() {
		return getConstraints(triple.getFst().getChildren());
	}

	// Method to provide list of constraints of the model
	public List<AstConstraint> getConstraints(List<AstConcreteClafer> type) {
		List<AstConstraint> constarint = new ArrayList<AstConstraint>();

		for (AstConcreteClafer object : type) {
			if (object.hasChildren()) {
				constarint.addAll(this.getConstraints(object.getChildren()));

			} else
				constarint.addAll(object.getConstraints());
		}
		return constarint;
	}

	public List<AstConcreteClafer> getChildByName(String name,
			List<AstConcreteClafer> type) {
		List<AstConcreteClafer> children = new ArrayList<AstConcreteClafer>();
		for (AstConcreteClafer object : type) {

			if (object.hasChildren()) {
				children.addAll(this.getChildByName(name, object.getChildren()));

			} else {
				if (object.getName().toString().contains(name)) {
					children.add(object);
				}
			}
		}
		return children;
	}

	public Map<String, AstConcreteClafer> getTaskList(AstModel model) {
		return Check.notNull(StringLabelMapper.getTaskLabels());
	}

	void setTaskList(AstModel model) {
		String key = "";
		for (AstAbstractClafer object : model.getAbstracts()) {
			if (object.getName().contains("Task") == true) {
				for (AstClafer clafer : object.getSubs()) {
					for (AstConstraint constraint : clafer.getConstraints()) {
						if (constraint.getExpr().toString()
								.contains("description . ref")) {
							key = constraint
									.getExpr()
									.toString()
									.substring(
											constraint.getExpr().toString()
													.indexOf("=") + 1,
											constraint.getExpr().toString()
													.length()).trim().replace("\"", "");
						}
					}
					System.out.println(clafer.getConstraints().get(0).getExpr()
							+ " EXPR");// .toString()
					StringLabelMapper.getTaskLabels().put(key,
							(AstConcreteClafer) clafer);

				}
			}
		}
	}

	public List<AstConcreteClafer> getClafersByName(String type) {

		return triple.getFst().getChildren().stream()
				.filter(child -> child.getName().contains(type))
				.collect(Collectors.toList());
	}

	public AstConcreteClafer getClafersByParent(String type) {

		for (AstConcreteClafer ast : triple.getFst().getChildren()) {
			if (ast.getSuperClafer().getName().contains(type))
				return ast;
		}
		return null;
	}

	public List<AstConcreteClafer> getClaferProperties(AstConcreteClafer clafer) {
		return clafer.getChildren();
	}

	public void setModelName(String modelName) {

		this.modelName = modelName;

	}

	public String getModelName() {
		return modelName;
	}

	public AstClafer getChild(String name) {
		for (AstClafer chil : AstUtil.getClafers(triple.getFst())) {
			if (chil.getName().contains(name)) {
				return chil;
			}
		}
		return null;
	}

	public AstModel getModel() {
		return triple.getFst();
	}

	/*
	 * This method is used to retrive the scope and objectives from compiles
	 * javaScript file This method will return collection if succeeded ,null
	 * otherwise
	 */
	public Triple<AstModel, Scope, Objective[]> getTriple() {
		return Check.notNull(triple);
	}

	public Map<String, AstConcreteClafer> getConstraintClafers() {
		return Check.notNull(constraintClafers);
	}
//
//	/**
//	 * @param astConcreteClafer
//	 */
//	public void getPrimitive(AstConcreteClafer astConcreteClafer) {
//		pClafer.getPrimitive(astConcreteClafer);
//
//	}
	
	public void addClaferProperties(AstClafer inputClafer) {
		try {
			if (inputClafer.hasChildren()) {
				if (inputClafer.getGroupCard().getLow() >= 1) {
					properties.add((AstConcreteClafer) inputClafer);
				} else
					for (AstConcreteClafer in : inputClafer.getChildren())
						addClaferProperties(in);
			}
			if (inputClafer.hasRef()) {
				if (inputClafer.getRef().getTargetType().isPrimitive() == true
						&& (inputClafer.getRef().getTargetType().getName()
								.contains("string") == false)) {
					properties.add((AstConcreteClafer) inputClafer);

				} else if (inputClafer.getRef().getTargetType().isPrimitive() == false) {
					addClaferProperties(inputClafer.getRef().getTargetType());
				}
			}
			if (inputClafer.getSuperClafer() != null)
				addClaferProperties(inputClafer.getSuperClafer());
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	public void addClaferProperties(AstAbstractClafer inputClafer) {

		try {
			if (inputClafer.hasChildren()) {
				for (AstConcreteClafer in : inputClafer.getChildren())
					addClaferProperties(in);
			}
			if (inputClafer.hasRef())
				addClaferProperties(inputClafer.getRef().getTargetType());

			if (inputClafer.getSuperClafer() != null)
				addClaferProperties(inputClafer.getSuperClafer());

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	
	public void createClaferConstraintMap(AstConcreteClafer inputClafer) {
		if (inputClafer.hasChildren())
			for (AstConcreteClafer s : inputClafer.getChildren()) {
				properties = new ArrayList<AstConcreteClafer>();
				addClaferProperties(s);
				StringLabelMapper.getPropertyLabels().put(s, properties);
			}
	}


}
