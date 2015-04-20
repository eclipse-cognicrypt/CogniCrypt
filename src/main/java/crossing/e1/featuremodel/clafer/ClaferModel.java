package crossing.e1.featuremodel.clafer;

import static org.clafer.ast.Asts.newModel;

import java.util.List;
import java.util.stream.Collectors;

import org.clafer.ast.*;


public class ClaferModel {
	
	private String modelName;
	private AstModel model;
	
	public ClaferModel(){
		//hard-coded model for now
		//should have option to read from file later on
		model = newModel();

        AstAbstractClafer object = model.addAbstract("Object");
        object.addChild("Name").withCard(0, 1);

        AstAbstractClafer animal = model.addAbstract("Animal").extending(object);
        animal.addChild("Tail").withCard(0, 1);

        AstAbstractClafer primate = model.addAbstract("Primate").extending(animal);
        primate.addChild("Bipedal").withCard(0, 1);

        model.addChild("Human").withCard(1, 1).extending(primate);
        model.addChild("Beaver").withCard(1, 1).extending(animal);
        model.addChild("Sarah").withCard(1, 1).extending(primate);
	}
	
	public List<AstConcreteClafer> getClafersByType(String type){
		return model.getChildren().stream().filter(child -> child.getSuperClafer().getName() == type).collect(Collectors.toList());
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public AstModel getModel() {
		return model;
	}


}
