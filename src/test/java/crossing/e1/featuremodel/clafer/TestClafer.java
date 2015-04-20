package crossing.e1.featuremodel.clafer;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.clafer.ast.AstModel;
import org.clafer.ast.AstUtil;
import org.clafer.collection.Triple;
import org.clafer.javascript.Javascript;
import org.clafer.javascript.JavascriptShell;
import org.clafer.scope.Scope;

import crossing.e1.featuremodel.clafer.ClaferModel;

import org.clafer.objective.Objective;

public class TestClafer {

	/**
     * <pre>
     * abstract Object
     *     Name ?
     * abstract Animal : Object
     *     Tail ?
     * abstract Primate : Animal
     *     Bipedal ?
     * Human : Primate
     * Beaver : Animal
     * Sarah : Primate
     * </pre>
     */
	public static void main(String[] args) {
		ClaferModel model = new ClaferModel();
		model.getClafersByType("c0_Task").forEach(p -> System.out.println(p));
		
		
	}

}
