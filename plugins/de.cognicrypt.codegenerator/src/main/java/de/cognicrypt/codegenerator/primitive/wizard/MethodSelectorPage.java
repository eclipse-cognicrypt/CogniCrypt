package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * This class is responsible for displaying the methods related to the custom algorithm
 * For instance, in case of primitive of type symmetric block cipher, the required methods are encryption an decryption.  
 * 
 * @author Ahmed Ben Tahar
 */

public class MethodSelectorPage extends WizardPage {

	private Label question;
	private File javaFile;
	public MethodSelectorPage(File file) {
		super("Methods Selector");
		setTitle("Methods Selector");
		setDescription("Getting methods-related algorithm");
		this.javaFile=file;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));

		question = new Label(container, SWT.NULL);
		question.setText("Select the encryption method:");

		Combo combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblSelectTheDecryption = new Label(container, SWT.NONE);
		lblSelectTheDecryption.setText("Select the decryption method:");
		
				Combo combo_1 = new Combo(container, SWT.NONE);
				combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				combo_1.add(this.javaFile.getName());
	
		
	}
}
