package crossing.e1.configurator.wizard;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Aggegate;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;
import de.darmstadt.tu.crossing.cryptSL.Event;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.LabelMethodCall;
import de.darmstadt.tu.crossing.cryptSL.Method;
import de.darmstadt.tu.crossing.cryptSL.ObjectDecl;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.Par;
import de.darmstadt.tu.crossing.cryptSL.ParList;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;

public class CryptSLModelReader {

	private int nodeNameCounter = 0;
	
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
		classNames.add("MessageDigest");
		classNames.add("Cipher");
		
		for (String className : classNames) {
			EObject eObject = buildStateMachineGraph(resourceSet, className);
			String outputURI = storeModelToFile(resourceSet, eObject, className);
			loadModelFromFile(outputURI);
		}
		
	}

	private EObject buildStateMachineGraph(XtextResourceSet resourceSet, String className) {
		Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/" + className + ".cryptsl", true), true);
		EcoreUtil.resolveAll(resourceSet);
		EObject eObject = resource.getContents().get(0);
		
		Domainmodel dm = (Domainmodel) eObject;
		StateMachineGraph smg = new StateMachineGraph();
		smg.addNode(new StateNode("pre_init", true));
		nodeNameCounter = 0;
		iterateThroughSubtrees(smg, dm.getOrder(), null, null);
		iterateThroughSubtreesOptional(smg, dm.getOrder(), null, null);
		
		String filePath = "C:/Users/stefank3/Desktop/"+ className +".smg";
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(smg);
			out.close();
			fileOut.close();
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			smg = (StateMachineGraph) in.readObject();
			in.close();
			fileIn.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return eObject;
	}

	private void iterateThroughSubtreesOptional(StateMachineGraph smg, Expression order, StateNode prevNode, StateNode nextNode) {
		Expression left = order.getLeft();
		Expression right = order.getRight();
		String elementOp = order.getElementop();
		Boolean elOpNotNull = elementOp != null;
		String leftElOp = left.getElementop();
		String rightElOp = right.getElementop();
		
		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			iterateThroughSubtreesOptional(smg, right, null, nextNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			if(rightElOp != null && rightElOp.equals("?")) {
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
			
			if(rightElOp != null && rightElOp.equals("?")) {
				addSkipEdge(smg, right);
			}
		}
		
	}

	private void addSkipEdge(StateMachineGraph smg, Expression leaf) {
		List<TransitionEdge> tedges = new ArrayList<TransitionEdge>(smg.getEdges());
		for (TransitionEdge trans : tedges) {
			if (trans.getLabel().equals(leaf.getOrderEv().get(0).getName())) {
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
		Domainmodel dmro = (Domainmodel) xmiResourceRead.getContents().get(0);
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
		return nodes.get(nodes.size() -1);
	}

	private void handleOp(StateMachineGraph smg, String orderop, Expression leaf, StateNode prevNode, StateNode nextNode) {
		prevNode = (prevNode == null) ? getLastNode(smg) : prevNode; 
		if (nextNode == null) {
			nextNode = getNewNode();
			smg.addNode(nextNode);
		}
		
		StringBuilder labelBuilder = new StringBuilder();
		if (leaf.getOrderEv().get(0) instanceof Aggegate) {
			Aggegate ev = (Aggegate) leaf.getOrderEv().get(0);
			dealWithAggegate(labelBuilder, ev);
		} else {
			stringifyMethodSignature(labelBuilder, leaf.getOrderEv().get(0));
		}
		
		String label = labelBuilder.toString();
		smg.addEdge(new TransitionEdge(label, prevNode, nextNode));
		prevNode.setAccepting(false);
		if (leaf.getElementop() != null) {
			if (leaf.getElementop().equals("+")) {
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));
			} else if (leaf.getElementop().equals("*")){
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));				
				//handle extra edge in case of *
			} else if (leaf.getElementop().equals("?")) {
				//handle extra edge in case of ?
			}
		}
	}

	private void dealWithAggegate(StringBuilder labelBuilder, Aggegate ev) {
		for (Event lab : ev.getLab()) {
			if (lab instanceof Aggegate) {
				dealWithAggegate(labelBuilder, (Aggegate)lab);
			} else {
				stringifyMethodSignature(labelBuilder, lab);
			}
		}
		
	}

	private void stringifyMethodSignature(StringBuilder labelBuilder, Event lab) {
		Method method = ((LabelMethodCall) lab).getMeth();
		labelBuilder.append(method.getMethName().getQualifiedName());
		labelBuilder.append("(");
		ParList parList = method.getParList();
		if (parList != null) {
			for (Par par : parList.getParameters()) {
				String parValue = (par.getVal() == null) ? "_" : ((ObjectDecl)par.getVal().eContainer()).getObjectType().getIdentifier();
				labelBuilder.append(parValue);
				labelBuilder.append(",");
			}
		int length = labelBuilder.length();
		labelBuilder.replace(length - 1, length, "");
		}
		labelBuilder.append(");");
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter++), false, true);
	}

	private Expression getFirstMethod(Expression order) {
		Expression cur = (Expression) order;
		Expression prev = null;
		while (cur != null) {
			prev = cur;
			cur = cur.getLeft();	
		}
		return prev;
	}
}
