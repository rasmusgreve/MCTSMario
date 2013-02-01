package itu.ejjragr.graphing;

public class GraphableTestNode extends GraphableTreeNode {

	private String title;
	private GraphableTestNode[] children;
	public GraphableTestNode(String title, GraphableTestNode[] children)
	{
		this.title = title;
		this.children = children;
	}
	
	@Override
	protected GraphableTreeNode[] getChildren() {
		return children;
	}

	@Override
	protected String getTitle() {
		return title;
	}

}
