package de.cognicrypt.codegenerator.generator;

import java.lang.reflect.Method;
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
import java.util.Hashtable;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import crypto.interfaces.ICryptSLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

/**
 * 
 * @author Florian Breitfelder
 * @author Stefan Krueger
 *
 */
public class CrySLBasedCodeGenerator extends CodeGenerator {

	//	private List<CodeGenCrySLRule> rules;
	/**
	 * Hash table to store the values that are assigend to variables.
	 */
	Hashtable<String, String> parameterValues = new Hashtable<String, String>();

	/**
	 * Contains the exceptions classes that are thrown by the generated code.
	 */
	private List<String> exceptions = new ArrayList<String>();

	//	private Map<String, CryptSLObject> methToReturnValue = new HashMap<String, CryptSLObject>();

	private List<String> kills = new ArrayList<String>();

	List<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>> predicateConnections;
	Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> toBeEnsuredPred = null;

	/**
	 * This constructor allows it to set a specific class and method names that are used in the generated Java code.
	 * 
	 * @param cryptslRule
	 *        Name of the cryptsl rule that should by transformed into java code.
	 * @param className
	 *        Class name that is used for the generated Java class.
	 * @param methodName
	 *        Method name that is usd for the generated Java code
	 * @throws Exception
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
		ruleClass.setPackageName(Constants.PackageName.replace(Constants.innerFileSeparator, "."));
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
		Map<String, List<CryptSLPredicate>> reliablePreds = new HashMap<String, List<CryptSLPredicate>>();
		Map<String, List<String>> tmpUsagePars = new HashMap<String, List<String>>();

		GeneratorClass templateClass = new GeneratorClass();
		templateClass.setPackageName(Constants.PackageName.replace(Constants.innerFileSeparator, "."));

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

		RuleDependencyTree rdt = new RuleDependencyTree(Utils.readCrySLRules());

		for (GeneratorMethod method : ruleClass.getMethods()) {
			tmpUsagePars.put(method.getName(), new ArrayList<String>());
			List<CodeGenCrySLRule> rules = method.getRules();
			if (rules.isEmpty()) {
				continue;
			}
			String usedClass = rules.get(rules.size() - 1).getClassName();
			predicateConnections = new ArrayList<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>>();

			for (int i = 0; i < rules.size(); i++) {
				// Determine possible valid parameter values be analysing
				// the given constraints
				// ################################################################
				analyseConstraints(rules.get(i).getConstraints());

				if (i < rules.size() - 1) {
					CryptSLRule nextRule = rules.get(i + 1);
					CryptSLRule curRule = rules.get(i);

					if (rdt.hasDirectPath(curRule, nextRule)) {
						boolean now = false;
						for (CryptSLPredicate ensPred : curRule.getPredicates()) {
							String nextType = nextRule.getClassName();
							String predType = ((CryptSLObject) ensPred.getParameters().get(0)).getJavaType();
							if (Utils.isSubType(nextType, predType) || Utils.isSubType(predType, nextType)) {
								predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CryptSLRule, CryptSLRule>(curRule, nextRule)));
								now = true;
							}
							for (CryptSLPredicate reqPred : nextRule.getRequiredPredicates()) {
								if (reqPred.equals(ensPred) && Utils.isSubType(((CryptSLObject) reqPred.getParameters().get(0)).getJavaType(), predType)) {
									Optional<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>> matchedPred = predicateConnections.stream()
										.filter(e -> e.getKey().equals(ensPred)).findFirst();
									if (now && matchedPred.isPresent()) {
										int newParNumber = getParameterNumber(curRule, (CryptSLObject) ensPred.getParameters().get(0));
										Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> entry = matchedPred.get();
										int oldParNumber = getParameterNumber(curRule, (CryptSLObject) entry.getKey().getParameters().get(0));
										if (newParNumber < oldParNumber) {
											predicateConnections.remove(entry);
											predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CryptSLRule, CryptSLRule>(curRule, nextRule)));
										}
									} else {
										predicateConnections.add(new SimpleEntry<>(ensPred, new SimpleEntry<CryptSLRule, CryptSLRule>(curRule, nextRule)));
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
				boolean next = true;
				boolean lastRule = rules.get(rules.size() - 1).equals(rule);
				// get state machine of cryptsl rule
				StateMachineGraph stateMachine = rule.getUsagePattern();
				Optional<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>> toBeEnsured = Optional.empty();

				if (lastRule) {
					toBeEnsuredPred = null;
					for (CryptSLPredicate reqPred : rule.getPredicates()) {
						CryptSLObject a = ((CodeGenCrySLRule) rule).getRequiredRetObj();
						if (!(reqPred instanceof CryptSLCondPredicate)) {
							Optional<ICryptSLPredicateParameter> o = reqPred.getParameters().stream()
								.filter(
									e -> Utils.isSubType(((CryptSLObject) e).getJavaType(), a.getJavaType()) || Utils.isSubType(a.getJavaType(), ((CryptSLObject) e).getJavaType()))
								.findFirst();
							if (o.isPresent()) {
								toBeEnsuredPred = new SimpleEntry(reqPred, new SimpleEntry(rule, null));
								break;
							}
						}
					}
					if (toBeEnsuredPred == null) {
						for (CryptSLPredicate reqPred : rule.getPredicates()) {
							CryptSLObject a = ((CodeGenCrySLRule) rule).getRequiredRetObj();
							Optional<ICryptSLPredicateParameter> o = reqPred.getParameters().stream()
								.filter(e -> Utils.isSubType(((CryptSLObject) e).getJavaType(), a.getJavaType())).findFirst();
							if (o.isPresent()) {
								toBeEnsuredPred = new SimpleEntry(reqPred, new SimpleEntry(rule, null));
								break;
							}
						}
					}
				} else {
					Stream<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>> filter = predicateConnections.stream().filter(e -> {
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

					Map<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> mayUsePreds = new HashMap<>();
					for (Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> entry : predicateConnections) {
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
						Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> originalPred = toBeEnsured.get();
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
			//			final IFile outputFile = this.project.getIFile(templateClass.getAssociatedJavaFile().getAbsolutePath());
			//			IDE.openEditor(page, outputFile, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite().getId());
		} catch (Exception e) {
			Activator.getDefault().logError(e);
		}

		return generatedClasses != null;
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
				String returnType = gen.getReturnType();
				String varName = gen.getName() + "Res";
				if (!"void".equals(returnType)) {
					tmplUsage.addStatementToBody(returnType + " " + varName + " = "); 
				}
				
				tmplUsage.addStatementToBody(className.toLowerCase() + "." + gen.getName() + "(");
				for (Entry<String, String> par : gen.getParameters()) {
					if (!declaredVariables.contains(par)) {
						ArrayList<Entry<String, String>> redundantVarList = new ArrayList<>(declaredVariables);
						Collections.reverse(redundantVarList);
						Optional<Entry<String, String>> o = redundantVarList.parallelStream().filter(e -> Utils.isSubType(e.getValue(), par.getValue())).findFirst();
						if (o.isPresent()) {
							tmplUsage.addStatementToBody(o.get().getKey() + ", ");
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

	private int getParameterNumber(CryptSLRule curRule, CryptSLObject par) {
		Set<TransitionEdge> transitions = new HashSet<TransitionEdge>(curRule.getUsagePattern().getAllTransitions());
		for (TransitionEdge trans : transitions) {
			for (CryptSLMethod potMethod : trans.getLabel()) {
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
	 * This method generates a method invocation for every transition of a state machine that represents a cryptsl rule.
	 * 
	 * @param currentTranstions
	 *        List of transitions that represents a cryptsl rule's state machine.
	 * @param currentTransitions
	 * @param predicateConnections
	 * @param imports
	 */
	private ArrayList<String> generateMethodInvocations(CodeGenCrySLRule rule, GeneratorMethod useMethod, List<TransitionEdge> currentTransitions, Map<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> usablePreds, List<String> imports, boolean lastRule) {
		Set<StateNode> killStatements = rule.getPredicates().stream().filter(pred -> pred.isNegated() && pred instanceof CryptSLCondPredicate)
			.map(e -> ((CryptSLCondPredicate) e).getConditionalMethods()).reduce(new HashSet<>(), (a, b) -> {
				a.addAll(b);
				return a;
			});
		ArrayList<String> methodInvocations = new ArrayList<String>();
		rule.getPredicates().get(0).getConstraint();
		List<String> localKillers = new ArrayList<String>();
		boolean ensures = false;

		List<Entry<String, String>> useMethodParameters = new ArrayList<Entry<String, String>>();
		Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> pre = new SimpleEntry<>(toBeEnsuredPred.getKey(), toBeEnsuredPred.getValue());
		for (TransitionEdge transition : currentTransitions) {
			CryptSLMethod method = null;
			Set<String> reqMethodNames = new HashSet<>();
			rule.getRequiredPars().parallelStream().forEach(e -> reqMethodNames.add(e.getMethod()));
			List<CryptSLMethod> labels = transition.getLabel().stream().filter(e -> {
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
					if (Utils.isSubType(par.getJavaType(), e.getParameters().get(par.getPosition()).getValue()) || Utils
						.isSubType(e.getParameters().get(par.getPosition()).getValue(), par.getJavaType())) {
						found.add(par);
					}
				}
				return found.size() == objs.size();
			}).collect(Collectors.toList());

			for (CryptSLMethod meth : labels) {
				if (method != null) {
					break;
				} else {
					toBeEnsuredPred = pre;
				}

				if (toBeEnsuredPred != null) {
					//Predicate
					method = fetchCorrespondingMethod(toBeEnsuredPred, meth, null);
					if (method != null) {
						ensures = true;
					}
				}

				for (Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> usablePred : usablePreds.entrySet()) {
					if (method == null) {
						method = fetchCorrespondingMethod(usablePred, meth, null);
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

	private CryptSLMethod fetchCorrespondingMethod(Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>> pred, CryptSLMethod meth, Set<CryptSLObject> set) {
		CryptSLObject objectOfPred = (CryptSLObject) pred.getKey().getParameters().get(0);
		String predVarType = objectOfPred.getJavaType();
		String predVarName = objectOfPred.getVarName();

		//Method
		Entry<String, String> retObject = meth.getRetObject();
		String returnType = retObject.getValue();
		String returnVarName = retObject.getKey();
		//		String className = pred.getValue().getKey().getClassName();
		//		String classSimpleName = className.substring(className.lastIndexOf('.') + 1);

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
					System.out.println("No class found for type: " + parameter.getValue().toString());
					e.printStackTrace();
				}
			}
		}
		return methodParameter;
	}

	private Entry<String, List<Entry<String, String>>> generateMethodInvocation(GeneratorMethod useMethod, String lastInvokedMethod, List<String> imports, CryptSLMethod method, String methodName, List<Entry<String, String>> parameters, CodeGenCrySLRule rule, StringBuilder currentInvokedMethod, boolean lastRule) {
		// Generate method invocation. Hereafter, a method call is distinguished in three categories.
		String methodInvocation = "";

		String className = rule.getClassName();
		String simpleName = className.substring(className.lastIndexOf('.') + 1);
		String instanceName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);

		// 1. Constructor method calls
		// 2. Static method calls
		// 3. Instance method calls

		// 1. Constructor method call
		CryptSLObject retObjInTemplate = rule.getRequiredRetObj();
		if (currentInvokedMethod.substring(0, currentInvokedMethod.indexOf("(")).equals(simpleName)) {
			if (lastRule && retObjInTemplate != null && (Utils.isSubType(className, retObjInTemplate.getJavaType()) || Utils.isSubType(retObjInTemplate.getJavaType(),
				className))) {
				methodInvocation = retObjInTemplate.getName() + " = new " + currentInvokedMethod;
			} else {
				methodInvocation = className + " " + instanceName + " = new " + currentInvokedMethod;
			}
			//			if (methodName.equals(lastInvokedMethod.substring(lastInvokedMethod.lastIndexOf('.') + 1))) {
			//				methodInvocation = methodInvocation;// + "\nreturn " + instanceName + ";";
			//			}
		}
		// Static method call
		else if (currentInvokedMethod.toString().contains("getInstance")) {
			currentInvokedMethod = new StringBuilder(currentInvokedMethod.substring(currentInvokedMethod.lastIndexOf("=") + 1).trim());
			methodInvocation = className + " " + instanceName + " = " + simpleName + "." + currentInvokedMethod;

			//			if (methodName.equals(lastInvokedMethod.substring(lastInvokedMethod.lastIndexOf('.') + 1))) {
			//				methodInvocation = methodInvocation; // + "\nreturn " + instanceName + ";";
			//			}
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
						methodInvocation = retObjInTemplate.getVarName() + " = " +
						//"return " + 
							instanceName + "." + currentInvokedMethod;
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
	 * This method analyses ISLConstraints to determine possible valid values for variables.
	 * 
	 * @param constraints
	 *        List of constraints that are used for the analysis.
	 */
	private void analyseConstraints(List<ISLConstraint> constraints) {
		for (ISLConstraint constraint : constraints) {
			// handle CryptSLValueConstraint
			if (constraint instanceof CryptSLValueConstraint) {
				CryptSLValueConstraint cryptSLValueConstraint = (CryptSLValueConstraint) constraint;
				resolveCryptSLValueConstraint(cryptSLValueConstraint);
			}
			// handle CryptSLConstraint
			else if (constraint instanceof CryptSLConstraint) {
				CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) constraint;

				// (CryptSLConstrant | CryptSLValueConstraint => CryptSLConstrant | CryptSLValueConstraint)
				if ((cryptSLConstraint.getLeft() instanceof CryptSLConstraint || cryptSLConstraint.getRight() instanceof CryptSLValueConstraint) && cryptSLConstraint
					.getOperator() == LogOps.implies && (cryptSLConstraint
						.getRight() instanceof CryptSLConstraint || cryptSLConstraint.getRight() instanceof CryptSLValueConstraint)) {

					// 1. step verify premise
					if (resolveCryptSLConstraint(cryptSLConstraint.getLeft())) {
						// 2. step verify conclusion
						resolveCryptSLConstraint(cryptSLConstraint.getRight());
					}
				}
			} else if (constraint instanceof CryptSLComparisonConstraint) {
				CryptSLComparisonConstraint comp = (CryptSLComparisonConstraint) constraint;
				if (comp.getLeft().getLeft() instanceof CryptSLObject && comp.getRight().getLeft() instanceof CryptSLObject) {
					CryptSLObject left = (CryptSLObject) comp.getLeft().getLeft();
					CryptSLObject right = (CryptSLObject) comp.getRight().getLeft();
					int value;
					String varName = "";
					try {
						value = Integer.parseInt(left.getName());
						varName = right.getVarName();
					} catch (NumberFormatException ex) {
						value = Integer.parseInt(right.getName());
						varName = left.getVarName();
					}

					switch (comp.getOperator()) {
						case g:
						case ge:
							try {
								parameterValues.put(varName, String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(2 * value) + value));
							} catch (NoSuchAlgorithmException e1) {}
							break;
						case l:
						case le:
							try {
								parameterValues.put(varName, String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(value)));
							} catch (NoSuchAlgorithmException e) {}
							break;
						case neq:
							try {
								parameterValues.put(varName, String.valueOf(SecureRandom.getInstance("SHA1PRNG").nextInt(value - 1)));
							} catch (NoSuchAlgorithmException e) {}
							break;
						case eq:
						default:
							break;
					}
				}
			}
		}
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

		for (Entry<String, String> parameter : parametersOfCall) {
			Optional<Entry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>> entry = predicateConnections.stream().filter(
				e -> Utils.isSubType(e.getValue().getValue().getClassName(), rule.getClassName()) || Utils.isSubType(rule.getClassName(), e.getValue().getValue().getClassName()))
				.findFirst();
			CryptSLObject cryptSLObject = null;
			if (entry.isPresent()) {
				cryptSLObject = (CryptSLObject) entry.get().getKey().getParameters().get(0);
			}

			if (cryptSLObject != null) {
				if (!"this".equals(cryptSLObject.getVarName())) {
					if ((Utils.isSubType(cryptSLObject.getJavaType(), parameter.getValue()) || Utils.isSubType(parameter.getValue(), cryptSLObject.getJavaType()))) {
						methodParameter = methodParameter.replace(parameter.getKey(), cryptSLObject.getVarName());
						continue;
					}
				}
			}

			if (currentInvokedMethod.contains("Cipher.getInstance")) {
				String firstParameter = parameter.getKey() + "[0]";
				String secondParameter = parameter.getKey() + "[1]";
				String thirdParameter = parameter.getKey() + "[2]";
				String value = "\"";

				if (parameterValues.containsKey(firstParameter) && !parameterValues.get(firstParameter).isEmpty()) {
					value = value + parameterValues.get(firstParameter);

					if (parameterValues.containsKey(secondParameter) && !parameterValues.get(secondParameter).isEmpty()) {
						value = value + "/" + parameterValues.get(secondParameter);

						if (parameterValues.containsKey(thirdParameter) && !parameterValues.get(thirdParameter).isEmpty()) {
							value = value + "/" + parameterValues.get(thirdParameter);
						}
					}

					value = value + "\"";
					methodParameter = methodParameter.replace(parameter.getKey(), value);
					continue;
				}
			}

			int index = useMethod.getNumberOfVariablesInTemplate();
			List<Entry<String, String>> tmpVariables = new ArrayList<>();
			if (declaredVariables.size() > index) {
				tmpVariables.add(declaredVariables.get(declaredVariables.size() - 1));
			}
			tmpVariables.addAll(declaredVariables.subList(0, index));
			List<Entry<String, String>> declVariables = declaredVariables.subList(index, declaredVariables.size());
			Collections.reverse(declVariables);
			tmpVariables.addAll(declVariables);

			Optional<Entry<String, String>> typeMatch = tmpVariables.stream()
				.filter(e -> Utils.isSubType(e.getValue(), parameter.getValue()) || Utils.isSubType(parameter.getValue(), e.getValue())).findFirst();
			if (typeMatch.isPresent()) {
				updateToBeEnsured(typeMatch.get());
				methodParameter = methodParameter.replace(parameter.getKey(), typeMatch.get().getKey());
				continue;
			}

			Optional<CodeGenCrySLObject> parMatch = rule.getRequiredPars().parallelStream()
				.filter(e -> Utils.isSubType(parameter.getValue(), e.getJavaType()) || Utils.isSubType(e.getJavaType(), parameter.getValue())).findFirst();
			if (parMatch.isPresent()) {
				updateToBeEnsured(new SimpleEntry<String, String>(parMatch.get().getVarName(), parMatch.get().getJavaType()));
				methodParameter = methodParameter.replace(parameter.getKey(), parMatch.get().getVarName());
				continue;
			}

			if (parameterValues.containsKey(parameter.getKey())) {
				String value = parameterValues.get(parameter.getKey());
				// replace parameter by value
				if (parameter.getValue().equals("java.lang.String")) {
					methodParameter = methodParameter.replace(parameter.getKey(), "\"" + value + "\"");
					continue;
				} else {
					methodParameter = methodParameter.replace(parameter.getKey(), value);
					continue;
				}
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

	private void updateToBeEnsured(Entry<String, String> entry) {
		if (toBeEnsuredPred != null) {
			CryptSLPredicate existing = toBeEnsuredPred.getKey();
			CryptSLObject predicatePar = (CryptSLObject) existing.getParameters().get(0);

			if (!"this".equals(predicatePar.getVarName())) {
				List<ICryptSLPredicateParameter> parameters = new ArrayList<ICryptSLPredicateParameter>();
				for (ICryptSLPredicateParameter obj : existing.getParameters()) {
					CryptSLObject par = ((CryptSLObject) obj);
					if (Utils.isSubType(par.getJavaType(), predicatePar.getJavaType()) || Utils.isSubType(predicatePar.getJavaType(), par.getJavaType())) {
						parameters.add(new CryptSLObject(entry.getKey(), par.getJavaType(), par.getSplitter()));
					}
				}
				if (!parameters.isEmpty()) {
					toBeEnsuredPred = new SimpleEntry<CryptSLPredicate, Entry<CryptSLRule, CryptSLRule>>(new CryptSLPredicate(existing.getBaseObject(), existing
						.getPredName(), parameters, existing.isNegated(), existing.getConstraint()), toBeEnsuredPred.getValue());
				}
			}

		}
	}

	/**
	 * This method assigns a value to a variable by analysing a CryptSLValueConstraint object.
	 * 
	 * If the assigned value is valid this method returns true otherwise false
	 * 
	 * @param cryptSLValueConstraint
	 *        CryptSLValueConstraint object that is used to determine a value.
	 * 
	 * @return If the assigned value is valid this method returns true otherwise false
	 */
	private boolean resolveCryptSLValueConstraint(CryptSLValueConstraint cryptSLValueConstraint) {
		CryptSLObject cryptSLObject = cryptSLValueConstraint.getVar();
		CryptSLSplitter cryptSLSplitter = cryptSLObject.getSplitter();

		String parameterNameKey = "";

		// Distinguish between regular variable assignments and
		// part assignments.
		if (cryptSLSplitter == null) {
			parameterNameKey = cryptSLValueConstraint.getVarName();
		} else {
			parameterNameKey = cryptSLObject.getVarName() + "[" + cryptSLSplitter.getIndex() + "]";
		}

		if (!parameterValues.containsKey(parameterNameKey)) {
			if ("transformation[0]".equals(parameterNameKey)) {
				parameterValues.put("alg", cryptSLValueConstraint.getValueRange().get(0));
			}
			parameterValues.put(parameterNameKey, cryptSLValueConstraint.getValueRange().get(0));
		}

		if (cryptSLValueConstraint.getValueRange().contains(parameterValues.get(parameterNameKey))) {
			return true; // Assigned parameter value is in valid value range.
		} else {
			return false; // Assigned parameter value is not in valid value range.
		}
	}

	/**
	 * This method resolves constraints of a cryptsl rule recursively.
	 * 
	 * @param constraint
	 *        Constraint object that should be resolved.
	 * @return Returns true if the given constraint object describes a valid logical expression otherwise false.
	 */
	private boolean resolveCryptSLConstraint(ISLConstraint constraint) {
		if (constraint instanceof CryptSLValueConstraint) {

			CryptSLValueConstraint cryptSLValueConstraint = (CryptSLValueConstraint) constraint;
			return resolveCryptSLValueConstraint(cryptSLValueConstraint);

		} else if (constraint instanceof CryptSLConstraint) {

			CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) constraint;
			LogOps operator = cryptSLConstraint.getOperator();

			if (operator == LogOps.and) {
				return resolveCryptSLConstraint(cryptSLConstraint.getLeft()) && resolveCryptSLConstraint(cryptSLConstraint.getRight());
			} else if (operator == LogOps.or) {
				return resolveCryptSLConstraint(cryptSLConstraint.getLeft()) || resolveCryptSLConstraint(cryptSLConstraint.getRight());
			} else if (operator == LogOps.implies) {
				if (resolveCryptSLConstraint(cryptSLConstraint.getLeft())) {
					return resolveCryptSLConstraint(cryptSLConstraint.getRight());
				} else {
					return true;
				}
			} else {
				return false; // invalid operator
			}
		}
		return false; // unsupported object type
	}

	/**
	 * Determine return type. The return type of the last invoked method is used for the return type of the generated method. If there is no method invoked that has a return type
	 * unequal to void the type of the used class is used.
	 * 
	 * @param transitions
	 *        All transitions that are used to describe the source code.
	 * @return Returns the return type as string.
	 */
	private String getReturnType(List<TransitionEdge> transitions, String className) {
		// Get last 
		CryptSLMethod lastInvokedMethod = getLastInvokedMethod(transitions);

		// Get return type
		String type = lastInvokedMethod.getRetObject().getValue();

		if (type.equals("void")) {
			return className;
		} else {
			return type;
		}
	}

	/**
	 * Returns the last invoked method of a CryptSLMethod object sequence.
	 * 
	 * @param transitions
	 *        Sequence
	 * @return Last invoked method.
	 */
	private CryptSLMethod getLastInvokedMethod(List<TransitionEdge> transitions) {
		// Get last transition
		TransitionEdge lastTransition = transitions.get(transitions.size() - 1);

		// Get last 
		CryptSLMethod lastInvokedMethod = lastTransition.getLabel().get(0);

		return lastInvokedMethod;
	}

	/**
	 * Returns the name of the last method that is used by the currently analysed cryptsl API-rule
	 * 
	 * @param transitions
	 *        All transitions of a state machine that describes a cryptsl API-rule
	 * 
	 * @return Name of the last method that is used by the currently analysed cryptsl API-rule.
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

}