/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cognicrypt.codegenerator.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.utilities.Utils;
import de.cognicrypt.codegenerator.utilities.XMLParser;

/**
 * This class is responsible for displaying the instances the Clafer instance generator generated.
 *
 * @author Ram Kamath
 */
public class InstanceListPage extends WizardPage {

	private Composite control;
	private Text instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private Group instancePropertiesPanel;
	private TaskSelectionPage taskSelectionPage;
	private ConfiguratorWizard configuratorWizard;
  
	public InstanceListPage(final InstanceGenerator inst, final TaskSelectionPage taskSelectionPage, ConfiguratorWizard confWizard) {
		super(Constants.ALGORITHM_SELECTION_PAGE);
		setTitle("Possible solutions for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instanceGenerator = inst;
		this.taskSelectionPage = taskSelectionPage;
		this.configuratorWizard = confWizard;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		final GridLayout layout = new GridLayout(3, false);
		this.control.setLayout(layout);
		
		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");
		
		final Composite compositeControl = new Composite(this.control, SWT.NONE);		
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Constants.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		String firstInstance = inst.keySet().toArray()[0].toString();
		Combo combo = algorithmClass.getCombo();
	
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());		
		String key = instanceGenerator.getAlgorithmName();
		
		int count = combo.getItemCount();
		int variationCount = instanceGenerator.getAlgorithmCount();
		if(count > variationCount){
		    combo.setToolTipText("There are " + String.format("%d", count) + " solutions ");
		} else {
			combo.setToolTipText("There are " + String.format("%d", variationCount) + " variations of the algorithm " + key);
		}
		
		//Display help assist for the first instance in the combo box
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		Text infoText = new Text(control, SWT.BORDER | SWT.WRAP );
		infoText.setText(Constants.DEFAULT_ALGORITHM_NOTIFICATION);
		infoText.setEditable(false);
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));		
		final ControlDecoration deco = new ControlDecoration(infoText, SWT.RIGHT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage();		
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);	
		
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		Composite composite_Control = new Composite(this.instancePropertiesPanel, SWT.BOTTOM | SWT.CENTER);
		composite_Control.setLayoutData(new GridData(SWT.CENTER, GridData.FILL, true, false));
		composite_Control.setLayout(new GridLayout(3, true)); 
		
		//Back button to go to the previous algorithm in the combo box
		Button backIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
		backIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		backIcon.setText("<");	
		backIcon.setToolTipText("Previous");
		backIcon.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {				
				int temp = combo.getSelectionIndex();
				if (temp != 0){							
					temp = temp - 1;
				    final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
					algorithmClass.setSelection(selection);
				    }
			}
		});
		
		//Label that displays the current algorithm variation and the total number of variations
		Label algorithmVariation = new Label(composite_Control, SWT.CENTER | SWT.BOTTOM);
		algorithmVariation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		//Button to go to the next algorithm in the combo box
		Button nextIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
		nextIcon.setText(">");
		nextIcon.setToolTipText("Next");
		nextIcon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int temp = combo.getSelectionIndex();
				if (temp != (count-1)){
					temp = temp + 1;
			        final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
				    algorithmClass.setSelection(selection);				    
				}
				
			}
		});
		
		algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm = selection.getFirstElement().toString();	
			setValue(InstanceListPage.this.instanceGenerator.getInstances().get(selectedAlgorithm));
			InstanceListPage.this.instanceDetails.setText(getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(selectedAlgorithm)));
			int index = combo.getSelectionIndex();
			if(count > variationCount){
			    algorithmVariation.setText("  Solution  " + (index + 1) + " / " + String.format("%d  ",count ));
			} else {
				algorithmVariation.setText("  Variation  " + (index + 1) + " / " + String.format("%d  ",variationCount ));
			}
			if (!selectedAlgorithm.equals(firstInstance)) {
				//hide the help assist and the text if the selected algorithm is not the default algorithm
				deco.hide();
				infoText.setVisible(false);	
				backIcon.setEnabled(true);
			} else {
				infoText.setVisible(true);
				deco.show();
				//disable back button if the selected algorithm in the combo box is the first instance
				backIcon.setEnabled(false);
			}
			if (combo.getSelectionIndex() == count-1){
				//disable next button if the selected algorithm in the combo box is the last instance
				nextIcon.setEnabled(false);
			} else {
				nextIcon.setEnabled(true);
			}
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		});		
		
		this.instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout = new GridLayout();
		this.instancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		gridData.heightHint=200;
		this.instancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);
		this.instancePropertiesPanel.setLayoutData(gridData);
		final Font boldFont = new Font(this.instancePropertiesPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.instancePropertiesPanel.setFont(boldFont);
		
		//Hide scroll bar in instance details text box
		Listener scrollBarListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				Rectangle r1 = t.getClientArea();
				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
				Point p = t.computeSize(r1.x, SWT.DEFAULT, true);
				t.getVerticalBar().setVisible(r2.height <= p.y);
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		Display display = Display.getCurrent();
		this.instanceDetails.addListener(SWT.Resize, scrollBarListener);
		this.instanceDetails.addListener(SWT.Modify, scrollBarListener);
		this.instanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails.setBounds(10, 20, 400, 180);
		this.instanceDetails.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.instanceDetails.setBackground(white);
		
		// Initially instance properties panel will be hidden		
		this.instancePropertiesPanel.setVisible(false);
		setControl(this.control);
		
		final ISelection selection = new StructuredSelection(inst.keySet().toArray()[0]);
		algorithmClass.setSelection(selection);
		new Label(control, SWT.NONE);

		//Button to View the code that will be generated into the Java project
		Button codePreviewButton = new Button(this.control, SWT.NONE);
		codePreviewButton.setText("Code Preview");
		codePreviewButton.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		        MessageBox messageBox = new MessageBox(new Shell(),SWT.OK);
		        messageBox.setText("Code Preview");
		        messageBox.setMessage(getCodePreview() );
		        messageBox.open();		   		    	
		        }
		      
		      });
		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);		
	}

	private void getInstanceDetails(final InstanceClafer inst, final Map<String, String> algorithms) {
		String value;

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " : " + ClaferModelUtils.removeScopePrefix(inst.getType().getRef().getTargetType().getName().replaceAll("([a-z0-9])([A-Z])","$1 $2")) + Constants.lineSeparator;			
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName().replaceAll("([a-z0-9])([A-Z])","$1 $2"));
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(in.getType().getName().replaceAll("([a-z0-9])([A-Z])","$1 $2")) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
					value = value.replaceAll("([a-z0-9])([A-Z])","$1 $2");
				}
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName().replaceAll("([a-z0-9])([A-Z])","$1 $2")) + " : " + inst.getRef().toString();
				algo = algorithms.keySet().iterator().next();
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}

	/**
	 * The user might select an algorithm configuration/instance from the combobox. This method returns the details of the currently selected algorithm, which is passed as a
	 * parameter.
	 *
	 * @param inst
	 *        instance currently selected in the combo box
	 * @return details for chosen algorithm configuration
	 */
	private String getInstanceProperties(final InstanceClafer inst) {
		final Map<String, String> algorithms = new HashMap<>();
		for (InstanceClafer child : inst.getChildren()) {
			getInstanceDetails(child, algorithms);
		}

		StringBuilder output = new StringBuilder();
		for (final Map.Entry<String, String> entry : algorithms.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (!value.isEmpty()) {
				output.append(key);
				output.append(value);
				output.append(Constants.lineSeparator);
			}
		}
		return output.toString().replaceAll("([a-z0-9])([A-Z])","$1 $2");
	}

	/**
	 * This method extracts the provider's name from the instanceDetails
	 * 
	 * @return
	 */
	public String getProviderFromInstance() {
		for (String instance : this.instanceDetails.getText().split(Constants.lineSeparator)) {
			if (instance.contains("Provider")) {
				return instance.split(": ")[1];
			}
		}
		return "";
	}

	/**
	 * Preview of the code, for the algorithm configuration/instance selected from the combobox, can be displayed by calling this method .
	 * 
	 * @return preview of the code that will be generated in the Java project for chosen algorithm configuration
	 */
	public String getCodePreview() {
		XSLBasedGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject(), this.getProviderFromInstance());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.getValue(), this.configuratorWizard.getConstraints());
		try {
			xmlparser.writeClaferInstanceToFile(claferPreviewPath);
		} catch (IOException e) {
			Activator.getDefault().logError(e, Constants.WritingInstanceClaferErrorMessage);
			return "";
		}

		File claferPreviewFile = new File(claferPreviewPath);

		// Check whether directories and templates/model exist
		final File claferOutputFiles = claferPreviewFile != null && claferPreviewFile.exists() ? claferPreviewFile
			: Utils.getResourceFromWithin(Constants.pathToClaferInstanceFolder + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
		final File xslFile = Utils.getResourceFromWithin(this.taskSelectionPage.getSelectedTask().getXslFile());
		if (!claferOutputFiles.exists() || !xslFile.exists()) {
			Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
			return "";
		}
		// Perform actual transformation by calling XSLT processor.

		final String temporaryOutputFile = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.CodeGenerationCallFile;

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer(new StreamSource(xslFile));
		} catch (TransformerConfigurationException e) {
			Activator.getDefault().logError(e, Constants.TransformerConfigurationErrorMessage);
			return "";
		}
		File outputFile = new File(temporaryOutputFile);
		try {
			transformer.transform(new StreamSource(claferPreviewFile), new StreamResult(outputFile));
		} catch (TransformerException e) {
			Activator.getDefault().logError(e, Constants.TransformerErrorMessage);
			return "";
		}

		Path file = outputFile.toPath();
		try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("import")) {
					sb.append(line);
					sb.append("\n");
				}
			}

			return sb.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
		} catch (IOException e) {
			Activator.getDefault().logError(e, Constants.CodePreviewErrorMessage);
		}

		return "";
	}

	public TaskSelectionPage getTaskSelectionPage() {
		return taskSelectionPage;
	}
	
	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}
	
	public InstanceClafer getValue() {
		return this.value;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			control.setFocus();
		}
	}
	
}
