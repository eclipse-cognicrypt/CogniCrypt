/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.core.Constants;

public class CompareAlgorithmPage extends WizardPage {

	private IProject selectedProject = null;
	Text text1;
	private Composite control;
	private Group firstInstancePropertiesPanel;
	private Group secondInstancePropertiesPanel;
	private InstanceListPage instanceListPage;
	private InstanceGenerator instanceGenerator;
	private StyledText firstInstanceDetails;
	private StyledText secondInstanceDetails;
	private InstanceClafer value;
	private String algorithmSelectedSecond;
	private String algorithmSelectedFirst;
	private Text notifyText;
	private String algorithmSelected;

	public CompareAlgorithmPage(InstanceListPage instanceListPage, InstanceGenerator instanceGenerator) {
		super(Constants.COMPARE_ALGORITHM_PAGE);
		setTitle(Constants.COMPARE_TITLE);
		setDescription(Constants.COMPARE_DESCRIPTION);
		this.instanceListPage = instanceListPage;
		this.instanceGenerator = instanceGenerator;
	}

	@Override
	public void createControl(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Display display = Display.getCurrent();
		ComboViewer firstAlgorithmClass;
		Label firstLabelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.control.setLayout(layout);

		//Second Set
		ComboViewer secondAlgorithmClass;
		Label secondLabelInstanceList;

		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();

		//First set of Algorithm Combinations
		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		firstLabelInstanceList = new Label(compositeControl, SWT.NONE);
		firstLabelInstanceList.setText(Constants.COMPARE_LABEL);

		firstAlgorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		Object algorithmCombinationFirst = instanceListPage.getAlgorithmCombinations();
		firstAlgorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		firstAlgorithmClass.setInput(algorithmCombinationFirst);

		//Second set of Algorithm Combinations
		final Composite compositeControl1 = new Composite(this.control, SWT.NONE);
		compositeControl1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		setPageComplete(false);
		compositeControl1.setLayout(new GridLayout(2, false));
		secondLabelInstanceList = new Label(compositeControl1, SWT.NONE);
		secondLabelInstanceList.setText(Constants.COMPARE_LABEL);

		secondAlgorithmClass = new ComboViewer(compositeControl1, SWT.DROP_DOWN | SWT.READ_ONLY);
		Object algorithmCombinationSecond = instanceListPage.getAlgorithmCombinations();
		secondAlgorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		secondAlgorithmClass.setInput(algorithmCombinationSecond);
		notifyText = new Text(control, SWT.WRAP | SWT.BORDER);
		notifyText.setText(Constants.COMPARE_SAME_ALGORITHM);
		notifyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		notifyText.setEditable(false);
		ControlDecoration deco = new ControlDecoration(notifyText, SWT.RIGHT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage();
		deco.setImage(image);

		//First set of Instance details
		this.firstInstancePropertiesPanel = new Group(this.control, SWT.NONE);
		firstInstancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		this.firstInstanceDetails = new StyledText(this.firstInstancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.firstInstanceDetails.setAlwaysShowScrollBars(false);

		firstAlgorithmClass.addSelectionChangedListener(event -> {
			String selectedAlgorithmSecond = getSelectedAlgorithmSecond();
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.firstInstancePropertiesPanel.setVisible(true);
			final String selectedAlgorithmFirst = selection.getFirstElement().toString();
			setSelectedAlgorithmFirst(selectedAlgorithmFirst);
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmFirst));
			CompareAlgorithmPage.this.firstInstanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmFirst)));
			setHighlightFirst(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmFirst)));
			if (!selectedAlgorithmFirst.equals(selectedAlgorithmSecond)) {
				deco.hide();
				notifyText.setVisible(false);
			} else {
				notifyText.setVisible(true);
				deco.show();
			}

			compareHighlight();
		});

		GridLayout gridLayout = new GridLayout();
		this.firstInstancePropertiesPanel.setLayout(gridLayout);
		GridData gridDataFirst = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridDataFirst.widthHint = 60;
		gridDataFirst.horizontalSpan = 1;
		gridDataFirst.heightHint = 89;
		this.firstInstancePropertiesPanel.setLayoutData(gridDataFirst);
		this.firstInstancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		this.firstInstanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.firstInstanceDetails.setBounds(10, 20, 400, 180);
		this.firstInstanceDetails.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.firstInstanceDetails.setBackground(white);

		//Second set of Instance details
		this.secondInstancePropertiesPanel = new Group(this.control, SWT.NONE);
		secondInstancePropertiesPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.secondInstanceDetails = new StyledText(this.secondInstancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.secondInstanceDetails.setAlwaysShowScrollBars(false);

		//Tandem horizontal scrolling
		handleVerticalScrolling(firstInstanceDetails, secondInstanceDetails);

		secondAlgorithmClass.addSelectionChangedListener(event -> {
			String selectedAlgorithmFirst = getSelectedAlgorithmFirst();
			final IStructuredSelection selection1 = (IStructuredSelection) event.getSelection();
			CompareAlgorithmPage.this.secondInstancePropertiesPanel.setVisible(true);
			final String selectedAlgorithmSecond = selection1.getFirstElement().toString();
			setSelectedAlgorithmSecond(selectedAlgorithmSecond);
			setValue(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmSecond));
			CompareAlgorithmPage.this.secondInstanceDetails.setText(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmSecond)));
			setHighlightSecond(getInstanceProperties(CompareAlgorithmPage.this.instanceGenerator.getInstances().get(selectedAlgorithmSecond)));
			if (!selectedAlgorithmFirst.equals(selectedAlgorithmSecond)) {
				notifyText.setVisible(false);
				deco.hide();
			} else {
				notifyText.setVisible(true);
				deco.show();
			}
			compareHighlight();
		});

		this.secondInstancePropertiesPanel.setLayout(new GridLayout());
		GridData gridDataSecond = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridDataSecond.widthHint = 60;
		gridDataSecond.horizontalSpan = 1;
		gridDataSecond.heightHint = 89;
		this.secondInstancePropertiesPanel.setLayoutData(gridDataSecond);
		this.secondInstancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);

		this.secondInstanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.secondInstanceDetails.setBounds(10, 20, 259, 216);
		this.secondInstanceDetails.setEditable(false);
		this.secondInstanceDetails.setBackground(white);

		final ISelection defaultAlgorithm = new StructuredSelection(inst.keySet().toArray()[0]);
		firstAlgorithmClass.setSelection(defaultAlgorithm);
		secondAlgorithmClass.setSelection(defaultAlgorithm);

		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	// To automatically scroll the contents of one text if the scroll bar of the other text is scrolled.
	private static void handleVerticalScrolling(StyledText leftInstanceDetails, StyledText rightInstanceDetails) {
		ScrollBar left = leftInstanceDetails.getVerticalBar();
		ScrollBar right = rightInstanceDetails.getVerticalBar();

		left.addListener(SWT.Selection, e -> {
			int y = leftInstanceDetails.getTopPixel();
			rightInstanceDetails.setTopPixel(y);
		});
		right.addListener(SWT.Selection, e -> {
			int y = rightInstanceDetails.getTopPixel();
			leftInstanceDetails.setTopPixel(y);
		});
	}

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
		return output.toString();
	}

	public void getInstanceDetails(final InstanceClafer inst, final Map<String, String> algorithms) {
		String value;

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " : " + ClaferModelUtils
				.removeScopePrefix(inst.getType().getRef().getTargetType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + Constants.lineSeparator;
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils
						.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(
					in.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
					value = value.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
				}

				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName().replaceAll("([a-z0-9])([A-Z])", "$1 $2")) + " : " + inst.getRef().toString();
				algo = algorithms.keySet().iterator().next();
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}

	public void compareHighlight() {

		Display display = Display.getCurrent();
		String firstAlgorithmHighlight = getHighlightFirst();
		String secondAlgorithmHighlight = getHighlightSecond();
		if (secondAlgorithmHighlight != null) {
			Color cyan = display.getSystemColor(SWT.COLOR_CYAN);
			Color transparent = display.getSystemColor(SWT.COLOR_TRANSPARENT);

			String[] partFirstInstanceDetails = firstAlgorithmHighlight.split("\n\r\n");
			String[] lines1;
			String[] firstHalf1;

			String[] partSecondInstanceDetails = secondAlgorithmHighlight.split("\n\r\n");
			String[] lines2;
			String[] firstHalf2;

			/*
			 * The two windows are being declared into 2 separate ArrayLists in the following section. Each ArrayList is a collection of LinkedHashMaps which contains each of the
			 * blocks of data. Each line in the block are arranged as key value pairs in the LinkedHashMap
			 */
			ArrayList<LinkedHashMap<String, String>> firstPart = new ArrayList<LinkedHashMap<String, String>>();
			//Initializing the LinkedHashMaps for later use
			for (int i = 0; i < partFirstInstanceDetails.length; i++) {
				firstPart.add(new LinkedHashMap<String, String>());
			}
			//Populating the LinkedHashMaps with the lines(Key Value Pairs) and adding it to the ArrayList
			for (int x = 0; x < partFirstInstanceDetails.length; x++) {
				lines1 = partFirstInstanceDetails[x].split("\r\n\t");
				for (int y = 0; y < lines1.length; y++) {
					firstHalf1 = lines1[y].split(" : ");
					firstPart.get(x).put(firstHalf1[0], firstHalf1[1]);
				}

			}

			ArrayList<LinkedHashMap<String, String>> secondPart = new ArrayList<LinkedHashMap<String, String>>();
			//Initializing the LinkedHashMaps for later use
			for (int i = 0; i < partSecondInstanceDetails.length; i++) {
				secondPart.add(new LinkedHashMap<String, String>());
			}
			//Populating the LinkedHashMaps with the lines(Key Value Pairs) and adding it to the ArrayList
			for (int x = 0; x < partSecondInstanceDetails.length; x++) {
				lines2 = partSecondInstanceDetails[x].split("\r\n\t");
				for (int y = 0; y < lines2.length; y++) {
					firstHalf2 = lines2[y].split(" : ");
					secondPart.get(x).put(firstHalf2[0], firstHalf2[1]);
				}

			}
			String key = null;
			String value = null;
			int t = 0;		//position inside the first styled text box
			int s = 0;		//position inside the second styled text box
			int pos1;		//position inside a particular block of the first styled text box
			int pos2;		//position inside a particular block of the second styled text box
			int y;
			//first loop with x parameter loops through each block in the first styled text box
			for (int x = 0; x < firstPart.size(); x++) {
				//second loop with y parameter loops through each block in the second styled text box
				for (y = 0; y < secondPart.size(); y++) {
					//comparing the values for the 'Algorithm' Key
					String firstKey = (String) (firstPart.get(x).keySet().toArray())[0];
					String firstValue = firstPart.get(x).get(firstKey);
					if (secondPart.get(y).containsKey(firstKey)) {
						if (secondPart.get(y).get(firstKey).equals(firstValue)) {
							Iterator<String> iterator1 = firstPart.get(x).keySet().iterator();
							while (iterator1.hasNext()) {
								key = (String) iterator1.next();
								value = firstPart.get(x).get(key);
								if (secondPart.get(y).containsKey(key)) {
									if (secondPart.get(y).get(key).equals(value)) {

										//getting the index of the current key in the block
										pos1 = new ArrayList<String>(firstPart.get(x).keySet()).indexOf(key);
										//highlighting the line which is calculated by adding pos1 with the t offset
										firstInstanceDetails.setLineBackground(t + pos1, 1, transparent);
										//getting the index of the current key in the block
										pos2 = new ArrayList<String>(secondPart.get(y).keySet()).indexOf(key);
										//highlighting the line which is calculated by adding pos2 with the s offset
										secondInstanceDetails.setLineBackground(s + pos2, 1, transparent);
									} else {
										//getting the index of the current key in the block
										pos1 = new ArrayList<String>(firstPart.get(x).keySet()).indexOf(key);
										//highlighting the line which is calculated by adding pos1 with the t offset
										firstInstanceDetails.setLineBackground(t + pos1, 1, cyan);
										//getting the index of the current key in the block
										pos2 = new ArrayList<String>(secondPart.get(y).keySet()).indexOf(key);
										//highlighting the line which is calculated by adding pos2 with the s offset
										secondInstanceDetails.setLineBackground(s + pos2, 1, cyan);
									}
								}
								//								else {
								//									//show red
								//								}
							}
							break;
						}
					}
					//					else {
					//						//show the block red
					//					}
					//offsetting the block length by adding the current block size to s
					s = s + secondPart.get(y).size() + 1;
				}
				//resetting the offset as the second block comparison will begin from the start again with the main loop iteration.
				s = 0;
				//offsetting the block length by adding the current block size to t
				t = t + firstPart.get(x).size() + 1;

			}
		}

	}

	public String getText1() {
		return text1.getText();
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.control.setFocus();
		}
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	public void setHighlightFirst(final String algorithmSelectedFirst) {
		this.algorithmSelectedFirst = algorithmSelectedFirst;
	}

	public String getHighlightFirst() {
		return this.algorithmSelectedFirst;
	}

	public void setHighlightSecond(final String algorithmSelectedSecond) {
		this.algorithmSelectedSecond = algorithmSelectedSecond;
	}

	public String getHighlightSecond() {
		return this.algorithmSelectedSecond;
	}

	public void setSelectedAlgorithmFirst(final String algorithmSelected) {
		this.algorithmSelected = algorithmSelected;
	}

	public String getSelectedAlgorithmFirst() {
		return this.algorithmSelected;
	}

	public void setSelectedAlgorithmSecond(final String algorithmSelected) {
		this.algorithmSelected = algorithmSelected;
	}

	public String getSelectedAlgorithmSecond() {
		return this.algorithmSelected;

	}

}
