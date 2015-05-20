package crossing.e1.configurator.wizard;

import java.util.Arrays;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MyPageTwo extends WizardPage {
  
  private Composite container;
  private ComboViewer algorithmClass;
  private Label label1;
  private ComboViewer algorithm;
  private Label label2;
  private String[] example={"ex01","ex02","Example01","Example02","Example--01","Example--02"};//Tobe replaced by clafer values with constraint filter
  
  
  
  public MyPageTwo() {
    super("Second Page");
    setTitle("Available options");
    setDescription("User can choose below mentioned algorithm(s) for encryption");
    
  }

  @Override
  public void createControl(Composite parent) {
    container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 4;
   
    label1 = new Label(container, SWT.NONE);
	label1.setText("Select Algorithm Type");
    
    algorithmClass = new ComboViewer(container, SWT.BORDER | SWT.SINGLE);
	algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
    algorithmClass.setLabelProvider((new LabelProvider() {
	  @Override
	  public String getText(Object element) {
	    	    return element.toString();
	  }
	}));
	
    algorithmClass.setInput(Arrays.asList(example));
    algorithmClass.addSelectionChangedListener(new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event){
			
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		}

	});
    
    label2 = new Label(container, SWT.NONE);
	label2.setText("Select Algorithm");
    
    algorithm = new ComboViewer(container, SWT.BORDER );
    algorithm.setContentProvider(ArrayContentProvider.getInstance());
    algorithm.setLabelProvider((new LabelProvider() {
	  @Override
	  public String getText(Object element) {
	    	    return element.toString();
	  }
	}));
	
    algorithm.setInput(Arrays.asList(example));
    algorithm.addSelectionChangedListener(new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event){
			
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		}

	});
    
    
    
    
    
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
     
    // required to avoid an error in the system
    setControl(container);
    setPageComplete(false);
  }
  
  public void addField(String labelText){
	  System.out.println("adding field");
	  Label label = new Label(container, SWT.NONE);
	  label.setText(labelText);
	  container.layout();
  }

  
  public String getText1() {
    return "";
  }
}
 
