/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

	private Label lblPosition;
	private Text txtOption;
	private Button btnUp;
	private Button btnDown;
	private ControlDecoration decorationOption;

	/**
	 * instantiate the composite with a text box, a remove button and optional move up and down buttons
	 * 
	 * @param parent
	 *        parent {@link Composite}
	 * @param sortable
	 *        <code>true</code> if up and down buttons should be shown, <code>false</code> otherwise
	 */
	public CompositeSortableTextItem(Composite parent, boolean sortable) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(5, false));

		// label and text box

		lblPosition = new Label(this, SWT.NONE);
		lblPosition.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		decorationOption = new ControlDecoration(lblPosition, SWT.RIGHT | SWT.TOP);

		txtOption = new Text(this, SWT.BORDER);
		txtOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtOption.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				((CompositePattern) getParent().getParent().getParent()).notifyListeners(SWT.Selection, null);

			}
		});

		// move buttons

		if (sortable) {

			btnUp = new Button(this, SWT.NONE);
			btnUp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			btnUp.setText("up");
			btnUp.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
						CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
						compositePatternOrderedEnum.moveUp(CompositeSortableTextItem.this);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

			btnDown = new Button(this, SWT.NONE);
			btnDown.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			btnDown.setText("down");
			btnDown.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
						CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
						compositePatternOrderedEnum.moveDown(CompositeSortableTextItem.this);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

		}

		// remove button

		Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnRemove.setText("Remove");
		btnRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (getParent().getParent().getParent() instanceof CompositePatternEnum) {
					CompositePatternEnum compositePatternOrderedEnum = (CompositePatternEnum) getParent().getParent().getParent();
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

	public void setPosition(int position) {
		lblPosition.setText(String.valueOf(position));
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
	 * @param upEnabled
	 *        desired state of the button move up
	 * @param downEnabled
	 *        desired state of the button move down
	 */
	public void setMoveButtonsEnabled(boolean upEnabled, boolean downEnabled) {
		if (btnUp != null && btnDown != null) {
			btnUp.setEnabled(upEnabled);
			btnDown.setEnabled(downEnabled);
		}
	}

	public void showValidationError(Image image, String descriptionText) {
		decorationOption.setImage(image);
		decorationOption.setDescriptionText(descriptionText);
		decorationOption.show();
	}

	public void hideValidationError() {
		decorationOption.hide();
	}

}
