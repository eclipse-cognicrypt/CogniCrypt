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
	AstConcreteClafer claferByName;

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
		String val = value.substring(value.indexOf('_') + 1, value.length());
		val = val.substring(0, 1).toUpperCase()
				+ val.substring(1, val.length());
		return val;
	}

	/*
	 * Method to parse the clafer to find the primitive properties abstract Task
	 * 
	 * abstract Person name -> string age -> int
	 * 
	 * Bob: Person [name="bob"] [age = 20]
	 * 
	 * Alic: Person [name = "alice"] [age = 25]
	 * 
	 * myTask: Task person -> Person
	 * 
	 * for above cfr file getClafer method will invoked with parameters
	 * (AstClafer myTask,String age) return value should be Person
	 */
	public void setClafersByName(AstClafer astClafer, String field) {

		try {
			if (astClafer.hasChildren()) {
				for (AstConcreteClafer in : astClafer.getChildren())
					setClafersByName(in, field);
			}
			if (astClafer.hasRef())
				setClafersByName(astClafer.getRef().getTargetType(), field);

			if (astClafer.getSuperClafer() != null)
				setClafersByName(astClafer.getSuperClafer(), field);

		} catch (Exception E) {
			E.printStackTrace();
		}
		if (astClafer.getName().equals(field))
			claferByName = (AstConcreteClafer) astClafer;

	}

	AstConcreteClafer getClaferByName(AstClafer astClafer, String field) {
		setClafersByName(astClafer, field);
		return claferByName;
	}

}
