/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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
	Document document;
	
	/**
	 * Writes xml document to file
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void writeClaferInstanceToFile(String path) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter( new FileWriter(path), format );
		writer.write( document );
        writer.close();
	}
	
	/**
	 * builds xml document, returns it's string representation
	 *
	 * @param inst
	 * @param constraints
	 * @return
	 * @throws DocumentException 
	 */
	public String displayInstanceValues(final InstanceClafer inst, final HashMap<Question, Answer> constraints) throws DocumentException {
		document = DocumentHelper.createDocument();
		Element taskElem = document.addElement( Constants.Task );
		if (inst.hasChildren()) {
			final String taskName = inst.getType().getName();
			taskElem.addAttribute(Constants.Description, ClaferModelUtils.removeScopePrefix(taskName));
			taskElem.addElement(Constants.Package).addText(Constants.PackageName);	// Constants.xmlPackage
			Element xmlimports = taskElem.addElement(Constants.Imports);
			for(String file: Constants.xmlimportsarr){
				xmlimports.addElement(Constants.Import).addText(file);
			}
		}
		if (inst != null && inst.hasChildren()) {
			for (final InstanceClafer in : inst.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()) {
					Element algoElem = taskElem.addElement(Constants.ALGORITHM).addAttribute(Constants.Type, ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName()));
					displayInstanceXML(in, algoElem);
				} else {
					displayInstanceXML(in, taskElem);
				}
			}
		}
		Element codeElem = taskElem.addElement(Constants.Code);
		for (final Entry<Question, Answer> ent : constraints.entrySet()) {
			final ArrayList<CodeDependency> cdp = ent.getValue().getCodeDependencies();
			if (cdp != null) {
				for (final CodeDependency dep : cdp) {
					codeElem.addElement(dep.getOption()).addText(dep.getValue()+"");
				}
			}
		}

		return document.asXML();
	}

	/**
	 * Adds xml of inst to parent element
	 * 
	 * @param inst
	 * @param parent
	 * @return
	 */
	private void displayInstanceXML(final InstanceClafer inst, Element parent) {
		try {
			if (inst.hasChildren()) {
				for (final InstanceClafer in : inst.getChildren()) {
					if (isAlgorithm(in.getType())) {
						Element algoElem = parent.addElement(Constants.ALGORITHM);
						algoElem.addAttribute(Constants.Type, ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName()));
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
				// Removed if-else block after refactorng, while the following statement in both blocks was identical
				parent.addElement(superClaferName).addText(ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", ""));
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
	 * @param astClafer
	 * @return
	 */
	private boolean isAlgorithm(final AstClafer astClafer) {
		if (astClafer.hasRef()) {
			if (astClafer.getRef().getTargetType() != null && astClafer.getRef().getTargetType().getSuperClafer() != null) {
				return astClafer.getRef().getTargetType().getSuperClafer().getName().contains("_Algorithm");
			}
		}

		return false;

	}
}
