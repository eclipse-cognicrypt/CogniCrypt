package crossing.e1.configurator.Analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import crypto.rules.CryptSLMethod;
import de.darmstadt.tu.crossing.cryptSL.Aggregate;
import de.darmstadt.tu.crossing.cryptSL.Event;
import de.darmstadt.tu.crossing.cryptSL.Method;
import de.darmstadt.tu.crossing.cryptSL.Object;
import de.darmstadt.tu.crossing.cryptSL.ObjectDecl;
import de.darmstadt.tu.crossing.cryptSL.Par;
import de.darmstadt.tu.crossing.cryptSL.ParList;
import de.darmstadt.tu.crossing.cryptSL.SuperType;

public class CryptSLReaderUtils {

	protected static List<CryptSLMethod> resolveAggregateToMethodeNames(Event leaf) {
		if (leaf instanceof Aggregate) {
			Aggregate ev = (Aggregate) leaf;
			return dealWithAggregate(ev);
		} else {
			ArrayList<CryptSLMethod> statements = new ArrayList<CryptSLMethod>();
			statements.add(stringifyMethodSignature(leaf));
			return statements;
		}
	}
	
	protected static List<CryptSLMethod> dealWithAggregate(Aggregate ev) {
		List<CryptSLMethod> statements = new ArrayList<CryptSLMethod>();
		
		for (Event lab : ev.getLab()) {
			if (lab instanceof Aggregate) {
				statements.addAll(dealWithAggregate((Aggregate) lab));
			} else {
				statements.add(stringifyMethodSignature(lab));
			}
		}
		return statements;
	}

	protected static  CryptSLMethod stringifyMethodSignature(Event lab) {
		Method method = ((SuperType) lab).getMeth();
		
		String qualifiedName = method.getMethName().getQualifiedName();
		if (qualifiedName == null) {
			qualifiedName = ((de.darmstadt.tu.crossing.cryptSL.impl.DomainmodelImpl) (method.eContainer().eContainer())).getJavaType().getQualifiedName();
		}
		qualifiedName = removeSPI(qualifiedName);
		List<Entry<String, String>> pars = new ArrayList<Entry<String, String>>();
		Object returnValue = method.getLeftSide();
		Entry<String, String> returnObject = null;
		if (returnValue != null && returnValue.getName() != null) {
			ObjectDecl v = ((ObjectDecl) returnValue.eContainer());
			returnObject = new SimpleEntry<String, String>(returnValue.getName(), v.getObjectType().getQualifiedName() + ((v.getArray() != null) ? v.getArray() : ""));
		} else {
			returnObject = new SimpleEntry<String, String>("_", "AnyType");
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
//		for (Entry<String, String> par : pars) {
//			boolean backwards = true;
//			for (CryptSLPredicate pred : predicates) {
//				if (par.getKey().equals(pred.getParameters().get(0))) {
//					backwards = false;
//					continue;
//				}
//			}
//			backw.add(backwards);
//		}
		return new CryptSLMethod(qualifiedName, pars, backw, returnObject);
	}
	
	protected static String removeSPI(String qualifiedName) {
		int spiIndex = qualifiedName.lastIndexOf("Spi");
		int dotIndex = qualifiedName.lastIndexOf(".");
		return (spiIndex == dotIndex - 3) ? qualifiedName.substring(0, spiIndex) + qualifiedName.substring(dotIndex) : qualifiedName;
	}
}
