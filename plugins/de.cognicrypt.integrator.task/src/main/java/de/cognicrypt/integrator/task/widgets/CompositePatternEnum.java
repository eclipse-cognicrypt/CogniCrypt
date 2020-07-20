/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import de.cognicrypt.integrator.task.UIConstants;

public class CompositePatternEnum extends CompositePattern {

	private final boolean sortable;
	private final ArrayList<CompositeSortableTextItem> sortableTextItems;

	/**
	 * instantiate the pattern composite with optional ordering functionality
	 *
	 * @param parent parent {@link Composite}
	 * @param sortable <code>true</code> if the enumeration items should be sortable, <code>false</code> otherwise
	 */
	public CompositePatternEnum(final Composite parent, final boolean sortable) {
		super(parent);

		this.sortable = sortable;
		this.sortableTextItems = new ArrayList<>();

		final Button btnAddOption = new Button(this, SWT.NONE);
		btnAddOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddOption.setText("Add option");
		btnAddOption.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final CompositeSortableTextItem compositeSortableTextItem = new CompositeSortableTextItem(CompositePatternEnum.this.compositeOptions, sortable);
				compositeSortableTextItem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				CompositePatternEnum.this.sortableTextItems.add(compositeSortableTextItem);

				CompositePatternEnum.this.compositeOptions.layout();
				CompositePatternEnum.this.compositeScrolledOptions.setMinSize(CompositePatternEnum.this.compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));

				updatePatternItemWidgets();

				super.widgetSelected(e);
			}
		});

		this.compositeScrolledOptions = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		this.compositeScrolledOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.compositeScrolledOptions.setLayout(new GridLayout(1, false));

		this.compositeOptions = new Composite(this.compositeScrolledOptions, SWT.NONE);
		this.compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.compositeOptions.setLayout(new GridLayout(1, false));
		this.compositeScrolledOptions.setContent(this.compositeOptions);

		this.compositeScrolledOptions.setExpandHorizontal(true);
		this.compositeScrolledOptions.setExpandVertical(true);
		this.compositeScrolledOptions.setMinSize(this.compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private int getItemPosition(final CompositeSortableTextItem needleItem) {
		int index = -1;

		for (int i = 0; i < this.sortableTextItems.size(); i++) {
			final CompositeSortableTextItem refComposite = this.sortableTextItems.get(i);
			if (refComposite == needleItem) {
				index = i;
			}
		}

		return index;
	}

	private void swapTexts(final int i, final int j) {
		final String tempString = this.sortableTextItems.get(i).getText();
		this.sortableTextItems.get(i).setText(this.sortableTextItems.get(j).getText());
		this.sortableTextItems.get(j).setText(tempString);
	}

	public void moveUp(final CompositeSortableTextItem targetComposite) {

		final int targetIndex = getItemPosition(targetComposite);

		if (targetIndex == -1 || targetIndex == 0) {
			return;
		}

		swapTexts(targetIndex, targetIndex - 1);
		updatePatternItemWidgets();
	}

	public void moveDown(final CompositeSortableTextItem targetComposite) {

		final int targetIndex = getItemPosition(targetComposite);

		if (targetIndex == -1 || targetIndex == this.sortableTextItems.size() - 1) {
			return;
		}

		swapTexts(targetIndex, targetIndex + 1);
		updatePatternItemWidgets();
	}

	public void remove(final CompositeSortableTextItem targetComposite) {
		this.sortableTextItems.remove(targetComposite);
		targetComposite.dispose();

		updatePatternItemWidgets();

		this.compositeOptions.layout();
		this.compositeScrolledOptions.setMinSize(this.compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * update list positions and set the move buttons to enabled or disabled
	 */
	private void updatePatternItemWidgets() {
		for (int i = 0; i < this.sortableTextItems.size(); i++) {
			final CompositeSortableTextItem item = this.sortableTextItems.get(i);
			item.setMoveButtonsEnabled();
			item.setPosition(i + 1);
		}

		if (this.sortableTextItems.size() == 1) {
			this.sortableTextItems.get(0).setMoveButtonsEnabled(false, false);
		} else if (this.sortableTextItems.size() > 1) {
			this.sortableTextItems.get(0).setMoveButtonsEnabled(false, true);
			this.sortableTextItems.get(this.sortableTextItems.size() - 1).setMoveButtonsEnabled(true, false);
		}
	}

	
	@Override
	public boolean validate() {
		boolean itemsValid = true;

		// check that items are unique
		final HashSet<String> itemNames = new HashSet<>();

		for (final CompositeSortableTextItem textItem : this.sortableTextItems) {
			// adding to the HashSet will return false if item already in set
			if (!textItem.getText().isEmpty() && !itemNames.add(textItem.getText())) {
				textItem.showValidationError(UIConstants.DEC_ERROR, "The name already exists.");
				itemsValid = false;
			} else {
				textItem.hideValidationError();
			}
		}

		return itemsValid && super.validate();
	}

}
