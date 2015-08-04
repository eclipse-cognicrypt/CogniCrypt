/**
 * 
 */
package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;

/**
 * @author Ram
 *
 */
public class ParseClafer {
	ArrayList<AstConcreteClafer> properties;

	public void setConstraintClafers(AstConcreteClafer claf) {
		if (claf.hasChildren())
			for (AstConcreteClafer s : claf.getChildren()) {
				properties = new ArrayList<AstConcreteClafer>();
				getPrimitive(s);
				StringLableMapper.getPropertiesLables().put(s, properties);
			}
	}

	public void getPrimitive(AstClafer inst) {

		try {
			if (inst.hasChildren()) {
				for (AstConcreteClafer in : inst.getChildren())
					getPrimitive(in);
			}
			if (inst.hasRef()) {
				if (inst.getRef().getTargetType().isPrimitive() == true
						&& (inst.getRef().getTargetType().getName()
								.contains("string") == false)) {
					properties.add((AstConcreteClafer) inst);

				} else if (inst.getRef().getTargetType().isPrimitive() == false) {
					getPrimitive(inst.getRef().getTargetType());
				}
			}
			if (inst.getSuperClafer() != null)
				getPrimitive(inst.getSuperClafer());
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	public void getPrimitive(AstAbstractClafer inst) {

		try {
			if (inst.hasChildren()) {
				for (AstConcreteClafer in : inst.getChildren())
					getPrimitive(in);
			}
			if (inst.hasRef())
				getPrimitive(inst.getRef().getTargetType());

			if (inst.getSuperClafer() != null)
				getPrimitive(inst.getSuperClafer());

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	public String trim(String value) {
		return value.substring(value.indexOf('_') + 1, value.length());
	}
}
