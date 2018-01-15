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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
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

		switch (this.getName()) {
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				container.setLayout(new FillLayout(SWT.HORIZONTAL));
				setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE, this));
				break;
			case Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);

				Button btnAddFeature = new Button(container, SWT.NONE);
				btnAddFeature.setBounds(Constants.RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
				btnAddFeature.setText("Add Feature");
				btnAddFeature.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						counter++;
						ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), compositeToHoldGranularUIElements.getListOfAllClaferFeatures());
						if (cfrFeatureDialog.open() == 0) {
							ClaferFeature tempFeature = cfrFeatureDialog.getResult();

							// Update the array list.							
							compositeToHoldGranularUIElements.getListOfAllClaferFeatures().add(tempFeature);
							compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);
						}

					}

				});
				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:

				this.setCompositeForXsl(new CompositeForXsl(container, SWT.NONE));

				Button btnAddXSLTag = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnAddXSLTag.setBounds(Constants.RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
				btnAddXSLTag.setText("Add Xsl Tag");
				Button btnReadCode = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnReadCode.setBounds(Constants.RECTANGLE_FOR_SECOND_BUTTON_FOR_NON_MODE_SELECTION_PAGES);
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
						((CompositeForXsl) getCompositeForXsl()).updateTheTextFieldWithFileData(fileDialog.open());
					}

				});

				btnAddXSLTag.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						ArrayList<ClaferFeature> cfrFeatures = null;
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
										cfrFeatures = comp.getListOfAllClaferFeatures();

										// get all the Clafer features' properties
										for (ClaferFeature cfrFtr : cfrFeatures) {
											String ftrName = cfrFtr.getFeatureName();
											for (FeatureProperty prop : cfrFtr.getfeatureProperties()) {
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
				this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);

				TaskIntegrationWizard tiWizard = null;

				if (TaskIntegrationWizard.class.isInstance(getWizard())) {
					tiWizard = (TaskIntegrationWizard) getWizard();
				} else {
					Activator.getDefault().logError("PageForTaskIntegratorWizard was instantiated by a wizard other than TaskIntegrationWizard");
				}

				PageForTaskIntegratorWizard claferPage = tiWizard.getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION);
				CompositeToHoldGranularUIElements claferPageComposite = (CompositeToHoldGranularUIElements) claferPage.getCompositeToHoldGranularUIElements();

				QuestionDialog questionDialog = new QuestionDialog(parent
					.getShell() /* compositeToHoldGranularUIElements.getListOfAllClaferFeatures()claferFeatures,claferPageComposite */);
				Button qstnDialog = new Button(container, SWT.NONE);
				qstnDialog.setBounds(889, 10, 115, 29);
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
							compositeToHoldGranularUIElements.addQuestionUIElements(questionDetails, claferPageComposite.getListOfAllClaferFeatures(), false);
						}
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_LINK_ANSWERS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, SWT.NONE, this.getName()));
				this.compositeToHoldGranularUIElements.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
				break;
		}
	}

	/**
	 * Overwriting the getNextPage method to extract the list of all questions
	 * from highLevelQuestion page and forward the data to pageForLinkAnswers at runtime
	 */
	public IWizardPage getNextPage() {
//		boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
//		if (isNextPressed) {
//			boolean validatedNextPress = this.nextPressed(this);
//			if (!validatedNextPress) {
//				return this;
//			}
//		}
		
//		if (this.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
//			return null;
//		}
		return super.getNextPage();

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

	/**
	 * For testing only. Remove later.
	 * 
	 * @return
	 */
	private ClaferFeature getDummyClaferFeature() {
		/*
		 * ClaferFeature tempFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, Integer.toString(counter), // Counter as the name to make each addition identifiable. new
		 * FeatureProperty("Enum", "integer"), null);
		 */ClaferFeature tempFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, Integer.toString(counter), // Counter as the name to make each addition identifiable.
			"");

		// from symmetric encryption abstract Algorithm
		tempFeature.getfeatureProperties().add(new FeatureProperty("name", "string"));
		tempFeature.getfeatureProperties().add(new FeatureProperty("description", "string"));
		tempFeature.getfeatureProperties().add(new FeatureProperty("security", "Security"));
		tempFeature.getfeatureProperties().add(new FeatureProperty("performance", "Performance"));
		tempFeature.getfeatureProperties().add(new FeatureProperty("classPerformance", "Performance"));

		// from symmetric encryption concrete SHA: Digest

		tempFeature.getFeatureConstraints().add(new ClaferConstraint("outputSize = 160 || outputSize = 224 || outputSize = 256 || outputSize = 384 || outputSize = 512"));
		tempFeature.getFeatureConstraints().add(new ClaferConstraint("outputSize = 160 => performance = VeryFast && security = Weak"));
		tempFeature.getFeatureConstraints().add(new ClaferConstraint("outputSize = 224 => performance = Fast && security = Strong"));
		tempFeature.getFeatureConstraints().add(new ClaferConstraint("description = \"PBKDF2 key derivation\""));
		tempFeature.getFeatureConstraints().add(new ClaferConstraint("security = cipher.security"));

		return tempFeature;

	}

	private ArrayList<ClaferFeature> getDummyClaferFeatures() {
		ArrayList<ClaferFeature> tempFeatures = new ArrayList<ClaferFeature>();
		ClaferFeature tempFeatureOne = new ClaferFeature(Constants.FeatureType.ABSTRACT, "race", "");
		ClaferFeature tempFeatureTwo = new ClaferFeature(Constants.FeatureType.CONCRETE, "altmer", "race");
		ClaferFeature tempFeatureThree = new ClaferFeature(Constants.FeatureType.CONCRETE, "dunmer", "race");
		tempFeatures.add(tempFeatureOne);
		tempFeatures.add(tempFeatureTwo);
		tempFeatures.add(tempFeatureThree);

		return tempFeatures;
	}

	/**
	 * For testing only. Remove later.
	 * 
	 * @return
	 */
	private Question getDummyQuestion() {
		Question tempQuestion = new Question();
		tempQuestion.setId(counter);
		tempQuestion.setQuestionText("question?");

		Answer answer = new Answer();
		answer.setValue("answer");
		answer.setDefaultAnswer(false);
		answer.setNextID(counter);
		ClaferDependency claferDependency = new ClaferDependency();
		claferDependency.setAlgorithm("algoritm");
		claferDependency.setOperand("operand");
		claferDependency.setOperator(Constants.FeatureConstraintRelationship.AND.toString());
		claferDependency.setValue("value");
		ArrayList<ClaferDependency> claferDependencies = new ArrayList<ClaferDependency>();
		claferDependencies.add(claferDependency);

		CodeDependency codeDependency = new CodeDependency();
		codeDependency.setOption("option");
		codeDependency.setValue("value");
		ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
		codeDependencies.add(codeDependency);

		answer.setCodeDependencies(codeDependencies);
		answer.setClaferDependencies(claferDependencies);

		ArrayList<Answer> answers = new ArrayList<Answer>();
		answers.add(answer);

		tempQuestion.setAnswers(answers);

		return tempQuestion;
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
		for (Control control : ((Group)getCompositeChoiceForModeOfWizard().getChildren()[0]).getChildren()) {
			if (control.getClass().getName().equals("org.eclipse.swt.widgets.Group") && control.isVisible()) {
				
				for (Control subGroup : ((Group)control).getChildren()) {					
					if (subGroup.getClass().getName().equals("de.cognicrypt.codegenerator.taskintegrator.widgets.GroupBrowseForFile")) {
						GroupBrowseForFile tempVaraiable = (GroupBrowseForFile) subGroup;
						System.out.println((tempVaraiable).getDecFilePath().getDescriptionText());
						if ((tempVaraiable).getDecFilePath().getDescriptionText().contains("ERROR")) {
							errorOnFileWidgets = true;
						}
					}
					
				}	
				
			}
		}		
		
		boolean errorOnTaskName = getCompositeChoiceForModeOfWizard().getDecNameOfTheTask().getDescriptionText().contains("ERROR");

		if (errorOnTaskName || errorOnFileWidgets) {
			setPageComplete(false);
			
		} else {
			setPageComplete(true);
		}
	}

	/**
	 * @return the compositeChoiceForModeOfWizard
	 */
	public CompositeChoiceForModeOfWizard getCompositeChoiceForModeOfWizard() {
		return compositeChoiceForModeOfWizard;
	}

	/**
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
	 * @return the compositeForXsl
	 */
	public CompositeForXsl getCompositeForXsl() {
		return compositeForXsl;
	}

	/**
	 * @param compositeForXsl
	 *        the compositeForXsl to set
	 */
	public void setCompositeForXsl(CompositeForXsl compositeForXsl) {
		this.compositeForXsl = compositeForXsl;

	}

}
