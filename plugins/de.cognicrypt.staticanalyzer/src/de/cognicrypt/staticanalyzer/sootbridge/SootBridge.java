package de.cognicrypt.staticanalyzer.sootbridge;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Registers an analysis pack with Soot, which can then be executed by calling {@link soot.Main#main(String[])}.
 * This is currently used internally within the plugin but also by the test harness.
 */
public class SootBridge {

//	private final static Logger LOGGER = LoggerFactory.getLogger(SootBridge.class);
	public static final boolean USE_MUST_ALIAS_ANALYSIS = true;
	public static final String MARKER_TYPE = "de.fraunhofer.sit.codescan.androidssl.findingmarker";
	public static final String MARKER_ATTRIBUTE_ANALYSIS_ID = "de.fraunhofer.sit.codescan.androidssl.findingmarker.analysisid";
	public static final String EXTENSION_POINT_ID = "de.fraunhofer.sit.codescan.framework.analysis";
	public static final String SOOT_ARGS = "-keep-line-number -f none -p cg all-reachable:true -no-bodies-for-excluded -w -pp";

	
	
	private static final Set<String> PRIMITIVE_TYPE_NAMES;
	private Map<AnalysisConfiguration, Set<ErrorMarker>> result;
	static {
		PRIMITIVE_TYPE_NAMES = new HashSet<String>();
		PRIMITIVE_TYPE_NAMES.add("void");
		PRIMITIVE_TYPE_NAMES.add("byte");
		PRIMITIVE_TYPE_NAMES.add("int");
		PRIMITIVE_TYPE_NAMES.add("boolean");
		PRIMITIVE_TYPE_NAMES.add("long");
		PRIMITIVE_TYPE_NAMES.add("short");
		PRIMITIVE_TYPE_NAMES.add("float");
		PRIMITIVE_TYPE_NAMES.add("double");
	}

	public static Map<AnalysisConfiguration, Set<ErrorMarker>> runSootAnalysis(IJavaProject project, Map<AnalysisConfiguration, Set<IMethod>> analysisToRelevantMethods) {
		Map<AnalysisConfiguration,Set<String>> analysisToMethodSignatures = new HashMap<AnalysisConfiguration, Set<String>>();
		for(Map.Entry<AnalysisConfiguration, Set<IMethod>> analysisAndMethods: analysisToRelevantMethods.entrySet()) {
			Set<String> signatures = new HashSet<String>(analysisAndMethods.getValue().size());
			for(IMethod m: analysisAndMethods.getValue()) {
				String sig = getSootMethodSignature(m);
				if(sig!=null)
					signatures.add(sig);
			}
			analysisToMethodSignatures.put(analysisAndMethods.getKey(), signatures);
		}
		
		Map<AnalysisConfiguration, Set<ErrorMarker>> results = SootRunner.runSoot(analysisToMethodSignatures, SOOT_ARGS, getSootClasspath(project));
		for (Entry<AnalysisConfiguration, Set<ErrorMarker>> analysisAndErrorMarkers : results.entrySet()) {
			AnalysisConfiguration analysisConfiguration = analysisAndErrorMarkers.getKey();
			Set<ErrorMarker> errorMarkers = analysisAndErrorMarkers.getValue();
			for (ErrorMarker errorMarker : errorMarkers) {
				try {
					//TODO may need to map back Soot's inner-class names to the ones used by Eclipse
					IResource erroneousFile = project.findType(errorMarker.getClassName()).getResource();
					IMarker marker = erroneousFile.createMarker(MARKER_TYPE);
					marker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					marker.setAttribute(IMarker.LINE_NUMBER, errorMarker.getLineNumber());
					marker.setAttribute(IMarker.USER_EDITABLE, false);
					marker.setAttribute(IMarker.MESSAGE, errorMarker.getErrorMessage());
					marker.setAttribute(MARKER_ATTRIBUTE_ANALYSIS_ID, analysisConfiguration.getID());
				} catch (CoreException e) {
//					LOGGER.error("ERROR while setting marker", e);
				}
			}
		}
		return results;
	}
	
	private static String getSootMethodSignature(IMethod iMethod)
	{
		try {
	        StringBuilder name = new StringBuilder();
	        name.append("<");
	        name.append(iMethod.getDeclaringType().getFullyQualifiedName());
	        name.append(": ");
	        String retTypeName = resolveName(iMethod, iMethod.getReturnType());
	        if(retTypeName==null) return null;
	        name.append(retTypeName);
	        name.append(" ");
	        name.append(iMethod.getElementName());
	        name.append("(");

	        String comma = "";
			String[] parameterTypes = iMethod.getParameterTypes();
				for (int i=0; i<iMethod.getParameterTypes().length; ++i) {
					name.append(comma);
					String readableName = resolveName(iMethod, parameterTypes[i]);
					if(readableName==null) return null;
					name.append(readableName);
	                comma = ",";
				}

	        name.append(")");
	        name.append(">");
	        
	        //workaround for this bug in Eclipse:
	        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=423358
	        //ignore inner classes for now	        
	        if(name.toString().contains("$")) {
//				LOGGER.warn("Ignoring inner type:"+name.toString());			
	        	return null;
	        }

	        return name.toString();
		} catch (JavaModelException e) {
//			LOGGER.error("Error building Soot method signature",e);			
			return null;
		}
	}

	private static String resolveName(IMethod iMethod, String simpleName) throws JavaModelException {
		String readableName = Signature.toString(simpleName);
		String arraySuffix = "";
		if(readableName.contains("[]")) {
			int arraySuffixStart = readableName.indexOf("[]");
			arraySuffix = readableName.substring(arraySuffixStart);
			readableName = readableName.substring(0,arraySuffixStart);
		}
		if(!PRIMITIVE_TYPE_NAMES.contains(readableName)) {
			String[][] fqTypes = iMethod.getDeclaringType().resolveType(readableName);
			if(fqTypes==null || fqTypes.length==0) {
//				LOGGER.debug("Failed to resolve type "+readableName+" in "+iMethod.getDeclaringType().getFullyQualifiedName());  
				return null;
			} else if(fqTypes.length>1) {
//				LOGGER.debug("Type "+readableName+" is ambiguous "+iMethod.getDeclaringType().getFullyQualifiedName()+":");
				for(int i=0;i<fqTypes.length;i++) {
//					LOGGER.debug("    "+fqTypes[i][0]+"."+fqTypes[i][1]);
				}
				return null;
			}
			String pkg = fqTypes[0][0];
			String className = fqTypes[0][1];
			readableName = pkg+"."+className;
		}
		return readableName+arraySuffix;
	}
	
	private static URL[] projectClassPath(IJavaProject javaProject) {
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IClasspathEntry[] cp;
	    try {
	            cp = javaProject.getResolvedClasspath(true);
	            List<URL> urls = new ArrayList<URL>();
	            String uriString = workspace.getRoot().getFile(
	                            javaProject.getOutputLocation()).getLocationURI().toString()
	                            + "/";
	            urls.add(new URI(uriString).toURL());
	            for (IClasspathEntry entry : cp) {
	                    File file = entry.getPath().toFile();
	                    URL url = file.toURI().toURL();
	                    urls.add(url);
	            }
	            URL[] array = new URL[urls.size()];
	            urls.toArray(array);
	            return array;
	    } catch (Exception e) {
//	    	LOGGER.error("Error building project classpath",e);
	    	return new URL[0];
	    }
	}

	private static String getSootClasspath(IJavaProject javaProject) {
	    return urlsToString(projectClassPath(javaProject));
	}

	private static String urlsToString(URL[] urls) {
	    StringBuffer cp = new StringBuffer();
	    for (URL url : urls) {
	            cp.append(url.getPath());
	            cp.append(File.pathSeparator);
	    }
	    
	    return cp.toString();
	}

}
