package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;


public class GroupModifyDeleteButtons extends Group {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupModifyDeleteButtons(Composite parent) {
		super(parent, SWT.RIGHT_TO_LEFT);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		setLayout(rowLayout);
		
		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnDelete.setText("Delete");
		
		Button btnModify = new Button(this, SWT.NONE);
		btnModify.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnModify.setText("Modify");

		this.setSize(SWT.DEFAULT, 40);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
