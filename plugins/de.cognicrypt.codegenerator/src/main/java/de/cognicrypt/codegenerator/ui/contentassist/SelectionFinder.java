package de.cognicrypt.codegenerator.ui.contentassist;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.text.IRegion;

public class SelectionFinder extends ASTVisitor {
	private ICompilationUnit unit;
	private int offset;

	private String memberName;
	private IRegion region;
	private String selectionName;

	public SelectionFinder(ICompilationUnit unit, int offset) {
		this.unit = unit;
		this.offset = offset;
	}

	public String getMemberName() {
		visit();
		return memberName;
	}

	public String getSelectionName() {
		visit();
		return selectionName;
	}

	public IRegion getRegion() {
		visit();
		return region;
	}

	private boolean visited = false;

	private void visit() {
		if (visited) {
			return;
		}
		visited = true;

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(unit);
		// TODO find out if offset can be used to get current call name
		parser.setSourceRange(offset, 1);
		ASTNode node = parser.createAST(new NullProgressMonitor());
		node.accept(this);
	}
	
	@Override
	public boolean preVisit2(ASTNode node) {
		int offset = node.getStartPosition();
		int length = node.getLength();
		return offset <= this.offset && this.offset <= offset + length;
	}
	
	// FIXME current its returning last call name in the call chain instead of current call
	@Override
	public boolean visit(MethodInvocation node) {
		String name = node.getName().getFullyQualifiedName();
		if ("includeClass".equals(name)) {
			this.memberName = name;
			return true;
		}
		return false;
	}

//	@Override
//	public boolean visit(StringLiteral node) {
//		String value = node.getLiteralValue();
//		this.selectionName = value.trim();
//		this.region = new Region(node.getStartPosition() + 1, node.getLength() - 2);
//		return false;
//	}
}
