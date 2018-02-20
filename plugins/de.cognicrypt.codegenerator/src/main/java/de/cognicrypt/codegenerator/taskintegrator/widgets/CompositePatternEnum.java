package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;

public class CompositePatternEnum extends CompositePattern {
	
	private ArrayList<StringBuilder> options;

	public CompositePatternEnum(Composite parent) {
		super(parent);
		
		options = new ArrayList<>();

		Button btnAddOption = new Button(this, SWT.NONE);
		btnAddOption.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddOption.setText("Add option");
		btnAddOption.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuilder strOption = new StringBuilder();
				options.add(strOption);

				Text txtOption = new Text(compositeOptions, SWT.BORDER);
				txtOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				txtOption.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent arg0) {
						strOption.delete(0, strOption.length());
						strOption.append(txtOption.getText());

					}
				});

				Button btnRemove = new Button(compositeOptions, SWT.NONE);
				btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
				btnRemove.setText("Remove");
				btnRemove.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						txtOption.dispose();
						btnRemove.dispose();
						options.remove(strOption);

						compositeOptions.layout();
						super.widgetSelected(e);
					}
				});

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
		compositeOptions.setLayout(new GridLayout(2, false));
		compositeScrolledOptions.setContent(compositeOptions);

		compositeScrolledOptions.setExpandHorizontal(true);
		compositeScrolledOptions.setExpandVertical(true);
		compositeScrolledOptions.setMinSize(compositeOptions.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public ClaferModel getResultModel() {
		ClaferModel resultModel = new ClaferModel();
		resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, patternName, "Enum"));
		for (StringBuilder sb : options) {
			resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, sb.toString(), patternName));
		}
		return resultModel;
	}

}
