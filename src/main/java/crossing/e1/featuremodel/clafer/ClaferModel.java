package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.newModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.*;
import org.clafer.collection.Triple;
import org.clafer.javascript.Javascript;
import org.clafer.javascript.JavascriptShell;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import crossing.e1.configurator.Activator;

public class ClaferModel {

	private String modelName;
	private AstModel model;

	public ClaferModel() {
		loadModel();
	}

	// temporarily hard coding model file
	private void loadModel() {
		Triple<AstModel, Scope, Objective[]> pair = null;
		try {
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			if (bundle == null) {
				// running as application
				pair = Javascript.readModel(new File(ClassLoader
						.getSystemResource("hashing.js").getFile()), Javascript
						.newEngine());
			} else {
				// running as plugin
				Path originPath = new Path("src/main/resources/hashing.js");

				URL bundledFileURL = FileLocator.find(bundle, originPath, null);

				bundledFileURL = FileLocator.resolve(bundledFileURL);
				pair = Javascript.readModel(new File(bundledFileURL.getFile()),
						Javascript.newEngine());
			}

			model = pair.getFst();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<AstConcreteClafer> getClafersByType(String type) {
		return model.getChildren().stream()
				.filter(child -> child.getSuperClafer().getName().equals(type))
				.collect(Collectors.toList());
	}
	
	public List<AstConcreteClafer> getClaferProperties(AstConcreteClafer clafer){
		return clafer.getChildren();
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}

	public AstModel getModel() {
		return model;
	}

}
