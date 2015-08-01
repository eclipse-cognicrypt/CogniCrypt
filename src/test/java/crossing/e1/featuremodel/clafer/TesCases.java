package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;

import crossing.e1.configurator.ReadConfig;
/**
 * @author Ram
 *
 */


public class TesCases {

	public static void main(String[] args) {
		TesCases test = new TesCases();
		String path = new ReadConfig().getClaferPath();
		ClaferModel model = new ClaferModel(path);

		InstanceGenerator inst = test.getInstance(model);
		test.displayInstances(inst);
		//test.displayConstraints(model);
		//test.displaySuperClafers(model);

	}
	private void displaySuperClafers(ClaferModel model){

		System.out.println("--Testing displaySuperClafers Method--");
		ArrayList<AstConcreteClafer> taskList=new ArrayList<AstConcreteClafer>();
		taskList= (ArrayList<AstConcreteClafer>) model.getSuperClafer(model.getModel());
		
		for( AstConcreteClafer clafer : taskList){
			System.out.println("VALUE : "+ clafer.getName());
		}
		
	}

	private void displayConstraints(ClaferModel model) {

		System.out.println("--Testing Constrainable properties--");
		for (AstConcreteClafer clafer : model.getConstraintClafers())
			System.out.println(clafer.toString());

	}

	Map<String, Integer> getMap() {
		Map<String, Integer> filters = new HashMap<String, Integer>();
		filters.put("performance", 3);
		filters.put("keyLength", 256);
		return filters;
	}

	void displayInstances(InstanceGenerator instance) {
		System.out.println("----Diplaying instances----");
		for (String b : instance.getInstances().keySet())
			System.out.println(instance.displayInstanceValues(instance
					.getInstances().get(b), ""));
	}

	InstanceGenerator getInstance(ClaferModel model) {
		System.out.println("-- Testing instance Generator method--");
		InstanceGenerator instance = new InstanceGenerator();
		;
		instance.generateInstances(model, getMap());
		System.out.println("There are " + instance.getNoOfInstances()
				+ " instances");
		return instance;
		// instance.displayInstances();
	}

}
