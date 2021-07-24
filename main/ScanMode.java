package main;

public enum ScanMode {
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
	BodyArithmetic0,
	BodyArithmetic1,
	BodyProcedure0,
	BodyProcedure1,
	BodyProcedure2,
	BodyProcedure3
}
