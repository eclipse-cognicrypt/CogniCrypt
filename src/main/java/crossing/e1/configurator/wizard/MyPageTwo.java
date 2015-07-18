package crossing.e1.configurator.wizard;


import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class MyPageTwo extends WizardPage {

	private Composite container;
	private ComboViewer algorithmClass;
	private Label label1;
	private Label label2;
	private List<InstanceClafer> options;
	private InstanceGenerator instance;

	public MyPageTwo(InstanceGenerator inst) {
		super("Second page");
		setTitle("Available options");
		setDescription("User can choose below mentioned algorithm(s) for encryption");
		this.instance=inst;
		

	}
	void setValue(Set<String> set){
		
		algorithmClass.setInput(set);
	}
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

		label1 = new Label(container, SWT.NONE);
		label1.setText("Select Algorithm Type");
		label2 = new Label(container, SWT.NONE);		
		Map<String,InstanceClafer> inst=instance.getInstances();
		algorithmClass = new ComboViewer(container, SWT.COMPOSITION_SELECTION);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
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
						String value="";
						String b=(String)selection.getFirstElement().toString();
						instance.displayInstanceValues(instance.getInstances().get(b));
						
						if (selection.size() > 0) {
							canFlipToNextPage();
							setPageComplete(true);
						}
					}

				});

		setControl(container);
		;
	}
	public boolean canFlipToNextPage() {

		return true;
	}

	public String getText1() {
		return "Heyyyy";
	}
}
