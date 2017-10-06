package de.cognicrypt.staticanalyzer.sootbridge;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import soot.BodyTransformer;
import soot.SceneTransformer;
import soot.Transformer;

public class PluggableTransformer {
	protected IConfigurationElement transformerConfigElement;

	public PluggableTransformer(IConfigurationElement config) {
		transformerConfigElement = config;
	}

	public String getPack() {
		String name = transformerConfigElement.getAttribute("packagename");
		try{
			name = name.substring(0, name.indexOf("."));
		} catch (StringIndexOutOfBoundsException ex){
			throw new RuntimeException("The packagename of the SootTransformer must contain a dot(.)!");
		}
		return name;
	}

	public String getPackageName() {
		return transformerConfigElement.getAttribute("packagename");
	}

	public Transformer getInstance() {
		try {
			if(isSceneTransformer())
				return (SceneTransformer) transformerConfigElement
						.createExecutableExtension("class");
			else
				//We cannot execute a BodyTransformer in the jb phase, why?
				throw new RuntimeException("Adding a BodyTransformer is not yet supported!");
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	public boolean executeBeforeAnalysis(){
		String executionTime = transformerConfigElement.getAttribute("executionTime");
		return executionTime.equals("beforeAnalysis");
	}
	
	private boolean isSceneTransformer(){
		String executionTime = transformerConfigElement.getAttribute("transformerType");
		return executionTime.equals("Scene");
	}
}
