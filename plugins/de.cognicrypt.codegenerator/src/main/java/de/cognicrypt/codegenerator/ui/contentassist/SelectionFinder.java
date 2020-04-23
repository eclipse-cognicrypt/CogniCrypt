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
//	private IRegion region;
//	private String selectionName;

	public SelectionFinder(ICompilationUnit unit, int offset) {
		this.unit = unit;
		this.offset = offset;
	}

	public String getMemberName() {
		visit();
		return memberName;
	}

//	public String getSelectionName() {
//		visit();
//		return selectionName;
//	}
//
//	public IRegion getRegion() {
//		visit();
//		return region;
//	}

	private boolean visited = false;

	private void visit() {
		if (visited) {
			return;
		}
		visited = true;

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(unit);
		parser.setSourceRange(offset, -1);
		ASTNode node = parser.createAST(new NullProgressMonitor());
		node.accept(this);
	}
//	
//	@Override
//	public boolean preVisit2(ASTNode node) {
//		int offset = node.getStartPosition();
//		int length = node.getLength();
//		System.out.println(offset <= this.offset && this.offset <= offset + length);
//		return offset <= this.offset && this.offset <= offset + length;
//	}
	
//	@Override
//	public boolean visit(StringLiteral node) {
//		ASTNode parent = node.getParent();
//		String methodName = ((MethodInvocation) parent).getName().getFullyQualifiedName();
//		if(("includeClass").equals(methodName)) {
//			this.memberName = methodName;
//			return true;
//		}
//		return false;
//	}
	
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
