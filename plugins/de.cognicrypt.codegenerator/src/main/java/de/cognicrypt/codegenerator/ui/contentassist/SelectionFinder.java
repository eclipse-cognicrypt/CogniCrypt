package de.cognicrypt.codegenerator.ui.contentassist;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class SelectionFinder extends ASTVisitor {
	private ICompilationUnit unit;
	private int offset;
	private String memberName;

	public SelectionFinder(ICompilationUnit unit, int offset) {
		this.unit = unit;
		this.offset = offset;
	}

	public String getMemberName() {
		visit();
		return memberName;
	}

	private boolean visited = false;

	private void visit() {
		if (visited) {
			return;
		}
		visited = true;

		ASTParser parser = ASTParser.newParser(AST.JLS12);
		parser.setSource(unit);
		parser.setSourceRange(offset, -1);
		ASTNode node = parser.createAST(new NullProgressMonitor());
		node.accept(this);
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
        return treatMethodName(node);
	}
	
    private boolean treatMethodName(MethodInvocation node) {
        String methodName = node.getName().getFullyQualifiedName();
        int currentOffset = node.getLength() + node.getStartPosition() - 1;
        if(("includeClass").equals(methodName) && currentOffset == offset) {
        	this.memberName = methodName;
        	return true;
		} else if ((node.getExpression() instanceof MethodInvocation)) {
            return treatMethodName((MethodInvocation) node.getExpression());
        } else {
            return false;
        }
    }

	
}
