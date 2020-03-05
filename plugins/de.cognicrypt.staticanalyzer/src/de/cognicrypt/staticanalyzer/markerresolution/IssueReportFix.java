package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.Severities;
import de.cognicrypt.staticanalyzer.utilities.QuickFixUtils;
import de.cognicrypt.staticanalyzer.utilities.Reporter;

public class IssueReportFix implements IMarkerResolution {

	private final String label;
	private IMarker marker;

	public IssueReportFix(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Reads and decodes the CROSSINGExtServices GitHub personal access token
	 * @return
	 */
	private String readToken() {
		Scanner scanner = new Scanner(IssueReportFix.class.getResourceAsStream(Constants.innerFileSeparator+"Token.txt"));
		String base64Token = new String(Base64.getDecoder().decode(scanner.nextLine()));
		return base64Token;
	}

	@Override
	public void run(IMarker arg0) {
		marker = arg0;
		
		Reporter dialog = new Reporter(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), marker.getResource().getName());
		dialog.create();
		
		if (dialog.open() == Window.OK) {
			String attachment = getAttachment(dialog.getAttachmentIndex());
			try {
				createIssue(dialog.getIssueTitle(), dialog.getIssueText(), dialog.getAttachmentIndex(), attachment);
			} catch (CoreException e) {
				Activator.getDefault().logError(e);
			}
		}
	}


	/**
	 * sends the {@link Issue} to the eclipse-cognicrypt/CogniCrypt GitHub repository. 
	 * @param issue issue to send
	 */
	private void send(Issue issue) {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(readToken());
		IssueService issueService = new IssueService(client);
		try {
			issueService.createIssue("eclipse-cognicrypt","CogniCrypt", issue);
		} catch (IOException e) {
			Activator.getDefault().logError(e);			
		}
	}
	
	
	/**
	 * Creates a {@link Issue} object.
	 * @param issueTitle issue title
	 * @param issueText issue description
	 * @param attachmentIndex index of selected attachment
	 * @param attachment error Java and Jimple code
	 * @throws CoreException
	 */
	private void createIssue(String issueTitle, String issueText, int attachmentIndex ,String attachment) throws CoreException{

		Issue issue = new Issue();
		
		if(issueTitle.trim().isEmpty()) {
			issueTitle = "Bug Report";
		}
		issue.setTitle(issueTitle);

		Label bugLabel = new Label();
		bugLabel.setName("bug");
		Label feedBackLabel = new Label();
		feedBackLabel.setName("UserFeedback");
		Label sastLabel = new Label();
		sastLabel.setName("SAST");
		ArrayList<Label> labelList = new ArrayList<>();
		labelList.add(bugLabel);
		labelList.add(feedBackLabel);
		labelList.add(sastLabel);
		issue.setLabels(labelList);
		
		
		StringBuilder builder = new StringBuilder();		
		builder.append("**User Issue Description**\n");
		builder.append(issueText+"\n\n");
		builder.append("**Configuration:**\n");
		builder.append("- Eclipse version: "+
		Platform.getBundle("org.eclipse.platform").getVersion() +"\n");
		builder.append("- Java version: " + System.getProperty("java.version") + "\n");
		builder.append("- OS: " + System.getProperty("os.name").toLowerCase() + "\n\n");
		builder.append("**CogniCrypt Error Information:**\n");
		builder.append("- Violated CrySL rule: "+ (String) marker.getAttribute("crySLRuleName")+ "\n");
		builder.append("- Error type: "+ QuickFixUtils.getErrorTypeFromMarkerType((String) marker.getAttribute("errorType"))+ "\n");
		builder.append("- Error message: "+ (String) marker.getAttribute(IMarker.MESSAGE)+ "\n");
		builder.append("- Severity: "+Severities.get((int) marker.getAttribute(IMarker.SEVERITY)).toString()+ "\n\n");
		
		if(!attachment.isEmpty()) {
			builder.append("**Java Code**\n\n");
			builder.append("Error line: `"+getErrorLineCode()+"`\n");
			builder.append("```java\n"+attachment+"\n```");
			builder.append("\n\n");
			
			if(attachmentIndex == 1 || attachmentIndex == 0) {
				builder.append("**Jimple Code**\n\n");
				builder.append("```java\n"+(String) marker.getAttribute("errorJimpleCode")+"```");
				builder.append("\n\n");
			}
			
		}
		
		String body = builder.toString();
		issue.setBody(body);
		
		send(issue);
	}
	
	/**
	 * Returns the content for the attachment
	 * @param i selected attachment index 
	 * @return content of the attachment
	 */
	private String getAttachment(int i) {
		String attachment = "";
		switch (i) {
		case 0:	attachment = getFileContent();
			break;
		case 1: attachment = getMethodBody();
			break;
		case 2:
				attachment = "";
			break;
		}
		return attachment;
	}

	/**
	 * Returns the Java method body that contains the error.
	 * @return method body as {@link String}
	 */
	private String getMethodBody() {
		String methodBody = "";
		ICompilationUnit sourceUnit = null;
		try {
			sourceUnit = QuickFixUtils.getCompilationUnitFromMarker(marker);
			final ASTParser parser = ASTParser.newParser(AST.JLS9);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(sourceUnit);
			final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
			HashMap<Integer, MethodDeclaration> methodMap = new HashMap<>();
			
			unit.accept(new ASTVisitor() {
				@Override
				public boolean visit(final MethodDeclaration node) {
					methodMap.put(unit.getLineNumber(node.getStartPosition()), node);
					return true;
				}
			});

			int errorLineNumber = (int) marker.getAttribute(IMarker.LINE_NUMBER);
			ArrayList<Integer> methodStartLineNumber = new ArrayList<>(methodMap.keySet());
			Collections.sort(methodStartLineNumber);

			if (errorLineNumber < methodStartLineNumber.get(0)) {
				// error in the variable declarations
			} else if (errorLineNumber > methodStartLineNumber.get(methodStartLineNumber.size() - 1)) {
				methodBody = methodMap.get(methodStartLineNumber.get(methodStartLineNumber.size() - 1)).toString();
			} else {
				for (int i = 0; i < methodStartLineNumber.size() - 1; i++) {
					int start = methodStartLineNumber.get(i);
					int end = methodStartLineNumber.get(i + 1);

					if (errorLineNumber > start && errorLineNumber < end) {
						methodBody = methodMap.get(methodStartLineNumber.get(i)).toString();
						break;
					}
				}
			}

		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}

		return methodBody;
	}

	
	/**
	 * Returns the Java line code of the error.
	 * @return error line code
	 */
	private String getErrorLineCode() {
	    String errorLine = "";
		try {
		List<String> lines = Files.readAllLines(Paths.get(marker.getResource().getLocation().toOSString()));
		errorLine = lines.get((int) marker.getAttribute(IMarker.LINE_NUMBER)-1);
		
		} catch (IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
			e.printStackTrace();
		}
		return errorLine;
	}
	
	/**
	 * Returns the Java file as {@link String}.
	 * @return Java file content
	 */
	private String getFileContent() {
	    String fileContent = "";
		try {
			fileContent = new String(Files.readAllBytes(Paths.get(marker.getResource().getLocation().toOSString())), StandardCharsets.UTF_8);
		} catch (IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		}
		return fileContent;
	}
}
