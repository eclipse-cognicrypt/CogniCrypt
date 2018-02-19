package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CompositePatternOrderedEnum extends Composite {

	private Composite compositeOptions;
	private ScrolledComposite compositeScrolledOptions;
	
	private ArrayList<CompositeSortableTextItem> sortableTextItems;

	public CompositePatternOrderedEnum(Composite parent) {
		super(parent, SWT.NONE);
		
		sortableTextItems = new ArrayList<>();

		setLayout(new GridLayout(1, false));

		Button btnAddOption = new Button(this, SWT.NONE);
		btnAddOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddOption.setText("Add option");
		btnAddOption.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CompositeSortableTextItem compositeSortableTextItem = new CompositeSortableTextItem(compositeOptions);
				compositeSortableTextItem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				sortableTextItems.add(compositeSortableTextItem);

				compositeOptions.layout();
				compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				super.widgetSelected(e);
			}
		});

		compositeScrolledOptions = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		compositeScrolledOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		compositeScrolledOptions.setLayout(new GridLayout(1, false));

		compositeOptions = new Composite(compositeScrolledOptions, SWT.NONE);
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeOptions.setLayout(new GridLayout(1, false));
		compositeScrolledOptions.setContent(compositeOptions);

		compositeScrolledOptions.setExpandHorizontal(true);
		compositeScrolledOptions.setExpandVertical(true);
		compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private int getItemPosition(CompositeSortableTextItem needleItem) {
		int index = -1;
		
		for (int i=0; i < sortableTextItems.size(); i++) {
			CompositeSortableTextItem refComposite = sortableTextItems.get(i);
			if (refComposite == needleItem) {
				index = i;
			}
		}
		
		return index;
	}

	private void swapTexts(int i, int j) {
		String tempString = sortableTextItems.get(i).getText();
		sortableTextItems.get(i).setText(sortableTextItems.get(j).getText());
		sortableTextItems.get(j).setText(tempString);
	}

	public void moveUp(CompositeSortableTextItem targetComposite) {

		int targetIndex = getItemPosition(targetComposite);

		if (targetIndex == -1 || targetIndex == 0) {
			return;
		}
		
		swapTexts(targetIndex, targetIndex - 1);
	}

	public void moveDown(CompositeSortableTextItem targetComposite) {

		int targetIndex = getItemPosition(targetComposite);

		if (targetIndex == -1 || targetIndex == sortableTextItems.size() - 1) {
			return;
		}

		swapTexts(targetIndex, targetIndex + 1);
	}

	public void remove(CompositeSortableTextItem targetComposite) {
		sortableTextItems.remove(targetComposite);
		targetComposite.dispose();

		compositeOptions.layout();
		compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public ArrayList<String> getElements() {
		ArrayList<String> resultStrings = new ArrayList<>();

		for (CompositeSortableTextItem refItem : sortableTextItems) {
			resultStrings.add(refItem.getText());
		}

		return resultStrings;
	}

}
