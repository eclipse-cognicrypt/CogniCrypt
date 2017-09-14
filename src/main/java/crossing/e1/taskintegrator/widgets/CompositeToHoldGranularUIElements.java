package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import crossing.e1.configurator.Constants;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.models.FeatureProperty;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;


public class CompositeToHoldGranularUIElements extends ScrolledComposite {
	private String targetPageName;
	private int lowestWidgetYAxisValue = 10;
	private ArrayList<ClaferFeature> listOfAllClaferFeatures;
	
	/**
	 * Create the composite.
	 * TODO try to add the handling of the granular elements here.
	 * @param parent
	 * @param style
	 */
	public CompositeToHoldGranularUIElements(Composite parent, int style, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		
		listOfAllClaferFeatures = new ArrayList<ClaferFeature>();
		
		
		setExpandHorizontal(true);
		/*addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {				
				setMinHeight(getClientArea().height);
				System.out.println(getMinHeight());
			}
		});*/
		setExpandVertical(true);
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		setContent(composite);
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setTargetPageName(pageName);
		composite.setLayout(null);
		
		/*TODO for testing only
		 * CompositeGranularUIForClaferFeature granularClaferFeature = new CompositeGranularUIForClaferFeature
			(composite, 
			SWT.NONE, 
			new ClaferFeature(
				Constants.FeatureType.ABSTRACT,
				"Security",
				new FeatureProperty("Enum", "integer"),
				null));
		granularClaferFeature.setBounds(10, 10, 744, 280);*/
		
		

	}
	
	public void addGranularClaferUIElements(ClaferFeature claferFeature){
		listOfAllClaferFeatures.add(claferFeature);
		CompositeGranularUIForClaferFeature granularClaferFeature = new CompositeGranularUIForClaferFeature
			((Composite) this.getContent(), 
			SWT.NONE, 
			claferFeature);
		granularClaferFeature.setBounds(10, getLowestWidgetYAxisValue(), 744, 280);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 280);
		setMinHeight(getLowestWidgetYAxisValue());
	}
	
	public void deleteClaferFeature(ClaferFeature featureToBeDeleted){		
		
		for(ClaferFeature featureUnderConsideration:listOfAllClaferFeatures){
			if(featureUnderConsideration.equals(featureToBeDeleted)){
				listOfAllClaferFeatures.remove(featureUnderConsideration);
				break;
			}
		}
		
		updateClaferContainer();
		
	}
	
	private void updateClaferContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		for(Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfClaferFeatures.dispose();
		}
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		for(ClaferFeature featureUnderConsideration : listOfAllClaferFeatures){
			CompositeGranularUIForClaferFeature granularClaferFeature = new CompositeGranularUIForClaferFeature
			(compositeContentOfThisScrolledComposite, 
			SWT.NONE, 
			featureUnderConsideration);
		granularClaferFeature.setBounds(10, getLowestWidgetYAxisValue(), 744, 280);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 280);
		}
		
		setMinHeight(getLowestWidgetYAxisValue());
		
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

}
