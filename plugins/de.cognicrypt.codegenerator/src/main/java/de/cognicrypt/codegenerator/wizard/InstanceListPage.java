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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.utilities.Labels;
import de.cognicrypt.codegenerator.utilities.Utils;
import de.cognicrypt.codegenerator.utilities.XMLParser;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

/**
 * This class is responsible for displaying the instances the Clafer instance generator generated.
 *
 * @author Ram Kamath
 */
public class InstanceListPage extends WizardPage implements Labels {

	private Composite control;
	private Text instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private Group instancePropertiesPanel;
	private TaskSelectionPage taskSelectionPage;
	private ConfiguratorWizard configuratorWizard;

	public InstanceListPage(final InstanceGenerator inst, final TaskSelectionPage taskSelectionPage, ConfiguratorWizard confWizard) {
		super(Labels.ALGORITHM_SELECTION_PAGE);
		setTitle("Possible solutions for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Labels.DESCRIPTION_INSTANCE_LIST_PAGE);
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
		
		/** To display the Help view after clicking the help icon
		 * @param help_id_2 
		 *        This id refers to HelpContexts_1.xml
		 */
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");
		
		final Composite compositeControl = new Composite(this.control, SWT.NONE);		
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Labels.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		String firstInstance = inst.keySet().toArray()[0].toString();
		Combo combo = algorithmClass.getCombo();
		String key=instanceGenerator.getAlgorithmName();
		int count=instanceGenerator.getAlgorithmCount();
		combo.setToolTipText("There are " + String.format("%d",count ) +" variations of the algorithm "+key);
//		combo.setToolTipText(Constants.ALGORITHM_COMBO_TOOLTIP);
		
		//Display help assist for the first instance in the combo box
		final ControlDecoration deco = new ControlDecoration(combo, SWT.TOP | SWT.RIGHT );
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
		.getImage();
		
		deco.setDescriptionText("This algorithm was presented to you previously,\n as the best algorithm combination.");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
		
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());	
        algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String b = selection.getFirstElement().toString();
			setValue(InstanceListPage.this.instanceGenerator.getInstances().get(b));
			InstanceListPage.this.instanceDetails.setText(getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(b)));
			
			if(!b.equals(firstInstance))
				//hide the help assist if the selected algorithm is not the default algorithm
				deco.hide();
			else
				deco.show();
			
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		});
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);

		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.instancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		this.instancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);
		this.instancePropertiesPanel.setLayoutData(gridData);
//		this.instancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final Font boldFont = new Font(this.instancePropertiesPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.instancePropertiesPanel.setFont(boldFont);

		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		//Hide scroll bar 
		Listener scrollBarListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				Rectangle r1 = t.getClientArea();
				// use r1.x as wHint instead of SWT.DEFAULT
				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
				Point p = t.computeSize(r1.x, SWT.DEFAULT, true);
				t.getVerticalBar().setVisible(r2.height <= p.y);
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		this.instanceDetails.addListener(SWT.Resize, scrollBarListener);
		this.instanceDetails.addListener(SWT.Modify, scrollBarListener);
		this.instanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails.setBounds(10, 20, 400, 180);
		this.instanceDetails.setEditable(false);
		/*
		 * Initially instance properties panel will be hidden
		 */
		this.instancePropertiesPanel.setVisible(false);
		setControl(this.control);
		final ISelection selection = new StructuredSelection(inst.keySet().toArray()[0]);
		algorithmClass.setSelection(selection);
		new Label(control, SWT.NONE);
		
		//Button to View the code that will be generated into the Java project
		
		Button codePreviewButton = new Button(control, SWT.NONE);
		codePreviewButton.setText("Code Preview");
		codePreviewButton.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
//		    	PopupDialog pop= new PopupDialog(new Shell(),3,true,true,true,true,true,"Code Preview",getCodePreview());
//		    	pop.open();
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
		String value = "";

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " :" + ClaferModelUtils.removeScopePrefix(inst.getType().getRef().getTargetType().getName()) + Constants.lineSeparator;
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName());
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(in.getType().getName()) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
				}
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + " : " + inst.getRef().toString().replace("\"", "");
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
		final InstanceClafer[] children = inst.getChildren();
		for (int i = 0; i < children.length; i++) {
			getInstanceDetails(children[i], algorithms);
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
		return output.toString();
	}

	/**
	 * This method extracts the provider's name from the instanceDetails
	 * @return
	 */
	public String getProviderFromInstance() {
		String providerName = "";
		String[] inst = this.instanceDetails.getText().split(System.getProperty("line.separator"));
		for (int i = 0; i < inst.length; i++) {
			if (inst[i].contains("Provider")) {
				providerName=inst[i].split(": ")[1];
				break;
			}
		}
		return providerName;
	}

	private String getCodePreview() {
		XSLBasedGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.getValue(), this.configuratorWizard.getConstraints());
		try {
			xmlparser.writeClaferInstanceToFile(claferPreviewPath);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		File claferPreviewFile = new File(claferPreviewPath);

		// Check whether directories and templates/model exist
		final File claferOutputFiles = claferPreviewFile != null && claferPreviewFile.exists() ? claferPreviewFile
			: Utils.getResourceFromWithin(Constants.pathToClaferInstanceFolder + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
		final File xslFile = Utils.getResourceFromWithin(Constants.pathToXSLFile);
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
			e.printStackTrace();
			return "";
		}
		File outputFile = new File(temporaryOutputFile);
		try {
			transformer.transform(new StreamSource(claferPreviewFile), new StreamResult(outputFile));
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}

		Path file = outputFile.toPath();
		try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(!line.startsWith("import")){
				sb.append(line);
				sb.append("\n");
			}
			}

			return sb.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
		} catch (IOException x) {
			System.err.println(x);
		}

		return "";
	}

	public TaskSelectionPage getTaskSelectionPage() {
		return taskSelectionPage;
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete);
	}

	@Override
	public void setVisible( boolean visible ) {
	  super.setVisible( visible );
	  if( visible ) {
	    control.setFocus();
	  }
	}
	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}
}
