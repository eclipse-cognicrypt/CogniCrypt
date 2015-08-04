package crossing.e1.featuremodel.clafer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.clafer.ast.*;
import org.clafer.collection.Triple;
import org.clafer.common.Check;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import crossing.e1.configurator.Activator;

/**
 * @author Ram
 *
 */

public class ClaferModel {

	private String modelName;
	private AstModel model;
	private AstModel modelNoCon;
	private Bundle bundle;
	private Path originPath;
	private URL bundledFileURL;
	Triple<AstModel, Scope, Objective[]> pair;
	private Map<String, AstConcreteClafer> constraintClafers;
	private ParseClafer pClafer = new ParseClafer();

	public ClaferModel(String path) {
		path = "src/main/resources/test.js";
		// path = "PBE.js";
		loadModel("test.js");
	}

	// temporarily hard coding model file
	private void loadModel(String path) {
		pair = null;
		try {
			bundle = Platform.getBundle(Activator.PLUGIN_ID);
			if (bundle == null) {
				System.out.println("path: " + path);
				File filename = new File(ClassLoader.getSystemResource(path)
						.getFile());
				// running as application
				pair = Javascript.readModel(filename, Javascript.newEngine());
			} else {
				// running as plugin
				originPath = new Path(path);

				bundledFileURL = FileLocator.find(bundle, originPath, null);

				bundledFileURL = FileLocator.resolve(bundledFileURL);
				pair = Javascript.readModel(new File(bundledFileURL.getFile()),
						Javascript.newEngine());
			}
			this.setModelName("hashings");

			setModel(pair.getFst());
			setModelNoCon(model);
			setTaskList(model);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setModel(AstModel model) {
		this.model = model;
	}

	public List<AstConcreteClafer> getClafersByType(String type) {
		return model.getChildren().stream()
				.filter(child -> child.getSuperClafer().getName().equals(type))
				.collect(Collectors.toList());
	}

	public List<AstConstraint> getConstraints() {
		return getConstraints(model.getChildren());
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
		return StringLableMapper.getTaskLables();
	}

	void setTaskList(AstModel model) {
		for (AstAbstractClafer object : model.getAbstracts()) {
			if (object.getName().contains("Task") == true) {
				for (AstClafer clafers : object.getSubs()) {
					StringLableMapper.getTaskLables().put(clafers.getName(),
							(AstConcreteClafer) clafers);
				}
			}
		}
	}

	// public int addConstraint(AstConcreteClafer name, AstBoolExpr constraint,
	// ClaferModel model) {
	// try {
	// model.getChild(name.getName()).addConstraint(constraint);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return 1;
	// }
	// return 0;
	// }

	public List<AstConcreteClafer> getClafersByName(String type) {

		return model.getChildren().stream()
				.filter(child -> child.getName().contains(type))
				.collect(Collectors.toList());
	}

	public AstConcreteClafer getClafersByParent(String type) {

		for (AstConcreteClafer ast : model.getChildren()) {
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
		for (AstClafer chil : AstUtil.getClafers(model)) {
			if (chil.getName().contains(name)) {
				return chil;
			}
		}
		return null;
	}

	public AstModel getModel() {
		return this.model;
	}

	/*
	 * This method is used to retrive the scope and objectives from compiles
	 * javaScript file This method will return collection if succeeded ,null
	 * otherwise
	 */
	public Triple<AstModel, Scope, Objective[]> getTriple() {
		return Check.notNull(pair);
	}

	public AstModel getModelNoCon() {
		return modelNoCon;
	}

	public void setModelNoCon(AstModel modelNoCon) {
		this.modelNoCon = modelNoCon;
	}

	public Map<String, AstConcreteClafer> getConstraintClafers() {
		return Check.notNull(constraintClafers);
	}

	/**
	 * @param astConcreteClafer
	 */
	public void getPrimitive(AstConcreteClafer astConcreteClafer) {
		pClafer.getPrimitive(astConcreteClafer);

	}

}
