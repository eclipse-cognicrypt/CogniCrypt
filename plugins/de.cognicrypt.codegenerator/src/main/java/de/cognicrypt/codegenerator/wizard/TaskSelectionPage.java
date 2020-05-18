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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.core.Constants;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private Task selectedTask = null;

	public TaskSelectionPage() {
		super(Constants.SELECT_TASK);
		setTitle(Constants.TASK_LIST);
		setDescription(Constants.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {
		final List<Task> tasks = TaskJSONReader.getTasks();

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.container = new Composite(sc, SWT.NONE);
		this.container.setBounds(10, 10, 450, 200);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.TaskSelectionHelp");

		final GridLayout gl = new GridLayout(2, false);
		gl.verticalSpacing = -6;
		this.container.setLayout(gl);

		new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		new Label(this.container, SWT.NONE);
		final Label useCaseDescriptionLabel = new Label(this.container, SWT.WRAP);
		final GridData gd_selectProjectLabel = new GridData(SWT.FILL, SWT.FILL, false, false, 1, tasks.size() + 1);
		gd_selectProjectLabel.heightHint = 200;
		gd_selectProjectLabel.widthHint = 600;
		useCaseDescriptionLabel.setLayoutData(gd_selectProjectLabel);
		Font a = useCaseDescriptionLabel.getFont();
		useCaseDescriptionLabel.setFont(new Font(useCaseDescriptionLabel.getDisplay(), new FontData(a.getFontData()[0].getName(), 12, SWT.None)));

		final List<Button> buttons = new ArrayList<Button>();
		final List<Image> unclickedImages = new ArrayList<Image>();
		new Label(this.container, SWT.NONE);
		for (Task ccTask : tasks) {
			final Image taskImage = loadImage(ccTask.getImage());
			unclickedImages.add(taskImage);

			final Button taskButton = createImageButton(this.container, taskImage, ccTask.getDescription());
			buttons.add(taskButton);
		}
		buttons.stream().forEach(e -> e.addListener(SWT.Selection, new SelectionButtonListener(buttons, unclickedImages, tasks, useCaseDescriptionLabel)));
		buttons.get(0).notifyListeners(SWT.Selection, new Event());

		setControl(this.container);
		new Label(this.container, SWT.NONE);
		new Label(this.container, SWT.NONE);

		sc.setContent(this.container);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	public Task getSelectedTask() {
		return this.selectedTask;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}

	private Button createImageButton(final Composite container, final Image startImage, String taskName) {
		final Button imageButton = new Button(container, SWT.WRAP | SWT.TOGGLE);
		final Rectangle bounds = startImage.getBounds();
		imageButton.setSize(bounds.width, bounds.height);
		imageButton.setImage(startImage);
		imageButton.setToolTipText(taskName);
		return imageButton;
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

	class SelectionButtonListener implements Listener {

		private final List<Button> buttons;
		private final List<Image> buttonImages;
		private final List<Task> tasks;

		private final Label targetLabel;

		public SelectionButtonListener(final List<Button> buttons, final List<Image> buttonImages, final List<Task> tasks, final Label targetLabel) {
			if (buttons.size() != buttonImages.size() || buttons.size() != tasks.size()) {
				throw new IllegalArgumentException("All arrays are required to have the same length." + "If not it indicates an incomplete setup for buttons and their images");
			}

			this.buttons = buttons;
			this.buttonImages = buttonImages;
			this.tasks = tasks;
			this.targetLabel = targetLabel;
		}

		@Override
		public void handleEvent(final Event event) {
			final Button eventButton = (Button) event.widget;
			for (int i = 0; i < this.buttons.size(); i++) {
				final Button curIterationButton = this.buttons.get(i);
				if (eventButton.equals(curIterationButton)) {
					TaskSelectionPage.this.selectedTask = this.tasks.get(i);
					curIterationButton.setSelection(true);
					this.targetLabel.setText(TaskSelectionPage.this.selectedTask.getTaskDescription());
					setPageComplete(true);
				} else {
					curIterationButton.setSelection(false);
					curIterationButton.setImage(this.buttonImages.get(i));
				}
			}
		}
	}
}
