/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
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
package crossing.e1.configurator.wizard.beginner;

import java.util.List;

import crossing.e1.configurator.beginer.question.Page;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;
import crossing.e1.configurator.tasks.Task;

public class BeginnerModeQuestionnaire {

	private final List<Question> questionList;
	private final List<Page> pageList;
	private Task task;
	private int ID;
	private int pageID;

	public BeginnerModeQuestionnaire(final Task task, final String filePath) {
		this.task = task;
		this.questionList = (new QuestionsJSONReader()).getQuestions(filePath);		
		this.ID = 0;
		
		this.pageList = null;
		
	}
	/**
	 * 
	 * @param task
	 * @param filePath
	 * @param mode Call to this constructor if questions are being grouped into pages.
	 */
	public BeginnerModeQuestionnaire(final Task task, final String filePath, final String mode){
		this.task = task;
		this.pageList = (new QuestionsJSONReader()).getPages(filePath);
		this.pageID = 0;
		
		this.questionList = null;
	}

	public int getCurrentID() {
		return this.ID;
	}

	public Question getQuestionByID(final int ID) {
		return this.questionList.get(ID);
	}

	public List<Page> getQutionare() throws NullPointerException {
		return this.pageList;
	}

	public List<Question> getQuestionList() {
		return this.questionList;
	}
	
	public Task getTask() {
		return this.task;
	}

	public boolean hasMoreQuestions() {
		return this.ID < getQutionare().size();
	}

	public boolean isFirstQuestion() {
		return this.ID == 0;
	}

	public Question nextQuestion() {
		return this.questionList.get(this.ID++);
	}

	public Question previousQuestion() {
		return this.questionList.get(--this.ID);
	}

	public Question setQuestionByID(final int ID) {
		this.ID = ID;
		return this.questionList.get(this.ID);
	}

	public void setTask(final Task task) {
		this.task = task;
	}
	/**
	 * 
	 * @param pageID
	 * @return Return the page at pageID.
	 */
	public Page getPageByID(final int pageID) {
		return this.pageList.get(pageID);
	}
	
	/**
	 * 
	 * @return Return the list of pages.
	 * @throws NullPointerException
	 */
	public List<Page> getPages() throws NullPointerException {
		return this.pageList;
	}
	
	/**
	 * 
	 * @return Return the next page.
	 */
	public Page nextPage() {
		return this.pageList.get(this.pageID++);
	}

	/**
	 * 
	 * @return Return the previous page.
	 */
	public Page previousPage() {
		return this.pageList.get(--this.pageID);
	}

	/**
	 * 
	 * @param pageID
	 * @return Return the page that has been set.
	 */
	public Page setPageByID(final int pageID) {
		this.pageID = pageID;
		return this.pageList.get(this.pageID);
	}

	/**
	 * 
	 * @return Whether this is the first page.
	 */
	public boolean isFirstPage() {
		return this.pageID == 0;
	}
	
	/**
	 * 
	 * @return Return whether there are more pages.
	 */
	public boolean hasMorePages() {
		return this.pageID < getPages().size();
	}
	
	/**
	 * 
	 * @return Return the current pageID.
	 */
	public int getCurrentPageID() {
		return this.pageID;
	}

}
