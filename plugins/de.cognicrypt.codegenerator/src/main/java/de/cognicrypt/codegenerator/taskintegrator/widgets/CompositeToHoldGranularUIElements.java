package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;


public class CompositeToHoldGranularUIElements extends ScrolledComposite {
	private String targetPageName;
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	private ArrayList<ClaferFeature> listOfAllClaferFeatures;
	
	private ArrayList<Question> listOfAllQuestions;
	int counter;
	
	/**
	 * Create the composite.  
	 * @param parent
	 * @param style
	 */
	public CompositeToHoldGranularUIElements(Composite parent, int style, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		
		listOfAllClaferFeatures = new ArrayList<ClaferFeature>();
		
		listOfAllQuestions = new ArrayList<Question>();
		
		
		setTargetPageName(pageName);
		
		setExpandHorizontal(true);		
		setExpandVertical(true);
		setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		// All the granular UI elements will be added to this composite for the ScrolledComposite to work.
		Composite contentComposite = new Composite(this, SWT.NONE);
		setContent(contentComposite);
		setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
		contentComposite.setLayout(null);	

	}
	
	public void addGranularClaferUIElements(ClaferFeature claferFeature){
		// Update the array list.
		//listOfAllClaferFeatures.add(claferFeature);
		
		CompositeGranularUIForClaferFeature granularClaferFeature = new CompositeGranularUIForClaferFeature
			((Composite) this.getContent(), // the content composite of ScrolledComposite.
			SWT.NONE, 
			claferFeature);
		granularClaferFeature.setBounds(
			Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS, 
			getLowestWidgetYAxisValue(), 
			Constants.WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT, 
			//Constants.HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT
			granularClaferFeature.getSize().y);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + granularClaferFeature.getSize().y);
		setMinHeight(getLowestWidgetYAxisValue());
	}

	public void deleteClaferFeature(ClaferFeature featureToBeDeleted) {
		listOfAllClaferFeatures.remove(featureToBeDeleted);
		updateClaferContainer();
	}

	private void updateClaferContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		
		// first dispose all the granular UI elements (which includes the deleted one).
		for(Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfClaferFeatures.dispose();
		}
		
		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		// add all the clafer features excluding the deleted one.
		for(ClaferFeature featureUnderConsideration : listOfAllClaferFeatures){
			addGranularClaferUIElements(featureUnderConsideration);
		}
	}
	
	public void modifyClaferFeature(ClaferFeature originalClaferFeature, ClaferFeature modifiedClaferFeature ){
		for(ClaferFeature featureUnderConsideration:listOfAllClaferFeatures){
			if(featureUnderConsideration.equals(originalClaferFeature)){
				featureUnderConsideration = modifiedClaferFeature;
				break;
			}
		}
		
		updateClaferContainer();
	}
	
	
	public void addQuestionUIElements(Question question, ArrayList<ClaferFeature> claferFeatures, boolean linkAnswerPage) {
		// Update the array list.
		//listOfAllClaferFeatures.add(claferFeature);
		setClaferFeatures(claferFeatures);

		CompositeGranularUIForHighLevelQuestions granularQuestion = new CompositeGranularUIForHighLevelQuestions((Composite) this.getContent(), // the content composite of ScrolledComposite.
			SWT.NONE, question, linkAnswerPage);

		//heightOfTheGranularComposite variable is used to determine the height of the granular Composite
		int heightOfTheGranularComposite = 0;
		if (linkAnswerPage) {
			heightOfTheGranularComposite = granularQuestion.getSize().y - 55;
		} else if (!linkAnswerPage) {
			heightOfTheGranularComposite = granularQuestion.getSize().y;
		}
		granularQuestion.setBounds(Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT,
			//Constants.HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT
			heightOfTheGranularComposite);

		//granularQuestion.setSize(SWT.DEFAULT, granularQuestion.getSize().y);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + heightOfTheGranularComposite);
		setMinHeight(getLowestWidgetYAxisValue());
	}
	
	public void deleteQuestion(Question questionToBeDeleted){		
		
		listOfAllQuestions.remove(questionToBeDeleted);
				
		updateQuestionContainer();
		
	}
	
	/**	 
	 * executes when next button of "highLevelQuestion" is pressed
	 * deletes listOfAllQuestions of "pageForLinkAnswers"
	 * to refresh the question list of "pagForLinkAnswers"
	 */
	public void deleteAllQuestion(){
		listOfAllQuestions.clear();
		
		updateQuestionContainer();
	}
	
	private void updateQuestionContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		
		// first dispose all the granular UI elements (which includes the deleted one).
		for(Control uiRepresentationOfQuestions : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfQuestions.dispose();
		}
		
		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		// add all the clafer features excluding the deleted one.
		for(Question questionUnderConsideration : listOfAllQuestions){
			addQuestionUIElements(questionUnderConsideration,listOfAllClaferFeatures,false);
		}
	}
	
	public void modifyQuestion(Question originalQuestion, Question modifiedQuestion ){
		for(Question questionUnderConsideration:listOfAllQuestions){
			if(questionUnderConsideration.equals(originalQuestion)){
				questionUnderConsideration = modifiedQuestion;
				break;
			}
		}
		updateClaferContainer();
	}
	
	public void modifyHighLevelQuestion(Question originalQuestion, Question modifiedQuestion ){
		for(Question questionUnderConsideration:listOfAllQuestions){
			if(questionUnderConsideration.equals(originalQuestion)){
				questionUnderConsideration.setQuestionText(modifiedQuestion.getQuestionText());
				questionUnderConsideration.setQuestionType(modifiedQuestion.getQuestionType());
				questionUnderConsideration.getAnswers().clear();
				questionUnderConsideration.setAnswers(modifiedQuestion.getAnswers());
				break;
			}
		}
		//deleteQuestion(originalQuestion);
		updateQuestionContainer();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the targetPageName
	 */
	public String getTargetPageName() {
		return targetPageName;
	}

	/**
	 * @param targetPageName the targetPageName to set
	 */
	private void setTargetPageName(String targetPageName) {
		this.targetPageName = targetPageName;
	}

	/**
	 * @return the lowestWidgetYAxisValue
	 */
	public int getLowestWidgetYAxisValue() {
		return lowestWidgetYAxisValue;
	}

	/**
	 * @param lowestWidgetYAxisValue the lowestWidgetYAxisValue to set
	 */
	public void setLowestWidgetYAxisValue(int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	}
	
	/**
	 * @return the listOfAllClaferFeatures
	 */
	public ArrayList<ClaferFeature> getListOfAllClaferFeatures() {
		return listOfAllClaferFeatures;
	}

	public void setClaferFeatures(ArrayList<ClaferFeature> claferFeatures){
		//listOfAllClaferFeatures.clear();
		this.listOfAllClaferFeatures=claferFeatures;
	}
	/**
	 * @return the listOfAllQuestions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return listOfAllQuestions;
	}

	/**
	 * @param listOfAllQuestions the listOfAllQuestions to set
	 */
	public void setListOfAllQuestions(ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}

}
