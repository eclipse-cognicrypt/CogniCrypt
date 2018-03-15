package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XSLStringGeneration;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegion;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegionAnalyzer;



public class CompositeForXsl extends Composite {

	private StyledText xslTxtBox; // this text box needs to be accessible on the page as well.
	
	public CompositeForXsl(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(2, false));
			
		//UI Widgets for xslPage
		setXslTxtBox(new StyledText(this, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
		xslTxtBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		xslTxtBox.setCursor(null);
	}

	/**
	 * Read the text from the file provided and update the {@link StyledText} with the processed data.
	 * 
	 * @param filePath
	 *        the path that is chosen from the FileDialog.
	 */
	public void updateTheTextFieldWithFileData(String filePath) {

		getXslTxtBox().setText(XSLStringGeneration.generateXSLStringFromPath(filePath, getXslTxtBox().getText(), getXslTxtBox().getSelection(), null));

		colorizeTextBox();
	}

	/**
	 * Analyzes the {@link StyledText}, calculates the {@link XmlRegion} and {@link StyleRange} and applies the syntax highlight.
	 */
	public void colorizeTextBox() {
		List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(xslTxtBox.getText());
		List<StyleRange> ranges = computeStyle(regions);

		for (StyleRange styleRange : ranges) {
			getXslTxtBox().setStyleRange(styleRange);
		}

	}


	/**
	 * Set the colors to all the {@link XmlRegion}.
	 * 
	 * @param regions
	 *        the List of {@link XmlRegion}
	 * @return returns the {@link StyleRange} for the given code.
	 */
	private List<StyleRange> computeStyle(List<XmlRegion> regions) {
		List<StyleRange> styleRanges = new ArrayList<StyleRange>();
		for (XmlRegion xr : regions) {

			// The style itself depends on the region type
			// In this example, we use colors from the system
			StyleRange sr = new StyleRange();
			switch (xr.getXmlRegionType()) {
				case MARKUP:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;

				case ATTRIBUTE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
					break;

				case ATTRIBUTE_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
					break;

				case MARKUP_VALUE:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
					break;
				case COMMENT:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
					break;
				case INSTRUCTION:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
					break;
				case CDATA:
					sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
					break;
				case WHITESPACE:
					break;
				default:
					break;
			}

			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add(sr);
		}

		return styleRanges;
	}

	/**
	 * @return the xslTxtBox
	 */
	public StyledText getXslTxtBox() {
		return xslTxtBox;
	}

	/**
	 * @param xslTxtBox
	 *        the xslTxtBox to set
	 */
	private void setXslTxtBox(StyledText xslTxtBox) {
		this.xslTxtBox = xslTxtBox;
	}

		
}


