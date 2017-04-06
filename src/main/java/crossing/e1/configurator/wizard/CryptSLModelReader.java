package crossing.e1.configurator.wizard;

import java.io.IOException;
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
import crossing.e1.cryptsl.analysis.StateMachineGraph;
import crossing.e1.cryptsl.analysis.StateNode;
import crossing.e1.cryptsl.analysis.TransitionEdge;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Constraint;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.Order;
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
		Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/Mac.cryptsl", true), true);
		EcoreUtil.resolveAll(resourceSet);
		EObject eObject = resource.getContents().get(0);
		
		Domainmodel dm = (Domainmodel) eObject;
		StateMachineGraph smg = new StateMachineGraph();
		smg.addNode(new StateNode("pre_init", true));
		iterateThroughSubtrees(smg, dm.getOrder());
		
		System.out.println(dm.getOrder());
		for (Constraint req : dm.getReq()) {
			System.out.println(req);
		}
		
		//Store the model to path outputURI
		String outputURI = "file:///C:/Users/stefank3/Desktop/Output.xmi";
		Resource xmiResource = resourceSet.createResource(URI.createURI(outputURI));
		xmiResource.getContents().add(eObject);
		xmiResource.save(null);

		//Load the model from path outputURI
		ResourceSet resSet = new ResourceSetImpl();
		Resource xmiResourceRead = resSet.getResource(URI.createURI(outputURI), true);
		Domainmodel dmro = (Domainmodel) xmiResourceRead.getContents().get(0);
		
	}
	
	private void iterateThroughSubtrees(StateMachineGraph smg, Expression order) {
		//if order.getLeft == null && order.getRight == null => no nesting whatsoever todo
		int skipCounter = 0;
		Expression left = order.getLeft();
		Expression right = order.getRight();
		
		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtrees(smg, left);
			iterateThroughSubtrees(smg, right);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtrees(smg, left);
			handleOp(smg, order.getOrderop(), right);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			handleOp(smg, order.getOrderop(), left);
			iterateThroughSubtrees(smg, right);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			handleOp(smg, order.getOrderop(), left);
			if (skipCounter == 0) {
				handleOp(smg, order.getOrderop(), right);
			}
		}
//		if (left instanceof Order || left instanceof SimpleOrder) {
//			int prevSize = smg.getNodes().size() ;
//			iterateThroughSubtrees(smg, left);
//		} else {
//			handleOp(smg, order.getOrderop(), left);
//		}
//		
//		if (right instanceof Order || right instanceof SimpleOrder) {
//			int prevSize = smg.getNodes().size() ;
//			iterateThroughSubtrees(smg, right);
//			
//		} else {
//			handleOp(smg, order.getOrderop(), right);
//		}
	}

	private int handleOp(StateMachineGraph smg, String orderop, Expression leaf) {
		int skipCounter = 0;
		StateNode newNode = getNewNode();
		List<StateNode> nodes = smg.getNodes();
		int curLength = nodes.size();
		smg.addNode(newNode);
		
		if (orderop.equals(",") && !leaf.equals("?")) {
			smg.addEdge(new TransitionEdge(leaf.getOrderEv().get(0).getName(), nodes.get(curLength -1), newNode));
			if (leaf.getOrderop() != null) {
				if (leaf.getOrderop().equals("+")) {
					smg.addEdge(new TransitionEdge(leaf.getOrderEv().get(0).getName(), newNode, newNode));
				} else if (leaf.getOrderop().equals("*")){
					smg.addEdge(new TransitionEdge(leaf.getOrderEv().get(0).getName(), newNode, newNode));				
					//handle extra edge in case of *
				}
			}
		} else if (orderop.equals(",")) {
			
		} else if (orderop.equals("|")) {
			
			
			skipCounter++;
		}
		
		return skipCounter;
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter++));
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

	private void iterateThrough(StateMachineGraph smg, Expression first) {
		/*
		 * 
		 * 1. Get Left most child - check 
		 * 2. Add Init nodes - check 
		 * 3. Add init edge(s) 
		 * 		depends on orderop of node
		 * 		, -> only edge btwn pre_init and init node
		 * 		+ -> add another edge from pre_init node to itself
		 * 		* -> , plus + plus get (leftest child of) right child of parent and add edge from pre_init node to init node (do 
		 * 4. Iterate through remaining aggs
		 * 		For each agg
		 * 		if (skipCounter > 0) { skip;}
		 * 		else {
		 * 			if (!OrderImpl || SimpleOrder) {
		 * 				5.
		 * 			else {
		 * 				
		 * 			}
		 * 
		 * 		}
		 */
		
		StateNode pre_initNode = new StateNode("pre_init", true);
		StateNode initNode = new StateNode("init", false);

		int nodeLabelIterator = 0;
		smg.addNode(pre_initNode);
		smg.addNode(initNode);
		smg.addEdge(new TransitionEdge(first.getOrderEv().toString(), pre_initNode, initNode));
		
		
		
		
//		EObject container = first.eContainer();
//		if (container instanceof Expression) {
//			Expression parent = (Expression) container;
//			String operator = parent.getOrderop();
//			StateNode nodeAddedLast = smg.getNodes().get(smg.getNodes().size() -1);
//			if (operator.equals(",")) {
//
//				StateNode newNode = new StateNode(String.valueOf(nodeLabelIterator++));
//				smg.addNode(newNode);
//				smg.addEdge(new Edge(right))
//			}
//		}
		
//		if (oi.getLeft() != null) {
//			iterateThrough(smg, (ExpressionImpl)oi.getLeft());
//		}
//		
//		
//		
//		if (oi.getRight() != null) {
//			iterateThrough(smg, (ExpressionImpl)oi.getRight());
//		}
		
		
		
		
	}
	
	/* both expressions ->
	* , -> addNode(right), addedge(agg, left, right)
	* | -> addNode(right), addedge(agg, edge_BeforeLeft, right)
	*
	*
	*
	*
	*/
	
	
	
	

}
