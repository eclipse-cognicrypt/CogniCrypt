package de.cognicrypt.staticanalyzer.sootbridge;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CryptoScanner;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.Utils;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

/**
 * This runner triggers Soot.
 *
 * @author Johannes Spaeth
 * @author Eric Bodden
 */
public class SootRunner {

	private static final File RULES_DIR = Utils.getResourceFromWithin("/resources/CrySLRules/");
	private static CG DEFAULT_CALL_GRAPH = CG.CHA;
	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	private static SceneTransformer createAnalysisTransformer(final CrySLAnalysisListener reporter) {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(final String phaseName, final Map<String, String> options) {
				final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);

				final CryptoScanner scanner = new CryptoScanner(getRules()) {

					@Override
					public JimpleBasedInterproceduralCFG icfg() {
						return icfg;
					}

					@Override
					public boolean isCommandLineMode() {
						return true;
					}

				};
				scanner.getAnalysisListener().addReportListener(reporter);
				scanner.scan();
			}
		};
	}

	private static List<CryptSLRule> getRules() {
		final List<CryptSLRule> rules = Lists.newArrayList();
		final File[] listFiles = SootRunner.RULES_DIR.listFiles();
		assert listFiles != null;
		for (final File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	private static List<String> projectClassPath(final IJavaProject javaProject) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final List<String> urls = new ArrayList<>();
			final URI uriString = workspace.getRoot().getFile(javaProject.getOutputLocation()).getLocationURI();
			urls.add(new File(uriString).getAbsolutePath());
			return urls;
		} catch (final Exception e) {
			Activator.getDefault().logError(e, "Error building project classpath");
			return Lists.newArrayList();
		}
	}


	public static boolean runSoot(final IJavaProject project, final CrySLAnalysisListener reporter) {
		G.reset();
		setSootOptions(project);
		registerTransformers(reporter);
		try {
			runSoot();
		} catch (final Exception t) {
			Activator.getDefault().logError(t);
			return false;
		}
		return true;
	}

	private static void runSoot() {
		Scene.v().loadNecessaryClasses();
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}

	private static void setSootOptions(final IJavaProject project) {
		Options.v().set_soot_classpath(getSootClasspath(project));
		Options.v().set_process_dir(Lists.newArrayList(projectClassPath(project)));

		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
		Options.v().set_no_bodies_for_excluded(true);
		switch(DEFAULT_CALL_GRAPH){
			case SPARK:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg", "all-reachable:true,library:any-subtype");
				break;
			case CHA:
			default:
				Options.v().setPhaseOption("cg.cha", "on");
				Options.v().setPhaseOption("cg", "all-reachable:true");
		}
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().set_output_format(Options.output_format_none);
	}
	

	private static void registerTransformers(CrySLAnalysisListener reporter) {
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", createAnalysisTransformer(reporter)));
	}
	
	private static String getSootClasspath(final IJavaProject javaProject) {
		return Joiner.on(File.pathSeparator).join(projectClassPath(javaProject));
	}

}
