/**
 * Copyright 2015 Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Ram Kamath
 *
 */
package crossing.e1.configurator.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.CodeDependency;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

/**
 * @author Ram
 *
 */
public class XMLParser implements Labels {
	
	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 * @throws DocumentException 
	 */
	public String displayInstanceValues(final InstanceClafer inst, HashMap<Question, Answer> constraints, String value) throws DocumentException {
		// ToDo: check if there is any use where "value" is NOT "", if so remove "value" from list of parameters
		Document document = DocumentHelper.createDocument();
		Element taskElem = document.addElement( "task" );
		if (inst.hasChildren()) {
			final String taskName = inst.getType().getName();
			taskElem.addAttribute("description", ClaferModelUtils.removeScopePrefix(taskName));
			taskElem.addElement("Package").addText(Constants.PackageName);	// Constants.xmlPackage
			Element xmlimports = taskElem.addElement("Imports");
			for(String file: Constants.xmlimportsarr){
				xmlimports.addElement("Import").addText(file);
			}
		}
		if (inst != null && inst.hasChildren()) {
			for (final InstanceClafer in : inst.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()) {
					Element algoElem = taskElem.addElement(Constants.ALGORITHM).addAttribute("type", ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName()));
					displayInstanceXML(in, algoElem);
				} else {
					displayInstanceXML(in, taskElem);
				}
			}
		}
		Element codeElem = taskElem.addElement("code");
		for (Entry<Question, Answer> ent : constraints.entrySet()) {
			ArrayList<CodeDependency> cdp = ent.getValue().getCodeDependencies();
			if (cdp != null) {
				for (CodeDependency dep : cdp) {
					codeElem.addElement(dep.getOption()).addText(dep.getValue()+"");
				}
			}
		}

		return document.asXML();
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	private void displayInstanceXML(final InstanceClafer inst, Element parent) {
		String value;
		try {
			if (inst.hasChildren()) {
				for (final InstanceClafer in : inst.getChildren()) {
					if (isAlgorithm(in.getType())) {
						Element algoElem = parent.addElement(Constants.ALGORITHM);
						algoElem.addAttribute("type", ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName()));
						displayInstanceXML(in, algoElem);
					} else {
						displayInstanceXML(in, parent);
					}
				}
			} else if (inst.hasRef() && inst.getType().isPrimitive() != true && inst.getRef().getClass().toString().contains(Constants.INTEGER) == false && inst.getRef().getClass()
				.toString().contains(Constants.STRING) == false && inst.getRef().getClass().toString().contains(Constants.BOOLEAN) == false) {
				displayInstanceXML((InstanceClafer) inst.getRef(), parent);
			} else if (PropertiesMapperUtil.getenumMap().keySet().contains(inst.getType().getSuperClafer())) {
				String superClaferName = ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName());
				superClaferName = Character.toLowerCase(superClaferName.charAt(0)) + superClaferName.substring(1);
				// Todo : This if and it's else do exactly the same thing, It will be commented out in refactoring
//				if (inst.hasRef()) {
					// For group properties
					parent.addElement(superClaferName).addText(ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", ""));
//				} else {
					//enums that don't have a reference type (e.g., Mode, Padding etc)
//					parent.addElement(superClaferName).addText(ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", ""));
//				}
			} else {
				String instName = ClaferModelUtils.removeScopePrefix(inst.getType().getName());
				instName = Character.toLowerCase(instName.charAt(0)) + instName.substring(1);
				if (inst.hasRef()) {
					parent.addElement(instName).addText(inst.getRef().toString().replace("\"", ""));
				} else {
					String instparentName = ClaferModelUtils.removeScopePrefix(((AstConcreteClafer) inst.getType()).getParent().getName());
					instparentName = Character.toLowerCase(instparentName.charAt(0)) + instparentName.substring(1);
					parent.addElement(instparentName).addText(instName);
				}
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String getInstanceProperties(final InstanceClafer inst, String value) {
		InstanceClafer instan = null;
		if (inst.hasChildren()) {
			instan = (InstanceClafer) inst.getChildren()[0].getRef();
		}
		if (instan != null && instan.hasChildren()) {
			for (final InstanceClafer in : instan.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()) {
					value += Constants.ALGORITHM + " :" + ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName()) + Constants.lineSeparator;
					value += getInstancePropertiesDetails(in, "");
					value += Constants.lineSeparator;
				} else {
					value += getInstancePropertiesDetails(in, "");
				}
			}
		}
		return value;
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String getInstancePropertiesDetails(final InstanceClafer inst, String value) {
		try {
			// if (inst.getType().hasRef()) {
			// if (getSuperClaferName(inst.getType().getRef().getTargetType()))
			// {
			//
			// }
			// }
			if (inst.hasChildren()) {
				for (final InstanceClafer in : inst.getChildren()) {
					value += getInstancePropertiesDetails(in, "");
				}
			} else if (inst.hasRef() && inst.getType().isPrimitive() != true && inst.getRef().getClass().toString().contains(Constants.INTEGER) == false && inst.getRef().getClass()
				.toString().contains(Constants.STRING) == false && inst.getRef().getClass().toString().contains(Constants.BOOLEAN) == false) {
				value += getInstancePropertiesDetails((InstanceClafer) inst.getRef(), "");
			} else if (PropertiesMapperUtil.getenumMap().keySet().contains(inst.getType().getSuperClafer())) {
				if (inst.hasRef()) {
					// For group properties
					return "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ":" + ClaferModelUtils
						.removeScopePrefix(inst.getType().toString()).replace("\"", "") + Constants.lineSeparator;
				} else {
					//enums that don't have a reference type (e.g., Mode, Padding etc)
					return "\t" + ClaferModelUtils.removeScopePrefix(((AstConcreteClafer) inst.getType()).getSuperClafer().getName()) + " : " + ClaferModelUtils
						.removeScopePrefix(inst.getType().getName()) + Constants.lineSeparator;
				}
			} else {
				if (inst.hasRef()) {
					return "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + " : " + inst.getRef().toString().replace("\"", "") + Constants.lineSeparator;
				} else {
					return "\t" + ClaferModelUtils.removeScopePrefix(((AstConcreteClafer) inst.getType()).getParent().getName()) + " : " + ClaferModelUtils
						.removeScopePrefix(inst.getType().getName()) + Constants.lineSeparator;
				}
			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		return value;
	}

	/**
	 *
	 * @param astClafer
	 * @return
	 */
	private boolean isAlgorithm(final AstClafer astClafer) {
		if (astClafer.hasRef())
			if (astClafer.getRef().getTargetType() != null && astClafer.getRef().getTargetType().getSuperClafer() != null)
				return astClafer.getRef().getTargetType().getSuperClafer().getName().contains("_Algorithm");

		return false;

	}
}
