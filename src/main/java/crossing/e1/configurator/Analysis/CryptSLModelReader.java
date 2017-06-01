package crossing.e1.configurator.Analysis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLArithmeticConstraint.ArithOp;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.StatementLabel;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Aggegate;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticExpression;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticOperator;
import de.darmstadt.tu.crossing.cryptSL.ComparisonExpression;
import de.darmstadt.tu.crossing.cryptSL.Constraint;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;
import de.darmstadt.tu.crossing.cryptSL.Event;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.ForbMethod;
import de.darmstadt.tu.crossing.cryptSL.LabelMethodCall;
import de.darmstadt.tu.crossing.cryptSL.Literal;
import de.darmstadt.tu.crossing.cryptSL.LiteralExpression;
import de.darmstadt.tu.crossing.cryptSL.Method;
import de.darmstadt.tu.crossing.cryptSL.Object;
import de.darmstadt.tu.crossing.cryptSL.ObjectDecl;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.Par;
import de.darmstadt.tu.crossing.cryptSL.ParList;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;
import de.darmstadt.tu.crossing.cryptSL.SuPar;
import de.darmstadt.tu.crossing.cryptSL.SuperType;
import de.darmstadt.tu.crossing.cryptSL.UnaryPreExpression;
import typestate.interfaces.ISLConstraint;

public class CryptSLModelReader {

	private int nodeNameCounter = 0;
	List<CryptSLPredicate> predicates = null;

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

		List<String> classNames = new ArrayList<String>();
		classNames.add("KeyGenerator");
		classNames.add("KeyPairGenerator");
//		classNames.add("KeyStore");
		classNames.add("Mac");
		classNames.add("PBEKeySpec");
		classNames.add("SecretKeyFactory");
//		classNames.add("MessageDigest");
		classNames.add("Cipher");

		for (String className : classNames) {
			Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/" + className + ".cryptsl", true), true);
			EcoreUtil.resolveAll(resourceSet);
			EObject eObject = resource.getContents().get(0);
			Domainmodel dm = (Domainmodel) eObject;
			List<String> forbiddenMethods = getForbiddenMethods(dm.getMethod());
			predicates = getPredicates(dm.getEns());
			StateMachineGraph smg = buildStateMachineGraph(dm.getOrder(), className);

			List<ISLConstraint> constraints = buildUpConstraints(dm.getReq());
			
			CryptSLRule rule = new CryptSLRule(className, forbiddenMethods, smg, constraints, predicates);
			storeRuletoFile(rule, className);
			//String outputURI = storeModelToFile(resourceSet, eObject, className);
			//loadModelFromFile(outputURI);
		}

	}

	private void storeRuletoFile(CryptSLRule rule, String className) {
		String filePath = "C:\\Users\\stefank3\\git\\CryptoAnalysis\\CryptoAnalysis\\src\\test\\resources\\" + className + ".cryptslbin";
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(rule);
			out.close();
			fileOut.close();
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			CryptSLRule inRule = (CryptSLRule) in.readObject();
			in.close();
			fileIn.close();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<CryptSLPredicate> getPredicates(List<Constraint> predList) {
		List<CryptSLPredicate> preds = new ArrayList<CryptSLPredicate>();
		for (Constraint pred : predList) {
			List<String> variables = new ArrayList<String>();
			if (pred.getParList() != null) {
				for (SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						variables.add(((LiteralExpression) var.getVal().getName()).getValue().getName());
					} else {
						variables.add("_");
					}
				}
			}
			preds.add(new CryptSLPredicate(pred.getPredName(), variables, false));
			
		}
		return preds;
	}

	private List<ISLConstraint> buildUpConstraints(List<Constraint> constraints) {
		List<ISLConstraint> slCons = new ArrayList<ISLConstraint>();
		for (Constraint cons : constraints) {
			slCons.add(getConstraint(cons));
		}
		return slCons;
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
					parList.add(a.getVal());
				}
			}
			slci = new CryptSLValueConstraint(((LiteralExpression) lit.getCons().getName()).getValue().getName(), parList);
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
			}  else {
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
			slci = new CryptSLComparisonConstraint(left, right,	op);
		} else if (cons instanceof UnaryPreExpression) {
			UnaryPreExpression un = (UnaryPreExpression) cons;
			List<String> vars = new ArrayList<String>();
			Constraint innerPredicate = un.getEnclosedExpression();
			if (innerPredicate.getParList() != null) {
				for (SuPar sup : innerPredicate.getParList().getParameters()) {
					if (sup.getVal() == null) {
						vars.add("_");
					} else {
						vars.add(((LiteralExpression) sup.getVal().getName()).getValue().getName());
					}
				}
			}
			slci = new CryptSLPredicate(innerPredicate.getPredName(), vars, true);
			System.out.println(un);
		} else if (cons instanceof Constraint) {
			if (cons.getPredName() != null && !cons.getPredName().isEmpty()) {
				List<String> vars = new ArrayList<String>();
				for (SuPar sup : cons.getParList().getParameters()) {
					if (sup.getVal() == null) {
						vars.add("_");
					} else {
						vars.add(((LiteralExpression) sup.getVal().getName()).getValue().getName());
					}
				}
				slci = new CryptSLPredicate(cons.getPredName(), vars, false);
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
		return value;
	}

	private List<String> getForbiddenMethods(EList<ForbMethod> methods) {
		List<String> methodSignatures = new ArrayList<String>();
		for (ForbMethod fm : methods) {
			methodSignatures.add(fm.getJavaMeth().getQualifiedName());
		}
		return methodSignatures;
	}

	private StateMachineGraph buildStateMachineGraph(Expression order, String className) {

		StateMachineGraph smg = new StateMachineGraph();
		smg.addNode(new StateNode("pre_init", true));
		nodeNameCounter = 0;
		iterateThroughSubtrees(smg, order, null, null);
		iterateThroughSubtreesOptional(smg, order, null, null);

		return smg;
	}

	private void iterateThroughSubtreesOptional(StateMachineGraph smg, Expression order, StateNode prevNode, StateNode nextNode) {
		Expression left = order.getLeft();
		Expression right = order.getRight();
		String leftElOp = left.getElementop();
		String rightElOp = right.getElementop();

		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			iterateThroughSubtreesOptional(smg, right, null, nextNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			if (rightElOp != null && rightElOp.equals("?")) {
				addSkipEdge(smg, right);
			}
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			if (leftElOp != null && leftElOp.equals("?")) {
				addSkipEdge(smg, left);
			}

			iterateThroughSubtreesOptional(smg, right, null, nextNode);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			if (leftElOp != null && leftElOp.equals("?")) {
				addSkipEdge(smg, left);
			}

			if (rightElOp != null && rightElOp.equals("?")) {
				addSkipEdge(smg, right);
			}
		}

	}

	private void addSkipEdge(StateMachineGraph smg, Expression leaf) {
		List<TransitionEdge> tedges = new ArrayList<TransitionEdge>(smg.getEdges());
		for (TransitionEdge trans : tedges) {
			if (trans.getLabel().equals(resolveAggegateToMethodeNames(leaf))) {
				for (TransitionEdge innerTrans : tedges) {
					if (innerTrans.from().equals(trans.to())) {
						smg.addEdge(new TransitionEdge(innerTrans.getLabel(), trans.from(), innerTrans.to()));
					}
				}
			}
		}
	}

	private void loadModelFromFile(String outputURI) {
		ResourceSet resSet = new ResourceSetImpl();
		Resource xmiResourceRead = resSet.getResource(URI.createURI(outputURI), true);
		xmiResourceRead.getContents().get(0);
//		Domainmodel dmro = 
	}

	private String storeModelToFile(XtextResourceSet resourceSet, EObject eObject, String className) throws IOException {
		//Store the model to path outputURI
		String outputURI = "file:///C:/Users/stefank3/Desktop/" + className + ".xmi";
		Resource xmiResource = resourceSet.createResource(URI.createURI(outputURI));
		xmiResource.getContents().add(eObject);
		xmiResource.save(null);
		return outputURI;
	}

	private void iterateThroughSubtrees(StateMachineGraph smg, Expression order, StateNode prevNode, StateNode nextNode) {
		//if order.getLeft == null && order.getRight == null => no nesting whatsoever todo
		Expression left = order.getLeft();
		Expression right = order.getRight();
		String elementOp = order.getElementop();
		Boolean elOpNotNull = elementOp != null;

		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtrees(smg, left, null, nextNode);
			prevNode = getLastNode(smg);

			iterateThroughSubtrees(smg, right, prevNode, nextNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtrees(smg, left, prevNode, nextNode);
			handleOp(smg, order.getOrderop(), right, prevNode, nextNode);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			if (order.getOrderop().equals("|")) {
				prevNode = getLastNode(smg);
			}
			handleOp(smg, order.getOrderop(), left, prevNode, nextNode);
			if (order.getOrderop().equals("|")) {
				nextNode = getLastNode(smg);
			}

			if (elOpNotNull && elementOp.equals("+")) {
				StateNode linkBackNode = prevNode;

				iterateThroughSubtrees(smg, right, prevNode, nextNode);

				List<TransitionEdge> transEdges = new ArrayList<TransitionEdge>(smg.getEdges());
				for (TransitionEdge trans : transEdges) {
					if (trans.to().equals(nextNode)) {
						if (trans.from().equals(linkBackNode)) {
							smg.addEdge(new TransitionEdge(trans.getLabel(), trans.to(), trans.to()));
						} else {
							for (TransitionEdge innerTrans : transEdges) {
								if (innerTrans.to().equals(trans.from())) {
									smg.addEdge(new TransitionEdge(innerTrans.getLabel(), trans.to(), trans.from()));
								}
							}
						}
					}
				}
			} else {
				iterateThroughSubtrees(smg, right, prevNode, nextNode);
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			if (order.getOrderop().equals("|")) {
				prevNode = getLastNode(smg);
			}
			handleOp(smg, order.getOrderop(), left, prevNode, null);
			if (order.getOrderop().equals("|")) {
				nextNode = getLastNode(smg);
				handleOp(smg, order.getOrderop(), right, prevNode, nextNode);
			} else {
				handleOp(smg, order.getOrderop(), right, null, nextNode);
			}
		}
	}

	private StateNode getLastNode(StateMachineGraph smg) {
		List<StateNode> nodes = smg.getNodes();
		return nodes.get(nodes.size() - 1);
	}

	private void handleOp(StateMachineGraph smg, String orderop, Expression leaf, StateNode prevNode, StateNode nextNode) {
		prevNode = (prevNode == null) ? getLastNode(smg) : prevNode;
		if (nextNode == null) {
			nextNode = getNewNode();
			smg.addNode(nextNode);
		}

		List<StatementLabel> label = resolveAggegateToMethodeNames(leaf);
		smg.addEdge(new TransitionEdge(label, prevNode, nextNode));
		prevNode.setAccepting(false);
		if (leaf.getElementop() != null) {
			if (leaf.getElementop().equals("+")) {
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));
			} else if (leaf.getElementop().equals("*")) {
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));
				//handle extra edge in case of *
			} else if (leaf.getElementop().equals("?")) {
//				handle extra edge in case of ?
			}
		}
	}

	private List<StatementLabel> resolveAggegateToMethodeNames(Expression leaf) {
		if (leaf.getOrderEv().get(0) instanceof Aggegate) {
			Aggegate ev = (Aggegate) leaf.getOrderEv().get(0);
			return dealWithAggegate(ev);
		} else {
			ArrayList<StatementLabel> statements = new ArrayList<StatementLabel>();
			statements.add(stringifyMethodSignature(leaf.getOrderEv().get(0)));
			return statements;
		}
	}

	private List<StatementLabel> dealWithAggegate(Aggegate ev) {
		List<StatementLabel> statements = new ArrayList<StatementLabel>();
		for (Event lab : ev.getLab()) {
			if (lab instanceof Aggegate) {
				statements.addAll(dealWithAggegate((Aggegate) lab));
			} else {
				statements.add(stringifyMethodSignature(lab));
			}
		}
		return statements;
	}

	private StatementLabel stringifyMethodSignature(Event lab) {
		Method method = ((LabelMethodCall) lab).getMeth();
		String qualifiedName = method.getMethName().getQualifiedName();
		if (qualifiedName == null) {
			qualifiedName = ((de.darmstadt.tu.crossing.cryptSL.impl.DomainmodelImpl) (method.eContainer().eContainer())).getJavaType().getQualifiedName();
		}
		List<Entry<String, String>> pars = new ArrayList<Entry<String, String>>();
		Object returnValue = method.getLeftSide();
		if (returnValue != null && returnValue.getName() != null) {
			ObjectDecl v = ((ObjectDecl) returnValue.eContainer());
			pars.add(new SimpleEntry<String, String>(returnValue.getName(), v.getObjectType().getQualifiedName() + ((v.getArray() != null) ? v.getArray() : "")));
		} else {
			pars.add(new SimpleEntry<String, String>("_", "AnyType"));
		}
		ParList parList = method.getParList();
		if (parList != null) {
			for (Par par : parList.getParameters()) {
				String parValue = "_";
				if (par.getVal() != null && par.getVal().getName() != null) {
					ObjectDecl objectDecl = (ObjectDecl) par.getVal().eContainer();
					parValue = par.getVal().getName();
					String parType = objectDecl.getObjectType().getIdentifier() + ((objectDecl.getArray() != null) ? objectDecl.getArray() : "");
					pars.add(new SimpleEntry<String, String>(parValue, parType));
					
				} else {
					pars.add(new SimpleEntry<String, String>(parValue, "AnyType"));
				}
			}
		}
		List<Boolean> backw = new ArrayList<Boolean>(); 
		for (CryptSLPredicate pred : predicates) {
			for (Entry<String, String> par : pars) {
				if (par.getKey().equals(pred.getParameters().get(0))) {
					backw.add(false);
					continue;
				}
				backw.add(true);
			}
		}
		return new StatementLabel(qualifiedName, pars, backw);
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter++), false, true);
	}

//	private Expression getFirstMethod(Expression order) {
//		Expression cur = (Expression) order;
//		Expression prev = null;
//		while (cur != null) {
//			prev = cur;
//			cur = cur.getLeft();
//		}
//		return prev;
//	}
}
