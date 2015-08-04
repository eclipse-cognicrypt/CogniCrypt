/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;

/**
 * @author Ram
 *
 */
public class StringLableMapper {
	private static Map<String,AstConcreteClafer> task=null;
	private static Map<AstConcreteClafer,ArrayList<AstConcreteClafer>> properties=null;

	private StringLableMapper(){
		
	}
	
	public static Map<String,AstConcreteClafer>getTaskLables(){
		if(task==null){
			task=new HashMap<String, AstConcreteClafer>();
		}
		return task;
	}
	
	public static Map<AstConcreteClafer,ArrayList<AstConcreteClafer>>getPropertiesLables(){
		if(properties==null){
			properties=new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
		}
		return properties;
	}
	public static void resetProperties(){
		properties=null;
	}
}
