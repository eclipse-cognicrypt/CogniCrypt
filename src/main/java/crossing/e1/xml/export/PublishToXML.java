/**
 * 
 */
package crossing.e1.xml.export;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;

import crossing.e1.featuremodel.clafer.ParseClafer;

/**
 * @author Ram
 *
 */
public class PublishToXML {

	ParseClafer parser = new ParseClafer();
	
	public String displayInstanceValues(InstanceClafer inst, String value) {
		InstanceClafer instan = null;
		
		if(inst.hasChildren()){
			instan = (InstanceClafer)inst.getChildren()[0].getRef();
			String taskName= instan.getType().getName();
			value="<Task description=\""+parser.trim(taskName)+"\">\n";
			
			}
		else{
			value="<Task>\n";
		}
		if (instan!=null && instan.hasChildren()) {
			for (InstanceClafer in : instan.getChildren()) {
				if(!in.getType().getRef().getTargetType().isPrimitive()){
				value+="<Algorithm type=\""+parser.trim(in.getType().getRef().getTargetType().getName())+"\"> \n";
				value+=displayInstanceXML((InstanceClafer)in, "");
				value+="</Algorithm> \n";}
				else{
					value+=displayInstanceXML((InstanceClafer)in, "");
				}
			}
			
		}
		value+="</Task>";
		return value;
	}
	public String displayInstanceXML(InstanceClafer inst, String value) {
		try {
			if(inst.getType().hasRef()){
				if(getSuperClaferName(inst.getType().getRef().getTargetType()))
				System.out.println("YES => "+inst);
			}
			if (inst.hasChildren()) {
				for (InstanceClafer in : inst.getChildren()) {
					value += displayInstanceXML(in, "");
				}

			} else if (inst.hasRef()
					&& (inst.getType().isPrimitive() != true)
					&& (inst.getRef().getClass().toString().contains("Integer") == false)
					&& (inst.getRef().getClass().toString().contains("String") == false)
					&& (inst.getRef().getClass().toString().contains("Boolean") == false)) {
				value += displayInstanceXML((InstanceClafer) inst.getRef(),
						"");
			} else {
				if (inst.hasRef())
					return ("\t<"+parser.trim(inst.getType().getName()) + ">"
							+ inst.getRef().toString().replace("\"", "") +"</"+parser.trim(inst.getType().getName()) + ">\n");
				else
					return ("\t<"+parser.trim(((AstConcreteClafer)inst.getType()).getParent().getName()) + ">"
							+parser.trim(inst.getType().getName()) + "</"+parser.trim(((AstConcreteClafer)inst.getType()).getParent().getName()) + ">\n");

			}
		} catch (Exception E) {
			E.printStackTrace();
		}
		return value;
	}
boolean getSuperClaferName(AstClafer astClafer){
	if(astClafer.getSuperClafer()!=null)
		getSuperClaferName(astClafer.getSuperClafer());
	if(astClafer.getName().contains("_Algorithm"))
		return true;
	return false;
	
}
}
