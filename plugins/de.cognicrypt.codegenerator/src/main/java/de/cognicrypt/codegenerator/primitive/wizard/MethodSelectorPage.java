package de.cognicrypt.codegenerator.primitive.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MethodSelectorPage extends WizardPage {

	private Label question;

	public MethodSelectorPage(String pageName) {
		super(pageName);
		setTitle("Methods Selector");
		setDescription("Getting methods-related algorithm");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		question = new Label(container, SWT.NULL);
		question.setBounds(39, 33, 214, 21);
		question.setText("Please select the encryption method");

		Combo combo = new Combo(container, SWT.NONE);
		combo.setBounds(39, 60, 188, 23);

		Label label = new Label(container, SWT.NONE);
		label.setText("Please select the decryption method");
		label.setBounds(39, 121, 214, 21);

		Combo combo_1 = new Combo(container, SWT.NONE);
		combo_1.setBounds(39, 148, 188, 23);

	}
}
