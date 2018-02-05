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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegion;
import de.cognicrypt.codegenerator.taskintegrator.controllers.XmlRegionAnalyzer;



public class CompositeForXsl extends Composite {
	private StyledText xslTxtBox;
	
		public CompositeForXsl(Composite parent, int style) {
			super(parent,SWT.BORDER);
			this.setBounds(0,0,887,500);
			setLayout(null);
			
			//UI Widgets for xslPage
			setXslTxtBox(new StyledText(this,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
			xslTxtBox.setBounds(0, 0, 887, 480);
			xslTxtBox.setCursor(null);
			
			
		}
		
		public void updateTheTextFieldWithFileData(String filePath){
						
			StringBuilder dataFromFile = new StringBuilder();
			
			Path path = Paths.get(filePath);
			
		if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
			.getFileName().toString().endsWith(".TXT")) {
				dataFromFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				dataFromFile.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n");
				dataFromFile.append("<xsl:output method=\"text\"/>\n");
				dataFromFile.append("<xsl:template match=\"/\">\n");
				// TODO for demo only. This data will be generated based on the XML document,.
				dataFromFile.append("<xsl:if test=\"//task[@description='LongTermArchiving']\">\n");
				dataFromFile.append("<xsl:result-document href=\"LongTermArchivingClient.java\">\n");
				dataFromFile.append("package <xsl:value-of select=\"//task/Package\"/>; \n");
				dataFromFile.append("<xsl:apply-templates select=\"//Import\"/>\n");
				
			}
			
			
			
			try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			    
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
			
		if (path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA") || path.getFileName().toString().endsWith(".txt") || path
			.getFileName().toString().endsWith(".TXT")) {
				dataFromFile.append("\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("<xsl:template match=\"Import\">\n");
				dataFromFile.append("import <xsl:value-of select=\".\"/>;\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("</xsl:stylesheet>");
				
				
			}
		
		List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(dataFromFile.toString());
		
		List<StyleRange> ranges =  computeStyle(regions);
		xslTxtBox.setText(dataFromFile.toString());
		
		for (StyleRange styleRange : ranges) {
			xslTxtBox.setStyleRange(styleRange);
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


