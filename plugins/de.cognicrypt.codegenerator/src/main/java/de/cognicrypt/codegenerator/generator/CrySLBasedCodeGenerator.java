/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLCondPredicate;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLConstraint.LogOps;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.CrySLUtils;
import de.cognicrypt.utils.Utils;

/**
 * 
 * @author Stefan Krueger
 * @author Florian Breitfelder
 *
 */
public class CrySLBasedCodeGenerator extends CodeGenerator {

	private static HashMap<String, String> parameterCache = new HashMap<String, String>();

	public static void clearParameterCache() {
		parameterCache.clear();
	}

	private static HashMap<String, String> ruleParameterCache = new HashMap<String, String>();

	private static void clearRuleParameterCache() {
		ruleParameterCache.clear();
	}

	/**
	 * Contains the exceptions classes that are thrown by the generated code.
	 */
	private List<String> exceptions = new ArrayList<String>();

	private List<String> kills = new ArrayList<String>();

	List<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>> predicateConnections;
	Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> toBeEnsuredPred = null;

	CodeGenCrySLRule curRule = null;

	/**
	 * This constructor allows it to set a specific class and method names that are used in the generated Java code.
	 * 
	 * @param selectedFile
	 *        File the templateUsage method should be generated into
	 */
	public CrySLBasedCodeGenerator(IResource selectedFile) {
		super(selectedFile);
	}

	@Override
	public boolean generateCodeTemplates(Configuration chosenConfig, String pathToFolderWithAdditionalResources) {
		GeneratorClass ruleClass = null;
		if (chosenConfig instanceof CrySLConfiguration) {
			ruleClass = ((CrySLConfiguration) chosenConfig).getTemplateClass();
		}
		ruleClass.setPackageName(Constants.PackageNameAsName);
		ruleClass.setModifier("public");

		exceptions.add("GeneralSecurityException");
		String genFolder = "";
		try {
			genFolder = this.project.getProjectPath() + Constants.innerFileSeparator + this.project
				.getSourcePath() + Constants.CodeGenerationCallFolder + Constants.innerFileSeparator;
		} catch (CoreException e1) {
			Activator.getDefault().logError(e1);
		}
		Set<GeneratorClass> generatedClasses = new HashSet<GeneratorClass>();
		Map<String, List<CrySLPredicate>> reliablePreds = new HashMap<String, List<CrySLPredicate>>();
		Map<String, List<String>> tmpUsagePars = new HashMap<String, List<String>>();

		GeneratorClass templateClass = new GeneratorClass();
		templateClass.setPackageName(Constants.PackageNameAsName);

		for (String imp : Constants.xmlimportsarr) {
			templateClass.addImport(imp);
		}
		templateClass.setClassName("Output");
		templateClass.setModifier("public");
		GeneratorMethod tmplUsage = new GeneratorMethod();
		templateClass.addMethod(tmplUsage);
		tmplUsage.setModifier("public");
		tmplUsage.setReturnType("void");
		tmplUsage.setName(Constants.NameOfTemporaryMethod);

		RuleDependencyTree rdt = new RuleDependencyTree(CrySLUtils.readCrySLRules());

		for (GeneratorMethod method : ruleClass.getMethods()) {
			tmpUsagePars.put(method.getName(), new ArrayList<String>());
			List<CodeGenCrySLRule> rules = method.getRules();
			if (rules.isEmpty()) {
				continue;
			}
			String usedClass = rules.get(rules.size() - 1).getClassName();
			predicateConnections = new ArrayList<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>>();

			for (int i = 0; i < rules.size(); i++) {
				//				analyseConstraints(rules.get(i).getConstraints());

				if (i < rules.size() - 1) {
					CrySLRule nextRule = rules.get(i + 1);
					CrySLRule curRule = rules.get(i);

					if (rdt.hasDirectPath(curRule, nextRule)) {
						boolean now = false;
						for (CrySLPredicate ensPred : curRule.getPredicates()) {
							String nextType = nextRule.getClassName();
							String predType = ((CrySLObject) ensPred.getParameters().get(0)).getJavaType();
							if (Utils.isSubType(nextType, predType) || Utils.isSubType(predType, nextType)) {
								predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CrySLRule, CrySLRule>(curRule, nextRule)));
								now = true;
							}
							for (CrySLPredicate reqPred : nextRule.getRequiredPredicates()) {
								if (reqPred.equals(ensPred) && Utils.isSubType(((CrySLObject) reqPred.getParameters().get(0)).getJavaType(), predType)) {
									Optional<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>> matchedPred = predicateConnections.stream()
										.filter(e -> e.getKey().equals(ensPred)).findFirst();
									if (now && matchedPred.isPresent()) {
										int newParNumber = getParameterNumber(curRule, (CrySLObject) ensPred.getParameters().get(0));
										Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> entry = matchedPred.get();
										int oldParNumber = getParameterNumber(curRule, (CrySLObject) entry.getKey().getParameters().get(0));
										if (newParNumber < oldParNumber) {
											predicateConnections.remove(entry);
											predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CrySLRule, CrySLRule>(curRule, nextRule)));
										}
									} else {
										predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CrySLRule, CrySLRule>(curRule, nextRule)));
										now = true;
									}
								}
							}
						}
					}
				}
			}

			String[] originalBody = method.getBody().split("\n");
			method.clearBody();
			int posInBody;
			for (posInBody = 0; posInBody < originalBody.length; posInBody++) {
				if (originalBody[posInBody].startsWith("CrySLCodeGenerator")) {
					posInBody++;
					break;
				}
				method.addStatementToBody(originalBody[posInBody]);
			}

			for (CodeGenCrySLRule rule : rules) {
				curRule = rule;
				clearRuleParameterCache();
				boolean next = true;
				boolean lastRule = rules.get(rules.size() - 1).equals(rule);
				// get state machine of crysl rule
				StateMachineGraph stateMachine = rule.getUsagePattern();
				Optional<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>> toBeEnsured = Optional.empty();

				if (lastRule) {
					CrySLObject reqReturnObject = ((CodeGenCrySLRule) rule).getRequiredRetObj();
					toBeEnsuredPred = null;
					List<CrySLPredicate> candidates = new ArrayList<CrySLPredicate>();
					for (CrySLPredicate reqPred : rule.getPredicates()) {
						if (!(reqPred instanceof CrySLCondPredicate)) {
							String parType = ((CrySLObject) reqPred.getParameters().get(0)).getJavaType();
							if (Utils.isSubType(parType, reqReturnObject.getJavaType()) || Utils.isSubType(reqReturnObject.getJavaType(), parType)) {
								candidates.add(reqPred);
							}
						}
					}
					if (candidates.size() == 1) {
						toBeEnsuredPred = new SimpleEntry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>(candidates.get(0), new SimpleEntry<CrySLRule, CrySLRule>(rule, null));
					} else if (candidates.size() > 1) {
						Entry<CrySLPredicate, Integer> candHD = null;
						for (CrySLPredicate candidate : candidates) {
							String retName = reqReturnObject.getVarName();
							String candName = ((CrySLObject) candidate.getParameters().get(0)).getVarName();

							if (candHD == null) {
								candHD = new SimpleEntry<CrySLPredicate, Integer>(candidate, getHD(retName, candName));
							}
							if (getHD(retName, candName) < candHD.getValue()) {
								candHD = new SimpleEntry<CrySLPredicate, Integer>(candidate, getHD(retName, candName));
							}
						}
						toBeEnsuredPred = new SimpleEntry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>(candHD.getKey(), new SimpleEntry<CrySLRule, CrySLRule>(rule, null));
					}
					if (toBeEnsuredPred == null) {
						for (CrySLPredicate reqPred : rule.getPredicates()) {
							CrySLObject a = ((CodeGenCrySLRule) rule).getRequiredRetObj();
							boolean baseMatch = Utils.isSubType(((CrySLObject) reqPred.getParameters().get(0)).getJavaType(), a.getJavaType());
							if (!baseMatch) {
								continue;
							}
							if (toBeEnsuredPred != null) {
								Optional<ICrySLPredicateParameter> opt = reqPred.getParameters().subList(1, reqPred.getParameters().size()).stream().filter(e -> rule.getRequiredPars().parallelStream().anyMatch(f -> f.getCrySLVariable().equals(((CrySLObject)e).getVarName()))).findFirst();
								if (!opt.isPresent()) {
									continue;
								}
							}
							toBeEnsuredPred = new SimpleEntry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>(reqPred, new SimpleEntry<CrySLRule, CrySLRule>(rule, null));
//							break;
						}
					}
				} else {
					Stream<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>> filter = predicateConnections.stream().filter(e -> {
						String ruleClassName = rule.getClassName();
						String keyClassName = e.getValue().getKey().getClassName();
						return Utils.isSubType(ruleClassName, keyClassName) || Utils.isSubType(keyClassName, ruleClassName);
					});
					toBeEnsured = filter.findFirst();
					if (toBeEnsured.isPresent()) {
						toBeEnsuredPred = toBeEnsured.get();
					}
				}

				// analyse state machine
				StateMachineGraphAnalyser stateMachineGraphAnalyser = new StateMachineGraphAnalyser(stateMachine);
				ArrayList<List<TransitionEdge>> transitionsList;
				Iterator<List<TransitionEdge>> transitions = null;
				try {
					transitionsList = stateMachineGraphAnalyser.getTransitions();
					transitionsList.sort(new Comparator<List<TransitionEdge>>() {

						@Override
						public int compare(List<TransitionEdge> element1, List<TransitionEdge> element2) {
							return Integer.compare(element1.size(), element2.size());
						}
					});
					transitions = transitionsList.iterator();
				} catch (Exception e) {
					Activator.getDefault().logError(e);
				}

				do {
					// Load one possible path through the state machine.
					List<TransitionEdge> currentTransitions = transitions.next();
					ArrayList<Entry<String, String>> methodParametersOfSuperMethod = new ArrayList<Entry<String, String>>();

					// Determine imports, method calls and thrown exceptions
					ArrayList<String> imports = new ArrayList<String>(determineImports(currentTransitions));
					imports.addAll(Arrays.asList(Constants.xmlimportsarr));
					ruleClass.addImports(imports);

					Map<CrySLPredicate, Entry<CrySLRule, CrySLRule>> mayUsePreds = new HashMap<>();
					for (Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> entry : predicateConnections) {
						if (entry.getValue().getValue().getClassName().equals(usedClass)) {
							mayUsePreds.put(entry.getKey(), entry.getValue());
						}
					}

					ArrayList<String> methodInvocations = generateMethodInvocations(rule, method, currentTransitions, mayUsePreds, imports, lastRule);
					if (methodInvocations.isEmpty()) {
						continue;
					}

					if (toBeEnsuredPred != null && toBeEnsured.isPresent() && !toBeEnsured.get().getKey().getParameters().get(0)
						.equals(toBeEnsuredPred.getKey().getParameters().get(0))) {
						Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> originalPred = toBeEnsured.get();
						int indexOf = predicateConnections.indexOf(originalPred);
						predicateConnections.remove(indexOf);
						predicateConnections.add(indexOf, toBeEnsuredPred);
					}

					method.addStatementToBody("");
					for (String methodInvocation : methodInvocations) {
						method.addStatementToBody(methodInvocation);
					}

					// add thrown exceptions
					method.addExceptions(exceptions);

					Iterator<Entry<String, String>> iMethodParameters = methodParametersOfSuperMethod.iterator();
					do {
						if (iMethodParameters.hasNext()) {
							Entry<String, String> parameter = iMethodParameters.next();
							method.addParameter(parameter);
							tmpUsagePars.get(method.getName()).add(parameter.getValue() + " " + parameter.getKey());
						}

					} while (iMethodParameters.hasNext());

					reliablePreds.put(rule.getClassName(), rule.getPredicates());
					next = false;

				} while (next);
			}
			for (String killStatement : kills) {
				method.addKillStatement(killStatement);
			}
			kills.clear();

			for (; posInBody < originalBody.length; posInBody++) {
				method.addStatementToBody(originalBody[posInBody]);
			}
		}

		generatedClasses.add(ruleClass);

		generateTemplateUsageBody(generatedClasses, tmplUsage);

		tmplUsage.addException("GeneralSecurityException");
		generatedClasses.add(templateClass);
		CodeHandler codeHandler = new CodeHandler(generatedClasses);
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			codeHandler.writeToDisk(genFolder);
			cleanUpProject(page.getActiveEditor());
		} catch (Exception e) {
			Activator.getDefault().logError(e);
		}

		try {
			insertCallCodeIntoFile(this.project.getProjectPath() + Constants.innerFileSeparator + this.project.getSourcePath() + Constants.CodeGenerationCallFile, true, false,
				false);
			removeCryptoPackageIfEmpty();
		} catch (CoreException | BadLocationException | IOException e) {
			Activator.getDefault().logError(e);
		}

		return generatedClasses != null;
	}

	private Integer getHD(String left, String right) {
		int distance = 0;
		int leftSize = left.length();
		int rightSize = right.length();
		for (int i = 0; i < (leftSize < rightSize ? leftSize : rightSize); i++) {
			if (left.charAt(i) != right.charAt(i)) {
				distance++;
			}
		}
		distance += Math.abs(leftSize - rightSize);

		return distance;
	}

	private void generateTemplateUsageBody(Set<GeneratorClass> generatedClasses, GeneratorMethod tmplUsage) {
		for (GeneratorClass generatedClass : generatedClasses) {

			String className = generatedClass.getClassName();
			tmplUsage.addStatementToBody(className + " " + className.toLowerCase() + " = new " + className + "();");

			List<Entry<String, String>> declaredVariables = new ArrayList<>();
			declaredVariables.addAll(tmplUsage.getParameters());
			for (GeneratorMethod gen : generatedClass.getMethods()) {
				if (gen.getRules().isEmpty()) {
					continue;
				}

				tmplUsage.addExceptions(gen.getExceptions());
				String returnType = gen.getReturnType();
				String varName = gen.getName() + "Res";
				if (!"void".equals(returnType)) {
					tmplUsage.addStatementToBody(returnType + " " + varName + " = ");
				}

				tmplUsage.addStatementToBody(className.toLowerCase() + "." + gen.getName() + "(");
				ArrayList<Entry<String, String>> usedVars = new ArrayList<>();
				for (Entry<String, String> par : gen.getParameters()) {
					if (!declaredVariables.contains(par)) {
						ArrayList<Entry<String, String>> redundantVarList = new ArrayList<>(declaredVariables);
						Collections.reverse(redundantVarList);
						Optional<Entry<String, String>> o = redundantVarList.parallelStream().filter(e -> !usedVars.contains(e) && Utils.isSubType(e.getValue(), par.getValue())).findFirst();
						if (o.isPresent()) {
							tmplUsage.addStatementToBody(o.get().getKey() + ", ");
							usedVars.add(o.get());
							continue;
						} else {
							tmplUsage.addParameter(par);
							declaredVariables.add(par);
						}
					}
					tmplUsage.addStatementToBody(par.getKey() + ", ");
				}
				tmplUsage.addStatementToBody(");");
				if (!"void".equals(returnType)) {
					declaredVariables.add(new SimpleEntry<String, String>(varName, returnType));
				}
			}
		}
	}

	private int getParameterNumber(CrySLRule curRule, CrySLObject par) {
		Set<TransitionEdge> transitions = new HashSet<TransitionEdge>(curRule.getUsagePattern().getAllTransitions());
		for (TransitionEdge trans : transitions) {
			for (CrySLMethod potMethod : trans.getLabel()) {
				SimpleEntry<String, String> cmpPar = new SimpleEntry<String, String>(par.getVarName(), par.getJavaType());
				if (potMethod.getParameters().parallelStream()
					.anyMatch(e -> e.getKey().equals(cmpPar.getKey()) && (e.getValue().equals(cmpPar.getValue()) || e.getValue().equals(cmpPar.getValue() + "[]")))) {
					return potMethod.getParameters().size() - 1;
				} else if (potMethod.getRetObject().getKey().equals(
					cmpPar.getKey()) && (potMethod.getRetObject().getValue().equals(cmpPar.getValue()) || potMethod.getRetObject().getValue().equals(cmpPar.getValue() + "[]"))) {
					return potMethod.getParameters().size();
				}

			}
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * This method generates a method invocation for every transition of a state machine that represents a crysl rule.
	 * 
	 * @param currentTranstions
	 *        List of transitions that represents a crysl rule's state machine.
	 * @param currentTransitions
	 * @param predicateConnections
	 * @param imports
	 */
	private ArrayList<String> generateMethodInvocations(CodeGenCrySLRule rule, GeneratorMethod useMethod, List<TransitionEdge> currentTransitions, Map<CrySLPredicate, Entry<CrySLRule, CrySLRule>> usablePreds, List<String> imports, boolean lastRule) {
		Set<StateNode> killStatements = rule.getPredicates().stream().filter(pred -> pred.isNegated() && pred instanceof CrySLCondPredicate)
			.map(e -> ((CrySLCondPredicate) e).getConditionalMethods()).reduce(new HashSet<>(), (a, b) -> {
				a.addAll(b);
				return a;
			});
		ArrayList<String> methodInvocations = new ArrayList<String>();
		List<String> localKillers = new ArrayList<String>();
		boolean ensures = false;

		List<Entry<String, String>> useMethodParameters = new ArrayList<Entry<String, String>>();
		Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> pre = new SimpleEntry<>(toBeEnsuredPred.getKey(), toBeEnsuredPred.getValue());
		for (TransitionEdge transition : currentTransitions) {
			CrySLMethod method = null;
			Set<String> reqMethodNames = new HashSet<>();
			rule.getRequiredPars().parallelStream().forEach(e -> reqMethodNames.add(e.getMethod()));
			List<CrySLMethod> labels = transition.getLabel().stream().filter(e -> {
				String methodName = e.getMethodName().substring(e.getMethodName().lastIndexOf(".") + 1);
				if (!reqMethodNames.contains(methodName)) {
					return true;
				}
				List<CodeGenCrySLObject> objs = rule.getRequiredPars().parallelStream().filter(f -> f.getMethod().equals(methodName)).collect(Collectors.toList());
				List<CodeGenCrySLObject> found = new ArrayList<>();
				for (CodeGenCrySLObject par : objs) {
					if (found.contains(par)) {
						continue;
					}
					List<Entry<String, String>> parameters = e.getParameters();
					int parPos = par.getPosition();
					if (parameters.size() > parPos && (Utils.isSubType(par.getJavaType(), parameters.get(parPos).getValue()) || Utils.isSubType(parameters.get(parPos).getValue(),
						par.getJavaType()))) {
						found.add(par);
					}
				}
				return found.size() == objs.size();
			}).collect(Collectors.toList());

			for (CrySLMethod meth : labels) {
				if (method != null) {
					break;
				} else {
					toBeEnsuredPred = pre;
				}

				if (toBeEnsuredPred != null) {
					//Predicate
					method = fetchCorrespondingMethod(toBeEnsuredPred, meth);
					if (method != null) {
						ensures = true;
					}
				}

				for (Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> usablePred : usablePreds.entrySet()) {
					if (method == null) {
						method = fetchCorrespondingMethod(usablePred, meth);
					} else {
						break;
					}
				}

			}
			// Determine method name and signature
			if (method == null) {
				method = labels.get(0);
			}
			String methodName = method.getMethodName();
			methodName = methodName.substring(methodName.lastIndexOf(".") + 1);

			// Determine parameter of method.
			List<Entry<String, String>> parameters = method.getParameters();
			Iterator<Entry<String, String>> parametersIterator = parameters.iterator();

			StringBuilder sourceLineGenerator = new StringBuilder(methodName);
			sourceLineGenerator.append("(");

			do {
				if (parametersIterator.hasNext()) {
					sourceLineGenerator.append(parametersIterator.next().getKey());
				}

				if (parametersIterator.hasNext()) {
					sourceLineGenerator.append(", ");
				}

			} while (parametersIterator.hasNext());

			sourceLineGenerator.append(");");

			Class<?>[] methodParameter = collectParameterTypes(parameters);

			try {
				determineThrownExceptions(method.getMethodName().substring(0, method.getMethodName().lastIndexOf(".")), methodName, methodParameter, imports);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				Activator.getDefault().logError(e);
			}

			// TODO determine possible subclasses
			// ################################################################
			// see also method getSubClass(className);
			String lastInvokedMethod = getLastInvokedMethodName(currentTransitions).toString();
			Entry<String, List<Entry<String, String>>> methodInvocationWithUseMethodParameters = generateMethodInvocation(useMethod, lastInvokedMethod, imports, method, methodName,
				parameters, rule, sourceLineGenerator, lastRule);

			useMethodParameters.addAll(methodInvocationWithUseMethodParameters.getValue());
			String methodInvocation = methodInvocationWithUseMethodParameters.getKey();
			// Add new generated method invocation
			if (!methodInvocation.isEmpty()) {
				if (killStatements.contains(transition.to())) {
					localKillers.add(methodInvocation);
				} else {
					methodInvocations.add(methodInvocation);
				}
				methodInvocation = "";
			}

		}
		if (toBeEnsuredPred == null || ensures) {
			kills.addAll(localKillers);
			for (Entry<String, String> par : useMethodParameters) {
				useMethod.addParameter(par);
			}
			return methodInvocations;
		} else {
			toBeEnsuredPred = pre;
			return new ArrayList<String>();
		}
	}

	private CrySLMethod fetchCorrespondingMethod(Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>> pred, CrySLMethod meth) {
		CrySLObject objectOfPred = (CrySLObject) pred.getKey().getParameters().get(0);
		String predVarType = objectOfPred.getJavaType();
		String predVarName = objectOfPred.getVarName();

		//Method
		Entry<String, String> retObject = meth.getRetObject();
		String returnType = retObject.getValue();
		String returnVarName = retObject.getKey();

		if (Utils.isSubType(predVarType, returnType) && returnVarName
			.equals(predVarName) || (predVarName.equals("this") && meth.getMethodName().endsWith(predVarType.substring(predVarType.lastIndexOf('.') + 1)))) {
			return meth;
		}
		for (Entry<String, String> par : meth.getParameters()) {
			String parType = par.getValue();
			String parVarName = par.getKey();

			if ((Utils.isSubType(predVarType, parType) || Utils.isSubType(parType, predVarType)) && (parVarName.equals(predVarName) || "this".equals(predVarName))) {
				return meth;
			}
		}
		return null;
	}

	private Class<?>[] collectParameterTypes(List<Entry<String, String>> parameters) {
		Class<?>[] methodParameter = new Class<?>[parameters.size()];
		int i = 0;
		List<String> primitiveTypes = Arrays.asList(new String[] { "int", "boolean", "short", "double", "float", "long", "byte", "int[]", "byte[]", "char[]" });

		for (Entry<String, String> parameter : parameters) {
			if (primitiveTypes.contains(parameter.getValue())) {
				Class<?> primitiveType = null;
				switch (parameter.getValue()) {
					case "int":
						primitiveType = int.class;
						break;
					case "double":
						primitiveType = double.class;
						break;
					case "boolean":
						primitiveType = boolean.class;
						break;
					case "float":
						primitiveType = float.class;
						break;
					case "byte":
						primitiveType = byte.class;
						break;
					case "byte[]":
						primitiveType = byte[].class;
						break;
					case "int[]":
						primitiveType = int[].class;
						break;
					case "char[]":
						primitiveType = char[].class;
						break;
					default:
						primitiveType = int.class;
				}
				methodParameter[i] = primitiveType;

			} else {
				try {
					methodParameter[i] = Class.forName(parameter.getValue());
					i++;
				} catch (ClassNotFoundException e) {
					Activator.getDefault().logError(e, "No class found for type: " + parameter.getValue().toString());
				}
			}
		}
		return methodParameter;
	}

	private Entry<String, List<Entry<String, String>>> generateMethodInvocation(GeneratorMethod useMethod, String lastInvokedMethod, List<String> imports, CrySLMethod method, String methodName, List<Entry<String, String>> parameters, CodeGenCrySLRule rule, StringBuilder currentInvokedMethod, boolean lastRule) {
		// Generate method invocation. Hereafter, a method call is distinguished in three categories.
		String methodInvocation = "";

		String className = rule.getClassName();
		String simpleName = className.substring(className.lastIndexOf('.') + 1);
		String instanceName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);

		// 1. Constructor method calls
		// 2. Static method calls
		// 3. Instance method calls

		// 1. Constructor method call
		CrySLObject retObjInTemplate = rule.getRequiredRetObj();
		if (currentInvokedMethod.substring(0, currentInvokedMethod.indexOf("(")).equals(simpleName)) {
			if (lastRule && retObjInTemplate != null && (Utils.isSubType(className, retObjInTemplate.getJavaType()) || Utils.isSubType(retObjInTemplate.getJavaType(),
				className))) {
				methodInvocation = retObjInTemplate.getName() + " = new " + currentInvokedMethod;
			} else {
				methodInvocation = className + " " + instanceName + " = new " + currentInvokedMethod;
			}
		}
		// Static method call
		else if (currentInvokedMethod.toString().contains("getInstance")) {
			currentInvokedMethod = new StringBuilder(currentInvokedMethod.substring(currentInvokedMethod.lastIndexOf("=") + 1).trim());
			methodInvocation = className + " " + instanceName + " = " + simpleName + "." + currentInvokedMethod;
		}
		// 3. Instance method call
		else {
			// Does method have a return value?
			if (method.getRetObject() != null) {
				String returnValueType = method.getRetObject().getValue();
				boolean generated = false;
				String voidString = "void";

				// Determine lastInvokedMethod
				lastInvokedMethod = lastInvokedMethod.substring(lastInvokedMethod.lastIndexOf('.') + 1);

				if (lastRule) {
					// Last invoked method and return type is not equal to "void".
					if (methodName.equals(lastInvokedMethod) && !returnValueType.equals(voidString)) {
						methodInvocation = retObjInTemplate.getVarName() + " = " + instanceName + "." + currentInvokedMethod;
						generated = true;
					}
					// Last invoked method and return type is equal to "void".
					else if (methodName.equals(lastInvokedMethod) && returnValueType.equals(voidString)) {
						methodInvocation = instanceName + "." + currentInvokedMethod; // + "\nreturn " + instanceName + ";";
						generated = true;
					}
					// Not the last invoked method and return type is not equal to "void".
					else if (!methodName.equals(lastInvokedMethod) && !returnValueType.equals(voidString)) {
						methodInvocation = returnValueType + " = " + instanceName + "." + currentInvokedMethod;
						generated = true;
					}
				}
				if (!generated) {
					if (!returnValueType.equals(voidString)) {
						String simpleType = returnValueType.substring(returnValueType.lastIndexOf('.') + 1);
						if (Character.isUpperCase(simpleType.charAt(0))) {
							methodInvocation = returnValueType + " " + Character.toLowerCase(simpleType.charAt(0)) + simpleType
								.substring(1) + " = " + instanceName + "." + currentInvokedMethod;
						} else {
							methodInvocation = returnValueType + " " + method.getRetObject().getKey() + " = " + instanceName + "." + currentInvokedMethod;
						}
					} else {
						methodInvocation = instanceName + "." + currentInvokedMethod;
					}
				}
			} else {
				methodInvocation = instanceName + "." + currentInvokedMethod;
			}
		}

		// Replace parameters by values that are defined in the previous step
		// ################################################################
		return replaceParameterByValue(rule, useMethod, parameters, methodInvocation, imports);
	}

	/**
	 * Replaces parameter names in method invocations by a value. This value is derived by constraints.
	 * 
	 * @param rule
	 * 
	 * @param parametersOfCall
	 *        All available parameters.
	 * @param constraints
	 *        Available constraints for parameters
	 * 
	 * @param currentInvokedMethod
	 *        Method invocation as string
	 * @param methodParametersOfSuperMethod
	 * @param imports
	 * @param toBeEnsuredPred
	 * 
	 * @return New method invocation as string (parameter names are replaces by values)
	 */
	private Entry<String, List<Entry<String, String>>> replaceParameterByValue(CodeGenCrySLRule rule, GeneratorMethod useMethod, List<Entry<String, String>> parametersOfCall, String currentInvokedMethod, List<String> imports) {

		// Split current method invocation "variable = method(method parameter)" in:
		// 1. variable = method
		// 2. (method parameter)
		// replace only parameter names by values in the second part.
		String methodNamdResultAssignment = currentInvokedMethod.substring(0, currentInvokedMethod.indexOf("("));
		String methodParameter = currentInvokedMethod.substring(currentInvokedMethod.indexOf("("), currentInvokedMethod.indexOf(")"));
		String appendix = currentInvokedMethod.substring(currentInvokedMethod.indexOf(")"), currentInvokedMethod.length());
		List<Entry<String, String>> parametersOfUseMethod = new ArrayList<Entry<String, String>>();
		List<Entry<String, String>> declaredVariables = useMethod.getDeclaredVariables();
		List<CodeGenCrySLObject> reqPars = rule.getRequiredPars();
		
		List<Entry<String, String>> usedVars = new ArrayList<Entry<String, String>>();
		reqPars.stream().forEach(e -> usedVars.add(new SimpleEntry<>(e.getVarName(), e.getJavaType())));
		if (rule.getRequiredRetObj() != null) {
			usedVars.add(new SimpleEntry<String, String>(rule.getRequiredRetObj().getVarName(), rule.getRequiredRetObj().getJavaType()));
		}
		
		for (Entry<String, String> parameter : parametersOfCall) {
			boolean inTemplate = false;

			for (CodeGenCrySLObject par : reqPars) {
				if (methodNamdResultAssignment.endsWith(par.getMethod()) && par.getCrySLVariable().equals(parameter.getKey())) {
					methodParameter = methodParameter.replace(parameter.getKey(), par.getVarName());
					updateToBeEnsured(new SimpleEntry<String, String>(par.getVarName(), parameter.getValue()));
					inTemplate = true;
					break;
				}
			}
			if (inTemplate) {
				continue;
			}

			Optional<Entry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>> entry = predicateConnections.stream().filter(
				e -> Utils.isSubType(e.getValue().getValue().getClassName(), rule.getClassName()) || Utils.isSubType(rule.getClassName(), e.getValue().getValue().getClassName()))
				.findFirst();
			if (entry.isPresent()) {
				
				final CrySLObject crySLObject = (CrySLObject) entry.get().getKey().getParameters().get(0);
				if (!"this".equals(crySLObject.getVarName())) {
					if (usedVars.contains(new SimpleEntry<String, String>(crySLObject.getVarName(), crySLObject.getJavaType())) && (Utils.isSubType(crySLObject.getJavaType(),
						parameter.getValue()) || Utils.isSubType(parameter.getValue(), crySLObject.getJavaType()))) {
						methodParameter = methodParameter.replace(parameter.getKey(), crySLObject.getVarName());
						continue;
					}
				}
			}

			List<Entry<String, String>> tmpVariables = new ArrayList<>();			
			declaredVariables.stream().filter(e -> !usedVars.contains(e)).filter(e -> !useMethod.getPostCGVars().contains(e)).forEach(e -> tmpVariables.add(e));
			Collections.reverse(tmpVariables);	

			Optional<Entry<String, String>> typeMatch = tmpVariables.stream()
				.filter(e -> (Utils.isSubType(e.getValue(), parameter.getValue()) || Utils.isSubType(parameter.getValue(), e.getValue()))).findFirst();
			if (typeMatch.isPresent()) {
				updateToBeEnsured(typeMatch.get());
				methodParameter = methodParameter.replace(parameter.getKey(), typeMatch.get().getKey());
				continue;
			}

			if (parameterCache.containsKey(parameter.getKey())) {
				methodParameter = methodParameter.replace(parameter.getKey(), parameterCache.get(parameter.getKey()));
				continue;
			}

			String name = analyseConstraints(parameter, rule, methodNamdResultAssignment.substring(methodNamdResultAssignment.lastIndexOf(".") + 1));
			if (!name.isEmpty()) {
				methodParameter = methodParameter.replace(parameter.getKey(), name);
				continue;
			}

			parametersOfUseMethod.add(parameter);
			if (parameter.getValue().contains(".")) {
				// If no value can be assigned add variable to the parameter list of the super method
				// Check type name for "."
				imports.add(parameter.getValue());
			}
		}

		currentInvokedMethod = methodNamdResultAssignment + methodParameter + appendix;
		return new SimpleEntry<>(currentInvokedMethod, parametersOfUseMethod);
		
	}

	/**
	 * This method analyses ISLConstraints to determine possible valid values for variables.
	 * 
	 * @param methodName
	 * 
	 * @param constraints
	 *        List of constraints that are used for the analysis.
	 * @return
	 */
	private String analyseConstraints(Entry<String, String> parameter, CodeGenCrySLRule rule, String methodName) {
		List<ISLConstraint> constraints = rule.getConstraints().stream().filter(e -> e.getInvolvedVarNames().contains(parameter.getKey())).collect(Collectors.toList());

		for (ISLConstraint constraint : constraints) {
			// handle CrySLValueConstraint
			String name = resolveCrySLConstraint(parameter, constraint, methodName, rule.getRequiredPars());
			if (!name.isEmpty()) {
				if ("java.lang.String".equals(parameter.getValue())) {
					name = "\"" + name + "\"";
				} else {
					ruleParameterCache.putIfAbsent(parameter.getKey(), name);
				}
				return name;
			}
		}
		return "";
	}

	private String resolveCrySLConstraint(Entry<String, String> parameter, ISLConstraint constraint, String methodName, List<CodeGenCrySLObject> list) {
		return resolveCrySLConstraint(parameter, constraint, methodName, list, false);
	}

	/**
	 * This method resolves constraints of a crysl rule recursively.
	 * 
	 * @param parameter
	 * 
	 * @param constraint
	 *        Constraint object that should be resolved.
	 * @param methodName
	 * @return Returns true if the given constraint object describes a valid logical expression otherwise false.
	 */
	private String resolveCrySLConstraint(Entry<String, String> parameter, ISLConstraint constraint, String methodName, List<CodeGenCrySLObject> list, boolean onlyEval) {
		String parVarName = parameter.getKey();
		if (constraint instanceof CrySLValueConstraint) {
			CrySLValueConstraint asVC = (CrySLValueConstraint) constraint;
			String constraintValue = asVC.getValueRange().get(0);
			if (onlyEval) {
				if (ruleParameterCache.containsKey(parVarName) && asVC.getValueRange().contains(ruleParameterCache.get(parVarName))) {
					return constraintValue;
				}
			} else if (asVC.getInvolvedVarNames().contains(parVarName)) {
				if ("transformation".equals(parVarName) && Arrays.asList(new String[] { "AES" }).contains(constraintValue)) {
					constraintValue += dealWithCipherGetInstance();
				}
				ruleParameterCache.putIfAbsent(parVarName, constraintValue);
				return constraintValue;
			}
		} else if (constraint instanceof CrySLComparisonConstraint) {
			CrySLComparisonConstraint comp = (CrySLComparisonConstraint) constraint;
			if (comp.getLeft().getLeft() instanceof CrySLObject && comp.getRight().getLeft() instanceof CrySLObject) {
				CrySLObject left = (CrySLObject) comp.getLeft().getLeft();
				CrySLObject right = (CrySLObject) comp.getRight().getLeft();
				int value = Integer.MIN_VALUE;
				String varName = "";
				try {
					value = Integer.parseInt(left.getName());
					varName = right.getVarName();
				} catch (NumberFormatException ex) {
					try {
						value = Integer.parseInt(right.getName());
						varName = left.getVarName();
					} catch (NumberFormatException ex2) {
						return "";
					}
				}
				String secureInt = "";
				switch (comp.getOperator()) {
					case g:
					case ge:
						try {
							secureInt = String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(2 * value) + value);
						} catch (NoSuchAlgorithmException e1) {}
						break;
					case l:
					case le:
						try {
							secureInt = String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(value));
						} catch (NoSuchAlgorithmException e) {}
						break;
					case neq:
						try {
							secureInt = String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(value - 1));
						} catch (NoSuchAlgorithmException e) {}
						break;
					case eq:
					default:
						break;
				}
				parameterCache.putIfAbsent(varName, secureInt);
				return secureInt;
			}
		} else if (constraint instanceof CrySLPredicate && "instanceOf".equals(((CrySLPredicate) constraint).getPredName())) {
			for (CodeGenCrySLObject obj : list) {
				List<ICrySLPredicateParameter> instanceOfPred = ((CrySLPredicate) constraint).getParameters();
				if (((CrySLObject) instanceOfPred.get(1)).getVarName().equals(obj.getJavaType()) && obj.getMethod()
					.equals(findMethodForParameter((CrySLObject) instanceOfPred.get(0)))) {
					return ((CrySLObject) instanceOfPred.get(0)).getVarName();
				}
			}
		} else if (constraint instanceof CrySLConstraint) {

			CrySLConstraint crySLConstraint = (CrySLConstraint) constraint;
			LogOps operator = crySLConstraint.getOperator();
			ISLConstraint left = crySLConstraint.getLeft();
			ISLConstraint right = crySLConstraint.getRight();
			Entry<String, String> leftAlternative = new SimpleEntry<String, String>(left.getInvolvedVarNames().iterator().next(), parameter.getValue());
			Entry<String, String> rightAlternative = new SimpleEntry<String, String>(right.getInvolvedVarNames().iterator().next(), parameter.getValue());

			if (operator == LogOps.and) {
				if (left.getInvolvedVarNames().contains(parVarName)) {
					if (!right.getInvolvedVarNames().contains(parVarName)) {
						if (!resolveCrySLConstraint(parameter, right, methodName, list, true).isEmpty()) {
							return resolveCrySLConstraint(parameter, left, methodName, list);
						} else {
							return "";
						}
					} else {
						if (resolveCrySLConstraint(parameter, left, methodName, list, true).isEmpty()) {
							return resolveCrySLConstraint(parameter, right, methodName, list);
						} else {
							return resolveCrySLConstraint(parameter, left, methodName, list);
						}
					}
				} else if (!resolveCrySLConstraint(parameter, left, methodName, list).isEmpty()) {
					return resolveCrySLConstraint(parameter, right, methodName, list);
				}
				return "";
			} else if (operator == LogOps.or) {
				if (!onlyEval) {
					if (left.getInvolvedVarNames().contains(parVarName)) {
						if (resolveCrySLConstraint(parameter, left, methodName, list).isEmpty()) {
							if (right.getInvolvedVarNames().contains(parVarName) && !resolveCrySLConstraint(parameter, right, methodName, list, true).isEmpty()) {
								return resolveCrySLConstraint(parameter, right, methodName, list);
							} else {
								return "";
							}
						}
						return resolveCrySLConstraint(parameter, left, methodName, list);
					}
					return resolveCrySLConstraint(parameter, right, methodName, list);
				} else {
					String leftResult = resolveCrySLConstraint(parameter, left, methodName, list, onlyEval);
					if (!leftResult.isEmpty()) {
						return leftResult;
					} else {
						return resolveCrySLConstraint(rightAlternative, right, methodName, list, onlyEval);
					}
				}
			} else if (operator == LogOps.implies) {
				if (!right.getInvolvedVarNames().contains(parVarName) || resolveCrySLConstraint(leftAlternative, left, methodName, list, true).isEmpty()) {
					return "";
				}
				return resolveCrySLConstraint(parameter, right, methodName, list);
			} else {
				return ""; // invalid operator
			}
		}
		return ""; // unsupported object type
	}

	private String dealWithCipherGetInstance() {
		String mode = "";
		String pad = "";
		List<ISLConstraint> constraints = curRule.getConstraints().parallelStream().filter(e -> e.getInvolvedVarNames().contains("transformation"))
			.filter(e -> e instanceof CrySLConstraint && ((CrySLConstraint) e).getLeft().getName().contains("AES")).collect(Collectors.toList());
		for (ISLConstraint cons : constraints) {
			if (cons instanceof CrySLConstraint && ((CrySLConstraint) cons).getOperator() == LogOps.implies) {
				CrySLValueConstraint valCons = (CrySLValueConstraint) ((CrySLConstraint) cons).getRight();
				int pos = valCons.getVar().getSplitter().getIndex();
				if (pos == 1 && mode.isEmpty()) {
					mode = valCons.getValueRange().get(0);
				} else if (pos == 2 && pad.isEmpty()) {
					pad = valCons.getValueRange().get(0);
				}
			}
		}
		//if all fails
		if (mode.isEmpty()) {
			mode = "CBC";
		}
		if (pad.isEmpty()) {
			pad = "PKCS5Padding";
		}
		return "/" + mode + "/" + pad;
	}

	private String findMethodForParameter(CrySLObject crySLObject) {
		for (TransitionEdge te : curRule.getUsagePattern().getAllTransitions()) {
			for (CrySLMethod method : te.getLabel()) {
				if (method.getParameters().parallelStream().anyMatch(f -> f.getKey().equals(crySLObject.getVarName()) && f.getValue().equals(crySLObject.getJavaType()))) {
					return method.getMethodName().substring(method.getMethodName().lastIndexOf(".") + 1);
				}
			}
		}
		return "";
	}

	/**
	 * Returns the last invoked method of a CrySLMethod object sequence.
	 * 
	 * @param transitions
	 *        Sequence
	 * @return Last invoked method.
	 */
	private CrySLMethod getLastInvokedMethod(List<TransitionEdge> transitions) {
		// Get last transition
		TransitionEdge lastTransition = transitions.get(transitions.size() - 1);

		// Get last 
		CrySLMethod lastInvokedMethod = lastTransition.getLabel().get(0);

		return lastInvokedMethod;
	}

	/**
	 * Returns the name of the last method that is used by the currently analysed crysl API-rule
	 * 
	 * @param transitions
	 *        All transitions of a state machine that describes a crysl API-rule
	 * 
	 * @return Name of the last method that is used by the currently analysed crysl API-rule.
	 */
	private String getLastInvokedMethodName(List<TransitionEdge> transitions) {
		String lastInvokedMethodName = getLastInvokedMethod(transitions).toString();
		lastInvokedMethodName = lastInvokedMethodName.substring(0, lastInvokedMethodName.lastIndexOf("("));

		if (lastInvokedMethodName.contains("=")) {
			lastInvokedMethodName = lastInvokedMethodName.substring(lastInvokedMethodName.lastIndexOf("=") + 1);
			lastInvokedMethodName = lastInvokedMethodName.trim();
		}
		return lastInvokedMethodName;
	}

	/**
	 * Adds the needed import instructions.
	 * 
	 * @param javaCodeFile
	 *        Java code object where the imports are added.
	 * 
	 * @param transitions
	 *        All transitions that are used to describe the source code.
	 */
	private Collection<String> determineImports(List<TransitionEdge> transitions) {
		Set<String> imports = new HashSet<String>();
		for (TransitionEdge transition : transitions) {
			String completeMethodName = transition.getLabel().get(0).getMethodName();
			imports.add(completeMethodName.substring(0, completeMethodName.lastIndexOf(".")));
		}
		return imports;
	}

	/**
	 * This method determines the exception classes that are thrown by a given method.
	 * 
	 * @param className
	 *        Class that contains the method that should by analysed.
	 * 
	 * @param methodName
	 *        Name of method that should by analysed.
	 * 
	 * @param methodParameters
	 *        Parameter of method to identify the method by their signature.
	 * @param imports
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void determineThrownExceptions(String className, String methodName, Class<?>[] methodParameters, List<String> imports) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		List<Class<?>> exceptionClasses = new ArrayList<Class<?>>();
		Method[] methods = java.lang.Class.forName(className).getMethods();
		for (Method meth : methods) {
			if (meth.getName().equals(methodName) && methodParameters.length == meth.getParameterCount()) {
				if (matchMethodParameters(methodParameters, meth.getParameterTypes())) {
					exceptionClasses.addAll(Arrays.asList(meth.getExceptionTypes()));
				}
			}
		}

		//.getMethod(methodName, methodParameters).getExceptionTypes();
		for (Class<?> exception : exceptionClasses) {

			imports.add(exception.getName());

			String exceptionClass = exception.getSimpleName();
			if (!exceptions.contains(exceptionClass)) {
				exceptions.add(exceptionClass);
			}

		}
	}

	private boolean matchMethodParameters(Class<?>[] methodParameters, Class<?>[] classes) {
		for (int i = 0; i < methodParameters.length; i++) {
			if (methodParameters[i].getName().equals("AnyType")) {
				continue;
			} else if (!methodParameters[i].equals(classes[i])) {
				return false;
			}
		}
		return true;
	}

	private void updateToBeEnsured(Entry<String, String> entry) {
		if (toBeEnsuredPred != null) {
			CrySLPredicate existing = toBeEnsuredPred.getKey();
			CrySLObject predicatePar = (CrySLObject) existing.getParameters().get(0);

			if (!"this".equals(predicatePar.getVarName())) {
				List<ICrySLPredicateParameter> parameters = new ArrayList<ICrySLPredicateParameter>();
				for (ICrySLPredicateParameter obj : existing.getParameters()) {
					CrySLObject par = ((CrySLObject) obj);
					if (Utils.isSubType(par.getJavaType(), predicatePar.getJavaType()) || Utils.isSubType(predicatePar.getJavaType(), par.getJavaType())) {
						parameters.add(new CrySLObject(entry.getKey(), par.getJavaType(), par.getSplitter()));
					}
				}
				if (!parameters.isEmpty()) {
					toBeEnsuredPred = new SimpleEntry<CrySLPredicate, Entry<CrySLRule, CrySLRule>>(new CrySLPredicate(existing.getBaseObject(), existing
						.getPredName(), parameters, existing.isNegated(), existing.getConstraint()), toBeEnsuredPred.getValue());
				}
			}

		}
	}

	public GeneratorClass setUpTemplateClass(String pathToTemplateFile) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource((ICompilationUnit) JavaCore.create(getDeveloperProject().getFile(pathToTemplateFile)));
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		final Map<Integer, Integer> methLims = new HashMap<>();

		GeneratorClass templateClass = new GeneratorClass();

		final ASTVisitor astVisitor = new ASTVisitor(true) {

			GeneratorMethod curMethod = null;
			CrySLObject retObj = null;
			List<CodeGenCrySLObject> pars = new ArrayList<>();
			Map<SimpleName, CrySLObject> preCGVars = new HashMap<SimpleName, CrySLObject>();
			Map<SimpleName, CrySLObject> postCGVars = new HashMap<SimpleName, CrySLObject>();
			
			List<CodeGenCrySLRule> rules = new ArrayList<CodeGenCrySLRule>();

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public boolean visit(MethodInvocation node) {
				MethodInvocation mi = node;
				String calledMethodName = mi.getName().getFullyQualifiedName();

				List arguments = mi.arguments();
				if ("addReturnObject".equals(calledMethodName)) {
					for (SimpleName var : preCGVars.keySet()) {
						String varfqn = var.getFullyQualifiedName();

						for (SimpleName name : (List<SimpleName>) arguments) {
							String efqn = name.getFullyQualifiedName();
							if (efqn.equals(varfqn)) {
								CrySLObject crySLObject = preCGVars.get(var);
								retObj = crySLObject;
								break;
							}
						}
					}
				} else if ("addParameter".equals(calledMethodName)) {
					for (SimpleName var : preCGVars.keySet()) {
						String varfqn = var.getFullyQualifiedName();
						SimpleName name = (SimpleName) arguments.get(0);
						String efqn = name.getFullyQualifiedName();
						if (efqn.equals(varfqn)) {
							pars.add(new CodeGenCrySLObject(preCGVars.get(var), (String) ((StringLiteral) arguments.get(1)).resolveConstantExpressionValue()));
							break;
						}
					}
				} else if ("includeClass".equals(calledMethodName)) {
					String rule = Utils.filterQuotes(arguments.get(0).toString());
					String simpleRuleName = rule.substring(rule.lastIndexOf(".") + 1);
					try {
						CrySLRule crySLRule = CrySLUtils.getCrySLRule(simpleRuleName);
						for (CodeGenCrySLObject o : pars) {
							for (TransitionEdge edge : crySLRule.getUsagePattern().getEdges()) {
								for (CrySLMethod method : edge.getLabel()) {
									List<Entry<String, String>> parameters = method.getParameters();
									for (int i = 0; i < parameters.size(); i++) {
										if (parameters.get(i).getKey().equals(o.getCrySLVariable())) {
											o.setMethod(method.getShortMethodName(), i);
										}
									}
								}
							}
						}
						rules.add(new CodeGenCrySLRule(crySLRule, pars, retObj));
					} catch (MalformedURLException e) {
						Activator.getDefault().logError(e);
					}
					retObj = null;
					pars = new ArrayList<>();

				} else if ("generate".equals(calledMethodName)) {
					methLims.put(1, node.getStartPosition() + node.getLength());
				} else if ("getInstance".equals(calledMethodName)) {
					methLims.put(0, node.getStartPosition() - "CrySLCodeGenerator.".length());
				}
				return super.visit(node);
			}

			@Override
			public void postVisit(ASTNode node) {
				if (node.getLocationInParent() != null && "thrownExceptionTypes".equals(node.getLocationInParent().getId())) {
					curMethod.addException(node.toString());
				}
				super.postVisit(node);
			}

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				SimpleName varName = ((VariableDeclarationFragment) ((VariableDeclarationStatement) node).fragments().get(0)).getName();
				int codeGenStmt = -1;
				for (Object s : ((Block) node.getParent()).statements()) {
					Statement statement = (Statement) s;
					if (s instanceof ExpressionStatement && ((ExpressionStatement) s).toString().contains("CrySLCodeGenerator") ) {
						codeGenStmt = statement.getStartPosition();
						break;
					} 
				}

				if(node.getStartPosition() < codeGenStmt) {
					preCGVars.put(varName, new CrySLObject(varName.getFullyQualifiedName(), ((VariableDeclarationStatement) node).getType().toString()));
				} else {
					postCGVars.put(varName, new CrySLObject(varName.getFullyQualifiedName(), ((VariableDeclarationStatement) node).getType().toString()));
				}
				return super.visit(node);
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean visit(MethodDeclaration node) {
				curMethod = new GeneratorMethod();
				curMethod.setName(node.getName().getFullyQualifiedName());
				curMethod.setReturnType(node.getReturnType2().toString());
				curMethod.setModifier("public");

				for (Statement s : (List<Statement>) node.getBody().statements()) {
					curMethod.addStatementToBody(s.toString());
				}

				for (SingleVariableDeclaration svd : (List<SingleVariableDeclaration>) node.parameters()) {
					preCGVars.put(svd.getName(), new CrySLObject(svd.getName().getFullyQualifiedName(), svd.getType().toString()));
					curMethod.addParameter(new SimpleEntry<String, String>(svd.getName().getFullyQualifiedName(), svd.getType().toString()));
				}
				curMethod.setNumberOfVariablesInTemplate(curMethod.getDeclaredVariables().size());
				templateClass.addMethod(curMethod);
				return super.visit(node);
			}

			@Override
			public void endVisit(MethodDeclaration node) {
				Collections.reverse(rules);
				curMethod.setRules(new ArrayList<>(rules));
				postCGVars.entrySet().forEach(e -> curMethod.addPostCGVars(new SimpleEntry<String, String>(e.getKey().getFullyQualifiedName(), e.getValue().getJavaType())));
				rules.clear();
				super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				templateClass.setClassName(node.getName().getFullyQualifiedName());
				return super.visit(node);
			}

			@Override
			public boolean visit(ImportDeclaration node) {
				String importedClass = node.getName().getFullyQualifiedName();
				if (!"de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator".equals(importedClass)) {
					templateClass.addImport(importedClass);
				}
				return super.visit(node);
			}

		};
		cu.accept(astVisitor);
		return templateClass;
	}

}
