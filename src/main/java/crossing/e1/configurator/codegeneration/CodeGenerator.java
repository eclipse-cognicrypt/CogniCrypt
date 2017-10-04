package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import crossing.e1.configurator.analysis.CryptSLModelReader;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
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
	 * Method name that is used in the new generated Java class.
	 */
	private String newMethod = "VALUE_IS_NOT_SET";

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
	 * Contains the import instructions that are needed for the generated code.
	 */
	private ArrayList<String> imports = new ArrayList<String>();

	/**
	 * Contains the exceptions classes that are thrown by the generated code.
	 */
	private ArrayList<String> exceptions = new ArrayList<String>();

	/**
	 * Contains all method invocations that are executed by an API-Rule.
	 */
	private ArrayList<String> methodInvocations = new ArrayList<String>();

	/**
	 * Contains all parameters that cannot be replaced by a final value. These parameters are added as method parameters for the super method "use()".
	 */
	private ArrayList<Entry<String, String>> methodParametersOfSuperMethod = new ArrayList<Entry<String, String>>();

	/**
	 * First constructor
	 * 
	 * Loads the given rule and determines possible sequences of method calls to implement the rule as java code.
	 * 
	 * @param cryptslRule
	 *        Name of the cryptsl rule that should by transformed into java code.
	 */
	public CodeGenerator(String cryptslRule) throws Exception {
		// load cryptsl rule
		rule = CryptSLModelReader.getCryptSLRule(cryptslRule);

		// Determine class name
		usedClass = rule.getClassName();
		newClass = usedClass + "Provider";

		// initialise code generator
		init();
	}

	/**
	 * Second constructor version
	 * 
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
	public CodeGenerator(String cryptslRule, String className, String methodName) throws Exception {
		// load cryptsl rule
		rule = CryptSLModelReader.getCryptSLRule(cryptslRule);

		// Determine class name
		usedClass = rule.getClassName();
		newClass = className;

		// TODO prove if the given name is an appropriate 
		// java method identifier.
		// If not. Do not store the value.
		newMethod = methodName;

		// initialise code generator
		init();
	}

	/**
	 * This method initialises the a new CodeGenerator object and is invoked by the constructor.
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		// get state machine of cryptsl rule
		StateMachineGraph stateMachine = rule.getUsagePattern();

		// analyse state machine
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
	 * The method hasNext() returns true if it exists a further variation of code that describes the given API-rule.
	 * 
	 * @return true, if it exists a further variation of code that describes the given API-rule, otherwise false.
	 */
	public boolean hasNext() {
		return this.transitions.hasNext();
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

			// Load one possible path through the state machine.
			List<TransitionEdge> currentTransitions = transitions.next();

			// Determine imports, method calls and thrown exceptions
			determineImports(currentTransitions);
			generateMethodInvocations(currentTransitions);

			// Create code object that includes the generated java code
			JavaCodeFile javaCodeFile = new JavaCodeFile(newClass + ".java");

			// generate Java code
			// ################################################################

			// first add imports
			for (String ip : imports) {
				javaCodeFile.addCodeLine(ip);
			}

			// class definition
			javaCodeFile.addCodeLine("public class " + newClass + " {");

			// method definition
			// ################################################################

			// Determine method name. We still found no solution to determine an appropriate method name.
			// Therefore we use the name "use()" as default.
			// This default name can be altered by using the following constructor:
			// CodeGenerator(String cryptslRule, String className, String methodName)
			String methodName;

			if (newMethod.equals("VALUE_IS_NOT_SET")) {
				methodName = "use";
			} else {
				methodName = newMethod;
			}

			String returnType = getReturnType(currentTransitions);

			String methodDefintion = "public " + returnType + " " + methodName + "(";

			Iterator<Entry<String, String>> iMethodParameters = methodParametersOfSuperMethod.iterator();

			do {
				if (iMethodParameters.hasNext()) {
					Entry<String, String> parameter = iMethodParameters.next();
					methodDefintion = methodDefintion + parameter.getValue() + " " + parameter.getKey();
				}

				// if a further parameters exist separate them by comma.
				if (iMethodParameters.hasNext()) {
					methodDefintion = methodDefintion + ", ";
				}

			} while (iMethodParameters.hasNext());

			methodDefintion = methodDefintion + ") ";

			// add thrown exceptions
			if (exceptions.size() > 0) {
				methodDefintion = methodDefintion + "throws ";

				Iterator<String> iExceptions = exceptions.iterator();

				do {
					String exception = iExceptions.next();
					methodDefintion = methodDefintion + exception;

					// if a further exception class follows separate them by comma
					if (iExceptions.hasNext()) {
						methodDefintion = methodDefintion + ", ";
					}

				} while (iExceptions.hasNext());

			}

			methodDefintion = methodDefintion + " {";

			javaCodeFile.addCodeLine(methodDefintion);

			// add method body

			// first method code line for test reasons
			javaCodeFile.addCodeLine("System.out.println(\"Method is running :-)\");");

			for (String methodInvocation : methodInvocations) {
				javaCodeFile.addCodeLine(methodInvocation);
			}

			// close method definition
			javaCodeFile.addCodeLine("}");
			// close class definition
			javaCodeFile.addCodeLine("}");

			// compile code
			// ################################################################
			File[] codeFiles = { javaCodeFile.writeToDisk() };
			codeFileList = codeFiles;

			// TODO
			// Compiling is enabled for testing
			CodeHandler codeHandler = new CodeHandler(codeFileList);
			//codeHandler.compile();

			// execute code
			//next = !(codeHandler.run(newClass, "use", null, null));

			next = false;

		} while (next);

		return codeFileList;
	}

	/**
	 * This method generates a method invocation for every transition of a state machine that represents a cryptsl rule.
	 * 
	 * @param currentTransitions
	 *        List of transitions that represents a cryptsl rule's state machine.
	 */
	private void generateMethodInvocations(List<TransitionEdge> currentTransitions) {
		// Determine possible valid parameter values be analysing
		// the given constraints
		// ################################################################
		analyseConstraints(rule.getConstraints());

		for (TransitionEdge transition : currentTransitions) {

			// Determine method name and signature
			// ################################################################
			CryptSLMethod method = transition.getLabel().get(0);
			String methodName = method.getMethodName().substring(method.getMethodName().lastIndexOf(".") + 1);

			// Determine parameter of method.
			List<Entry<String, String>> parameters = method.getParameters();
			Iterator<Entry<String, String>> parametersIterator = parameters.iterator();

			String currentInvokedMethod = methodName + "(";

			do {
				if (parametersIterator.hasNext()) {
					currentInvokedMethod = currentInvokedMethod + parametersIterator.next().getKey();
				}

				if (parametersIterator.hasNext()) {
					currentInvokedMethod = currentInvokedMethod + ", ";
				}

			} while (parametersIterator.hasNext());

			currentInvokedMethod = currentInvokedMethod + ");";

			// Determine exceptions that could be thrown be the invoked method.
			// ################################################################

			Class<?>[] methodParameter = new Class<?>[parameters.size()];
			int i = 0;
			for (Entry<String, String> parameter : parameters) {
				try {
					methodParameter[i] = Class.forName(parameter.getValue());
					i++;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("No class found for type: " + parameter.getValue().toString());
					e.printStackTrace();
				}
			}

			// Determine name of class that contains the given method
			String className = method.getMethodName().substring(0, method.getMethodName().lastIndexOf("."));

			try {
				determineThrownExceptions(className, methodName, methodParameter);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO determine possible subclasses
			// ################################################################
			// see also method getSubClass(className);

			// Generate method invocation. Hereafter, a method call is distinguished in three categories.
			String methodInvocation = "";

			// 1. Constructor method calls
			// 2. Static method calls
			// 3. Instance method calls

			// 1. Constructor method call
			if (currentInvokedMethod.substring(0, currentInvokedMethod.indexOf("(")).equals(usedClass)) {

				// determine name of current instance
				instanceName = usedClass.substring(0, 1).toLowerCase() + usedClass.substring(1);

				methodInvocation = usedClass + " " + instanceName + " = new " + currentInvokedMethod;

				// If constructor call is the last method call return new object.
				String lastInvokedMethod = getLastInvokedMethodName(currentTransitions).toString();

				if (methodName.equals(lastInvokedMethod)) {
					methodInvocation = methodInvocation + "\nreturn " + instanceName + ";";
				}
			}
			// Static method call
			else if (currentInvokedMethod.contains("getInstance")) {
				currentInvokedMethod = currentInvokedMethod.substring(currentInvokedMethod.lastIndexOf("=") + 1).trim();

				// determine name of current instance
				instanceName = usedClass.substring(0, 1).toLowerCase() + usedClass.substring(1);

				methodInvocation = usedClass + " " + instanceName + " = " + usedClass + "." + currentInvokedMethod;

				// If constructor call is the last method call return new object.
				String lastInvokedMethod = getLastInvokedMethodName(currentTransitions).toString();

				if (methodName.equals(lastInvokedMethod)) {
					methodInvocation = methodInvocation + "\nreturn " + instanceName + ";";
				}
			}
			// 3. Instance method call
			else {
				// Does method have a return value?
				if (method.getRetObject() != null) {
					String returnValueType = method.getRetObject().getValue();

					// Determine lastInvokedMethod
					String lastInvokedMethod = getLastInvokedMethodName(currentTransitions).toString();

					// FIXME Currently methods with return type void are
					// tagged by "AnyType". "AnyType" should be replaced by "void"
					// form methods with return type void.
					// Opened a new issue in the project CROSSINGTUD/CryptoAnalysis:
					// Return value of CryptSLMethod.getRetObject().getValue() #30

					// Last invoked method and return type is not equal to "void".
					if (methodName.equals(lastInvokedMethod) && !returnValueType.equals("AnyType")) {
						methodInvocation = "return " + instanceName + "." + currentInvokedMethod;
					}
					// Last invoked method and return type is equal to "void".
					else if (methodName.equals(lastInvokedMethod) && returnValueType.equals("AnyType")) {
						methodInvocation = instanceName + "." + currentInvokedMethod + "\nreturn " + instanceName + ";";
					}
					// Not the last invoked method and return type is not equal to "void".
					else if (!methodName.equals(lastInvokedMethod) && !returnValueType.equals("AnyType")) {
						methodInvocation = returnValueType + " = " + instanceName + "." + currentInvokedMethod;
					}
					// Not the last invoked method and return type is equal to "void"
					else if (!methodName.equals(lastInvokedMethod) && returnValueType.equals("AnyType")) {
						methodInvocation = instanceName + "." + currentInvokedMethod;
					} else {
						methodInvocation = instanceName + "." + currentInvokedMethod;
					}

				} else {
					methodInvocation = instanceName + "." + currentInvokedMethod;
				}
			}

			// Replace parameters by values that are defined in the previous step
			// ################################################################
			methodInvocation = replaceParameterByValue(parameters, methodInvocation);

			// Add new generated method invocation
			if (!methodInvocation.equals("")) {
				methodInvocations.add(methodInvocation);
				methodInvocation = "";
			}

		}
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
	private String replaceParameterByValue(List<Entry<String, String>> parameters, String currentInvokedMethod) {

		// Split current method invocation "variable = method(method parameter)" in:
		// 1. variable = method
		// 2. (method parameter)
		// replace only parameter names by values in the second part.
		String methodNamdResultAssignment = currentInvokedMethod.substring(0, currentInvokedMethod.indexOf("("));
		String methodParameter = currentInvokedMethod.substring(currentInvokedMethod.indexOf("("), currentInvokedMethod.indexOf(")"));
		String appendix = currentInvokedMethod.substring(currentInvokedMethod.indexOf(")"), currentInvokedMethod.length());

		for (Entry<String, String> parameter : parameters) {

			if (parameterValues.containsKey(parameter.getKey())) {
				String value = parameterValues.get(parameter.getKey());
				// replace parameter by value
				if (parameter.getValue().equals("java.lang.String")) {
					methodParameter = methodParameter.replace(parameter.getKey(), "\"" + value + "\"");
				} else {
					methodParameter = methodParameter.replace(parameter.getKey(), value);
				}
			} else if (currentInvokedMethod.contains("Cipher.getInstance")) {
				String firstParameter = parameter.getKey() + "[0]";
				String secondParameter = parameter.getKey() + "[1]";
				String thirdParameter = parameter.getKey() + "[2]";
				String value = "\"";

				if (parameterValues.containsKey(firstParameter) && !parameterValues.get(firstParameter).equals("")) {
					value = value + parameterValues.get(firstParameter);

					if (parameterValues.containsKey(secondParameter) && !parameterValues.get(secondParameter).equals("")) {
						value = value + "/" + parameterValues.get(secondParameter);

						if (parameterValues.containsKey(thirdParameter) && !parameterValues.get(thirdParameter).equals("")) {
							value = value + "/" + parameterValues.get(thirdParameter);
						}
					}

					value = value + "\"";
					methodParameter = methodParameter.replace(parameter.getKey(), value);
				}
			} else {
				// If no value can be assigned add variable to the parameter list of the super method
				// Check type name for "."
				if (parameter.getValue().contains(".")) {
					methodParametersOfSuperMethod
						.add(new SimpleEntry<String, String>(parameter.getKey(), parameter.getValue().substring(parameter.getValue().lastIndexOf(".") + 1)));
					imports.add("import " + parameter.getValue());
				} else {
					methodParametersOfSuperMethod.add(parameter);
				}

			}
		}

		currentInvokedMethod = methodNamdResultAssignment + methodParameter + appendix;
		return currentInvokedMethod;
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
	private void determineImports(List<TransitionEdge> transitions) {
		//ArrayList<String> imports = new ArrayList<String>();
		for (TransitionEdge transition : transitions) {
			String completeMethodName = transition.getLabel().get(0).getMethodName();
			String importInstruction = "import " + completeMethodName.substring(0, completeMethodName.lastIndexOf(".")) + ";";

			if (!imports.contains(importInstruction)) {
				//javaCodeFile.addCodeLine(importInstruction);
				imports.add(importInstruction);
			}

		}
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
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void determineThrownExceptions(String className, String methodName, Class<?>[] methodParameters) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?>[] exceptionClasses = java.lang.Class.forName(className).getMethod(methodName, methodParameters).getExceptionTypes();

		for (Class<?> exception : exceptionClasses) {

			String exceptionImport = "import " + exception.getName() + ";";
			if (!exceptions.contains(exceptionImport)) {
				imports.add(exceptionImport);
			}

			String exceptionClass = exception.getSimpleName();
			if (!exceptions.contains(exceptionClass)) {
				exceptions.add(exceptionClass);
			}

		}
	}

	/**
	 * TODO resolve sub types
	 * 
	 * This method should determine subclasses to resolve sub types of interfaces. A possible approach is to use "reflections" (https://code.google.com/archive/p/reflections/) by
	 * adding
	 * 
	 * <dependency> <groupId>org.reflections</groupId> <artifactId>reflections</artifactId> <version>0.9.11</version> </dependency>
	 * 
	 * to the pom.xml
	 * 
	 * @param className
	 * @return
	 */
	private String getSubClass(String className) {

		//		Class<?> clazz;
		//		try {
		//			Reflections reflections = new Reflections("");
		//			clazz = java.lang.Class.forName(className);
		//
		//			Package p = Package.getPackage(clazz.getPackage().getName());
		//
		//			Set<Class<?>> subTypes = reflections.getSubTypesOf(clazz.class);
		//
		//		} catch (ClassNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		return "";

	}
}
