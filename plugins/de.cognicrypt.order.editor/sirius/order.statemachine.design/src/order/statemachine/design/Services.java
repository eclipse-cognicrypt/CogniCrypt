package order.statemachine.design;

import org.eclipse.emf.ecore.EObject;
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
}
