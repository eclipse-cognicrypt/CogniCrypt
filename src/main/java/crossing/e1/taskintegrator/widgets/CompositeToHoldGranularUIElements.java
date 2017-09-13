package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;

import crossing.e1.configurator.Constants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;


public class CompositeToHoldGranularUIElements extends ScrolledComposite {
	private String targetPageName;
	private int lowestWidgetYAxisValue = 10;

	/**
	 * Create the composite.
	 * TODO try to add the handling of the granular elements here.
	 * @param parent
	 * @param style
	 */
	public CompositeToHoldGranularUIElements(Composite parent, int style, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
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
		setAlwaysShowScrollBars(true);
		
		Composite composite = new Composite(this, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		composite.setLayout(rl_composite);
		setContent(composite);
		setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setTargetPageName(pageName);	
		
		
		

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
