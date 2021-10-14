package order.statemachine.design;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.cognicrypt.order.editor.statemachine.Event;
import de.cognicrypt.order.editor.statemachine.State;

/**
 * The services class used by VSM.
 */
public class Services {
    
    /**
    * See http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.sirius.doc%2Fdoc%2Findex.html&cp=24 for documentation on how to write service methods.
    */
    public EObject myService(EObject self, String arg) {
       // TODO Auto-generated code
      return self;
    }
    
    public int changeBorderFinalNode(EObject self) {
    	
    	if(self instanceof State) {
    		State s = (State) self;
    		boolean finalNode = s.isIsFinal();
    		if(finalNode) {
    			return 5;
    		}
    	}
    	
    	return 1;
    }
    
    // JavaService for a simple String output:
	public String nameService(EObject self) {
	        // TODO Auto-generated code
	    	
	       return "t";
	}
	
	public String createEventName(EObject self) {
		
		System.out.println("self: " + self); // TransitionImpl
		System.out.println("self.ecrossreferences: " + self.eCrossReferences()); // source and target states
		System.out.println("self.econtainingfeature: " + self.eContainingFeature()); // EReferenceImpl with variable name transitions
		EStructuralFeature se = self.eContainingFeature();
		// Execution stops here
		Event e = null; // kein Sinn
		System.out.println("eGet " + e.eGet(se));
		e.eSet(se, e);
		System.out.println("eGet after " + e.eGet(se));
		
		
		
		return "enew";
	}

    // JavaService debugging for the event creation:
    public Event createEvent(EObject self) {
        // TODO Auto-generated code
    	
    	System.out.println("Hello event2");
    	System.out.println(self);
    	
    	//self.eSet(type name = new type();Event);
    	Event e = null;
    	if(self.eAllContents().hasNext()) {
    		System.out.println(self.eAllContents().next());
    	}
    	else {
    		System.out.println("else");
    	}
    	System.out.println("eAllContents " + self.eAllContents()); //eAllContents is empty
    	System.out.println("econtainingFeature " + self.eContainingFeature());
    	System.out.println("econtainmentFeature " + self.eContainmentFeature());
    	System.out.println("econtainer " + self.eContainer());
		/*for(Object i:self.eAllContents()) {
    		System.out.println("loop index: " + i)
    	}*/
    	
    	System.out.println("eallattr " + self.eClass().getEAllAttributes());
    	System.out.println("feature count " + self.eClass().getFeatureCount());
    	System.out.println("eallref " + self.eClass().getEAllReferences());
    	System.out.println("eallstructfeature " + self.eClass().getEAllStructuralFeatures());
    	
    	//gets the event object
    	EReference eventref = self.eClass().getEAllReferences().get(1);
    	System.out.println("eventref " + eventref);
    	eventref.setName("new");
    	System.out.println("eventref " + eventref);
    	System.out.println("eventref get " + eventref.eGet(eventref)); //returns true
    	self.eSet(eventref, "ds");
    	System.out.println("klapp");
    	
    	System.out.println("Hello event");
    	
       return e;
     }

}
