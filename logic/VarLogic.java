package logic;

import main.*;
import node.*;

public abstract class VarLogic {
	public static final PackageNode declare = new PackageNode();
	
	public static final PackageNode content = new PackageNode();
	public static final PackageNode content_append = new PackageNode();
	
	public static final PackageNode array = new PackageNode();
	
	public static void initialize() {
		declare.set(() -> INode.stack(
				"Var.Declare",
				Resources.VAR,
				content
		));
		
		content.set(() -> INode.stack(
				"Var.Content",
				Resources.IDENTIFIER,
				new OrNode(content_append, null),
				Resources.COLON,
				new OrNode(array, Resources.DATA_TYPE),
				Resources.SEMICOLON,
				new OrNode(content, null)
		));
		
		content_append.set(() -> INode.stack(
				"Var.Content.Append",
				Resources.COMMA,
				Resources.IDENTIFIER,
				new OrNode(content_append, null)
		));
		
		array.set(() -> INode.stack(
				"Var.Array",
				Resources.OPEN_BRACKET,
				Resources.INTEGER,
				Resources.DOUBLE_DOT,
				Resources.INTEGER,
				Resources.CLOSE_BRACKET,
				Resources.OF,
				Resources.DATA_TYPE
		));
	}
}
