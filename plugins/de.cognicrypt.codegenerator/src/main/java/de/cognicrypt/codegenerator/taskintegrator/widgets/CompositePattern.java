package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;

public class CompositePattern extends Composite {

	protected Composite compositeOptions;
	protected ScrolledComposite compositeScrolledOptions;

	protected String patternName;

	public CompositePattern(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(2, false));

		patternName = "";

		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblName.setText("Name");

		Text txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				patternName = txtName.getText();

			}
		});
	}

	public ClaferModel getResultModel() {
		return new ClaferModel();
	}

}
