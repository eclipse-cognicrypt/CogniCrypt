package crossing.e1.configurator.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Constraint;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;

public class CryptSLModelReader {

	public CryptSLModelReader() throws ClassNotFoundException, CoreException {
		Injector injector = CryptSLActivator.getInstance().getInjector(CryptSLActivator.DE_DARMSTADT_TU_CROSSING_CRYPTSL);

		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		
		final IProject iproject = Utils.getIProjectFromSelection();
		if (iproject == null) {
			// if no project selected abort with error message
			Activator.getDefault().logError(null, Constants.NoFileandNoProjectOpened);
		}
		if (iproject.isOpen() && iproject.hasNature(Constants.JavaNatureID)) {
			resourceSet.setClasspathURIContext(this.getClass().getClassLoader());
		}
		new JdtTypeProviderFactory(injector.getInstance(IJavaProjectProvider.class)).createTypeProvider(resourceSet);
		
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/Mac.cryptsl", true), true);
		EcoreUtil.resolveAll(resourceSet);
		Domainmodel dm = (Domainmodel) resource.getContents().get(0);
		
		System.out.println(dm.getOrder());
		for (Constraint req : dm.getReq()) {
			System.out.println(req);
		}
	}
	
}
