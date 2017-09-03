package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import crossing.e1.configurator.analysis.CryptSLModelReader;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.TransitionEdge;
import typestate.interfaces.ISLConstraint;

/**
 * Provides a method to generate Java code by using rules that are defined by the DSL cryptsl.
 */
public class CodeGenerator {

	/**
	 * Iterator over all possible transitions that describe the source code of the given rule.
	 */
	private Iterator<List<TransitionEdge>> transitions;

	/**
	 * Object of the parsed rule.
	 */
	private CryptSLRule rule;

	/**
	 * Class name of the new generated Java class.
	 */
	private String newClass;

	/**
	 * Name of the Java class that is used in the generated Java code.
	 */
	private String usedClass;

	/**
	 * Hash table to store the values that are assigend to variables.
	 */
	Hashtable<String, String> parameterValues = new Hashtable<String, String>();

	/**
	 * Name of instance that is used for method invocations
	 */
	private String instanceName = "";

	/**
	 * Constructor
	 * 
	 * Loads the given rule and determines possible sequences of method class to implement the rule as java code.
	 * 
	 * @param cryptslRule
	 *        Name of the cryptsl rule that should by used
	 */
	public CodeGenerator(String cryptslRule) throws Exception {
		// load cryptsl rule
		rule = CryptSLModelReader.getCryptSLRule(cryptslRule);

		// Determine class name
		newClass = rule.getClassName() + "Provider";
		usedClass = rule.getClassName();

		// get state machine of cryptsl rule
		StateMachineGraph stateMachine = rule.getUsagePattern();

		// analyze state machine
		StateMachineGraphAnalyser stateMachineGraphAnalyser = new StateMachineGraphAnalyser(stateMachine);
		ArrayList<List<TransitionEdge>> transitionsList = stateMachineGraphAnalyser.getTransitions();

		// sort paths by number of nodes
		transitionsList.sort(new Comparator<List<TransitionEdge>>() {

			@Override
			public int compare(List<TransitionEdge> element1, List<TransitionEdge> element2) {
				if (element1.size() == element2.size())
					return 0;
				if (element1.size() < element2.size())
					return -1;
				else
					return 1; // element1.size() > element2.size()
			}
		});

		this.transitions = transitionsList.iterator();
	}

	/**
	 * The method next() returns one possible version of java code that describes the given rule.
	 * 
	 * @return Array of File objects that contain Java code that is described by the given cryptsl rule
	 * @throws Exception
	 *         <ul>
	 *         <li>Sequence of methods cannot be determined.</li>
	 * 
	 *         </ul>
	 */
	public File[] next() throws Exception {

		File[] codeFileList;

		boolean next = true;

		do {

			if (!this.transitions.hasNext()) {
				throw new Exception("Source code generation not possible.");
			}

			// Load one possible path through the state machine.
			List<TransitionEdge> currentTransitions = transitions.next();

			// Create code object that includes the generated java code
			JavaCodeFile javaCodeFile = new JavaCodeFile(newClass + ".java");

			// Determine imports
			addImports(javaCodeFile, currentTransitions);

			javaCodeFile.addCodeLine("public class " + newClass + " {");

			/*
			 * Determine method name. We still found no solution to determine an appropriate method name. Therefore we use the name "use()".
			 */
			String methodName = "use() throws Exception";
				
			//FIXME java.lang.Class.forName("className").getMethod("a").getExceptionTypes();
			
			javaCodeFile.addCodeLine("public " + getReturnType(currentTransitions) + " " + methodName + " {");

			// first code line for test reasons
			javaCodeFile.addCodeLine("System.out.println(\"Method is running :-)\");");
			
			generateMethodCalls(javaCodeFile, currentTransitions);

			javaCodeFile.addCodeLine("}"); // Close method.
			javaCodeFile.addCodeLine("}"); // Close class.

			javaCodeFile.printCode();

			// compile code

			File[] codeFiles = { javaCodeFile.writeToDisk() };
			codeFileList = codeFiles;

			CodeHandler codeHandler = new CodeHandler(codeFileList);
			codeHandler.compile();

			// execute code
			next = !(codeHandler.run(newClass, "use", null, null));

		} while (next);

		return codeFileList;
	}

	private void generateMethodCalls(JavaCodeFile javaCodeFile, List<TransitionEdge> currentTransitions) {
		for (TransitionEdge transition : currentTransitions) {

			String currentInvokedMethod = transition.getLabel().get(0).getName();
			List<Entry<String, String>> parameters = transition.getLabel().get(0).getParameters();

			// replace parameters by values
			currentInvokedMethod = replaceParameterByValue(parameters, rule.getConstraints(), currentInvokedMethod);

			// generate method call
			// Static call
			if (currentInvokedMethod.contains("getInstance")) {
				currentInvokedMethod = currentInvokedMethod.substring(currentInvokedMethod.lastIndexOf("=") + 1).trim();

				// determine name of current instance
				instanceName = usedClass.substring(0, 1).toLowerCase() + usedClass.substring(1);

				String invokeMethod = usedClass + " " + instanceName + " = " + usedClass + "." + currentInvokedMethod;
				javaCodeFile.addCodeLine(invokeMethod);
			} else { // instance access
				String invokeMethod;
				// Does method have a return value
				if (currentInvokedMethod.contains("=")) {
					String[] invocationParts = currentInvokedMethod.split("=");
					String returnValueType = invocationParts[0].trim();

					if (currentInvokedMethod.equals(getLastInvokedMethod(currentTransitions).toString())) {
						invokeMethod = "return " + instanceName + "." + invocationParts[1].trim();
					} else if (returnValueType.equals("AnyType")) {
						invokeMethod = instanceName + "." + invocationParts[1].trim();
					} else {
						invokeMethod = returnValueType + " = " + instanceName + "." + invocationParts[1].trim();
					}

				} else {
					invokeMethod = instanceName + "." + currentInvokedMethod;
				}

				javaCodeFile.addCodeLine(invokeMethod);
			}
		}
	}

	/**
	 * Replaces parameter names in method invocations by a value. This value is derived by constraints.
	 * 
	 * @param parameters
	 *        All available parameters.
	 * @param constraints
	 *        Available constraints for parameters
	 * 
	 * @param currentInvokedMethod
	 *        Method invocation as string
	 * 
	 * @return New method invocation as string (parameter names are replaces by values)
	 */
	private String replaceParameterByValue(List<Entry<String, String>> parameters, List<ISLConstraint> constraints, String currentInvokedMethod) {

		for (Entry<String, String> parameter : parameters) {
			// Check if currentInvokedMethod has parameters with constraints
			for (ISLConstraint constraint : constraints) {
				if (constraint.getInvolvedVarNames().contains(parameter.getKey())) {

					// handle CryptSLValueConstraint
					if (constraint instanceof CryptSLValueConstraint) {
						CryptSLValueConstraint cryptSLValueConstraint = (CryptSLValueConstraint) constraint;

						// replace parameter by value
						if (parameter.getValue().equals("java.lang.String")) {
							currentInvokedMethod = currentInvokedMethod.replace(cryptSLValueConstraint.getVarName(), "\"" + cryptSLValueConstraint.getValueRange().get(0) + "\"");
						} else {
							currentInvokedMethod = currentInvokedMethod.replace(cryptSLValueConstraint.getVarName(), cryptSLValueConstraint.getValueRange().get(0));
						}

						// store assigned value
						parameterValues.put(cryptSLValueConstraint.getVarName(), cryptSLValueConstraint.getValueRange().get(0));
					}
					// handle CryptSLConstraint
					else if (constraint instanceof CryptSLConstraint) {
						CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) constraint;

						// check if (variableX in {} => variableY in {})
						if (cryptSLConstraint.getLeft() instanceof CryptSLValueConstraint && cryptSLConstraint.getRight() instanceof CryptSLValueConstraint) {
							CryptSLValueConstraint cryptSLValueConstraintLeft = (CryptSLValueConstraint) cryptSLConstraint.getLeft();
							CryptSLValueConstraint cryptSLValueConstraintRight = (CryptSLValueConstraint) cryptSLConstraint.getRight();

							if (cryptSLValueConstraintRight.getVarName().equals(parameter.getKey()) && cryptSLConstraint.getOperator().toString()
								.equals("implies") && cryptSLValueConstraintLeft.getValueRange().contains(parameterValues.get(cryptSLValueConstraintLeft.getVarName()))) {

								// replace parameter by value
								if (parameter.getValue().equals("java.lang.String")) {
									currentInvokedMethod = currentInvokedMethod.replace(cryptSLValueConstraintRight.getVarName(),
										"\"" + cryptSLValueConstraintRight.getValueRange().get(0) + "\"");
								} else {
									currentInvokedMethod = currentInvokedMethod.replace(cryptSLValueConstraintRight.getVarName(),
										cryptSLValueConstraintRight.getValueRange().get(0));
								}

								// store assigned value
								parameterValues.put(cryptSLValueConstraintRight.getVarName(), cryptSLValueConstraintRight.getValueRange().get(0));

							}
						}
					}
				}
			}
		}

		return currentInvokedMethod;
	}

	/**
	 * Determine return type. The return type of the last invoked method is used for the return type of the generated method. If there is no method invoked that has a return type
	 * unequal to void the type of the used class is used.
	 * 
	 * @param transitions
	 *        All transitions that are used to describe the source code.
	 * @return Returns the return type as string.
	 */
	private String getReturnType(List<TransitionEdge> transitions) {
		String returnType = "void";
		// Get last 
		CryptSLMethod lastInvokedMethod = getLastInvokedMethod(transitions);

		// Get return type
		String type = lastInvokedMethod.getRetObject().getValue();

		if (type.equals("AnyType")) {
			returnType = usedClass;
		} else {
			returnType = type;
		}

		return returnType;
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
	 * Adds the needed import instructions.
	 * 
	 * @param javaCodeFile
	 *        Java code object where the imports are added.
	 * 
	 * @param transitions
	 *        All transitions that are used to describe the source code.
	 */
	private void addImports(JavaCodeFile javaCodeFile, List<TransitionEdge> transitions) {
		ArrayList<String> imports = new ArrayList<String>();
		for (TransitionEdge transition : transitions) {
			String completeMethodName = transition.getLabel().get(0).getMethodName();
			String importInstruction = "import " + completeMethodName.substring(0, completeMethodName.lastIndexOf(".")) + ";";

			if (!imports.contains(importInstruction)) {
				javaCodeFile.addCodeLine(importInstruction);
				imports.add(importInstruction);
			}

		}
	}
}
