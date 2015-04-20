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
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class ClaferModel {
	
	private String modelName;
	private AstModel model;
	
	public ClaferModel(){
		loadModel();
	}
	
	//temporarily hard coding model file
	private void loadModel(){
		try {
//			IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//			IProject myWebProject = myWorkspaceRoot.getProject("ClaferConfigurator");
//			if (myWebProject.exists() && !myWebProject.isOpen())
//			      myWebProject.open(null);
			
	
			//IFolder imagesFolder = myWebProject.getFolder("src/main/resources");
			
			//System.out.println("exists: " + imagesFolder.exists());
			Triple<AstModel, Scope, Objective[]> pair = Javascript.readModel(new File(ClassLoader.getSystemResource("hashing.js").getFile()), Javascript.newEngine());
            model = pair.getFst();
            System.out.println("Loaded " + AstUtil.getNames(AstUtil.getClafers(model)) + ".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public List<AstConcreteClafer> getClafersByType(String type){
		return model.getChildren().stream().filter(child -> child.getSuperClafer().getName().equals(type)).collect(Collectors.toList());
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
