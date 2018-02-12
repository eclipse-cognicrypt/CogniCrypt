package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeClaferFeedback;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;

public class ClaferPage extends PageForTaskIntegratorWizard {

	private CompositeClaferFeedback feedbackComposite;

	public ClaferPage() {
		super(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION, Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayout(new GridLayout(2, false));

		setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
		getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));

		Button btnAddFeature = new Button(container, SWT.NONE);
		btnAddFeature.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddFeature.setText("Add Feature");
		btnAddFeature.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), compositeToHoldGranularUIElements.getClaferModel());
				if (cfrFeatureDialog.open() == 0) {
					ClaferFeature tempFeature = cfrFeatureDialog.getResult();

					// if features are missing, ask the user whether to implement them							
					ClaferModel missingFeatures = compositeToHoldGranularUIElements.getClaferModel().getMissingFeatures(tempFeature);

					if (!missingFeatures.getClaferModel().isEmpty()) {
						MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						dialog.setText("Additional features can be created");
						dialog.setMessage("Some of the used features don't exist yet. Should we create them for you?");

						if (dialog.open() == SWT.YES) {
							compositeToHoldGranularUIElements.getClaferModel().implementMissingFeatures(tempFeature);
						}
					}

					compositeToHoldGranularUIElements.getClaferModel().add(tempFeature);
					compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);
					compositeToHoldGranularUIElements.updateClaferContainer();
				}

			}

		});

		Button btnAddPattern = new Button(container, SWT.NONE);
		btnAddPattern.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddPattern.setText("Add Pattern");
		btnAddPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferFeaturePatternDialog patternDialog = new ClaferFeaturePatternDialog(getShell());
				if (patternDialog.open() == 0) {
					for (ClaferFeature cfrFeature : patternDialog.getResultModel()) {
						compositeToHoldGranularUIElements.getClaferModel().add(cfrFeature);
						compositeToHoldGranularUIElements.addGranularClaferUIElements(cfrFeature);
						compositeToHoldGranularUIElements.updateClaferContainer();
					}
				}
				super.widgetSelected(e);
			}
		});

		Button importFeatures = new Button(container, SWT.NONE);
		importFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		importFeatures.setText("Import Features");
		importFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferImportDialog claferImportDialog = new ClaferImportDialog(getShell());
				if (claferImportDialog.open() == 0) {
					ClaferModel currentModel = compositeToHoldGranularUIElements.getClaferModel();
					System.out.println(currentModel);
					currentModel.add(claferImportDialog.getResult());
					compositeToHoldGranularUIElements.updateClaferContainer();
				}
				Label lblFeaturesImported = new Label(feedbackComposite, SWT.NONE);

				lblFeaturesImported.setText("Features imported");
				feedbackComposite.layout();
				super.widgetSelected(e);
			}
		});

		Button exportFeatures = new Button(container, SWT.NONE);
		exportFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		exportFeatures.setText("Export Features");
		exportFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog saveDialog = new FileDialog(getShell(), SWT.SAVE);
				LocalDateTime date = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");
				saveDialog.setFileName(date.format(formatter) + ".dat");
				String[] filterNames = new String[] { "CogniCrypt binary Clafer model (*.dat)", "Human-readable non-importable Clafer file (*.cfr)" };
				String[] filterExtensions = new String[] { "*.dat", "*.cfr" };
				saveDialog.setFilterNames(filterNames);
				saveDialog.setFilterExtensions(filterExtensions);
				String targetFilename = saveDialog.open();
				if (targetFilename != null) {
					if (targetFilename.endsWith(".dat")) {
						compositeToHoldGranularUIElements.getClaferModel().toBinary(targetFilename);
					} else if (targetFilename.endsWith(".cfr")) {
						compositeToHoldGranularUIElements.getClaferModel().toFile(targetFilename);
					}
				}
				super.widgetSelected(e);
			}
		});

		feedbackComposite = new CompositeClaferFeedback(container, SWT.BORDER);
		((CompositeToHoldGranularUIElements) getCompositeToHoldGranularUIElements()).setCompositeClaferFeedback(feedbackComposite);
		feedbackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

	}

}
