package crossing.e1.featuremodel.clafer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.*;
import org.clafer.collection.Triple;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import crossing.e1.configurator.Activator;

public class ClaferModel {

	private String modelName;
	public AstModel model;

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
			this.setModelName("hashing");
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
	
	public List<AstConstraint> getConstraints() {
				return getConstraints(model.getChildren());
	}
	
	//Method to provide list of constraints of the model
	public List<AstConstraint> getConstraints(List<AstConcreteClafer> type) {
		List<AstConstraint> constarint= new ArrayList<AstConstraint>();
		
		for (AstConcreteClafer object : type) {
			if(object.hasChildren()){
				constarint.addAll(this.getConstraints(object.getChildren()));
				 
			}
			else
				constarint.addAll(object.getConstraints());
		}
	return constarint;
	}
	
	public List<AstConcreteClafer> getChildByName(String name,List<AstConcreteClafer> type) {
		List<AstConcreteClafer> constarint= new ArrayList<AstConcreteClafer>();
		for (AstConcreteClafer object : type) {
			
			if(object.hasChildren()){
				constarint.addAll(this.getChildByName(name,object.getChildren()));
				 
			}
			else{
				if(object.getConstraints().toString().contains(name)){
					constarint.add(object);
					}
				}
		}
	return constarint;
	}
	
	
	public List<AstConcreteClafer> getClafersByName(String type) {
		
		return model.getChildren().stream()
				.filter(child -> child.getName().contains(type)).collect(Collectors.toList());
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

	public AstConcreteClafer getChild(String name){
		for(AstConcreteClafer chil : AstUtil.getConcreteClafers(model)){
			if(chil.getName().contains(name)){
				return chil;
			}
		}
	return null;
	}
	public AstModel getModel() {
		return model;
	}

	/* This method is used to retrive the scope and objectives from
	 * compiles javaScript file
	 * This method will return collection if succeeded ,null otherwise 
	 * */
	public Triple<AstModel, Scope, Objective[]> getTriple() {
		try {
			return Javascript.readModel(new File(ClassLoader
					.getSystemResource("hashing.js").getFile()), Javascript
					.newEngine());
		} catch (IOException e) {
					e.printStackTrace();
		}
		return null;
	}

}
