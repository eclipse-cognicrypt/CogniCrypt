package de.cognicrypt.order.editor.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.common.types.access.impl.ClasspathTypeProvider;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

import de.cognicrypt.order.editor.Activator;
import de.cognicrypt.order.editor.Constants;
import de.cognicrypt.order.editor.config.CryslFile;
import de.cognicrypt.order.editor.statemachine.Statemachine;
import de.cognicrypt.order.editor.statemachine.StatemachineFactory;
import de.cognicrypt.order.editor.statemachine.StatemachinePackage;
import de.darmstadt.tu.crossing.CrySLStandaloneSetup;
import de.cognicrypt.order.editor.StatemachineStandaloneSetup;
import de.darmstadt.tu.crossing.crySL.Domainmodel;
import de.darmstadt.tu.crossing.crySL.Expression;
import de.darmstadt.tu.crossing.crySL.SuperType;
import de.cognicrypt.order.editor.statemachine.Event;
import de.cognicrypt.order.editor.statemachine.State;
import de.cognicrypt.order.editor.statemachine.Transition;
import de.cognicrypt.order.editor.statemachine.StateMachineGraph;
import de.cognicrypt.order.editor.statemachine.StateMachineGraphBuilder;
import de.cognicrypt.order.editor.statemachine.StateNode;
import de.cognicrypt.order.editor.statemachine.TransitionEdge;

public class StatemachineParser {

	public static void generate(List<CryslFile> cryslFileList) {
    	HashMap<String, EObject> selfs = new HashMap<String, EObject>();
    	for(CryslFile f : cryslFileList) {
    		
    		try {
    			EObject self = provideCrySLEObject(f.getPath());
    			generateStatemachineXtextResource(self, f.getRuleName());
    		} catch (MalformedURLException e2) {
    			e2.printStackTrace();
    		}
    	}
    }
    
    public static void generateStatemachineXtextResource(EObject self, String ruleName) {
		final Domainmodel dm = (Domainmodel) self;
		Expression order = dm.getOrder();
    	
    	StateMachineGraph smgb = new StateMachineGraphBuilder(order).buildSMG();
    	Set<StateNode> stateNodes = smgb.getNodes();
    	java.util.List<TransitionEdge> transitionEdges = smgb.getEdges();
    	java.util.List<de.darmstadt.tu.crossing.crySL.Event> myTransitionEvents = new ArrayList<de.darmstadt.tu.crossing.crySL.Event>(); // only for labels as they do not provide info what is source and target
    	//java.util.List<Event> myTransitionEvents = new ArrayList<Event>(); // only for labels as they do not provide info what is source and target
    	    	
    	for(TransitionEdge e : transitionEdges) {
    		myTransitionEvents.add((de.darmstadt.tu.crossing.crySL.Event) e.getLabel());
    	}		
		
		StatemachineStandaloneSetup.doSetup(); 
		
		StatemachineStandaloneSetup stmStandaloneSetup = new StatemachineStandaloneSetup();
		final Injector injector = stmStandaloneSetup.createInjectorAndDoEMFRegistration();
		stmStandaloneSetup.register(injector);
		
		ResourceSet resourceSet = new ResourceSetImpl(); 
		StatemachinePackage.eINSTANCE.eClass();
		
		String path = Activator.PLUGIN_ID + "/output/" + ruleName + Constants.STATEMACHINE_EXTENSION;
		Resource resource = createAndAddXtextResourcePlatformPluginURI(path, resourceSet);
		
		resourceSet.getResources().add(resource);
		
		Statemachine statemachine = StatemachineFactory.eINSTANCE.createStatemachine();

		State state = null;
		Event event = null;
		//de.darmstadt.tu.crossing.statemachine.Transition transition = null;
		Transition transition = null;
		HashMap<StateNode, State> stateNodeMap = new HashMap<StateNode, State>();
		
		SuperType ev = null;
		int counter = 0;
		
		//process stateNodes separately, still unsorted
		for(StateNode s: stateNodes) {
			state = (State) StatemachineFactory.eINSTANCE.createState();
			state.setName("s" + s.getName());
			if(s.getAccepting().equals(true)) {
				state.setIsFinal(true);
			}
			else {
				state.setIsFinal(false);
			}
			statemachine.getStates().add((de.cognicrypt.order.editor.statemachine.State) state);
			counter++;
			stateNodeMap.put(s, state);
		}
		
		//process transition edges for states and transitions
		for(int i = 0; i < transitionEdges.size(); i++) {
			if(transitionEdges.get(i).getLabel() instanceof SuperType) {
	    		ev = (SuperType) transitionEdges.get(i).getLabel();
		    	event = (Event) StatemachineFactory.eINSTANCE.createEvent();
		    	// check for duplicate events to avoid same naming for different edges (causes serialization error)
		    	if(sameEvent(transitionEdges, transitionEdges.get(i))) {
		    		event.setName(((SuperType) ev).getName() + i);
				}
		    	else {
		    		event.setName(((SuperType) ev).getName());
		    	}
	    	}
			
			transition = StatemachineFactory.eINSTANCE.createTransition();
			
			transition.setName("t" + i);
			transition.setEvent(event);
			transition.setFromState(stateNodeMap.get(transitionEdges.get(i).from()));
	    	transition.setEndState(stateNodeMap.get(transitionEdges.get(i).to()));
	    	transition.getFromState().getTransitions().add(transition);
	    	statemachine.getTransitions().add(transition);
		}
		
		resource.getContents().add(statemachine);   
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
        try {
            resource.save(outputStream, SaveOptions.newBuilder().format().getOptions().toOptionsMap());
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
		
        // store in file
        java.net.URI resolvedURI = null;
        
        try {
			final Bundle bundle = Platform.getBundle(de.cognicrypt.order.editor.Activator.PLUGIN_ID);
			final URL entry = bundle.getEntry(Constants.RELATIVE_STATEMACHINE_MODELS_DIR);
			final URL resolvedURL = FileLocator.toFileURL(entry);
			if (!(resolvedURL == null)) {
				resolvedURI = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
			} else {
				resolvedURI = FileLocator.resolve(entry).toURI();
			}
		}
			catch (final IOException ex) {
				Activator.getDefault().logError(ex, Constants.ERROR_MESSAGE_NO_FILE);
			} catch (URISyntaxException ex) {
				Activator.getDefault().logError(ex);
			}
			java.net.URI fileURI = null;
			
			try {
				fileURI = new java.net.URI(resolvedURI + ruleName + Constants.STATEMACHINE_EXTENSION);
			} catch (URISyntaxException e3) {
				e3.printStackTrace();
			}
			
			File file = new File(fileURI);
			
			if(!file.exists()) {
				 try {
					 file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			 }
	         
	         try (OutputStream fileOutputStream = new FileOutputStream(file)) {
	             try {
	                 outputStream.writeTo(fileOutputStream);
	             } catch (IOException ioe) {
	                 ioe.printStackTrace();
	             } finally {
	            	 fileOutputStream.close();
	             }
	         } catch (IOException e2) {
	             e2.printStackTrace();
	         }
    }
	
	// method for checking whether events occur more frequently
	public static boolean sameEvent(List<TransitionEdge> edges, TransitionEdge e) {
		int counter = 0;
		for(TransitionEdge ed: edges) {
			if(ed != e) {
				if(ed.getLabel().equals(e.getLabel())) {
					counter++;
				}
			}
		}
		if(counter > 0) {
			return true;
		}
		return false;
	}
	
	public static XtextResource createAndAddXtextResourcePlatformPluginURI(String outputFile, ResourceSet resourceSet) {	
	    // parsing a plug-in-based path string, with an option to encode the created URI 
		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createPlatformPluginURI(outputFile, false);
	     XtextResource resource = (XtextResource) resourceSet.createResource(uri);
	     return resource;
	}		

	public static EObject provideCrySLEObject(String pathToCryslFile) throws MalformedURLException {
		// Loading model
    	CrySLStandaloneSetup crySLStandaloneSetup = new CrySLStandaloneSetup();
		final Injector injector = crySLStandaloneSetup.createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		String a = System.getProperty("java.class.path");
		String[] l = a.split(";");
		URL[] classpath = new URL[l.length];
		for (int i = 0; i < classpath.length; i++) {
			classpath[i] = new File(l[i]).toURI().toURL();
		}
		URLClassLoader ucl = new URLClassLoader(classpath);
		resourceSet.setClasspathURIContext(new URLClassLoader(classpath));
		new ClasspathTypeProvider(ucl, resourceSet, null, null);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		final Resource resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(pathToCryslFile), true);
		final EObject eObject = resource.getContents().get(0);
		return eObject;
	}
}
