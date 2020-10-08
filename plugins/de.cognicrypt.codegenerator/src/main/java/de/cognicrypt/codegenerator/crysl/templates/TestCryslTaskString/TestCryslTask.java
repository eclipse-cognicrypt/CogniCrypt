package de.cognicrypt.codegenerator.crysl.templates.TestCryslTaskString;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class TestCryslTask {

	public static java.lang.String sampleTemplateMethod(){
		int calories = 900;
		int amountOfCoffee = 10;

		CrySLCodeGenerator.getInstance().
		includeClass("de.coffee.api.Sugar").addParameter(calories, "calories").
		includeClass("de.test.api.Coffee").addParameter(amountOfCoffee,"amountOfCoffee").generate();
		java.lang.String returnVal = "return";
		return returnVal;
	}

}