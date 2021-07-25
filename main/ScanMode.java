package main;

public enum ScanMode {
	/**
	 * Expecting a semicolon.
	 */
	Semicolon,
	/**
	 * Expecting a dot.
	 */
	Dot,
	
	
	/**
	 * When the scanner expects a program name.
	 */
	Program,
	
	
	/**
	 * When the scanner expects an identifier.
	 */
	VarDeclare0,
	/**
	 * When the scanner expects a comma or a colon.
	 */
	VarDeclare1,
	/**
	 * When the scanner expects a data type.
	 */
	VarExit0,
	/**
	 * When the scanner expects a semicolon.
	 */
	VarExit1,
	/**
	 * When the scanner either expects an identifier, a procedure, a function, or the main program.
	 */
	VarExit2,
	
	
	/**
	 * procedure -> identifier
	 */
	ProcedureDeclare0,
	/**
	 * identifier -> open parenthesis
	 */
	ProcedureDeclare1,
	/**
	 * open parenthesis -> const
	 */
	ProcedureVar0,
	/**
	 * const -> identifier
	 */
	ProcedureVar1,
	/**
	 * identifier -> colon
	 */
	ProcedureVar2,
	/**
	 * colon -> data type
	 */
	ProcedureVar3,
	/**
	 * data type -> comma or close parenthesis
	 */
	ProcedureVar4,
	/**
	 * close parenthesis -> semicolon
	 */
	ProcedureExit0,
	/**
	 * Semicolon at the end of body declaration.
	 */
	ProcedureExit1,
	
	
	/**
	 * procedure -> identifier
	 */
	FunctionDeclare0,
	/**
	 * function IdentifierName
	 * identifier -> open parenthesis
	 */
	FunctionDeclare1,
	/**
	 * function IdentifierName(
	 * open parenthesis -> const or close parenthesis
	 */
	FunctionVar0,
	/**
	 * function Identifier(const
	 * const -> identifier
	 */
	FunctionVar1,
	/**
	 * function Identifier(const Identifier
	 * identifier -> colon
	 */
	FunctionVar2,
	/**
	 * function Identifier(const Identifier :
	 * colon -> data type
	 */
	FunctionVar3,
	/**
	 * function Identifier(const Identifier : DataType
	 * data type -> comma or close parenthesis
	 */
	FunctionVar4,
	/**
	 * function Identifier(const Identifier : DataType)
	 * close parenthesis -> colon
	 */
	FunctionExit0,
	/**
	 * function Identifier(const Identifier : DataType) :
	 * colon -> data type
	 */
	FunctionExit1,
	/**
	 * function Identifier(const Identifier : DataType) : DataType
	 * data type -> semicolon
	 */
	FunctionExit2,
	/**
	 * Semicolon at the end of body declaration.
	 */
	FunctionExit3,
	
	
	/**
	 * begin
	 */
	BodyDeclare0,
	/**
	 * begin -> ... or end
	 */
	BodyDeclare1,
	BodyExit0,
	BodyIdentifier0,
	BodyAssignment0,
	BodyAssignment1,
	BodyProcedure0,
	BodyProcedure1,
	BodyProcedure2,
	BodyProcedure3,
	

	Arithmetic0,
	Arithmetic1,
	ArithmeticGroup0,
	ArithmeticGroup1,
	
	
	ForLoop0,
	ForLoop1,
	ForLoop2,
	ForLoop3,
	ForLoop4,
	ForLoop5,
	ForLoop6,
	ForLoop7,
	
	
	If0,
	If1,
	If2,
	If3,
	Else0,
	Else1,
	
	
	Expression0,
	Expression1,
	Expression2,
	Expression3
}
