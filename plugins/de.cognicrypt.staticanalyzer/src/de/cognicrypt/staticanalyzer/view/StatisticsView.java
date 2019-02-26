package de.cognicrypt.staticanalyzer.view;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.handlers.AnalysisKickOff;
import de.cognicrypt.utils.Utils;

/**
 * This class creates a view which shows the results of an analysis.
 * 
 * @author Adnan Manzoor
 */

public class StatisticsView extends ViewPart {

	/**
	 * table which contains the results of the analysis
	 */
	private TableViewer viewer;
	private StyledText projectname;
	private StyledText timeofanalysis;
	private boolean resultsEnabled;
	private Button reRunButton;
	private Button stopAnalysisButton;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);
		resultsEnabled = true;

		// Project Name
		Label projectnameLabel = new Label(parent, SWT.NONE);
		projectnameLabel.setText("Project Name: ");
		projectname = new StyledText(parent, SWT.NONE);
		projectname.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		projectname.setText("Nothing for now");
		// projectname.setEditable(false);

		// Refresh Button
		reRunButton = new Button(parent, SWT.PUSH);
		reRunButton.setText("Rerun the Analysis on this Project");
		reRunButton.setEnabled(false);
		reRunButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		// register listener for the selection event
		reRunButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final AnalysisKickOff akf = new AnalysisKickOff();
				final IJavaElement iJavaElement = JavaCore.create(Utils.getCurrentProject());
				akf.setUp(iJavaElement);
				akf.run();
				resultsEnabled = true;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

		});

		// Time of Analysis
		Label timeofanalysisLabel = new Label(parent, SWT.NONE);
		timeofanalysisLabel.setText("Time of Analysis: ");
		timeofanalysis = new StyledText(parent, SWT.NONE);
		timeofanalysis.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		timeofanalysis.setWordWrap(true);

		// Stop Button
		stopAnalysisButton = new Button(parent, SWT.PUSH);
		stopAnalysisButton.setText("Stop");
		stopAnalysisButton.setEnabled(false);
		stopAnalysisButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		// register listener for the selection event
		stopAnalysisButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultsEnabled = false;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

		});

		// Results Table
		createViewer(parent);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.READ_ONLY | SWT.PUSH);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		getSite().setSelectionProvider(viewer);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = {"Class", "Seed", "Errors", "Health"};
		int[] bounds = {200, 250, 700, 200};

		// Class
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultsUnit u = (ResultsUnit) element;
				return u.getClassName();
			}
		});

		// Seed
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultsUnit u = (ResultsUnit) element;
				return u.getSeed();
			}
		});

		// Errors
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultsUnit u = (ResultsUnit) element;
				return u.getError();
			}
		});

		// Health
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultsUnit u = (ResultsUnit) element;
				return (u.isHealthy()) ? Constants.HEALTHY : Constants.UNHEALTHY;
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public String getProjectName() {
		return projectname.getText();
	}

	public void updateData(IProject project, String timeOfAnalysis, List<ResultsUnit> units) {
		if (resultsEnabled) {
			projectname.setText(project.getName());
			timeofanalysis.setText(timeOfAnalysis);
			viewer.setInput(units);
			viewer.refresh();
		}
	}

	private void allowAnalysisReRun(boolean isAllowed) {
		reRunButton.setEnabled(isAllowed);
		stopAnalysisButton.setEnabled(!isAllowed);
	}

	public static void allowAnalysisRerun(boolean isAllowed) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getView().allowAnalysisReRun(isAllowed);
				
			}
		});
	}

	public static void updateView(IProject project, String timeOfAnalysis, List<ResultsUnit> units) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getView().updateData(project, timeOfAnalysis, units);
			}
		});
	}

	private static StatisticsView getView() {
		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("de.cognicrypt.staticanalyzer.view.StatisticsView");
		if (viewPart != null) {
			return (StatisticsView) viewPart;
		}
		return null;
	}

}
