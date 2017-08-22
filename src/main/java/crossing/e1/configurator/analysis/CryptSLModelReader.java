package crossing.e1.configurator.analysis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.common.types.JvmExecutable;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLArithmeticConstraint.ArithOp;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Aggregate;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticExpression;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticOperator;
import de.darmstadt.tu.crossing.cryptSL.ComparisonExpression;
import de.darmstadt.tu.crossing.cryptSL.Constraint;
import de.darmstadt.tu.crossing.cryptSL.DestroysBlock;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;
import de.darmstadt.tu.crossing.cryptSL.EnsuresBlock;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.ForbMethod;
import de.darmstadt.tu.crossing.cryptSL.ForbiddenBlock;
import de.darmstadt.tu.crossing.cryptSL.Literal;
import de.darmstadt.tu.crossing.cryptSL.LiteralExpression;
import de.darmstadt.tu.crossing.cryptSL.ObjectDecl;
import de.darmstadt.tu.crossing.cryptSL.PreDefinedPredicates;
import de.darmstadt.tu.crossing.cryptSL.SuPar;
import de.darmstadt.tu.crossing.cryptSL.SuParList;
import de.darmstadt.tu.crossing.cryptSL.SuperType;
import de.darmstadt.tu.crossing.cryptSL.UnaryPreExpression;
import de.darmstadt.tu.crossing.cryptSL.UseBlock;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class CryptSLModelReader {

	private List<CryptSLForbiddenMethod> forbiddenMethods = null;
	private StateMachineGraph stateMachineGraph = null;
	
	/**
	 * Hashtable of CryptSLRule object. This objects are used
	 * for the automatic code generation of class
	 * crossing.e1.configurator.codegeneration.CodeGenerator
	 */
	public static Hashtable<String, CryptSLRule> rules = new Hashtable<String, CryptSLRule>();

	public CryptSLModelReader() throws ClassNotFoundException, CoreException, IOException {
		Injector injector = CryptSLActivator.getInstance().getInjector(CryptSLActivator.DE_DARMSTADT_TU_CROSSING_CRYPTSL);

		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		final IProject iproject = Utils.getIProjectFromSelection();
		if (iproject == null) {
			// if no project selected abort with error message
			Activator.getDefault().logError(null, Constants.NoFileandNoProjectOpened);
		}
		if (iproject.isOpen() && iproject.hasNature(Constants.JavaNatureID)) {
			resourceSet.setClasspathURIContext(JavaCore.create(iproject));
		}
		new JdtTypeProviderFactory(injector.getInstance(IJavaProjectProvider.class)).createTypeProvider(resourceSet);

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		List<String> exceptions = new ArrayList<String>();
		exceptions.add("String.cryptsl");
		for (IResource res : ResourcesPlugin.getWorkspace().getRoot().getFolder(Path.fromPortableString("/CryptSL Examples/src/de/darmstadt/tu/crossing/")).members()) {
			final String extension = res.getFileExtension();
			final String fileName = res.getName();
			if (!"cryptsl".equals(extension) || exceptions.contains(fileName)) {
				continue;
			}
			Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/" + fileName, true), true);
			EcoreUtil.resolveAll(resourceSet);
			EObject eObject = resource.getContents().get(0);
			Domainmodel domainModel = (Domainmodel) eObject;
			EnsuresBlock ensure = domainModel.getEnsure();
			Map<CryptSLPredicate, SuperType> pre_preds = Maps.newHashMap();
			DestroysBlock destroys = domainModel.getDestroy();
			if (destroys != null) {
				pre_preds.putAll(getKills(destroys.getPred()));
			}
			if (ensure != null) {
				pre_preds.putAll(getPredicates(ensure.getPred()));
			}
			stateMachineGraph = buildStateMachineGraph(domainModel.getOrder());
			ForbiddenBlock forbEvent = domainModel.getForbEvent();
			forbiddenMethods = (forbEvent != null) ? getForbiddenMethods(forbEvent.getForb_methods()) : Lists.newArrayList();

			List<ISLConstraint> constraints = (domainModel.getReqConstraints() != null) ? buildUpConstraints(domainModel.getReqConstraints().getReq()) : Lists.newArrayList();
			List<Entry<String, String>> objects = getObjects(domainModel.getUsage());

			List<CryptSLPredicate> actPreds = Lists.newArrayList();

			for (CryptSLPredicate pred : pre_preds.keySet()) {
				SuperType cond = pre_preds.get(pred);
				if (cond == null) {
					actPreds.add(pred);
				} else {
					actPreds.add(new CryptSLCondPredicate(pred.getBaseObject(), pred.getPredName(), pred.getParameters(), pred
						.isNegated(), getStatesForMethods(CryptSLReaderUtils.resolveAggregateToMethodeNames(cond))));
				}
			}
			final String className = fileName.substring(0, fileName.indexOf(extension) - 1);
			CryptSLRule rule = new CryptSLRule(className, objects, forbiddenMethods, stateMachineGraph, constraints, actPreds);

			/*
			 * Store parsed rule to a hashtable
			 */
			rules.put(className, rule);

		}

	}

	/**
	 * Returns the cryptsl rule with the name that is defined by the method parameter cryptslRule.
	 * 
	 * @param cryptslRule
	 *        Name of cryptsl rule that should by returend.
	 * 
	 * @return Returns the cryptsl rule with the name that is defined by the parameter cryptslRule.
	 * @throws Exception
	 *         Thows an exception if given rule name does not exist.
	 */
	public static CryptSLRule getCryptSLRule(String cryptslRule) throws Exception {
		if (rules.containsKey(cryptslRule)) {
			return rules.get(cryptslRule);
		} else {
			throw new Exception("Rules does not exist.");
		}
	}

	private Map<? extends CryptSLPredicate, ? extends SuperType> getKills(EList<Constraint> eList) {
		Map<CryptSLPredicate, SuperType> preds = new HashMap<CryptSLPredicate, SuperType>();
		for (Constraint pred : eList) {
			List<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();

			if (pred.getParList() != null) {
				for (SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						String name = ((LiteralExpression) var.getVal().getLit().getName()).getValue().getName();
						if (name == null) {
							name = "this";
						}
						variables.add(new CryptSLObject(name));
					} else {
						variables.add(new CryptSLObject("_"));
					}
				}
			}
			String meth = pred.getPredName();
			SuperType cond = pred.getLabelCond();
			if (cond == null) {
				preds.put(new CryptSLPredicate(null, meth, variables, true), null);
			} else {
				preds.put(new CryptSLPredicate(null, meth, variables, true), cond);
			}

		}
		return preds;
	}

	private List<Entry<String, String>> getObjects(UseBlock usage) {
		List<Entry<String, String>> objects = new ArrayList<>();

		for (ObjectDecl obj : usage.getObjects()) {
			objects.add(new SimpleEntry<String, String>(obj.getObjectType().getIdentifier(), obj.getObjectName().getName()));
		}

		return objects;
	}

	/**
	 * Stores a cryptsl rule to a binary file.
	 * 
	 * @param rule
	 *        Rule that should be stored to a file.
	 * 
	 * @param className
	 *        Name of rule that should by stored.
	 * 
	 * @param filePath
	 *        Path where to store the rule.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void storeRuletoFile(CryptSLRule rule, String className, String filePath) throws IOException, ClassNotFoundException {
		// Open file output
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

		// Write rule to file
		objectOutputStream.writeObject(rule);

		// Close file output
		objectOutputStream.close();
		fileOutputStream.close();

		// Open file input
		FileInputStream fileInputStream = new FileInputStream(filePath);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		// Try to read file
		objectInputStream.readObject();

		// Close file input
		objectInputStream.close();
		fileInputStream.close();
	}

	private Map<CryptSLPredicate, SuperType> getPredicates(List<Constraint> predList) {
		Map<CryptSLPredicate, SuperType> preds = new HashMap<CryptSLPredicate, SuperType>();
		for (Constraint pred : predList) {
			List<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();

			if (pred.getParList() != null) {
				boolean firstPar = true;
				for (SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						String name = ((LiteralExpression) var.getVal().getLit().getName()).getValue().getName();
						if (name == null) {
							name = "this";
						}
						variables.add(new CryptSLObject(name));
					} else {
						if (firstPar) {
							variables.add(new CryptSLObject("this"));
						} else {
							variables.add(new CryptSLObject("_"));
						}
					}
					firstPar = false;
				}
			}
			String meth = pred.getPredName();
			SuperType cond = pred.getLabelCond();
			//			CryptSLObject bobj = null;
			//			if (pred.getRet().getVal() == null) {
			//				bobj = new CryptSLObject("this");
			//			} else {
			//				bobj = new CryptSLObject(((LiteralExpression) pred.getRet().getVal().getLit().getName()).getValue().getName());
			//			}
			if (cond == null) {
				preds.put(new CryptSLPredicate(null, meth, variables, false), null);
			} else {
				preds.put(new CryptSLPredicate(null, meth, variables, false), cond);
			}

		}
		return preds;
	}

	private Set<StateNode> getStatesForMethods(List<CryptSLMethod> condMethods) {
		Set<StateNode> predGens = new HashSet<StateNode>();
		if (condMethods.size() != 0) {
			for (TransitionEdge methTrans : stateMachineGraph.getAllTransitions()) {
				final List<CryptSLMethod> transLabel = methTrans.getLabel();
				if (transLabel.size() > 0 && (transLabel.equals(condMethods) || (condMethods.size() == 1 && transLabel.contains(condMethods.get(0))))) {
					predGens.add(methTrans.getRight());
				}
			}
		}
		return predGens;
	}

	private List<ISLConstraint> buildUpConstraints(List<Constraint> constraints) {
		List<ISLConstraint> slCons = new ArrayList<ISLConstraint>();
		for (Constraint cons : constraints) {
			ISLConstraint constraint = getConstraint(cons);
			if (constraint != null) {
				slCons.add(constraint);
			}
		}
		return slCons;
	}

	private String filterQuotes(String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}

	private ISLConstraint getConstraint(Constraint cons) {
		ISLConstraint slci = null;

		if (cons instanceof ArithmeticExpression) {
			ArithmeticExpression ae = (ArithmeticExpression) cons;
			ae.getOperator().toString();
			slci = new CryptSLArithmeticConstraint("0", "1", ArithOp.n);
		} else if (cons instanceof LiteralExpression) {
			LiteralExpression lit = (LiteralExpression) cons;
			List<String> parList = new ArrayList<String>();
			if (lit.getLitsleft() != null) {
				for (Literal a : lit.getLitsleft().getParameters()) {
					parList.add(filterQuotes(a.getVal()));
				}
			}
			String pred = lit.getCons().getPredName();
			if (pred != null) {
				switch (pred) {
					case "callTo":
						List<ICryptSLPredicateParameter> methodsToBeCalled = new ArrayList<ICryptSLPredicateParameter>();
						methodsToBeCalled.addAll(CryptSLReaderUtils.resolveAggregateToMethodeNames((SuperType) ((PreDefinedPredicates) lit.getCons()).getObj().get(0)));
						slci = new CryptSLPredicate(null, pred, methodsToBeCalled, false);
						break;
					case "noCallTo":
						List<ICryptSLPredicateParameter> methodsNotToBeCalled = new ArrayList<ICryptSLPredicateParameter>();
						List<CryptSLMethod> resolvedMethodNames = CryptSLReaderUtils
							.resolveAggregateToMethodeNames((Aggregate) ((PreDefinedPredicates) lit.getCons()).getObj().get(0));
						for (CryptSLMethod csm : resolvedMethodNames) {
							forbiddenMethods.add(new CryptSLForbiddenMethod(csm, true));
							methodsNotToBeCalled.add(csm);
						}
						slci = new CryptSLPredicate(null, pred, methodsNotToBeCalled, false);
						break;
					case "neverTypeOf":
						List<ICryptSLPredicateParameter> varNType = new ArrayList<ICryptSLPredicateParameter>();
						varNType.add(new CryptSLObject(((de.darmstadt.tu.crossing.cryptSL.Object) ((PreDefinedPredicates) lit.getCons()).getObj().get(0)).getName()));
						varNType.add(new CryptSLObject(((PreDefinedPredicates) lit.getCons()).getType().getQualifiedName()));
						slci = new CryptSLPredicate(null, pred, varNType, false);
						break;
					default:
						new RuntimeException();
				}
			} else {
				String part = lit.getCons().getPart();
				if (part != null) {
					LiteralExpression name = (LiteralExpression) lit.getCons().getLit().getName();
					CryptSLObject variable = new CryptSLObject(name.getValue()
						.getName(), new CryptSLSplitter(Integer.parseInt(lit.getCons().getInd()), filterQuotes(lit.getCons().getSplit())));
					slci = new CryptSLValueConstraint(variable, parList);
				} else {
					LiteralExpression name = (LiteralExpression) lit.getCons().getName();
					if (name == null) {
						name = (LiteralExpression) lit.getCons().getLit().getName();
					}
					CryptSLObject variable = new CryptSLObject(name.getValue().getName());
					slci = new CryptSLValueConstraint(variable, parList);
				}
			}
		} else if (cons instanceof ComparisonExpression) {
			ComparisonExpression comp = (ComparisonExpression) cons;
			CompOp op = null;
			switch (comp.getOperator().toString()) {
				case ">":
					op = CompOp.g;
					break;
				case "<":
					op = CompOp.l;
					break;
				case ">=":
					op = CompOp.ge;
					break;
				case "<=":
					op = CompOp.le;
					break;
				default:
					op = CompOp.eq;
			}
			CryptSLArithmeticConstraint left;
			CryptSLArithmeticConstraint right;

			Constraint leftExpression = comp.getLeftExpression();
			if (leftExpression instanceof LiteralExpression) {
				left = convertLiteralToArithmetic(leftExpression);
			} else {
				left = (CryptSLArithmeticConstraint) leftExpression;
			}

			Constraint rightExpression = comp.getRightExpression();
			if (rightExpression instanceof LiteralExpression) {
				right = convertLiteralToArithmetic(rightExpression);
			} else {
				ArithmeticExpression ar = (ArithmeticExpression) rightExpression;
				String leftValue = getValueOfLiteral(ar.getLeftExpression());
				String rightValue = getValueOfLiteral(ar.getRightExpression());

				ArithmeticOperator aop = ((ArithmeticOperator) ar.getOperator());
				ArithOp operator = null;
				if (aop.getPLUS() != null && !aop.getPLUS().isEmpty()) {
					operator = ArithOp.p;
				} else {
					operator = ArithOp.n;
				}

				right = new CryptSLArithmeticConstraint(leftValue, rightValue, operator);
			}
			slci = new CryptSLComparisonConstraint(left, right, op);
		} else if (cons instanceof UnaryPreExpression) {
			UnaryPreExpression un = (UnaryPreExpression) cons;
			List<ICryptSLPredicateParameter> vars = new ArrayList<ICryptSLPredicateParameter>();
			Constraint innerPredicate = un.getEnclosedExpression();
			if (innerPredicate.getParList() != null) {
				for (SuPar sup : innerPredicate.getParList().getParameters()) {
					if (sup.getVal() == null) {
						vars.add(new CryptSLObject("_"));
					} else {
						LiteralExpression lit = sup.getVal();

						String variable = filterQuotes(((LiteralExpression) lit.getLit().getName()).getValue().getName());
						String part = sup.getVal().getPart();
						if (part != null) {
							vars.add(new CryptSLObject(variable, new CryptSLSplitter(Integer.parseInt(lit.getInd()), filterQuotes(lit.getSplit()))));
						} else {
							vars.add(new CryptSLObject(variable));
						}
					}
				}
			}
			slci = new CryptSLPredicate(null, innerPredicate.getPredName(), vars, true);
		} else if (cons instanceof Constraint) {
			if (cons.getPredName() != null && !cons.getPredName().isEmpty()) {
				List<ICryptSLPredicateParameter> vars = new ArrayList<ICryptSLPredicateParameter>();

				final SuParList parList = cons.getParList();
				if (parList != null) {
					for (SuPar sup : parList.getParameters()) {
						if (sup.getVal() == null) {
							vars.add(new CryptSLObject("_"));
						} else {
							LiteralExpression lit = sup.getVal();

							String variable = filterQuotes(((LiteralExpression) lit.getLit().getName()).getValue().getName());
							String part = sup.getVal().getPart();
							if (part != null) {
								vars.add(new CryptSLObject(variable, new CryptSLSplitter(Integer.parseInt(lit.getInd()), filterQuotes(lit.getSplit()))));
							} else {
								vars.add(new CryptSLObject(variable));
							}
						}
					}
				}
				slci = new CryptSLPredicate(null, cons.getPredName(), vars, false);
			} else {
				LogOps op = null;
				switch (cons.getOperator().toString()) {
					case "&&":
						op = LogOps.and;
						break;
					case "||":
						op = LogOps.or;
						break;
					case "=>":
						op = LogOps.implies;
						break;
					case "<=>":
						op = LogOps.eq;
						break;
					default:
						op = LogOps.and;
				}
				slci = new CryptSLConstraint(getConstraint(cons.getLeftExpression()), getConstraint(cons.getRightExpression()), op);
			}
		}

		return slci;
	}

	private CryptSLArithmeticConstraint convertLiteralToArithmetic(Constraint expression) {
		EObject name = ((LiteralExpression) expression).getCons().getName();
		return new CryptSLArithmeticConstraint(getValueOfLiteral(name), "0", crypto.rules.CryptSLArithmeticConstraint.ArithOp.p);
	}

	private String getValueOfLiteral(EObject name) {
		String value = "";
		if (name instanceof LiteralExpression) {
			SuperType preValue = ((LiteralExpression) name).getValue();
			if (preValue != null) {
				value = preValue.getName();
			} else {
				value = getValueOfLiteral(((LiteralExpression) name).getCons().getName());
			}
		} else {
			value = ((Literal) name).getVal();
		}
		return filterQuotes(value);
	}

	private List<CryptSLForbiddenMethod> getForbiddenMethods(EList<ForbMethod> methods) {
		List<CryptSLForbiddenMethod> methodSignatures = new ArrayList<CryptSLForbiddenMethod>();
		for (ForbMethod fm : methods) {
			JvmExecutable meth = fm.getJavaMeth();
			List<Entry<String, String>> pars = new ArrayList<Entry<String, String>>();
			for (JvmFormalParameter par : meth.getParameters()) {
				pars.add(new SimpleEntry<String, String>(par.getParameterType().getSimpleName(), par.getSimpleName()));
			}
			methodSignatures.add(new CryptSLForbiddenMethod(new CryptSLMethod(meth.getDeclaringType().getIdentifier() + "." + meth
				.getSimpleName(), pars, null, new SimpleEntry<String, String>("_", "AnyType")), false));
		}
		return methodSignatures;
	}

	/**
	 * Builds a state machine of a given order.
	 * 
	 * @param order
	 *        Order that is used for creating a state machine that describes this order.
	 * 
	 * @return Returns a StateMachineGraph object.
	 * 
	 */
	private StateMachineGraph buildStateMachineGraph(Expression order) {
		StateMachineGraphBuilder stateMachineGraphBuilder = new StateMachineGraphBuilder(order);
		return stateMachineGraphBuilder.buildSMG();
	}

}
