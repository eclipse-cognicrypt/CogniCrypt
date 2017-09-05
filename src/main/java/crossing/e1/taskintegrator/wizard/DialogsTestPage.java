package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DialogsTestPage extends WizardPage {

	public DialogsTestPage() {
		super("Dialogs test page");
		setDescription("This page enables testing of dialogs by opening them on button click.");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setBounds(10, 10, 450, 200);

		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		ClaferConstraintDialog cfrConstrDialog = new ClaferConstraintDialog(parent.getShell());
		ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(parent.getShell());

		Button btn = new Button(container, SWT.PUSH);
		btn.setText("Clafer Constraint Dialog");
		btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				cfrConstrDialog.open();
			}
		});

		Button btn2 = new Button(container, SWT.PUSH);
		btn2.setText("Clafer Feature Dialog");
		btn2.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				cfrFeatureDialog.open();
			}
		});

		setControl(container);
	}

}
