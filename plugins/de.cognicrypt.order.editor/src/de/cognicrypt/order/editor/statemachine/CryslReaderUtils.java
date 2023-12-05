package de.cognicrypt.order.editor.statemachine;

import java.util.AbstractMap.SimpleEntry;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.darmstadt.tu.crossing.crySL.Aggregate;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Method;
import de.darmstadt.tu.crossing.crySL.ObjectDecl;
import de.darmstadt.tu.crossing.crySL.Par;
import de.darmstadt.tu.crossing.crySL.ParList;
import de.darmstadt.tu.crossing.crySL.SuperType;

public class CryslReaderUtils {
	protected static Event resolveAggregateToMethodeNames(final Event leaf) {
		if (leaf instanceof Aggregate) {
			final Aggregate ev = (Aggregate) leaf;
			return ev;
		} else {
			final ArrayList<CrySLMethod> statements = new ArrayList<>();
			CrySLMethod stringifyMethodSignature = stringifyMethodSignature(leaf);
			if (stringifyMethodSignature != null) {
				statements.add(stringifyMethodSignature);
			}
			return leaf;
		}
	}
	
	protected static CrySLMethod stringifyMethodSignature(final Event lab) {
		if (!(lab instanceof SuperType)) {
			return null;
		}
		final Method method = ((SuperType) lab).getMeth();
		
		String methodName = method.getMethName().getSimpleName();
		if (methodName == null) {
			methodName = ((de.darmstadt.tu.crossing.crySL.Domainmodel) (method.eContainer().eContainer().eContainer())).getJavaType().getSimpleName();
		}
		final String qualifiedName =
				((de.darmstadt.tu.crossing.crySL.Domainmodel) (method.eContainer().eContainer().eContainer())).getJavaType().getQualifiedName() + "." + methodName; // method.getMethName().getQualifiedName();
		// qualifiedName = removeSPI(qualifiedName);
		final List<Entry<String, String>> pars = new ArrayList<>();
		final de.darmstadt.tu.crossing.crySL.Object returnValue = method.getLeftSide();
		Entry<String, String> returnObject = null;
		if (returnValue != null && returnValue.getName() != null) {
			final ObjectDecl v = ((ObjectDecl) returnValue.eContainer());
			returnObject = new SimpleEntry<>(returnValue.getName(), v.getObjectType().getQualifiedName() + ((v.getArray() != null) ? v.getArray() : ""));
		} else {
			returnObject = new SimpleEntry<>("_", "void");
		}
		final ParList parList = method.getParList();
		if (parList != null) {
			for (final Par par : parList.getParameters()) {
				String parValue = "_";
				if (par.getVal() != null && par.getVal().getName() != null) {
					final ObjectDecl objectDecl = (ObjectDecl) par.getVal().eContainer();
					parValue = par.getVal().getName();
					final String parType = objectDecl.getObjectType().getIdentifier() + ((objectDecl.getArray() != null) ? objectDecl.getArray() : "");
					pars.add(new SimpleEntry<>(parValue, parType));
				} else {
					pars.add(new SimpleEntry<>(parValue, "AnyType"));
				}
			}
		}
		return new CrySLMethod(qualifiedName, pars, new ArrayList<Boolean>(), returnObject);
	}
	
	public static File getResourceFromWithin(final String inputPath) {
		return new File(inputPath);
	}
}


