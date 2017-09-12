package crossing.e1.primitive.questionnaire;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.beginer.question.QuestionsJSONReader;

public class test {
public static void main (String args[])
{
	Gson gson = new Gson();
	List<Question> questionList=new QuestionsJSONReader().getQuestions("src/main/resources/Primitives/CipherQuestion.json");
	for(Question p: questionList)
		System.out.println(p.getAnswers());
}
}
