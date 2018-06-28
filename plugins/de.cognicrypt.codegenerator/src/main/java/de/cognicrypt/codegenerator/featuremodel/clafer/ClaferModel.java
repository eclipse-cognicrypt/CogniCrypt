package de.cognicrypt.codegenerator.featuremodel.clafer;

import java.io.File;
import java.io.IOException;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstModel;
import org.clafer.javascript.Javascript;
import org.clafer.javascript.JavascriptFile;
import org.clafer.scope.Scope;

import de.cognicrypt.codegenerator.Activator;

/**
 * This class handles the clafer model on the file-system level.
 * 
 * @author Ram Kamath
 * @author Stefan Krueger
 */
public class ClaferModel {

	private String modelName;
	private JavascriptFile jsFile;

	/**
	 * Constructor for claferModel
	 *
	 * @param path
	 *        absolute path to Javascript representation of the model
	 */
	public ClaferModel(final File path) {
		loadModel(path);
	}

	public ClaferModel(final String path) {
		loadModel(new File(path));
	}

	/**
	 * Getter method for AST representation of the clafer model
	 *
	 * @return AST model
	 */
	public AstModel getModel() {
		return this.jsFile.getModel();
	}

	/**
	 *
	 * @return
	 */
	public String getModelName() {
		return this.modelName;
	}

	/**
	 * Getter method for clafer-model's scope
	 *
	 * @return Scope of clafer model
	 */
	public Scope getScope() {
		return this.jsFile.getScope();
	}

	private void loadModel(final File file) {
		try {
			this.jsFile = Javascript.readModel(file, Javascript.newEngine());

			setModelName("Cryptography Task Configurator");
			final AstModel astModel = getModel();
			setEnumList(astModel);

		} catch (final IOException e) {
			Activator.getDefault().logError(e);
		}
	}

	private void setEnumList(final AstModel model) {
		PropertiesMapperUtil.resetEnumMap();
		for (final AstAbstractClafer abstractClafer : model.getAbstracts()) {
			if (abstractClafer.getName().contains("Enum")) {
				for (final AstClafer clafer : abstractClafer.getSubs()) {
					//construct a map of tasks, key is a clafer description, value is actual clafer. Key is used in Wizard as an input for taskList combobox
					PropertiesMapperUtil.getenumMap().put((AstAbstractClafer) clafer, ((AstAbstractClafer) clafer).getSubs());
				}
			}
		}
	}

	/**
	 * Setter method for the model's name
	 *
	 * @param modelName
	 *        New name of the model
	 */
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

}
