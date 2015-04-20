package crossing.e1.featuremodel.clafer;
import crossing.e1.featuremodel.clafer.ClaferModel;

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
		model.getClafersByType("Primate").forEach(p -> System.out.println(p));
	}

}
