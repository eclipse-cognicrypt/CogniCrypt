package de.cognicrypt.codegenerator.crysl.templates.DifferentInt;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class Test{

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

