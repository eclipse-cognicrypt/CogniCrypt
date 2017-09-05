package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;

import crossing.e1.configurator.Constants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;


public class CompositeToHoldGranularUIElements extends Composite {
	private String targetPageName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeToHoldGranularUIElements(Composite parent, int style, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
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

}
