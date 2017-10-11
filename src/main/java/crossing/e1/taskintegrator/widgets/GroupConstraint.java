package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import crossing.e1.taskintegrator.models.ClaferConstraint;

public class GroupConstraint extends Group {

	private ClaferConstraint constraint;
	private Text txtForFeatureConstraints;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param showRemoveButton TODO
	 */
	public GroupConstraint(Composite parent, int style, ClaferConstraint constraint, boolean showRemoveButton) {
		
		super(parent, SWT.BORDER);
		// Set the model for use first.
		this.setConstraint(constraint);
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		txtForFeatureConstraints = new Text(this, SWT.BORDER);
		txtForFeatureConstraints.setEditable(false);
		txtForFeatureConstraints.setText(constraint.getConstraint());
		txtForFeatureConstraints.setSize(200, 30);

		// TODO add useful listener here
		txtForFeatureConstraints.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
				constraint.setConstraint(txtForFeatureConstraints.getText());
				super.focusLost(e);
			}
		});

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

	public ClaferConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(ClaferConstraint constraint) {
		this.constraint = constraint;
	}

}
