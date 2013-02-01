package itu.ejjragr.graphing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class GraphableTreeNode {

	protected abstract GraphableTreeNode[] getChildren();
	protected abstract String getTitle();
	
	public void showGraphicalTree()
	{
		int numChildren = getChildren() == null ? 0 : getChildren().length;
		int width = getDepth() * numChildren * 20;
		int height = getDepth() * 50;
		
		
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, i.getWidth(), i.getHeight());
		drawNode(g,20,20);
		//*
		File outputfile = new File("C:/test/saved.png");
	    try {
			ImageIO.write(i, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/
		
		GraphableTreeNode[] children = getChildren();
		if (children == null || children.length == 0) //It's a leaf node
		{
			
		}
		else //The node has children
		{
			
		}
	}
	
	private void drawNode(Graphics2D g, int x, int y)
	{
		g.setColor(new Color(50,50,50));
		g.fillOval(x-8,y-8, 16, 16);
		g.setColor(Color.WHITE);
		g.fillOval(x-7,y-7, 14, 14);
	}
	
	private int getDepth()
	{
		int max = 0;
		if (getChildren() != null)
		{
		for(GraphableTreeNode t : getChildren())
		{
			max = Math.max(max, t.getDepth());
		}
		}
		return max + 1;
	}
	
	public static void main(String[] args)
	{
		GraphableTestNode[] testChildren = new GraphableTestNode[4];
		for (int i = 0; i < testChildren.length; i++)
			testChildren[i] = new GraphableTestNode("Child " + i, null);
		GraphableTreeNode testnode = new GraphableTestNode("Root", testChildren);
		testnode.showGraphicalTree();
	}
}
