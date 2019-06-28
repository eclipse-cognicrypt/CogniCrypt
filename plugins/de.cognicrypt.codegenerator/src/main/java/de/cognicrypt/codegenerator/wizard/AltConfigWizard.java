package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import crypto.rules.CryptSLObject;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;
import de.cognicrypt.utils.Utils;

public class AltConfigWizard extends Wizard {

	private TaskSelectionPage taskListPage;
	private HashMap<Question, Answer> constraints;
	private BeginnerModeQuestionnaire beginnerQuestions;
	private final CodeGenerators generator;

	public AltConfigWizard(CodeGenerators codeGen) {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("CogniCrypt");

		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "platform:/plugin/de.cognicrypt.core/icons/cognicrypt-medium.png ");
		setDefaultPageImageDescriptor(image);
		this.constraints = new HashMap<>();
		generator = codeGen;
	}

	@Override
	public void addPages() {
		this.taskListPage = new TaskSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
	}

	@Override
	public boolean canFinish() {
		final IWizardPage page = getContainer().getCurrentPage();
		return page instanceof LocatorPage && page.isPageComplete();

	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

	/**
	 * This method returns the next page. If current page is task list or any but the last question page, the first/next question page is returned. If the current page is the last
	 * question page, the instance list page is returned.
	 *
	 * @param currentPage
	 *        current page
	 * @return either next question page or instance list page
	 */
	@Override
	public IWizardPage getNextPage(final IWizardPage currentPage) {

		if (checkifInUpdateRound()) {
			return currentPage;
		}
		final Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage instanceof TaskSelectionPage) {
			this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getQuestionsJSONFile());
			// It is possible that now questions are within a BeginnerModeQuestionnaire

			if (this.beginnerQuestions.hasPages()) {
				final BeginnerTaskQuestionPage questionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextPage(), this.beginnerQuestions.getTask(), null);
				addPage(questionPage);
				return questionPage;
			} else {
				return addLocatorPage();
			}
		}

		//Only case that is left: BeginnerTaskQuestionPage
		final BeginnerTaskQuestionPage curQuestionPage = (BeginnerTaskQuestionPage) currentPage;
		final HashMap<Question, Answer> curQuestionAnswerMap = curQuestionPage.getMap();

		for (final Entry<Question, Answer> entry : curQuestionAnswerMap.entrySet()) {
			this.constraints.put(entry.getKey(), entry.getValue());
		}

		final int nextPageid = curQuestionPage.getPageNextID();
		if (this.beginnerQuestions.hasMorePages() && nextPageid > -1) {
			final BeginnerTaskQuestionPage questionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.getPageByID(nextPageid), this.beginnerQuestions.getTask(), null);
			addPage(questionPage);
			return questionPage;
		} else {
			final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

			instanceGenerator.generateInstances(this.constraints);

			if (instanceGenerator.getNoOfInstances() > 0) {
				return addLocatorPage();
			} else {
				final String message = Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
				MessageDialog.openError(new Shell(), "Error", message);
			}
		}
		return currentPage;
	}

	private IWizardPage addLocatorPage() {
		final LocatorPage locatorPage = new LocatorPage("Locator");
		addPage(locatorPage);
		return locatorPage;
	}

	/**
	 * This method returns previous page. If currentPage is the first question, the task list page is returned. If it is any other question page or the instance list page, the
	 * previous question page is returned.
	 *
	 * @param currentPage
	 *        current page, either instance list page or question page
	 * @return either previous question or task selection page
	 */
	@Override
	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
		if (!checkifInUpdateRound()) {
			final IWizardPage[] pages = getPages();
			for (int i = 0; i < pages.length; i++) {
				if (currentPage.equals(pages[i])) {
					if (currentPage instanceof BeginnerTaskQuestionPage) {
						((BeginnerTaskQuestionPage) currentPage).setPageInactive();
					}
					final BeginnerTaskQuestionPage prevPage = (BeginnerTaskQuestionPage) pages[i - 1];
					for (final Entry<Question, Answer> quesAns : prevPage.getSelection().entrySet()) {
						this.constraints.remove(quesAns.getKey());
					}
					return prevPage;
				}
			}
		}

		return super.getPreviousPage(currentPage);
	}

	/**
	 * This method is called once the user selects an instance. It writes the instance to an xml file and calls the code generation.
	 *
	 * @return <code>true</code>/<code>false</code> if writing instance file and code generation are (un)successful
	 */
	@Override
	public boolean performFinish() {
		boolean ret = false;
		final Task selectedTask = this.taskListPage.getSelectedTask();
		CodeGenerator codeGenerator;
		String additionalResources = selectedTask.getAdditionalResources();
		final LocatorPage currentPage = (LocatorPage) getContainer().getCurrentPage();
		IResource selectedFile = (IResource) currentPage.getSelectedResource().getFirstElement();
		
		switch (generator) {
			case CrySL:
				File templateFilea =  CodeGenUtils.getResourceFromWithin("src/main/java/de/cognicrypt/codegenerator/crysl/templates/EncryptionTemplate.java");
				String projectRelDir = Constants.outerFileSeparator + "src" + Constants.outerFileSeparator + Constants.PackageName + Constants.outerFileSeparator;
				String projectRelPath = projectRelDir + templateFilea.getName();
				
				try {
					Files.createDirectories(Paths.get(selectedFile.getProject().getRawLocation().toOSString() + projectRelDir));
					Files.copy(templateFilea.toPath(), Paths.get(selectedFile.getProject().getRawLocation().toOSString() + projectRelPath));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					selectedFile.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				IFile file = selectedFile.getProject().getFile(projectRelPath);
//				try {
//					file.create(stream, true, null);
//				} catch (CoreException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			
				ASTParser parser = ASTParser.newParser(AST.JLS11);
				parser.setSource((ICompilationUnit) JavaCore.create(file));

				parser.setResolveBindings(true);
				parser.setBindingsRecovery(true);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				CompilationUnit cu = (CompilationUnit) parser.createAST(null);

				Map<SimpleName, CryptSLObject> variableDefinitions = new HashMap<SimpleName, CryptSLObject>();
				List<CodeGenCrySLRule> rules = new ArrayList<CodeGenCrySLRule>();
				List<CryptSLObject> retObj = new ArrayList<CryptSLObject>();
				List<CryptSLObject> pars = new ArrayList<CryptSLObject>();
				final ASTVisitor astVisitor = new ASTVisitor(true) {

					@Override
					public boolean visit(MethodInvocation node) {
						// TODO Auto-generated method stub
						MethodInvocation mi = node;
						String calledMethodName = mi.getName().getFullyQualifiedName();
						if ("addReturnObject".equals(calledMethodName)) {
							Optional<SimpleName> variable = Optional.empty();
							for (SimpleName var : variableDefinitions.keySet()) {
								variable = ((List<SimpleName>) mi.arguments()).parallelStream().filter(e -> e.getFullyQualifiedName().equals(var.getFullyQualifiedName())).findFirst();
								if (variable.isPresent()) {
									break;
								}
							}
//							Optional<SimpleName> variable = variableDefinitions.keySet().stream().filter(e -> mi.arguments().contains(((SimpleName) e).getFullyQualifiedName())).findFirst();
							retObj.add(variableDefinitions.get(variable.get()));
						} else if ("addParameter".equals(calledMethodName)) {
							Optional<SimpleName> variable = Optional.empty();
							for (SimpleName var : variableDefinitions.keySet()) {
								variable = ((List<SimpleName>) mi.arguments()).parallelStream().filter(e -> e.getFullyQualifiedName().equals(var.getFullyQualifiedName())).findFirst();
								if (variable.isPresent()) {
									break;
								}
							}
							pars.add(variableDefinitions.get(variable.get()));
						} else if ("considerCrySLRule".equals(calledMethodName)){
							String rule = Utils.filterQuotes(mi.arguments().get(0).toString());
							try {
								String simpleRuleName = rule.substring(rule.lastIndexOf(".") + 1);
								rules.add(new CodeGenCrySLRule(Utils.getCryptSLRule(simpleRuleName), pars, (retObj.isEmpty()) ? null : retObj.get(0)));
							} catch (ClassNotFoundException | IOException e) {
								Activator.getDefault().logError(e);
							}
							pars.clear();
							retObj.clear();
						} 
						return super.visit(node);
					}

					@Override
					public boolean visit(PackageDeclaration node) {
//						Name a = ((PackageDeclaration)node).getName();
//						a.structuralPropertiesForType().get(0);
//						a.setProperty("qualifier", "aaa");
//						((PackageDeclaration)node).setName(a);
						return super.visit(node);
					}

					@Override
					public void preVisit(ASTNode node) {
						// TODO Auto-generated method stub
						super.preVisit(node);
					}

					@Override
					public boolean visit(VariableDeclarationExpression node) {
						// TODO Auto-generated method stub
						return super.visit(node);
					}

					@Override
					public boolean visit(VariableDeclarationStatement node) {
						// TODO Auto-generated method stub
						SimpleName varName = ((VariableDeclarationFragment) ((VariableDeclarationStatement)node).fragments().get(0)).getName();
						variableDefinitions.put(varName, new CryptSLObject(varName.getFullyQualifiedName(), ((VariableDeclarationStatement)node).getType().toString()));
						return super.visit(node);
					}

					@Override
					public boolean visit(MethodDeclaration node) {
						// TODO Auto-generated method stub
						for (SingleVariableDeclaration svd : (List<SingleVariableDeclaration>)node.parameters()) {
							variableDefinitions.put(svd.getName(), new CryptSLObject(svd.getName().getFullyQualifiedName(), svd.getType().toString()));
						}
						return super.visit(node);
					}

					@Override
					public boolean visit(MethodRefParameter node) {
						// TODO Auto-generated method stub
						return super.visit(node);
					}
					
					

				};
				cu.accept(astVisitor);
				
				Collections.reverse(rules);
				try {
					codeGenerator = new CrySLBasedCodeGenerator(selectedFile);
					
					Map<CodeGenCrySLRule, ?> constraints = new HashMap<CodeGenCrySLRule, Object>();
					Configuration chosenConfig = new CrySLConfiguration(rules, constraints, codeGenerator.getDeveloperProject()
						.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);

					ret = codeGenerator.generateCodeTemplates(chosenConfig, additionalResources);
					
				} catch (Exception e) {
					Activator.getDefault().logError(e);
					return false;
				}
				break;
			case XSL:
				
				this.constraints = (this.constraints != null) ? this.constraints : new HashMap<>();
				final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
					.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

				instanceGenerator.generateInstances(this.constraints);
				final Map<String, InstanceClafer> instances = instanceGenerator.getInstances();
				final InstanceClafer instance = instances.values().iterator().next();

				// Initialize Code Generation
				codeGenerator = new XSLBasedGenerator(selectedFile, selectedTask.getXslFile());
				final DeveloperProject developerProject = codeGenerator.getDeveloperProject();

				JOptionPane optionPane = new JOptionPane("CogniCrypt is now generating code that implements " + selectedTask.getDescription() + "\ninto file " + ((selectedFile != null) ? selectedFile.getName() : "Output.java") + ". This should take no longer than a few seconds.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
				JDialog waitingDialog = optionPane.createDialog("Generating Code");
				waitingDialog.setModal(false);
				waitingDialog.setVisible(true);
				
				// Generate code template
				XSLConfiguration chosenConfig = new XSLConfiguration(instance, this.constraints, developerProject.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
				ret &= codeGenerator.generateCodeTemplates(chosenConfig, selectedTask.getAdditionalResources());
				
				waitingDialog.setVisible(false);
				
				waitingDialog.dispose();
				break;
			default:
				ret = false;
		}
		return ret;
	}

	public HashMap<Question, Answer> getConstraints() {
		return this.constraints;
	}

}
