/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.reader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.common.types.JvmExecutable;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.access.impl.ClasspathTypeProvider;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Injector;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLArithmeticConstraint.ArithOp;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLComparisonConstraint.CompOp;
import crypto.rules.CrySLCondPredicate;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLConstraint.LogOps;
import crypto.rules.CrySLForbiddenMethod;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;
import crypto.rules.ParEqualsPredicate;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.Activator;
import de.cognicrypt.utils.Utils;
import de.darmstadt.tu.crossing.CrySLStandaloneSetup;
import de.darmstadt.tu.crossing.CrySL.ui.internal.CrySLActivator;
import de.darmstadt.tu.crossing.constraints.CrySLArithmeticOperator;
import de.darmstadt.tu.crossing.constraints.CrySLComparisonOperator;
import de.darmstadt.tu.crossing.constraints.CrySLLogicalOperator;
import de.darmstadt.tu.crossing.crySL.ArithmeticExpression;
import de.darmstadt.tu.crossing.crySL.ArithmeticOperator;
import de.darmstadt.tu.crossing.crySL.ArrayElements;
import de.darmstadt.tu.crossing.crySL.ComparingOperator;
import de.darmstadt.tu.crossing.crySL.ComparisonExpression;
import de.darmstadt.tu.crossing.crySL.Constraint;
import de.darmstadt.tu.crossing.crySL.DestroysBlock;
import de.darmstadt.tu.crossing.crySL.Domainmodel;
import de.darmstadt.tu.crossing.crySL.EnsuresBlock;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Expression;
import de.darmstadt.tu.crossing.crySL.ForbMethod;
import de.darmstadt.tu.crossing.crySL.ForbiddenBlock;
import de.darmstadt.tu.crossing.crySL.Literal;
import de.darmstadt.tu.crossing.crySL.LiteralExpression;
import de.darmstadt.tu.crossing.crySL.LogicalImply;
import de.darmstadt.tu.crossing.crySL.LogicalOperator;
import de.darmstadt.tu.crossing.crySL.Object;
import de.darmstadt.tu.crossing.crySL.ObjectDecl;
import de.darmstadt.tu.crossing.crySL.Order;
import de.darmstadt.tu.crossing.crySL.PreDefinedPredicates;
import de.darmstadt.tu.crossing.crySL.Pred;
import de.darmstadt.tu.crossing.crySL.PredLit;
import de.darmstadt.tu.crossing.crySL.ReqPred;
import de.darmstadt.tu.crossing.crySL.SimpleOrder;
import de.darmstadt.tu.crossing.crySL.SuPar;
import de.darmstadt.tu.crossing.crySL.SuParList;
import de.darmstadt.tu.crossing.crySL.SuperType;
import de.darmstadt.tu.crossing.crySL.UnaryPreExpression;
import de.darmstadt.tu.crossing.crySL.UseBlock;
import de.darmstadt.tu.crossing.crySL.impl.DomainmodelImpl;
import de.darmstadt.tu.crossing.crySL.impl.ObjectImpl;
import com.google.common.base.CharMatcher;

public class CrySLParser {

	private List<CrySLForbiddenMethod> forbiddenMethods = null;
	private StateMachineGraph smg = null;
	private XtextResourceSet resourceSet;

	private static final String INT = "int";
	private static final String THIS = "this";
	private static final String ANY_TYPE = "AnyType";
	private static final String NULL = "null";
	private static final String UNDERSCORE = "_";

	public CrySLParser(IProject iProject) throws CoreException, IOException {
		final Injector injector = CrySLActivator.getInstance().getInjector(CrySLActivator.DE_DARMSTADT_TU_CROSSING_CRYSL);
		resourceSet = injector.getInstance(XtextResourceSet.class);

		if (iProject == null) {
			// if no project selected abort with error message
			iProject = Utils.complileListOfJavaProjectsInWorkspace().get(0);
		}
		if (iProject.isOpen()) {
			resourceSet.setClasspathURIContext(JavaCore.create(iProject));
		}
		new JdtTypeProviderFactory(injector.getInstance(IJavaProjectProvider.class)).createTypeProvider(resourceSet);

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
	}

	/**
	 * This constructor use the CogniCyrpt Core plugin lib folder as classpath
	 */
	public CrySLParser() {
		CrySLStandaloneSetup crySLStandaloneSetup = new CrySLStandaloneSetup();
		final Injector injector = crySLStandaloneSetup.createInjectorAndDoEMFRegistration();
		this.resourceSet = injector.getInstance(XtextResourceSet.class);
		List<File> jars = new ArrayList<>();
		String[] l = System.getProperty("java.class.path").split(";");

		URL[] classpath = new URL[l.length];
		for (int i = 0; i < classpath.length; i++) {
			try {
				classpath[i] = new File(l[i]).toURI().toURL();
			}
			catch (MalformedURLException e) {
				Activator.getDefault().logError(e, "File path: " + jars.get(i) + " could not converted to java.net.URI object");
			}
		}

		URLClassLoader ucl = new URLClassLoader(classpath);
		this.resourceSet.setClasspathURIContext(new URLClassLoader(classpath));
		new ClasspathTypeProvider(ucl, this.resourceSet, null, null);
		this.resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
	}

	public CrySLRule readRule(File ruleFile) {
		final String fileName = ruleFile.getName();
		final String extension = fileName.substring(fileName.lastIndexOf("."));
		if (!Constants.cryslFileEnding.equals(extension)) {
			return null;
		}
		final Resource resource = resourceSet.getResource(URI.createFileURI(ruleFile.getAbsolutePath()), true);// URI.createPlatformResourceURI(ruleFile.getFullPath().toPortableString(),
																																																						// true), true);
		EcoreUtil.resolveAll(resourceSet);
		final EObject eObject = resource.getContents().get(0);
		final Domainmodel dm = (Domainmodel) eObject;
		String curClass = dm.getJavaType().getQualifiedName();
		final EnsuresBlock ensure = dm.getEnsure();
		final Map<ParEqualsPredicate, SuperType> pre_preds = Maps.newHashMap();
		final DestroysBlock destroys = dm.getDestroy();

		Expression order = dm.getOrder();
		try {
			if (order instanceof Order) {
				validateOrder((Order) order);
			}
		}
		catch (ClassCastException ex) {
			Activator.getDefault().logError(ex.getMessage() + " in rule " + curClass + ".");
			return null;
		}

		if (destroys != null) {
			pre_preds.putAll(getKills(destroys.getPred()));
		}
		if (ensure != null) {
			pre_preds.putAll(getPredicates(ensure.getPred()));
		}

		this.smg = buildStateMachineGraph(order);
		final ForbiddenBlock forbEvent = dm.getForbEvent();
		this.forbiddenMethods = (forbEvent != null) ? getForbiddenMethods(forbEvent.getForb_methods()) : Lists.newArrayList();

		final List<ISLConstraint> constraints = (dm.getReqConstraints() != null) ? buildUpConstraints(dm.getReqConstraints().getReq()) : Lists.newArrayList();
		constraints.addAll(((dm.getRequire() != null) ? collectRequiredPredicates(dm.getRequire().getPred()) : Lists.newArrayList()));
		final List<Entry<String, String>> objects = getObjects(dm.getUsage());

		final List<CrySLPredicate> actPreds = Lists.newArrayList();

		for (final ParEqualsPredicate pred : pre_preds.keySet()) {
			final SuperType cond = pre_preds.get(pred);
			if (cond == null) {
				actPreds.add(pred.tobasicPredicate());
			} else {
				actPreds.add(new CrySLCondPredicate(pred.getBaseObject(), pred.getPredName(), pred.getParameters(), pred.isNegated(),
						getStatesForMethods(CrySLParserUtils.resolveAggregateToMethodeNames(cond))));
			}
		}
		final CrySLRule rule = new CrySLRule(curClass, objects, this.forbiddenMethods, this.smg, constraints, actPreds);

		return rule;
	}

	private void validateOrder(Order order) {
		List<String> collected = new ArrayList<String>();
		collected.addAll(collectLabelsFromExpression(order.getLeft()));
		collected.addAll(collectLabelsFromExpression(order.getRight()));
	}

	private List<String> collectLabelsFromExpression(Expression exp) {
		List<String> collected = new ArrayList<String>();
		if (exp instanceof Order || exp instanceof SimpleOrder) {
			collected.addAll(collectLabelsFromExpression(exp.getLeft()));
			collected.addAll(collectLabelsFromExpression(exp.getRight()));
		} else {
			for (Event ev : exp.getOrderEv()) {
				if (ev instanceof SuperType) {
					if (ev instanceof de.darmstadt.tu.crossing.crySL.Aggregate) {
						for (Event lab : ((de.darmstadt.tu.crossing.crySL.Aggregate) ev).getLab()) {
							if (lab instanceof SuperType) {
								collected.add(((SuperType) lab).getName());
							} else {
								throw new ClassCastException("Parser error in the line after definition of label " + collected.get(collected.size() - 1));
							}
						}
					} else {
						collected.add(((SuperType) ev).getName());
					}
				}
			}
		}
		return collected;
	}

	public List<CrySLRule> readRulesOutside(String resourcesPath) throws CoreException {
		List<CrySLRule> rules = new ArrayList<CrySLRule>();
		for (File a : ((new File(resourcesPath)).listFiles())) {
			if (!a.isDirectory() && a.exists() && a.canRead()) {
				CrySLRule rule = readRule(a);
				if (rule != null) {
					rules.add(rule);
				}
			}
		}

		return rules;
	}

	public List<CrySLRule> readRulesWithin(String resourcesPath) throws CoreException {
		final IPath rulesFolder = (new org.eclipse.core.runtime.Path(resourcesPath));
		List<CrySLRule> rules = new ArrayList<CrySLRule>();
		if (rulesFolder.segmentCount() == 1) {
			return rules;
		}
		for (final IResource res : ResourcesPlugin.getWorkspace().getRoot().getFolder(rulesFolder).members()) {
			if (Constants.cryslFileEnding.equals("." + res.getFileExtension())) {
				File resAsFile = ((IFile) res).getRawLocation().makeAbsolute().toFile();
				CrySLRule rule = readRule(resAsFile);
				if (rule != null) {
					rules.add(rule);
					File to = new File(Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR, de.cognicrypt.core.Activator.PLUGIN_ID).getAbsolutePath()
							+ Constants.innerFileSeparator + rule.getClassName().substring(rule.getClassName().lastIndexOf(".") + 1) + Constants.cryslFileEnding);
					try {
						Files.copy(resAsFile, to);
					}
					catch (IOException e) {
						Activator.getDefault().logError(e, Constants.ERROR_MESSAGE_NO_FILE);
					}
				}
			} else if (res instanceof IFolder) {
				rules.addAll(readRulesWithin(res.getFullPath().toOSString()));
			}
		}
		return rules;
	}

	private StateMachineGraph buildStateMachineGraph(final Expression order) {
		final StateMachineGraphBuilder smgb = new StateMachineGraphBuilder(order);
		return smgb.buildSMG();
	}

	private List<ISLConstraint> buildUpConstraints(final List<Constraint> constraints) {
		final List<ISLConstraint> slCons = new ArrayList<>();
		for (final Constraint cons : constraints) {
			final ISLConstraint constraint = getConstraint(cons);
			if (constraint != null) {
				slCons.add(constraint);
			}
		}
		return slCons;
	}

	private List<ISLConstraint> collectRequiredPredicates(final EList<ReqPred> requiredPreds) {
		final List<ISLConstraint> preds = new ArrayList<>();
		for (final ReqPred pred : requiredPreds) {
			ISLConstraint reqPred = null;
			if (pred instanceof PredLit) {
				reqPred = extractReqPred(pred);
			} else {
				final ReqPred left = pred.getLeftExpression();
				final ReqPred right = pred.getRightExpression();

				List<CrySLPredicate> altPreds = retrieveReqPredFromAltPreds(left);
				altPreds.add(extractReqPred(right));
				reqPred = new CrySLConstraint(altPreds.get(0), altPreds.get(1), LogOps.or);
				for (int i = 2; i < altPreds.size(); i++) {
					reqPred = new CrySLConstraint(reqPred, altPreds.get(i), LogOps.or);
				}
			}
			preds.add(reqPred);
		}

		return preds;
	}

	private List<CrySLPredicate> retrieveReqPredFromAltPreds(ReqPred left) {
		List<CrySLPredicate> preds = new ArrayList<CrySLPredicate>();
		if (left instanceof PredLit) {
			preds.add(extractReqPred(left));
		} else {
			preds.addAll(retrieveReqPredFromAltPreds(left.getLeftExpression()));
			preds.add(extractReqPred(left.getRightExpression()));
		}
		return preds;
	}

	private CrySLPredicate extractReqPred(final ReqPred pred) {
		final List<ICrySLPredicateParameter> variables = new ArrayList<>();
		PredLit innerPred = (PredLit) pred;
		EObject cons = innerPred.getCons();
		ISLConstraint conditional = null;
		if (cons instanceof Constraint) {
			conditional = getConstraint((Constraint) cons);
		} else if (cons instanceof Pred) {
			conditional = getPredicate((Pred) cons);
		}
		if (innerPred.getPred().getParList() != null) {
			for (final SuPar var : innerPred.getPred().getParList().getParameters()) {
				if (var.getVal() != null) {
					final LiteralExpression lit = var.getVal();

					final ObjectImpl object = (ObjectImpl) ((LiteralExpression) lit.getLit().getName()).getValue();
					final String type = ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName();
					final String variable = object.getName();

					final String part = var.getVal().getPart();
					if (part != null) {
						variables.add(new CrySLObject(variable, type, new CrySLSplitter(Integer.parseInt(lit.getInd()), Utils.filterQuotes(lit.getSplit()))));
					} else {
						variables.add(new CrySLObject(variable, type));
					}
				} else {
					variables.add(new CrySLObject(UNDERSCORE, NULL));
				}
			}
		}
		return new CrySLPredicate(null, innerPred.getPred().getPredName(), variables, (innerPred.getNot() != null ? true : false), conditional);
	}

	private ISLConstraint getPredicate(Pred pred) {
		final List<ICrySLPredicateParameter> variables = new ArrayList<>();
		if (pred.getParList() != null) {
			for (final SuPar var : pred.getParList().getParameters()) {
				if (var.getVal() != null) {
					final LiteralExpression lit = var.getVal();
					final ObjectImpl object = (ObjectImpl) ((LiteralExpression) lit.getLit().getName()).getValue();
					final String type = ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName();
					final String variable = object.getName();
					final String part = var.getVal().getPart();
					if (part != null) {
						variables.add(new CrySLObject(variable, type, new CrySLSplitter(Integer.parseInt(lit.getInd()), filterQuotes(lit.getSplit()))));
					} else {
						final String consPred = var.getVal().getConsPred();
						int ind;
						if (consPred != null) {
							if (consPred.equals("alg(")) {
								ind = 0;
								variables.add(new CrySLObject(variable, type, new CrySLSplitter(ind, filterQuotes("/"))));
							} else if (consPred.equals("mode(")) {
								ind = 1;
								variables.add(new CrySLObject(variable, type, new CrySLSplitter(ind, filterQuotes("/"))));
							} else if (consPred.equals("pad(")) {
								ind = 2;
								variables.add(new CrySLObject(variable, type, new CrySLSplitter(ind, filterQuotes("/"))));
							}
						} else {
							variables.add(new CrySLObject(variable, type));
						}
					}
				} else {
					variables.add(new CrySLObject(UNDERSCORE, NULL));
				}
			}
		}
		return new CrySLPredicate(null, pred.getPredName(), variables, (((PredLit)pred.eContainer()).getNot() != null ? true : false), null);
	}

	private CrySLArithmeticConstraint convertLiteralToArithmetic(final Constraint expression) {
		final LiteralExpression cons = (LiteralExpression) ((LiteralExpression) expression).getCons();
		ICrySLPredicateParameter name;
		if (cons instanceof PreDefinedPredicates) {
			name = getPredefinedPredicate((LiteralExpression) expression);
		} else {
			final EObject constraint = cons.getName();
			final String object = getValueOfLiteral(constraint);
			if (constraint instanceof LiteralExpression) {
				name = new CrySLObject(object, ((ObjectDecl) ((ObjectImpl) ((LiteralExpression) constraint).getValue()).eContainer()).getObjectType().getQualifiedName());
			} else {
				name = new CrySLObject(object, INT);
			}
		}

		return new CrySLArithmeticConstraint(name, new CrySLObject("0", INT), crypto.rules.CrySLArithmeticConstraint.ArithOp.p);
	}

	private ISLConstraint getConstraint(final Constraint cons) {
		if (cons == null) {
			return null;
		}
		ISLConstraint slci = null;

		if (cons instanceof ArithmeticExpression) {
			final ArithmeticExpression ae = (ArithmeticExpression) cons;
			String op = new CrySLArithmeticOperator((ArithmeticOperator) ae.getOperator()).toString();
			ArithOp operator = ArithOp.n;
			if ("+".equals(op)) {
				operator = ArithOp.p;
			}
			ObjectDecl leftObj =
					(ObjectDecl) ((ObjectImpl) ((LiteralExpression) ((LiteralExpression) ((LiteralExpression) ae.getLeftExpression()).getCons()).getName()).getValue()).eContainer();
			CrySLObject leftSide = new CrySLObject(leftObj.getObjectName().getName(), leftObj.getObjectType().getQualifiedName());

			ObjectDecl rightObj =
					(ObjectDecl) ((ObjectImpl) ((LiteralExpression) ((LiteralExpression) ((LiteralExpression) ae.getRightExpression()).getCons()).getName()).getValue()).eContainer();
			CrySLObject rightSide = new CrySLObject(rightObj.getObjectName().getName(), rightObj.getObjectType().getQualifiedName());

			slci = new CrySLArithmeticConstraint(leftSide, rightSide, operator);
		} else if (cons instanceof LiteralExpression) {
			final LiteralExpression lit = (LiteralExpression) cons;
			final List<String> parList = new ArrayList<>();
			if (lit.getLitsleft() != null) {
				for (final Literal a : lit.getLitsleft().getParameters()) {
					parList.add(Utils.filterQuotes(a.getVal()));
				}
			}
			if (lit.getCons() instanceof PreDefinedPredicates) {
				slci = getPredefinedPredicate(lit);
			} else {
				final String part = ((ArrayElements) lit.getCons()).getCons().getPart();
				if (part != null) {
					final LiteralExpression name = (LiteralExpression) ((ArrayElements) lit.getCons()).getCons().getLit().getName();
					final SuperType object = name.getValue();
					final CrySLObject variable = new CrySLObject(object.getName(), ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName(),
							new CrySLSplitter(Integer.parseInt(((ArrayElements) lit.getCons()).getCons().getInd()), Utils.filterQuotes(((ArrayElements) lit.getCons()).getCons().getSplit())));
					slci = new CrySLValueConstraint(variable, parList);
				} else {
					final String consPred = ((ArrayElements) lit.getCons()).getCons().getConsPred();
					if (consPred != null) {
						final LiteralExpression name = (LiteralExpression) ((ArrayElements) lit.getCons()).getCons().getLit().getName();
						final SuperType object = name.getValue();
						int ind;
						if (consPred.equals("alg(")) {
							ind = 0;
							final CrySLObject variable =
									new CrySLObject(object.getName(), ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName(), new CrySLSplitter(ind, Utils.filterQuotes("/")));
							slci = new CrySLValueConstraint(variable, parList);
						} else if (consPred.equals("mode(")) {
							ind = 1;
							final CrySLObject variable =
									new CrySLObject(object.getName(), ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName(), new CrySLSplitter(ind, Utils.filterQuotes("/")));
							slci = new CrySLValueConstraint(variable, parList);
						} else if (consPred.equals("pad(")) {
							ind = 2;
							final CrySLObject variable =
									new CrySLObject(object.getName(), ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName(), new CrySLSplitter(ind, Utils.filterQuotes("/")));
							slci = new CrySLValueConstraint(variable, parList);
						}
					} else {
						LiteralExpression name = (LiteralExpression) ((ArrayElements) lit.getCons()).getCons().getName();
						if (name == null) {
							name = (LiteralExpression) ((ArrayElements) lit.getCons()).getCons().getLit().getName();
						}
						final SuperType object = name.getValue();
						final CrySLObject variable = new CrySLObject(object.getName(), ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName());
						slci = new CrySLValueConstraint(variable, parList);
					}
				}
			}
		} else if (cons instanceof ComparisonExpression) {
			final ComparisonExpression comp = (ComparisonExpression) cons;
			CompOp op = null;
			switch ((new CrySLComparisonOperator((ComparingOperator) comp.getOperator())).toString()) {
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
				case "!=":
					op = CompOp.neq;
					break;
				default:
					op = CompOp.eq;
			}
			CrySLArithmeticConstraint left;
			CrySLArithmeticConstraint right;

			final Constraint leftExpression = comp.getLeftExpression();
			if (leftExpression instanceof LiteralExpression) {
				left = convertLiteralToArithmetic(leftExpression);
			} else {
				left = (CrySLArithmeticConstraint) leftExpression;
			}

			final Constraint rightExpression = comp.getRightExpression();
			if (rightExpression instanceof LiteralExpression) {
				right = convertLiteralToArithmetic(rightExpression);
			} else {
				final ArithmeticExpression ar = (ArithmeticExpression) rightExpression;
				final String leftValue = getValueOfLiteral(ar.getLeftExpression());
				final String rightValue = getValueOfLiteral(ar.getRightExpression());

				final CrySLArithmeticOperator aop = new CrySLArithmeticOperator((ArithmeticOperator) ar.getOperator());
				ArithOp operator = null;
				if (aop.getPLUS() != null && !aop.getPLUS().isEmpty()) {
					operator = ArithOp.p;
				} else {
					operator = ArithOp.n;
				}

				right = new CrySLArithmeticConstraint(
						new CrySLObject(leftValue,
								((ObjectDecl) ((LiteralExpression) ((LiteralExpression) ((LiteralExpression) ar.getLeftExpression()).getCons()).getName()).getValue().eContainer()).getObjectType()
										.getQualifiedName()),
						new CrySLObject(rightValue,
								((ObjectDecl) ((LiteralExpression) ((LiteralExpression) ((LiteralExpression) ar.getRightExpression()).getCons()).getName()).getValue().eContainer()).getObjectType()
										.getQualifiedName()),
						operator);
			}
			slci = new CrySLComparisonConstraint(left, right, op);
		} else if (cons instanceof UnaryPreExpression) {
			final UnaryPreExpression un = (UnaryPreExpression) cons;
			final List<ICrySLPredicateParameter> vars = new ArrayList<>();
			final Pred innerPredicate = (Pred) un.getEnclosedExpression();
			if (innerPredicate.getParList() != null) {
				innerPredicate.getParList().getParameters().forEach(e -> vars.add(new CrySLObject(UNDERSCORE, NULL)));
			}
			slci = new CrySLPredicate(null, innerPredicate.getPredName(), vars, true);
		} else if (cons instanceof Pred) {
			if (((Pred) cons).getPredName() != null && !((Pred) cons).getPredName().isEmpty()) {
				final List<ICrySLPredicateParameter> vars = new ArrayList<>();

				final SuParList parList = ((Pred) cons).getParList();
				if (parList != null) {
					parList.getParameters().forEach(e -> vars.add(new CrySLObject(UNDERSCORE, NULL)));
				}
				slci = new CrySLPredicate(null, ((Pred) cons).getPredName(), vars, false);
			}
		} else if (cons instanceof Constraint) {
			LogOps op = null;
			final EObject operator = cons.getOperator();
			if (operator instanceof LogicalImply) {
				op = LogOps.implies;
			} else {
				switch ((new CrySLLogicalOperator((LogicalOperator) operator)).toString()) {
					case "&&":
						op = LogOps.and;
						break;
					case "||":
						op = LogOps.or;
						break;
					default:
						Activator.getDefault().logError("Sign " + operator.toString() + " was not properly translated.");
						op = LogOps.and;
				}
			}
			slci = new CrySLConstraint(getConstraint(cons.getLeftExpression()), getConstraint(cons.getRightExpression()), op);

		}

		return slci;
	}

	private List<CrySLForbiddenMethod> getForbiddenMethods(final EList<ForbMethod> methods) {
		final List<CrySLForbiddenMethod> methodSignatures = new ArrayList<>();
		for (final ForbMethod fm : methods) {
			final JvmExecutable meth = fm.getJavaMeth();
			final List<Entry<String, String>> pars = new ArrayList<>();
			for (final JvmFormalParameter par : meth.getParameters()) {
				pars.add(new SimpleEntry<>(par.getSimpleName(), par.getParameterType().getSimpleName()));
			}
			final List<CrySLMethod> crysl = new ArrayList<>();

			final Event alternative = fm.getRep();
			if (alternative != null) {
				crysl.addAll(CrySLParserUtils.resolveAggregateToMethodeNames(alternative));
			}
			methodSignatures.add(new CrySLForbiddenMethod(
					new CrySLMethod(meth.getDeclaringType().getIdentifier() + "." + meth.getSimpleName(), pars, null, new SimpleEntry<>(UNDERSCORE, ANY_TYPE)), false, crysl));
		}
		return methodSignatures;
	}

	private Map<? extends ParEqualsPredicate, ? extends SuperType> getKills(final EList<Constraint> eList) {
		final Map<ParEqualsPredicate, SuperType> preds = new HashMap<>();
		for (final Constraint cons : eList) {
			final Pred pred = (Pred) cons.getPredLit().getPred();

			final List<ICrySLPredicateParameter> variables = new ArrayList<>();

			if (pred.getParList() != null) {
				for (final SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						final ObjectImpl object = (ObjectImpl) ((LiteralExpression) var.getVal().getLit().getName()).getValue();
						String name = object.getName();
						String type = ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName();
						if (name == null) {
							name = THIS;
							type = "";// this.curClass;
						}
						variables.add(new CrySLObject(name, type));
					} else {
						variables.add(new CrySLObject(UNDERSCORE, NULL));
					}
				}
			}
			final String meth = pred.getPredName();
			final SuperType cond = cons.getLabelCond();
			if (cond == null) {
				preds.put(new ParEqualsPredicate(null, meth, variables, true), null);
			} else {
				preds.put(new ParEqualsPredicate(null, meth, variables, true), cond);
			}

		}
		return preds;
	}

	private List<Entry<String, String>> getObjects(final UseBlock usage) {
		final List<Entry<String, String>> objects = new ArrayList<>();

		for (final ObjectDecl obj : usage.getObjects()) {
			objects.add(new SimpleEntry<>(obj.getObjectType().getIdentifier(), obj.getObjectName().getName()));
		}

		return objects;
	}

	private ISLConstraint getPredefinedPredicate(final LiteralExpression lit) {
		final String pred = ((PreDefinedPredicates) lit.getCons()).getPredName();
		ISLConstraint slci = null;
		switch (pred) {
			case "callTo":
				final List<ICrySLPredicateParameter> methodsToBeCalled = new ArrayList<>();
				methodsToBeCalled.addAll(CrySLParserUtils.resolveAggregateToMethodeNames(((PreDefinedPredicates) lit.getCons()).getObj().get(0)));
				slci = new CrySLPredicate(null, pred, methodsToBeCalled, false);
				break;
			case "noCallTo":
				final List<ICrySLPredicateParameter> methodsNotToBeCalled = new ArrayList<>();
				final List<CrySLMethod> resolvedMethodNames = CrySLParserUtils.resolveAggregateToMethodeNames(((PreDefinedPredicates) lit.getCons()).getObj().get(0));
				for (final CrySLMethod csm : resolvedMethodNames) {
					this.forbiddenMethods.add(new CrySLForbiddenMethod(csm, true));
					methodsNotToBeCalled.add(csm);
				}
				slci = new CrySLPredicate(null, pred, methodsNotToBeCalled, false);
				break;
			case "neverTypeOf":
				final List<ICrySLPredicateParameter> varNType = new ArrayList<>();
				final Object object = (de.darmstadt.tu.crossing.crySL.Object) ((PreDefinedPredicates) lit.getCons()).getObj().get(0);
				final String type = ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName();
				varNType.add(new CrySLObject(object.getName(), type));
				final String qualifiedName = ((PreDefinedPredicates) lit.getCons()).getType().getType().getQualifiedName();
				varNType.add(new CrySLObject(qualifiedName, NULL));
				slci = new CrySLPredicate(null, pred, varNType, false);
				break;
			case "length":
				final List<ICrySLPredicateParameter> variables = new ArrayList<>();
				final Object objectL = (de.darmstadt.tu.crossing.crySL.Object) ((PreDefinedPredicates) lit.getCons()).getObj().get(0);
				final String typeL = ((ObjectDecl) objectL.eContainer()).getObjectType().getQualifiedName();
				variables.add(new CrySLObject(objectL.getName(), typeL));
				slci = new CrySLPredicate(null, pred, variables, false);
				break;
			case "notHardCoded":
				final List<ICrySLPredicateParameter> variables1 = new ArrayList<>();
				final Object objectL1 = (de.darmstadt.tu.crossing.crySL.Object) ((PreDefinedPredicates) lit.getCons()).getObj().get(0);
				final String typeL1 = ((ObjectDecl) objectL1.eContainer()).getObjectType().getQualifiedName();
				variables1.add(new CrySLObject(objectL1.getName(), typeL1));
				slci = new CrySLPredicate(null, pred, variables1, false);
				break;
			case "instanceOf":
				final List<ICrySLPredicateParameter> varInstOf = new ArrayList<>();
				final Object objInstOf = (de.darmstadt.tu.crossing.crySL.Object) ((PreDefinedPredicates) lit.getCons()).getObj().get(0);
				final String instOfType = ((ObjectDecl) objInstOf.eContainer()).getObjectType().getQualifiedName();
				varInstOf.add(new CrySLObject(objInstOf.getName(), instOfType));
				final String typeName = ((PreDefinedPredicates) lit.getCons()).getType().getType().getQualifiedName();
				varInstOf.add(new CrySLObject(typeName, NULL));
				slci = new CrySLPredicate(null, pred, varInstOf, false);
				break;
			default:
				new RuntimeException();
		}
		return slci;
	}

	private Map<? extends ParEqualsPredicate, ? extends SuperType> getPredicates(final List<Constraint> predList) {
		final Map<ParEqualsPredicate, SuperType> preds = new HashMap<>();
		for (final Constraint cons : predList) {
			final Pred pred = (Pred) cons.getPredLit().getPred();
			String curClass = ((DomainmodelImpl) cons.eContainer().eContainer()).getJavaType().getQualifiedName();
			final List<ICrySLPredicateParameter> variables = new ArrayList<>();

			if (pred.getParList() != null) {
				boolean firstPar = true;
				for (final SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						final ObjectImpl object = (ObjectImpl) ((LiteralExpression) var.getVal().getLit().getName()).getValue();
						String type = ((ObjectDecl) object.eContainer()).getObjectType().getQualifiedName();
						String name = object.getName();
						if (name == null) {
							name = THIS;
							type = curClass;
						}
						variables.add(new CrySLObject(name, type));
					} else {
						if (firstPar) {
							variables.add(new CrySLObject(THIS, curClass));
						} else {
							variables.add(new CrySLObject(UNDERSCORE, NULL));
						}
					}
					firstPar = false;
				}
			}

			final CrySLPredicate ensPredCons = extractReqPred(cons.getPredLit());
			final String meth = pred.getPredName();
			final SuperType cond = cons.getLabelCond();
			if (cond == null) {
				preds.put(new ParEqualsPredicate(null, meth, variables, false, ensPredCons.getConstraint()), null);
			} else {
				preds.put(new ParEqualsPredicate(null, meth, variables, false, ensPredCons.getConstraint()), cond);
			}

		}
		return preds;
	}

	private Set<StateNode> getStatesForMethods(final List<CrySLMethod> condMethods) {
		final Set<StateNode> predGens = new HashSet<>();
		if (condMethods.size() != 0) {
			for (final TransitionEdge methTrans : this.smg.getAllTransitions()) {
				final List<CrySLMethod> transLabel = methTrans.getLabel();
				if (transLabel.size() > 0 && (transLabel.equals(condMethods) || (condMethods.size() == 1 && transLabel.contains(condMethods.get(0))))) {
					predGens.add(methTrans.getRight());
				}
			}
		}
		return predGens;
	}

	private String getValueOfLiteral(final EObject name) {
		String value = "";
		if (name instanceof LiteralExpression) {
			final SuperType preValue = ((LiteralExpression) name).getValue();
			if (preValue != null) {
				value = preValue.getName();
			} else {
				final EObject cons = ((LiteralExpression) name).getCons();
				if (cons instanceof LiteralExpression) {
					value = getValueOfLiteral(((LiteralExpression) cons).getName());
				} else {
					value = "";
				}
			}
		} else {
			value = ((Literal) name).getVal();
		}
		return Utils.filterQuotes(value);
	}

	private static String filterQuotes(final String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}
}
