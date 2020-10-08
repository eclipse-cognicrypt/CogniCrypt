package de.cognicrypt.codegenerator.crysl.templates.TestCryslTaskInt;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class TestCryslTask {

	public static int sampleTemplateMethod(){
		int calories = 900;
		int amountOfCoffee = 10;

		CrySLCodeGenerator.getInstance().
		includeClass("de.coffee.api.Sugar").addParameter(calories, "calories").
		includeClass("de.test.api.Coffee").addParameter(amountOfCoffee,"amountOfCoffee").generate();
		int returnVal = 10;
		return returnVal;
	}

}

