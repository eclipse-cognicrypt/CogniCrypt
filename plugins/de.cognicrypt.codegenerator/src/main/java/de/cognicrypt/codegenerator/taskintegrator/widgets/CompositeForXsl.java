package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;



public class CompositeForXsl extends Composite {
	private StyledText xslTxtBox;
	
		public CompositeForXsl(Composite parent, int style) {
			super(parent,SWT.BORDER);
			this.setBounds(0,0,887,500);
			setLayout(null);
			
			//UI Widgets for xslPage
			//Text xslTxtBox= new Text(this,SWT.MULTI|SWT.V_SCROLL);
			setXslTxtBox(new StyledText(this,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL));
			xslTxtBox.setBounds(0, 0, 887, 480);
			xslTxtBox.setCursor(null);
			
		}
		
		public void updateTheTextFieldWithFileData(String filePath){
						
			StringBuilder dataFromFile = new StringBuilder();
			
			Path path = Paths.get(filePath);
			
			if(path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA")){
				dataFromFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				dataFromFile.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n");
				dataFromFile.append("<xsl:output method=\"text\"/>\n");
				dataFromFile.append("<xsl:template match=\"/\">\n");
				
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
			
			if(path.getFileName().toString().endsWith(".java") || path.getFileName().toString().endsWith(".JAVA")){
				dataFromFile.append("\n");
				dataFromFile.append("</xsl:template>\n");
				dataFromFile.append("</xsl:stylesheet>");
				
				
			}
			
			((StyledText) this.getChildren()[0]).setText(dataFromFile.toString());
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


