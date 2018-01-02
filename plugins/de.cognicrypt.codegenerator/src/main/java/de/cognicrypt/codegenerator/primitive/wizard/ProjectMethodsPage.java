package de.cognicrypt.codegenerator.primitive.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.DeveloperProject;


public class ProjectMethodsPage extends WizardPage {
private DeveloperProject project;
	
	protected ProjectMethodsPage() {
		super("Methods extraction");
		setDescription("This page helps us to get the methods from the user's project");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setBounds(10, 10, 200, 300);
		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		Combo combo=new Combo(container, SWT.NULL);
		
		
		
		
		
		
		setControl(container);

	}

}
