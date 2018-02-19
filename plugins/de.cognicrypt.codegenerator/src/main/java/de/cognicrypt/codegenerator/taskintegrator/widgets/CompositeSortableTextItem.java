package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class CompositeSortableTextItem extends Composite {

	private Text txtOption;

	public CompositeSortableTextItem(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(4, false));

		txtOption = new Text(this, SWT.BORDER);
		txtOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btnUp = new Button(this, SWT.NONE);
		btnUp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnUp.setText("up");
		btnUp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (getParent().getParent().getParent() instanceof CompositePatternOrderedEnum) {
					CompositePatternOrderedEnum compositePatternOrderedEnum = (CompositePatternOrderedEnum) getParent().getParent().getParent();
					compositePatternOrderedEnum.moveUp(CompositeSortableTextItem.this);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button btnDown = new Button(this, SWT.NONE);
		btnDown.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnDown.setText("down");
		btnDown.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (getParent().getParent().getParent() instanceof CompositePatternOrderedEnum) {
					CompositePatternOrderedEnum compositePatternOrderedEnum = (CompositePatternOrderedEnum) getParent().getParent().getParent();
					compositePatternOrderedEnum.moveDown(CompositeSortableTextItem.this);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnRemove.setText("Remove");
		btnRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (getParent().getParent().getParent() instanceof CompositePatternOrderedEnum) {
					CompositePatternOrderedEnum compositePatternOrderedEnum = (CompositePatternOrderedEnum) getParent().getParent().getParent();
					compositePatternOrderedEnum.remove(CompositeSortableTextItem.this);
				}

				super.widgetSelected(e);
			}
		});
	}

	public String getText() {
		return txtOption.getText();
	}

	public void setText(String text) {
		txtOption.setText(text);
	}

}
