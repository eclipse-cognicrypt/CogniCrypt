/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.creator.CrySLRuleCreator;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.utilities.QuickFixUtils;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.UIUtils;
import de.cognicrypt.utils.Utils;

/**
 * @author Andre Sonntag
 */
public class EnsuresPredicateFix implements IMarkerResolution {
	private final String label;
	private static final String INJAR_CLASS_NAME = "Ensurer";
	private static String VARIABLE_DECLARATION_NAME;
	private final CrySLRuleCreator creator;

	public EnsuresPredicateFix(final String label) {
		this.label = label;
		this.creator = new CrySLRuleCreator();
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void run(final IMarker marker) {

		UIUtils.getCurrentlyOpenEditor().doSave(null);

		String errorVarName = "";
		int errorVarIndex = 0;
		String predicate = "";
		int lineNumber = 0;
		int predicateParamCount = 0;

		try {
			lineNumber = (int) marker.getAttribute(IMarker.LINE_NUMBER);
			predicate = (String) marker.getAttribute("predicate");
			errorVarName = (String) marker.getAttribute("errorParam");
			predicateParamCount = Integer.parseInt((String)marker.getAttribute("predicateParamCount"));
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		//Currently, we have no solution for more as one predicate parameter
		if(predicateParamCount > 1) {
			final SuppressWarningFix tempFix = new SuppressWarningFix("");
			tempFix.run(marker);
			UIUtils.getCurrentlyOpenEditor().doSave(null);
			return;
		}
		
		JOptionPane optionPane = new JOptionPane("Now, CogniCrypt recognizes the object as secure", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		JDialog waitingDialog = optionPane.createDialog("CogniCrypt");
		waitingDialog.setModal(false);
		waitingDialog.setVisible(true);
		
		DeveloperProject devProject = new DeveloperProject(marker.getResource().getProject());
		ICompilationUnit sourceUnit = null;
		try {
			sourceUnit = QuickFixUtils.getCompilationUnitFromMarker(marker);
			QuickFixUtils.addAdditionalFiles("resources/PredicateEnsurer/Jar", de.cognicrypt.staticanalyzer.Activator.PLUGIN_ID, devProject);

			if (!QuickFixUtils.hasJarImport(sourceUnit, Constants.PREDICATEENSURER_JAR_IMPORT)) {
				QuickFixUtils.insertJarImport(sourceUnit, Constants.PREDICATEENSURER_JAR_IMPORT);
			}
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		String corePath = Utils.getResourceFromWithin("/resources/CrySLRules/Custom/", de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath();
		String filePath = corePath+Constants.outerFileSeparator+"Ensurer"+Constants.cryslFileEnding;
		File rule = new File(filePath);
		
		
		VARIABLE_DECLARATION_NAME = "ens"+predicate.substring(0, 1).toUpperCase() + predicate.substring(1, 3);
		if (rule.exists()) {
			updateRule(filePath, predicate);
		} else {
			createRule(filePath, predicate);
		}
		
		final ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceUnit);
		final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.accept(new ErrorSourceVisitor(marker, unit, sourceUnit, errorVarName, errorVarIndex ,lineNumber, predicate));
		
		waitingDialog.setVisible(false);
		waitingDialog.dispose();
		UIUtils.getCurrentlyOpenEditor().doSave(null);
	}

	/**
	 * This method creates a CrySL rule for the class de.upb.cognicrypt.predicateensurer.Ensurer
	 * @param filePath
	 * @param pred
	 * @return 
	 */
	private boolean createRule(String filePath, String pred) {
		String spec = "de.upb.cognicrypt.predicateensurer.Ensurer";
		List<String> objects = new ArrayList<String>();
		objects.add("java.lang.Object obj;");
		objects.add("java.lang.String pred;");
		List<String> events = new ArrayList<String>();
		events.add("c: Ensurer(obj,pred);");

		String order = "c";
		List<String> ensures = new ArrayList<String>();
		ensures.add("pred in {\""+ pred + "\"} => " + pred + "[obj];");
		return creator.createRule(filePath, spec, objects, events, order, null, null, ensures);
	}

	/**
	 * This method add a further predicate to the CrySL rule de.upb.cognicrypt.predicateensurer.Ensurer
	 * @param filePath
	 * @param pred
	 * @return
	 */
	private boolean updateRule(String filePath, String pred) {
		return creator.extendRule(filePath, "ENSURES", "pred in {\"" + pred + "\"} => " + pred + "[obj];");
	}
		
	/**
	 * This method creates and inserts the ensuresPredicate(predicate, errorVar) in
	 * the code
	 * 
	 * @param node
	 * @param unit
	 * @param sourceUnit
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 */
	private void addEnsuresConstructor(final ASTNode node, final IMarker marker, final CompilationUnit unit,
			final ICompilationUnit sourceUnit, final String varName,final int varIndex, final String predicate)
			throws JavaModelException, IllegalArgumentException, MalformedTreeException, BadLocationException {

		if(varName.contains("$") || varName.contains("varReplacer")) {
			final SuppressWarningFix tempFix = new SuppressWarningFix("");
			tempFix.run(marker);
			UIUtils.getCurrentlyOpenEditor().doSave(null);
		}
		
		final AST ast = unit.getAST();
		final ASTRewrite rewriter = ASTRewrite.create(ast);				
		final ClassInstanceCreation ensurerClassInstance = createEnsurerClassInstance(ast,varName, predicate);
		final VariableDeclarationFragment ensurerClassVarDeclarationFragment = createEnsurerVariableDeclarationFragment(ast, VARIABLE_DECLARATION_NAME, ensurerClassInstance);
	
		if (node.getNodeType() == ASTNode.METHOD_INVOCATION || node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION) {			

			// we need to find the right insert position 
			final MethodDeclaration parentMethod = getMethodDeclarationParentNode(node);
			final ListRewrite listRewrite = rewriter.getListRewrite(parentMethod.getBody(), Block.STATEMENTS_PROPERTY);
			ASTNode index = node;
			if (index.getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT
					|| index.getParent().getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
				index = index.getParent();
				if (node.getParent().getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT) {
					index = index.getParent();
				}
			}
			final FieldDeclaration ensurerFieldDeclaration = createEnsurerFieldDeclaration(ast, ensurerClassVarDeclarationFragment, Modifier.NONE);
			listRewrite.insertBefore(ensurerFieldDeclaration, index, null);

		} else if (node.getNodeType() == ASTNode.FIELD_DECLARATION) {
			final FieldDeclaration ensurerFieldDeclaration = createEnsurerFieldDeclaration(ast, ensurerClassVarDeclarationFragment, Modifier.PRIVATE);
			final ListRewrite listRewrite = rewriter.getListRewrite(((TypeDeclaration) unit.types().get(0)),TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			listRewrite.insertBefore(ensurerFieldDeclaration, node, null);
		}

		final TextEdit edits = rewriter.rewriteAST();
		final Document document = new Document(sourceUnit.getSource());
		edits.apply(document);
		sourceUnit.getBuffer().setContents(document.get());
	}
	
	/**
	 * This method determines the next {@link MethodDeclaration} parent node
	 * @param targetNode start node
	 * @return MethodDeclaration parent node
	 */
	private MethodDeclaration getMethodDeclarationParentNode(final ASTNode targetNode) {
		ASTNode parent = targetNode.getParent();
		while (true) {
			if (parent != null) {
				if (parent instanceof MethodDeclaration) {
					return (MethodDeclaration) parent;
				} else {
					parent = parent.getParent();
				}
			} else {
				Activator.getDefault().logError("Parent is null");
				return null;
			}
		}

	}

	/**
	 * This method builds a {@link FieldDeclaration} object (i.e. "public iv = ")
	 * 
	 * @param ast - current ast
	 * @param fragment
	 * @param type - i.e. PrimitiveType.BOOLEAN
	 * @param modifier - i.e. Modifier.PRIVATE
	 * @return
	 */
	private FieldDeclaration createEnsurerFieldDeclaration(final AST ast, final VariableDeclarationFragment varDeclarationFrag, final int modifier) {
		final FieldDeclaration declaration = ast.newFieldDeclaration(varDeclarationFrag);
		declaration.setType(createAstType(INJAR_CLASS_NAME, ast));
		declaration.modifiers().addAll(ASTNodeFactory.newModifiers(ast, modifier));
		return declaration;
	}
		
	/**
	 * This method builds a {@link VariableDeclarationFragment} object (i.e. "iv = ...")
	 * @param ast - current ast
	 * @param variableName - declaration variable
	 * @param initializer - initializer for the variable
	 * @return VariableDeclarationFragment obj
	 */
	private VariableDeclarationFragment createEnsurerVariableDeclarationFragment(final AST ast,
		final String variableName, final Expression initializer) {
		final VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(variableName));
		fragment.setInitializer(initializer);
		return fragment;
	}

	/**
	 * This method builds a {@link ClassInstanceCreation} object (i.e. "new Ensuerer(varName, predicate")
	 * @param ast
	 * @param varName
	 * @param predicate
	 * @return
	 */
	private ClassInstanceCreation createEnsurerClassInstance(final AST ast, String varName, String predicate) {
		final ClassInstanceCreation classInstance = ast.newClassInstanceCreation();
		classInstance.setType(createAstType(INJAR_CLASS_NAME, ast));
		final StringLiteral literalPredicate = ast.newStringLiteral();
		literalPredicate.setLiteralValue(predicate);
		classInstance.arguments().add(ast.newSimpleName(varName));
		classInstance.arguments().add(literalPredicate);
		
		return classInstance;
	}
	
	/**
	 * This method build {@link Type} object 
	 * @param type - class
	 * @param ast - current ast
	 * @return Type obj
	 */
	private Type createAstType(final String type, final AST ast) {
		return ast.newSimpleType(ast.newSimpleName(type));
	}
	
	private class ErrorSourceVisitor extends ASTVisitor {

		private final String varName;
		private final int errorVarIndex;
		private final String predicate;
		private final int lineNumber;
		private final CompilationUnit unit;
		private final ICompilationUnit sourceUnit;
		private final IMarker marker;
		private boolean sourceFound = false;

		public ErrorSourceVisitor(final IMarker marker, final CompilationUnit unit, final ICompilationUnit sourceUnit, final String varName, final int errorVarIndex,
				final int lineNumber, final String predicate) {
			this.marker = marker;
			this.varName = varName;
			this.errorVarIndex = errorVarIndex;
			this.predicate = predicate;
			this.lineNumber = lineNumber;
			this.unit = unit;
			this.sourceUnit = sourceUnit;
		}

		@Override
		public boolean visit(final MethodInvocation node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addEnsuresConstructor(node, this.marker, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean visit(final ClassInstanceCreation node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addEnsuresConstructor(node, this.marker, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean visit(final FieldDeclaration node) {
			if (!this.sourceFound) {
				if (this.lineNumber == this.unit.getLineNumber(node.getStartPosition())) {
					try {
						this.sourceFound = true;
						addEnsuresConstructor(node, this.marker, this.unit, this.sourceUnit, this.varName, this.errorVarIndex ,this.predicate);
						return false;
					} catch (JavaModelException | IllegalArgumentException | MalformedTreeException
							| BadLocationException e) {
						Activator.getDefault().logError(e);
					}
				} else {
					return true;
				}
			}

			return false;
		}
	}

}
