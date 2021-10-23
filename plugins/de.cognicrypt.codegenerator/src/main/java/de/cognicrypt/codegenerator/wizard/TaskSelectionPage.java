/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.core.Constants;

public class TaskSelectionPage extends WizardPage {

	private TaskItemComposite selectedTaskItem = null;
	private Composite listOfTaskItems; // Row Layout Composite that holds task items composite
	private List<TaskItemComposite> taskItems = new ArrayList<TaskItemComposite>(); // ArrayList of all tasks item composite
	
	public TaskSelectionPage() {
		super(Constants.SELECT_TASK);
		setTitle(Constants.TASK_LIST);
		setDescription(Constants.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {

		// Make the content able to scroll
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.TaskSelectionHelp");

		// listOfTaskItems will hold the selection items for all tasks
		// it is attached in the ScrolledComposite
		this.listOfTaskItems = new Composite(sc, SWT.NONE);
		
		// Task items are displayed as a list in the listOfTaskItems
		final GridLayout rl = new GridLayout(1, false);
		this.listOfTaskItems.setLayout(rl);
		this.listOfTaskItems.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		// add tasks items to listOfTaskItems
		for (Task ccTask: TaskJSONReader.getTasks()) {
			taskItems.add(new TaskItemComposite(this.listOfTaskItems, ccTask));
		}
		
		sc.setContent(this.listOfTaskItems);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setAlwaysShowScrollBars(true);
		sc.addListener( SWT.Resize, event -> {
			 sc.setMinSize(this.listOfTaskItems.computeSize( getShell().getClientArea().width - sc.getVerticalBar().getSize().x, SWT.DEFAULT));
			} );
		this.setControl(sc);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.listOfTaskItems.setFocus();
		}
	}

	private Image loadImage(final String image) {
		try {
			final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			if (bundle == null) {
				return null;
			}

			final URL entry = bundle.getEntry("src/main/resources/images/" + image + ".png");
			final URL resolvedURL = FileLocator.toFileURL(entry);
			URI resolvedURI = null;
			if (resolvedURL != null) {
				resolvedURI = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
			} else {
				resolvedURI = FileLocator.resolve(entry).toURI();
			}

			return new Image(PlatformUI.getWorkbench().getDisplay(), new FileInputStream(new File(resolvedURI)));
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
		
	}
	
	/**
	 * A single choice selection on all task items. When calling this method, only one task item will be selected.
	 */
	public void selectTaskItem(TaskItemComposite taskItem) {
		// unselect other task items
		for(TaskItemComposite notSelectedTaskItem: this.taskItems) {
			if(notSelectedTaskItem != taskItem) {
				notSelectedTaskItem.unselect();
			}
		}
		taskItem.select();
		TaskSelectionPage.this.selectedTaskItem = taskItem;
		
		setPageComplete(true); // next button on wizard is now clickable
	}
	
	public Task getSelectedTask() {
		return this.selectedTaskItem.getTask();
	}
	
	
	/**
	 * This class will append a row with a image button, title and description for a task.
	 * If any Element in this Composite is pressed, a ItemClickListener is triggered.
	 * @param listOfTaskItems is the list, where to add the item
	 * @param task to add in the row
	 */
	class TaskItemComposite extends Composite {
		
		private Task task;
		private Button button;

		TaskItemComposite(final Composite listOfTaskItems, Task task) {
			
			// listOfTaskItems is filled with a Group, which has a two row grid layout.
			super(listOfTaskItems, SWT.NONE);
			this.setLayout(new GridLayout(1, false));
			this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			final Group group = new Group(this, SWT.SHADOW_ETCHED_OUT);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			
			// First column gets a radio button with the image
			this.button = new Button(group, SWT.TOGGLE | SWT.RADIO);
			this.button.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			final Image taskImage = loadImage(task.getImage());
			if(taskImage == null) {
				throw new IllegalArgumentException("Missing Image for Task: " + task.getName());
			}
			final Rectangle bounds = taskImage.getBounds();
			this.button.setSize(bounds.width, bounds.height);
			this.button.setImage(taskImage);
			
			// Second column gets group with title and description
			final Composite descr = new Composite(group, SWT.NONE);
			descr.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			descr.setLayout(new GridLayout(1, false));
			
			// title
			final Label title = new Label(descr, SWT.WRAP);
			title.setText(task.getDescription());
			final Font boldFont = new Font( title.getDisplay(), new FontData( "Arial", 12, SWT.BOLD ) );
			title.setFont(boldFont);
			
			// description
			final Label taskdescr = new Label(descr, SWT.WRAP);
			final Font largeFont = new Font( descr.getDisplay(), new FontData( "Arial", 14, SWT.NONE ) );
			taskdescr.setFont(largeFont);
			
			final Color gray = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
			taskdescr.setForeground(gray);
			taskdescr.setText(task.getTaskDescription());
			taskdescr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			taskdescr.pack();
			
			this.task = task;
			
			// click listener to all elements
			// TODO: one Listener for the whole Composite would be nice
			ItemClickListener listener = new ItemClickListener(this);
			this.addListener(SWT.MouseUp, listener);
			this.button.addListener(SWT.MouseUp, listener);
			descr.addListener(SWT.MouseUp, listener);
			title.addListener(SWT.MouseUp, listener);
			taskdescr.addListener(SWT.MouseUp, listener);
			
			// add dispose listener (best practice: https://www.eclipse.org/articles/swt-design-2/swt-design-2.html)
			this.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent event) {
					boldFont.dispose();
					largeFont.dispose();
					taskImage.dispose();
				}
			});
			
			
		}
		
		public void select() {
			this.button.setSelection(true);
		}
		
		public void unselect() {
			this.button.setSelection(false);
		}
		
		public Task getTask() {
			return this.task;
		}
		
	}

	/**
	 * This class listens to clicks on task item components.
	 */
	class ItemClickListener implements Listener {

		private final TaskItemComposite taskItem;

		public ItemClickListener(TaskItemComposite taskItem) {
			this.taskItem = taskItem;
		}

		@Override
		public void handleEvent(Event event) {
			selectTaskItem(this.taskItem);
		}
	}
}
