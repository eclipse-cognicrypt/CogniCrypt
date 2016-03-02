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

package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import crossing.e1.configurator.utilities.Labels;

/**
 * @author Ram
 *
 */
public class Question implements Labels {
	private ArrayList<Answer> answers = null;
	private String def;
	private String display;
	private String refCalfer;
	private boolean isGroup = false;

	/**
	 * Each question will have multiple answers and associated display Value Question itself will have reference clafer
	 * and a display value.
	 *
	 * @param que
	 */
	Question(final Element que) {
		NodeList answer = null;
		if (que.hasChildNodes()) {
			Element answersList = null;
			if (que.getElementsByTagName(Labels.ANSWER_LIST).getLength() > 0) {
				answersList = (Element) que.getElementsByTagName(Labels.ANSWER_LIST).item(0);
			}
			if (answersList.hasChildNodes()) {
				answer = answersList.getElementsByTagName(Labels.ANSWER);
				for (int ans = 0; ans < answer.getLength(); ans++) {
					getAnswers().add(new Answer((Element) answer.item(ans)));
				}
			}
		}
		setDef(que.getAttribute(Labels.DEF));
		setDisplay(que.getAttribute(Labels.DISPLAY));
		setRefCalfer(que.getAttribute(Labels.REF_CLAFER));
		setGroup(Boolean.parseBoolean(que.getAttribute(Labels.IS_GROUP)));
	}

	/**
	 * @return the answers
	 */
	public ArrayList<Answer> getAnswers() {
		if (this.answers == null) {
			this.answers = new ArrayList<Answer>();
		}
		return this.answers;
	}

	/**
	 * @return the def
	 */
	public String getDef() {
		return this.def;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return this.display;
	}

	/**
	 * @return the refCalfer
	 */
	public String getRefCalfer() {
		return this.refCalfer;
	}

	/**
	 * @return the isGroup
	 */
	public boolean isGroup() {
		return this.isGroup;
	}

	/**
	 * @param answers
	 *            the answers to set
	 */
	public void setAnswers(final ArrayList<Answer> answers) {
		this.answers = answers;
	}

	/**
	 * @param def
	 *            the def to set
	 */
	public void setDef(final String def) {
		this.def = def;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(final String display) {
		this.display = display;
	}

	/**
	 * @param isGroup
	 *            the isGroup to set
	 */
	public void setGroup(final boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @param refCalfer
	 *            the refCalfer to set
	 */
	public void setRefCalfer(final String refCalfer) {
		this.refCalfer = refCalfer;
	}

}
