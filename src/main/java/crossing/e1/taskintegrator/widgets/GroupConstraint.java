package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


public class GroupConstraint extends Group {

	private String constraint;
	private Text txtForFeatureConstraints;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param showRemoveButton TODO
	 */
	public GroupConstraint(Composite parent, int style, String constraint, boolean showRemoveButton) {
		
		super(parent, SWT.BORDER);
		// Set the model for use first.
		this.setConstraint(constraint);
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		txtForFeatureConstraints = new Text(this, SWT.BORDER);
		txtForFeatureConstraints.setEditable(false);
		txtForFeatureConstraints.setText(constraint);
		txtForFeatureConstraints.setSize(200, 30);

		if (showRemoveButton) {
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText("Remove");
			btnRemove.setSize(70, 30);
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).removeFeatureConstraint(constraint);
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

}
