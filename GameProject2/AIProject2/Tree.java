package AIProject2;

import ProjectTwoEngine.*;

public class Tree 
{
    private Node root;  
    
    public Tree(Node n)
    {
        root = n;
    }
    
    public Node getRoot() 
    {
        return root;
    }

    public void setRoot(Node n) 
    {
        root = n;
    }
}
