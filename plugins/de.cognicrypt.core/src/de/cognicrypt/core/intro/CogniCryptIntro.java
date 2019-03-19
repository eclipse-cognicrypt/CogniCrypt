package de.cognicrypt.core.intro;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import de.cognicrypt.utils.Utils;

public class CogniCryptIntro implements IIntroPart {

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public IIntroSite getIntroSite() {
		return null;
	}

	@Override
	public void init(IIntroSite site, IMemento memento) throws PartInitException {

	}

	@Override
	public void standbyStateChanged(boolean standby) {

	}

	@Override
	public void saveState(IMemento memento) {

	}

	@Override
	public void addPropertyListener(IPropertyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite outerContainer = new Composite(parent, SWT.WRAP);
		GridLayout gridLayout = new GridLayout(2, true);
		outerContainer.setLayout(gridLayout);
		outerContainer.setBackground(outerContainer.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		Label label = new Label(outerContainer, SWT.CENTER);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.8));
		
		label.setText("Welcome the Study Prototype. Please read the following very carefully.");
		new Label(outerContainer, SWT.CENTER);
		
		new Label(outerContainer, SWT.CENTER);
		new Label(outerContainer, SWT.CENTER);

		label = new Label(outerContainer, SWT.WRAP | SWT.LEFT);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.4));
		label.setText(
				"You will find a Java project in the workspace. To finish this part of the study, please implement the task as described in the task.txt file in the folder src/resources. \nTo implement this task, the IDE provides you multiple means of assistance that are listed below.");
		new Label(outerContainer, SWT.CENTER);
		
		new Label(outerContainer, SWT.CENTER);
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.CENTER);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.5));
		label.setText("Tool Support");
		new Label(outerContainer, SWT.CENTER);
		
		new Label(outerContainer, SWT.CENTER);
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.CENTER | SWT.WRAP);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.3));
		label.setText("1. Content Assist");
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.CENTER | SWT.WRAP);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.3));
		label.setText("The IDE may suggest for any API needed in this task which calls are allowed to call on a given object. They will be displayed as in the image below.");
		new Label(outerContainer, SWT.CENTER);
		
		Button screenshot = new Button(outerContainer, SWT.None);
		screenshot.setImage(Utils.loadImage("icons/contentassist.png"));
		new Label(outerContainer, SWT.CENTER);

		new Label(outerContainer, SWT.CENTER);
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.CENTER | SWT.WRAP);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.4));
		label.setText("2. Use-case specific Code Generation");
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.LEFT | SWT.WRAP);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.4));
		label.setText("The IDE may generate implementations for different use cases that may cover (parts of) the tasks that are requested to be implemented as part of this study.\n The code generation may be launched through the button in the Eclipse toolbar or the context menu appearing when right-clicking a project.");
		new Label(outerContainer, SWT.CENTER);
		
		screenshot = new Button(outerContainer, SWT.None);
		screenshot.setImage(Utils.loadImage("icons/codegeneratorbutton.png"));
		new Label(outerContainer, SWT.CENTER);
		
		new Label(outerContainer, SWT.CENTER);new Label(outerContainer, SWT.CENTER);

		label = new Label(outerContainer, SWT.CENTER);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.4));
		label.setText("3. API Misuse Analysis");
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.LEFT);
		label.setFont(increaseFontSizeBy(label.getFont(), 1.4));
		label.setText("The IDE may apply a suite of static analyses ensuring the correct usage of APIs. \nThe analysis can be triggered by right-clicking a project in the package explorer and selecting the analysis in the context menu.");
		new Label(outerContainer, SWT.CENTER);
		
		screenshot = new Button(outerContainer, SWT.None);
		screenshot.setImage(Utils.loadImage("icons/codeanalysis.png"));
		new Label(outerContainer, SWT.CENTER);
		
		label = new Label(outerContainer, SWT.CENTER);
		label.setFont(increaseFontSizeBy(label.getFont(), 0.9));
		label
				.setText("Please note: Unfortunately, due to some imprecision in the analysis, it may show misuses in the code generated by the code generator(2.). These may be ignored.");
		new Label(outerContainer, SWT.CENTER);

	}

	private Font increaseFontSizeBy(Font defaultFont, double factor) {
		FontData data = defaultFont.getFontData()[0];
		return new Font(defaultFont.getDevice(), new FontData(data.getName(), (int) (data.getHeight() * factor), data.getStyle()));
	}

	@Override
	public void dispose() {

	}

	@Override
	public Image getTitleImage() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Study Welcome Screen";
	}

	@Override
	public void removePropertyListener(IPropertyListener listener) {

	}

	@Override
	public void setFocus() {

	}

}
