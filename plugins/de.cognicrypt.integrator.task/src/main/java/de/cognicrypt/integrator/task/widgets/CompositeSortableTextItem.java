/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CompositeSortableTextItem extends Composite {

	private final Label lblPosition;
	private final Text txtOption;
	private Button btnUp;
	private Button btnDown;
	private final ControlDecoration decorationOption;

	/**
	 * instantiate the composite with a text box, a remove button and optional move up and down buttons
	 *
	 * @param parent parent {@link Composite}
	 * @param sortable <code>true</code> if up and down buttons should be shown, <code>false</code> otherwise
	 */
	public CompositeSortableTextItem(final Composite parent, final boolean sortable) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(5, false));

		// label and text box

		this.lblPosition = new Label(this, SWT.NONE);
		this.lblPosition.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		this.decorationOption = new ControlDecoration(this.lblPosition, SWT.RIGHT | SWT.TOP);

		this.txtOption = new Text(this, SWT.BORDER);
		this.txtOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.txtOption.addModifyListener(arg0 -> ((CompositePattern) getParent().getParent().getParent()).notifyListeners(SWT.Selection, null));

		// move buttons

		if (sortable) {

			this.btnUp = new Button(this, SWT.NONE);
			this.btnUp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			this.btnUp.setText("up");
			this.btnUp.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
						final CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
						compositePatternOrderedEnum.moveUp(CompositeSortableTextItem.this);
					}
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

			this.btnDown = new Button(this, SWT.NONE);
			this.btnDown.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			this.btnDown.setText("down");
			this.btnDown.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
						final CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
						compositePatternOrderedEnum.moveDown(CompositeSortableTextItem.this);
					}
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

		}

		// remove button

		final Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnRemove.setText("Remove");
		btnRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
					final CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
					compositePatternOrderedEnum.remove(CompositeSortableTextItem.this);
				}

				super.widgetSelected(e);
			}
		});
	}

	public String getText() {
		return this.txtOption.getText();
	}

	public void setText(final String text) {
		this.txtOption.setText(text);
	}

	public void setPosition(final int position) {
		this.lblPosition.setText(String.valueOf(position));
		layout();
	}

	/**
	 * enable both the move up and the move down button
	 */
	public void setMoveButtonsEnabled() {
		setMoveButtonsEnabled(true, true);
	}

	/**
	 * enable or disable the buttons move up and move down according to boolean flags
	 *
	 * @param upEnabled desired state of the button move up
	 * @param downEnabled desired state of the button move down
	 */
	public void setMoveButtonsEnabled(final boolean upEnabled, final boolean downEnabled) {
		if (this.btnUp != null && this.btnDown != null) {
			this.btnUp.setEnabled(upEnabled);
			this.btnDown.setEnabled(downEnabled);
		}
	}

	public void showValidationError(final Image image, final String descriptionText) {
		this.decorationOption.setImage(image);
		this.decorationOption.setDescriptionText(descriptionText);
		this.decorationOption.show();
	}

	public void hideValidationError() {
		this.decorationOption.hide();
	}

}
