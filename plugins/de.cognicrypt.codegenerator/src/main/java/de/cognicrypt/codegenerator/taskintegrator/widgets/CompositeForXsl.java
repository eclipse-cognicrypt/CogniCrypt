package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegion;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegionAnalyzer;



public class CompositeForXsl extends Composite {
	private StyledText xslTxtBox;
	
		public CompositeForXsl(Composite parent, int style) {
			super(parent,SWT.BORDER);
		//this.setBounds(0,0,887,500);
		setLayout(new GridLayout(2, false));
			
			//UI Widgets for xslPage
			setXslTxtBox(new StyledText(this,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
		xslTxtBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
			xslTxtBox.setCursor(null);
			
			
		}
		
		public void updateTheTextFieldWithFileData(String filePath){
						
			StringBuilder dataFromFile = new StringBuilder();
			
			Path path = Paths.get(filePath);
			
		if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
			.getFileName().toString().endsWith(".TXT")) {

			// Check if the XSL tags have already been created. If yes then add the text at the cursor location.
			if (xslTxtBox.getText().contains("<?xml version=") || xslTxtBox.getText().contains("<xsl:stylesheet xmlns:xsl=")) {
				Point selected = xslTxtBox.getSelection();
				String xslTxtBoxContent = xslTxtBox.getText();

				dataFromFile.append(xslTxtBoxContent.substring(0, selected.x));
				dataFromFile.append("\n");
				dataFromFile.append("<xsl:result-document href=\"\">");
				dataFromFile.append("\n");
				appendTextFromFileToStringBuilder(dataFromFile, filePath);
				dataFromFile.append("\n");
				dataFromFile.append(xslTxtBoxContent.substring(selected.y, xslTxtBoxContent.length()));
				xslTxtBox.setCursor(xslTxtBox.getCursor());

			} else {
				dataFromFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				dataFromFile.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n");
				dataFromFile.append("<xsl:output method=\"text\"/>\n");
				dataFromFile.append("<xsl:template match=\"/\">\n");
				dataFromFile.append("package <xsl:value-of select=\"//task/Package\"/>; \n");
				dataFromFile.append("<xsl:apply-templates select=\"//Import\"/>\n");

				appendTextFromFileToStringBuilder(dataFromFile, filePath);

				dataFromFile.append("\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("<xsl:template match=\"Import\">\n");
				dataFromFile.append("import <xsl:value-of select=\".\"/>;\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("</xsl:stylesheet>");
			}

		} else {
			appendTextFromFileToStringBuilder(dataFromFile, filePath);
		}
		
		xslTxtBox.setText(dataFromFile.toString());

		colorizeTextBox();

		
	}

	public void colorizeTextBox() {
		List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(xslTxtBox.getText());
		List<StyleRange> ranges = computeStyle(regions);

		for (StyleRange styleRange : ranges) {
			xslTxtBox.setStyleRange(styleRange);
		}

	}

	private void appendTextFromFileToStringBuilder(StringBuilder dataFromFile, String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String line = br.readLine();

			while (line != null) {
				dataFromFile.append(line);
				dataFromFile.append(System.lineSeparator());
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

		private List<StyleRange> computeStyle(List<XmlRegion> regions) {
			List<StyleRange> styleRanges = new ArrayList<StyleRange> ();
		    for( XmlRegion xr : regions ) {
		 
		        // The style itself depends on the region type
		        // In this example, we use colors from the system
		        StyleRange sr = new StyleRange();
		        switch( xr.getXmlRegionType()) {
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
		            case WHITESPACE: break;
		            default: break;
		        }
		 
		        // Define the position and limit
		        sr.start = xr.getStart();
		        sr.length = xr.getEnd() - xr.getStart();
		        styleRanges.add( sr );
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
		 * @param xslTxtBox the xslTxtBox to set
		 */
		private void setXslTxtBox(StyledText xslTxtBox) {
			this.xslTxtBox = xslTxtBox;
		}

		
	}


