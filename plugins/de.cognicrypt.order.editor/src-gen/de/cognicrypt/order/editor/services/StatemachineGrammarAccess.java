/*
 * generated by Xtext 2.25.0
 */
package de.cognicrypt.order.editor.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.service.AbstractElementFinder;
import org.eclipse.xtext.service.GrammarProvider;
import org.eclipse.xtext.xbase.services.XtypeGrammarAccess;

@Singleton
public class StatemachineGrammarAccess extends AbstractElementFinder.AbstractGrammarElementFinder {
	
	public class StatemachineElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "de.cognicrypt.order.editor.Statemachine.Statemachine");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cStatemachineAction_0 = (Action)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Keyword cEventsKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final Assignment cEventsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cEventsEventParserRuleCall_1_1_0 = (RuleCall)cEventsAssignment_1_1.eContents().get(0);
		private final Keyword cEndKeyword_1_2 = (Keyword)cGroup_1.eContents().get(2);
		private final Assignment cStatesAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cStatesStateParserRuleCall_2_0 = (RuleCall)cStatesAssignment_2.eContents().get(0);
		private final Assignment cTransitionsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cTransitionsTransitionParserRuleCall_3_0 = (RuleCall)cTransitionsAssignment_3.eContents().get(0);
		
		//Statemachine :
		//    {Statemachine}
		//    ('events'
		//        events+=Event+
		//    'end')?
		//    states+=State*
		//    transitions+=Transition*
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//{Statemachine}
		//('events'
		//    events+=Event+
		//'end')?
		//states+=State*
		//transitions+=Transition*
		public Group getGroup() { return cGroup; }
		
		//{Statemachine}
		public Action getStatemachineAction_0() { return cStatemachineAction_0; }
		
		//('events'
		//    events+=Event+
		//'end')?
		public Group getGroup_1() { return cGroup_1; }
		
		//'events'
		public Keyword getEventsKeyword_1_0() { return cEventsKeyword_1_0; }
		
		//events+=Event+
		public Assignment getEventsAssignment_1_1() { return cEventsAssignment_1_1; }
		
		//Event
		public RuleCall getEventsEventParserRuleCall_1_1_0() { return cEventsEventParserRuleCall_1_1_0; }
		
		//'end'
		public Keyword getEndKeyword_1_2() { return cEndKeyword_1_2; }
		
		//states+=State*
		public Assignment getStatesAssignment_2() { return cStatesAssignment_2; }
		
		//State
		public RuleCall getStatesStateParserRuleCall_2_0() { return cStatesStateParserRuleCall_2_0; }
		
		//transitions+=Transition*
		public Assignment getTransitionsAssignment_3() { return cTransitionsAssignment_3; }
		
		//Transition
		public RuleCall getTransitionsTransitionParserRuleCall_3_0() { return cTransitionsTransitionParserRuleCall_3_0; }
	}
	public class EventElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "de.cognicrypt.order.editor.Statemachine.Event");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNameAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNameIDTerminalRuleCall_0_0 = (RuleCall)cNameAssignment_0.eContents().get(0);
		private final Assignment cCodeAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cCodeIDTerminalRuleCall_1_0 = (RuleCall)cCodeAssignment_1.eContents().get(0);
		
		//Event:
		//    name=ID code=ID
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//name=ID code=ID
		public Group getGroup() { return cGroup; }
		
		//name=ID
		public Assignment getNameAssignment_0() { return cNameAssignment_0; }
		
		//ID
		public RuleCall getNameIDTerminalRuleCall_0_0() { return cNameIDTerminalRuleCall_0_0; }
		
		//code=ID
		public Assignment getCodeAssignment_1() { return cCodeAssignment_1; }
		
		//ID
		public RuleCall getCodeIDTerminalRuleCall_1_0() { return cCodeIDTerminalRuleCall_1_0; }
	}
	public class StateElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "de.cognicrypt.order.editor.Statemachine.State");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cStateKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Assignment cTransitionsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cTransitionsTransitionParserRuleCall_2_0 = (RuleCall)cTransitionsAssignment_2.eContents().get(0);
		private final Keyword cEndKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//State:
		//    'state' name=ID
		//        transitions+=Transition*
		//    'end'
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//'state' name=ID
		//    transitions+=Transition*
		//'end'
		public Group getGroup() { return cGroup; }
		
		//'state'
		public Keyword getStateKeyword_0() { return cStateKeyword_0; }
		
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
		
		//transitions+=Transition*
		public Assignment getTransitionsAssignment_2() { return cTransitionsAssignment_2; }
		
		//Transition
		public RuleCall getTransitionsTransitionParserRuleCall_2_0() { return cTransitionsTransitionParserRuleCall_2_0; }
		
		//'end'
		public Keyword getEndKeyword_3() { return cEndKeyword_3; }
	}
	public class TransitionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "de.cognicrypt.order.editor.Statemachine.Transition");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cTransitionKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Assignment cFromStateAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final CrossReference cFromStateStateCrossReference_2_0 = (CrossReference)cFromStateAssignment_2.eContents().get(0);
		private final RuleCall cFromStateStateIDTerminalRuleCall_2_0_1 = (RuleCall)cFromStateStateCrossReference_2_0.eContents().get(1);
		private final Keyword cCommaKeyword_3 = (Keyword)cGroup.eContents().get(3);
		private final Assignment cEventAssignment_4 = (Assignment)cGroup.eContents().get(4);
		private final CrossReference cEventEventCrossReference_4_0 = (CrossReference)cEventAssignment_4.eContents().get(0);
		private final RuleCall cEventEventIDTerminalRuleCall_4_0_1 = (RuleCall)cEventEventCrossReference_4_0.eContents().get(1);
		private final Keyword cCommaKeyword_5 = (Keyword)cGroup.eContents().get(5);
		private final Assignment cEndStateAssignment_6 = (Assignment)cGroup.eContents().get(6);
		private final CrossReference cEndStateStateCrossReference_6_0 = (CrossReference)cEndStateAssignment_6.eContents().get(0);
		private final RuleCall cEndStateStateIDTerminalRuleCall_6_0_1 = (RuleCall)cEndStateStateCrossReference_6_0.eContents().get(1);
		private final Keyword cEndKeyword_7 = (Keyword)cGroup.eContents().get(7);
		
		//Transition:
		//    'transition' name=ID
		//    fromState=[State] ',' event=[Event] ',' endState=[State]
		//    'end'
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//'transition' name=ID
		//fromState=[State] ',' event=[Event] ',' endState=[State]
		//'end'
		public Group getGroup() { return cGroup; }
		
		//'transition'
		public Keyword getTransitionKeyword_0() { return cTransitionKeyword_0; }
		
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
		
		//fromState=[State]
		public Assignment getFromStateAssignment_2() { return cFromStateAssignment_2; }
		
		//[State]
		public CrossReference getFromStateStateCrossReference_2_0() { return cFromStateStateCrossReference_2_0; }
		
		//ID
		public RuleCall getFromStateStateIDTerminalRuleCall_2_0_1() { return cFromStateStateIDTerminalRuleCall_2_0_1; }
		
		//','
		public Keyword getCommaKeyword_3() { return cCommaKeyword_3; }
		
		//event=[Event]
		public Assignment getEventAssignment_4() { return cEventAssignment_4; }
		
		//[Event]
		public CrossReference getEventEventCrossReference_4_0() { return cEventEventCrossReference_4_0; }
		
		//ID
		public RuleCall getEventEventIDTerminalRuleCall_4_0_1() { return cEventEventIDTerminalRuleCall_4_0_1; }
		
		//','
		public Keyword getCommaKeyword_5() { return cCommaKeyword_5; }
		
		//endState=[State]
		public Assignment getEndStateAssignment_6() { return cEndStateAssignment_6; }
		
		//[State]
		public CrossReference getEndStateStateCrossReference_6_0() { return cEndStateStateCrossReference_6_0; }
		
		//ID
		public RuleCall getEndStateStateIDTerminalRuleCall_6_0_1() { return cEndStateStateIDTerminalRuleCall_6_0_1; }
		
		//'end'
		public Keyword getEndKeyword_7() { return cEndKeyword_7; }
	}
	
	
	private final StatemachineElements pStatemachine;
	private final EventElements pEvent;
	private final StateElements pState;
	private final TransitionElements pTransition;
	
	private final Grammar grammar;
	
	private final XtypeGrammarAccess gaXtype;

	@Inject
	public StatemachineGrammarAccess(GrammarProvider grammarProvider,
			XtypeGrammarAccess gaXtype) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.gaXtype = gaXtype;
		this.pStatemachine = new StatemachineElements();
		this.pEvent = new EventElements();
		this.pState = new StateElements();
		this.pTransition = new TransitionElements();
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("de.cognicrypt.order.editor.Statemachine".equals(grammar.getName())) {
				return grammar;
			}
			List<Grammar> grammars = grammar.getUsedGrammars();
			if (!grammars.isEmpty()) {
				grammar = grammars.iterator().next();
			} else {
				return null;
			}
		}
		return grammar;
	}
	
	@Override
	public Grammar getGrammar() {
		return grammar;
	}
	
	
	public XtypeGrammarAccess getXtypeGrammarAccess() {
		return gaXtype;
	}

	
	//Statemachine :
	//    {Statemachine}
	//    ('events'
	//        events+=Event+
	//    'end')?
	//    states+=State*
	//    transitions+=Transition*
	//;
	public StatemachineElements getStatemachineAccess() {
		return pStatemachine;
	}
	
	public ParserRule getStatemachineRule() {
		return getStatemachineAccess().getRule();
	}
	
	//Event:
	//    name=ID code=ID
	//;
	public EventElements getEventAccess() {
		return pEvent;
	}
	
	public ParserRule getEventRule() {
		return getEventAccess().getRule();
	}
	
	//State:
	//    'state' name=ID
	//        transitions+=Transition*
	//    'end'
	//;
	public StateElements getStateAccess() {
		return pState;
	}
	
	public ParserRule getStateRule() {
		return getStateAccess().getRule();
	}
	
	//Transition:
	//    'transition' name=ID
	//    fromState=[State] ',' event=[Event] ',' endState=[State]
	//    'end'
	//;
	public TransitionElements getTransitionAccess() {
		return pTransition;
	}
	
	public ParserRule getTransitionRule() {
		return getTransitionAccess().getRule();
	}
	
	//JvmTypeReference:
	//    JvmParameterizedTypeReference =>({JvmGenericArrayTypeReference.componentType=current} ArrayBrackets)*
	//    | XFunctionTypeRef;
	public XtypeGrammarAccess.JvmTypeReferenceElements getJvmTypeReferenceAccess() {
		return gaXtype.getJvmTypeReferenceAccess();
	}
	
	public ParserRule getJvmTypeReferenceRule() {
		return getJvmTypeReferenceAccess().getRule();
	}
	
	//ArrayBrackets :
	//    '[' ']'
	//;
	public XtypeGrammarAccess.ArrayBracketsElements getArrayBracketsAccess() {
		return gaXtype.getArrayBracketsAccess();
	}
	
	public ParserRule getArrayBracketsRule() {
		return getArrayBracketsAccess().getRule();
	}
	
	//XFunctionTypeRef:
	//    ('(' (paramTypes+=JvmTypeReference (',' paramTypes+=JvmTypeReference)*)? ')')? '=>' returnType=JvmTypeReference;
	public XtypeGrammarAccess.XFunctionTypeRefElements getXFunctionTypeRefAccess() {
		return gaXtype.getXFunctionTypeRefAccess();
	}
	
	public ParserRule getXFunctionTypeRefRule() {
		return getXFunctionTypeRefAccess().getRule();
	}
	
	//JvmParameterizedTypeReference:
	//    type=[JvmType|QualifiedName] (
	//        =>'<' arguments+=JvmArgumentTypeReference (',' arguments+=JvmArgumentTypeReference)* '>'
	//        (=>({JvmInnerTypeReference.outer=current} '.') type=[JvmType|ValidID] (=>'<' arguments+=JvmArgumentTypeReference (',' arguments+=JvmArgumentTypeReference)* '>')?)*
	//    )?;
	public XtypeGrammarAccess.JvmParameterizedTypeReferenceElements getJvmParameterizedTypeReferenceAccess() {
		return gaXtype.getJvmParameterizedTypeReferenceAccess();
	}
	
	public ParserRule getJvmParameterizedTypeReferenceRule() {
		return getJvmParameterizedTypeReferenceAccess().getRule();
	}
	
	//JvmArgumentTypeReference returns JvmTypeReference:
	//    JvmTypeReference | JvmWildcardTypeReference;
	public XtypeGrammarAccess.JvmArgumentTypeReferenceElements getJvmArgumentTypeReferenceAccess() {
		return gaXtype.getJvmArgumentTypeReferenceAccess();
	}
	
	public ParserRule getJvmArgumentTypeReferenceRule() {
		return getJvmArgumentTypeReferenceAccess().getRule();
	}
	
	//JvmWildcardTypeReference:
	//    {JvmWildcardTypeReference} '?' (
	//      constraints+=JvmUpperBound (constraints+=JvmUpperBoundAnded)*
	//    | constraints+=JvmLowerBound (constraints+=JvmLowerBoundAnded)*
	//    )?;
	public XtypeGrammarAccess.JvmWildcardTypeReferenceElements getJvmWildcardTypeReferenceAccess() {
		return gaXtype.getJvmWildcardTypeReferenceAccess();
	}
	
	public ParserRule getJvmWildcardTypeReferenceRule() {
		return getJvmWildcardTypeReferenceAccess().getRule();
	}
	
	//JvmUpperBound :
	//    'extends' typeReference=JvmTypeReference;
	public XtypeGrammarAccess.JvmUpperBoundElements getJvmUpperBoundAccess() {
		return gaXtype.getJvmUpperBoundAccess();
	}
	
	public ParserRule getJvmUpperBoundRule() {
		return getJvmUpperBoundAccess().getRule();
	}
	
	//JvmUpperBoundAnded returns JvmUpperBound:
	//    '&' typeReference=JvmTypeReference;
	public XtypeGrammarAccess.JvmUpperBoundAndedElements getJvmUpperBoundAndedAccess() {
		return gaXtype.getJvmUpperBoundAndedAccess();
	}
	
	public ParserRule getJvmUpperBoundAndedRule() {
		return getJvmUpperBoundAndedAccess().getRule();
	}
	
	//JvmLowerBound :
	//    'super' typeReference=JvmTypeReference;
	public XtypeGrammarAccess.JvmLowerBoundElements getJvmLowerBoundAccess() {
		return gaXtype.getJvmLowerBoundAccess();
	}
	
	public ParserRule getJvmLowerBoundRule() {
		return getJvmLowerBoundAccess().getRule();
	}
	
	//JvmLowerBoundAnded returns JvmLowerBound:
	//    '&' typeReference=JvmTypeReference;
	public XtypeGrammarAccess.JvmLowerBoundAndedElements getJvmLowerBoundAndedAccess() {
		return gaXtype.getJvmLowerBoundAndedAccess();
	}
	
	public ParserRule getJvmLowerBoundAndedRule() {
		return getJvmLowerBoundAndedAccess().getRule();
	}
	
	//JvmTypeParameter :
	//    name=ValidID
	//    (constraints+=JvmUpperBound (constraints+=JvmUpperBoundAnded)*)?;
	public XtypeGrammarAccess.JvmTypeParameterElements getJvmTypeParameterAccess() {
		return gaXtype.getJvmTypeParameterAccess();
	}
	
	public ParserRule getJvmTypeParameterRule() {
		return getJvmTypeParameterAccess().getRule();
	}
	
	//QualifiedName:
	//    ValidID ('.' ValidID)*;
	public XtypeGrammarAccess.QualifiedNameElements getQualifiedNameAccess() {
		return gaXtype.getQualifiedNameAccess();
	}
	
	public ParserRule getQualifiedNameRule() {
		return getQualifiedNameAccess().getRule();
	}
	
	//QualifiedNameWithWildcard :
	//    QualifiedName  '.' '*';
	public XtypeGrammarAccess.QualifiedNameWithWildcardElements getQualifiedNameWithWildcardAccess() {
		return gaXtype.getQualifiedNameWithWildcardAccess();
	}
	
	public ParserRule getQualifiedNameWithWildcardRule() {
		return getQualifiedNameWithWildcardAccess().getRule();
	}
	
	//ValidID:
	//    ID;
	public XtypeGrammarAccess.ValidIDElements getValidIDAccess() {
		return gaXtype.getValidIDAccess();
	}
	
	public ParserRule getValidIDRule() {
		return getValidIDAccess().getRule();
	}
	
	//XImportSection:
	//    importDeclarations+=XImportDeclaration+;
	public XtypeGrammarAccess.XImportSectionElements getXImportSectionAccess() {
		return gaXtype.getXImportSectionAccess();
	}
	
	public ParserRule getXImportSectionRule() {
		return getXImportSectionAccess().getRule();
	}
	
	//XImportDeclaration:
	//    'import' (
	//        (static?='static' extension?='extension'? importedType=[JvmDeclaredType|QualifiedNameInStaticImport] (wildcard?='*' | memberName=ValidID))
	//        | importedType=[JvmDeclaredType|QualifiedName]
	//        | importedNamespace=QualifiedNameWithWildcard) ';'?
	//;
	public XtypeGrammarAccess.XImportDeclarationElements getXImportDeclarationAccess() {
		return gaXtype.getXImportDeclarationAccess();
	}
	
	public ParserRule getXImportDeclarationRule() {
		return getXImportDeclarationAccess().getRule();
	}
	
	//QualifiedNameInStaticImport:
	//    (ValidID '.')+
	//;
	public XtypeGrammarAccess.QualifiedNameInStaticImportElements getQualifiedNameInStaticImportAccess() {
		return gaXtype.getQualifiedNameInStaticImportAccess();
	}
	
	public ParserRule getQualifiedNameInStaticImportRule() {
		return getQualifiedNameInStaticImportAccess().getRule();
	}
	
	//terminal ID:
	//    '^'? ('a'..'z'|'A'..'Z'|'$'|'_') ('a'..'z'|'A'..'Z'|'$'|'_'|'0'..'9')*;
	public TerminalRule getIDRule() {
		return gaXtype.getIDRule();
	}
	
	//terminal STRING:
	//            '"' ( '\\' . /* ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\') */ | !('\\'|'"') )* '"'? |
	//            "'" ( '\\' . /* ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\') */ | !('\\'|"'") )* "'"?;
	public TerminalRule getSTRINGRule() {
		return gaXtype.getSTRINGRule();
	}
	
	//terminal ML_COMMENT: '/*' -> '*/';
	public TerminalRule getML_COMMENTRule() {
		return gaXtype.getML_COMMENTRule();
	}
	
	//terminal SL_COMMENT: '//' !('\n'|'\r')* ('\r'? '\n')?;
	public TerminalRule getSL_COMMENTRule() {
		return gaXtype.getSL_COMMENTRule();
	}
	
	//terminal WS: (' '|'\t'|'\r'|'\n')+;
	public TerminalRule getWSRule() {
		return gaXtype.getWSRule();
	}
	
	//terminal ANY_OTHER: .;
	public TerminalRule getANY_OTHERRule() {
		return gaXtype.getANY_OTHERRule();
	}
}