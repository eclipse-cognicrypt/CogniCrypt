/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeChoiceForModeOfWizard;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeForXsl;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.codegenerator.taskintegrator.widgets.GroupBrowseForFile;

/**
 * @author rajiv
 *
 */
public class PageForTaskIntegratorWizard extends WizardPage {

	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	private CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;

	private CompositeForXsl compositeForXsl = null;

	int counter = 0;// TODO for testing only.
	protected ArrayList<ClaferFeature> cfrFeatures;

	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(String name, String title, String description) {
		super(name);
		setTitle(title);
		setDescription(description);		
		this.setPageComplete(false);		
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayout(new GridLayout(2, false));

		switch (this.getName()) {
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				container.setLayout(new FillLayout(SWT.HORIZONTAL));
				setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE, this));				
				break;
			case Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

				Button btnAddFeature = new Button(container, SWT.NONE);
				btnAddFeature.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				btnAddFeature.setText("Add Feature");
				btnAddFeature.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						counter++;
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

							// Update the array list.							
							compositeToHoldGranularUIElements.getClaferModel().add(tempFeature);
							compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);

							// rebuild the UI
							compositeToHoldGranularUIElements.updateClaferContainer();
						}

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
							compositeToHoldGranularUIElements.getClaferModel().add(claferImportDialog.getResult());
							compositeToHoldGranularUIElements.updateClaferContainer();
						}
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
						String targetFilename = saveDialog.open();
						if (targetFilename != null) {
							compositeToHoldGranularUIElements.getClaferModel().toBinary(targetFilename);
						}
						super.widgetSelected(e);
					}
				});

				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:

				setCompositeForXsl(new CompositeForXsl(container, SWT.NONE));
				// fill the available space on the with the big composite
				getCompositeForXsl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

				Button btnAddXSLTag = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnAddXSLTag.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				btnAddXSLTag.setText("Add Xsl Tag");
				Button btnReadCode = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnReadCode.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				btnReadCode.setText("Get the code");

				btnReadCode.addSelectionListener(new SelectionAdapter() {
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */

					@Override
					public void widgetSelected(SelectionEvent e) {

						super.widgetSelected(e);

						FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);

						fileDialog.setFilterExtensions(new String[] { "*.txt", "*.java", "*.xsl" });
						fileDialog.setText("Choose the code file:");

						String fileDialogResult = fileDialog.open();
						if (fileDialogResult != null) {
							((CompositeForXsl) getCompositeForXsl()).updateTheTextFieldWithFileData(fileDialogResult);
						}
					}

				});

				btnAddXSLTag.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						ClaferModel claferModel = null;
						ArrayList<Question> questions = null;
						ArrayList<String> strFeatures = new ArrayList<>();

						for (IWizardPage page : getWizard().getPages()) {
							// get the Clafer creation page
							if (page instanceof PageForTaskIntegratorWizard) {
								PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) page;
								if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
									CompositeToHoldGranularUIElements comp = (CompositeToHoldGranularUIElements) pftiw.getCompositeToHoldGranularUIElements();
									if (pftiw.getName() == Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION) {
										// get the Clafer features
										claferModel = comp.getClaferModel();

										// get all the Clafer features' properties
										for (ClaferFeature cfrFtr : claferModel) {
											String ftrName = cfrFtr.getFeatureName();
											for (FeatureProperty prop : cfrFtr.getFeatureProperties()) {
												// prepend the feature name and add the property to dropdown entries
												strFeatures.add(ftrName + "." + prop.getPropertyName());
											}
										}
									} else if (pftiw.getName() == Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS) {
										questions = comp.getListOfAllQuestions();

										for (Question question : questions) {
											// TODO compare against Constants.GUIElements.text
											if (question.getQuestionType().equals("text")) {
												strFeatures.add("[Answer to \"" + question.getQuestionText() + "\"]");
											}
										}
									}
								}
							}
						}

						XSLTagDialog dialog;
						if (strFeatures.size() > 0) {
							dialog = new XSLTagDialog(getShell(), strFeatures);
						} else {
							dialog = new XSLTagDialog(getShell());
						}

						if (dialog.open() == Window.OK) {
							// To locate the position of the xsl tag to be introduce						
							Point selected = getCompositeForXsl().getXslTxtBox().getSelection();
							String xslTxtBoxContent = getCompositeForXsl().getXslTxtBox().getText();
							xslTxtBoxContent = xslTxtBoxContent.substring(0, selected.x) + dialog.getTag().toString() + xslTxtBoxContent.substring(selected.y,
								xslTxtBoxContent.length());
							getCompositeForXsl().getXslTxtBox().setText(xslTxtBoxContent);
						}

					}
				});
				break;
			case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

				TaskIntegrationWizard tiWizard = null;

				if (TaskIntegrationWizard.class.isInstance(getWizard())) {
					tiWizard = (TaskIntegrationWizard) getWizard();
				} else {
					Activator.getDefault().logError("PageForTaskIntegratorWizard was instantiated by a wizard other than TaskIntegrationWizard");
				}

				PageForTaskIntegratorWizard claferPage = tiWizard.getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION);
				CompositeToHoldGranularUIElements claferPageComposite = (CompositeToHoldGranularUIElements) claferPage.getCompositeToHoldGranularUIElements();

				QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
				Button qstnDialog = new Button(container, SWT.NONE);
				qstnDialog.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				qstnDialog.setText("Add Question");

				qstnDialog.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						int response = questionDialog.open();
						if (response == Window.OK) {
							counter++;
							//Question questionDetails = getDummyQuestion(questionDialog.getQuestionText(),questionDialog.getquestionType(),questionDialog.getAnswerValue());
							Question questionDetails = questionDialog.getQuestionDetails();
							questionDetails.setId(counter);

							// Update the array list.
							compositeToHoldGranularUIElements.getListOfAllQuestions().add(questionDetails);
							compositeToHoldGranularUIElements.addQuestionUIElements(questionDetails, claferPageComposite.getClaferModel(), false);
						}
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_LINK_ANSWERS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				break;
		}
	}

	/**
	 * Overwriting the getNextPage method to extract the list of all questions
	 * from highLevelQuestion page and forward the data to pageForLinkAnswers at runtime
	 */
	public IWizardPage getNextPage() {
		boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
		if (isNextPressed) {
			boolean validatedNextPress = this.nextPressed(this);
			if (!validatedNextPress) {
				return this;
			}
		}
		
		if (this.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD) && !getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode().isGuidedModeChosen()) {
			return null;
		}
		
		
		/*
		 * This is for debugging only. To be removed for the final version.
		 * TODO Please add checks on the pages after mode selection to mark those pages as completed, or restrict the finish button.
		 */
		IWizardPage nextPage = super.getNextPage();
		((WizardPage)nextPage).setPageComplete(true);
		
		return nextPage;

	}

	/**
	 * Extract data from highLevelQuestions page and forward it to pageForLinkAnswers at runtime
	 * 
	 * @param page
	 *        highLevelQuestions page is received
	 * @return true always
	 */
	protected boolean nextPressed(IWizardPage page) {
		boolean ValidateNextPress = true;
		try {
			if (page.getName().equals(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS)) {
				PageForTaskIntegratorWizard highLevelQuestionPage = (PageForTaskIntegratorWizard) page;
				CompositeToHoldGranularUIElements highLevelQuestionPageComposite = (CompositeToHoldGranularUIElements) highLevelQuestionPage.getCompositeToHoldGranularUIElements();
				IWizardPage nextPage = super.getNextPage();
				ArrayList<Question> listOfAllQuestions = highLevelQuestionPageComposite.getListOfAllQuestions();
				if (nextPage instanceof PageForTaskIntegratorWizard) {
					PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) nextPage;
					if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
						CompositeToHoldGranularUIElements comp = (CompositeToHoldGranularUIElements) pftiw.getCompositeToHoldGranularUIElements();
						if (comp.getListOfAllQuestions().size() > 0) {
							comp.deleteAllQuestion();
						}
						for (Question question : listOfAllQuestions) {
							comp.getListOfAllQuestions().add(question);
							comp.addQuestionUIElements(question, null, true);

						}

					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Error validation when pressing Next: " + ex);

		}
		return ValidateNextPress;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {

		// each case needs to be handled separately. By default all cases will return false. 
		/*
		 * switch(this.getName()){ case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD: if(((boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN) ==
		 * true || (boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED) == true) && !this.isPageComplete()){ return true; } case
		 * Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION: return false; case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION: return false; case
		 * Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS: return false; default: return false; }
		 */
		return super.canFlipToNextPage();

	}
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR.
	 */
	public void checkIfModeSelectionPageIsComplete() {		
		boolean errorOnFileWidgets = false;
		// The first child of the composite is a group. Get the children of this group to iterated over.
		for (Control control : ((Group)getCompositeChoiceForModeOfWizard().getChildren()[0]).getChildren()) {
			// Check if the child is an instance of group and is visible.
			if (control instanceof Group && control.isVisible()) {
				
				// Get the children of this group and iterate over them. These are the widgets that get the file data. This loop generalizes for all these widgets.
				for (Control subGroup : ((Group)control).getChildren()) {					
					if (subGroup instanceof GroupBrowseForFile) {
						GroupBrowseForFile tempVaraiable = (GroupBrowseForFile) subGroup;
						if ((tempVaraiable).getDecFilePath().getDescriptionText().contains(Constants.ERROR)) {
							errorOnFileWidgets = true;
						}
					}
					
				}	
				
			}
		}		
		
		// Check if validation failed on the task name.
		boolean errorOnTaskName = getCompositeChoiceForModeOfWizard().getDecNameOfTheTask().getDescriptionText().contains(Constants.ERROR);
		
		// Set the page to incomplete if the validation failed on any of the text boxes.
		if (errorOnTaskName || errorOnFileWidgets) {
			setPageComplete(false);
			
		} else {
			setPageComplete(true);
		}
	}

	/**
	 * Return the composite for the first page, i.e. to choose the mode of the wizard.
	 * @return the compositeChoiceForModeOfWizard
	 */
	public CompositeChoiceForModeOfWizard getCompositeChoiceForModeOfWizard() {
		return compositeChoiceForModeOfWizard;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeChoiceForModeOfWizard
	 *        the compositeChoiceForModeOfWizard to set
	 */
	private void setCompositeChoiceForModeOfWizard(CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard) {
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
	}

	/**
	 * @return the compositeToHoldGranularUIElements
	 */
	public Composite getCompositeToHoldGranularUIElements() {
		return compositeToHoldGranularUIElements;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeToHoldGranularUIElements
	 *        the compositeToHoldGranularUIElements to set
	 */
	public void setCompositeToHoldGranularUIElements(CompositeToHoldGranularUIElements compositeToHoldGranularUIElements) {
		this.compositeToHoldGranularUIElements = compositeToHoldGranularUIElements;
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * Return the composite for the XSL page.
	 * @return the compositeForXsl
	 */
	public CompositeForXsl getCompositeForXsl() {
		return compositeForXsl;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeForXsl
	 *        the compositeForXsl to set
	 */
	public void setCompositeForXsl(CompositeForXsl compositeForXsl) {
		this.compositeForXsl = compositeForXsl;

	}

}
