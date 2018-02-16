package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompositeClaferFeedback extends Composite {

	private Label lblFeedback;

	public CompositeClaferFeedback(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		lblFeedback = new Label(this, SWT.NONE);
		lblFeedback.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void setFeedback(String feedback) {
		lblFeedback.setText(feedback);
	}

}
