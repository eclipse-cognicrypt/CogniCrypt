package crossing.e1.featuremodel.clafer;

import static crossing.e1.featuremodel.clafer.ClaferModelUtils.displayProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.clafer.ast.AstRef;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
//
//import com.core.plugin.dialog.DialogWithNoButtonsAtAll;
//import com.core.plugin.dialog.TestDialogWithNoButtonsAtAll;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.configurator.wizard.ClaferUI;

public class TesCases {

	public static void main(String[] args) {
		TesCases test = new TesCases();

		// test.getInstance();
		// test.getPrimitiveList();
		test.createWizard();

	}

	void getPrimitiveList() {
		System.out.println("--Testing getClafersByType--");
		ClaferModel model = new ClaferModel(new ReadConfig().getClaferPath());
		model.getClafersByType("Main").forEach(task -> {
			System.out.println("Task: " + task);
			model.getClaferProperties(task).forEach(property -> {
				AstRef referenceType = property.getRef();
				System.out.println("property: " + property);
				displayProperties(referenceType.getTargetType());
			});
		});
		for (AstAbstractClafer clafer : model.getModel().getAbstracts()) {
			for (AstConcreteClafer childClafer : clafer.getChildren()) {
				System.out.println("name is "
						+ childClafer.getName()
						+ " card "
						+ childClafer.getCard()
						+ (childClafer.hasRef() ? "type is "
								+ childClafer.getRef().getTargetType()
								: (childClafer.hasChildren() ? " children are "
										+ childClafer.getChildren().toString()
										+ "" + childClafer.getCard() : "")));
				System.out.println();
			}
		}
		List<AstConstraint> const_ = model.getConstraints();
		AstClafer ram = const_.get(0).getContext();
		System.out.println(ram.hasRef());
	}

	void createWizard() {
		// System.out.println("--Testing Creating wizard--");
		// ClaferUI dialog = new ClaferUI(new Shell(), 5);
		// dialog.create();
		// if (dialog.open() == Window.OK) {
		// System.out.println(dialog.getFirstName());
		// System.out.println(dialog.getLastName());
		// }
		InstanceGenerator instance = new InstanceGenerator();
		String path = new ReadConfig().getClaferPath();
		ClaferModel model = new ClaferModel(path);

		instance.generateInstances(model, getMap());
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.BORDER);
//		DialogWithNoButtonsAtAll x = new DialogWithNoButtonsAtAll(shell,
//				new TestDialogWithNoButtonsAtAll(model.getConstraintClafers(),
//						model.getModelName()));
//		DialogWithNoButtonsAtAll.setTitle("Hello");
//		x.open();
	}

	Map<String, Integer> getMap() {
		Map<String, Integer> filters = new HashMap<String, Integer>();
		filters.put("performance", 3);
		filters.put("keyLength", 256);
		return filters;
	}

	void getInstance() {
		System.out.println("-- Testing instance Generator method--");
		InstanceGenerator instance = new InstanceGenerator();
		String path = new ReadConfig().getClaferPath();
		ClaferModel model = new ClaferModel(path);

		instance.generateInstances(model, getMap());
		System.out.println("There are " + instance.getNoOfInstances()
				+ " instances");
		// instance.displayInstances();
	}

}
