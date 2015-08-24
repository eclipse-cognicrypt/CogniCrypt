package crossing.e1.configurator.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Lables;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLableMapper;

/**
 * @author Ram
 *
 */

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer algorithmClass;
	private Label label2;
	private String value = "";
	private ClaferModel model;
	private Map<String, String> tasks;

	public TaskSelectionPage(ClaferModel claferModel) {
		super(Lables.SELECT_TASK);
		setTitle(Lables.TASK_LIST);
		setDescription(Lables.DESCRIPTION_TASK_SELECTION_PAGE);
		this.model = claferModel;

	}

	@Override
	public void createControl(Composite parent) {
		tasks = new HashMap<String, String>();
		ParseClafer parser = new ParseClafer();
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);

		label2 = new Label(container, SWT.NONE);
		label2.setText(Lables.LABEL2);
		for (String val : model.getTaskList(model.getModel()).keySet())
			tasks.put(parser.trim(val), val);
		algorithmClass = new ComboViewer(container, SWT.COMPOSITION_SELECTION);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(tasks.keySet());
		if (tasks.keySet().size() > 0) {
			algorithmClass.setSelection(new StructuredSelection(tasks.keySet()
					.toArray()[0]));
		} else {
			setPageComplete(false);
			algorithmClass
					.setSelection(new StructuredSelection(Lables.NO_TASK));

		}
		algorithmClass.setLabelProvider((new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		}));
		algorithmClass
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();

						ParseClafer parser = new ParseClafer();
						String b = (String) selection.getFirstElement()
								.toString();
						StringLableMapper.resetProperties();
						parser.setConstraintClafers(StringLableMapper
								.getTaskLables().get(tasks.get(b)));
						setValue(tasks.get(b));

					}

				});

		setControl(container);
	}

	public boolean canProceed() {
		if (value.length() > 0)
			return true;
		else
			return false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
