package AIProject2;

import ProjectTwoEngine.*;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

public class Node
{
    State state;
    Node parent;
    PlayerID curPlayer;
    List<Node> childArray;

    public Node(State state, Node parent, PlayerID curPlayer)
    {
        this.state = state;
        this.parent = parent;
        this.curPlayer = curPlayer;
        childArray = new ArrayList<Node>();
    }

    public Node(Node node) //copy constructor
    {   
        State tempState = new State(node.getState());
        this.state = tempState;
        this.parent = node.getParent();
        this.curPlayer = node.getState().getCurPlayer();
        this.childArray = node.getChildArray();
    }


    public Node(GameState gs, Node parent, PlayerID curPlayer)
    {
        State s = new State(gs.getLastMove(), gs, curPlayer);
        this.state = s;
        this.parent = parent;
        this.curPlayer = curPlayer;
    }

    public List<Node> getChildArray() 
    {
        return childArray;
    }
    public boolean isOver()
    {
        return  MonteCarloTreeSearch.isGameOver(state.getGs());
    }
    public State getState() {
        return state;
    }

    public Node getRandomChildNode()
    {
        Random rand = new Random();
        List<Node> childNodes = this.getChildArray();
	
	    int i = rand.nextInt(childNodes.size());
	    return  childNodes.get(i);
    }

    public Node getParent()
    {
        return parent;
    }

    public Node getChildWithMaxScore()
    {
        List<Node> children = this.getChildArray();
        double maxScore = 0;
        Node maxChild = null;
        for (Node node : children) 
        {
            double winScore = node.getState().getWinScore();
            if(winScore > maxScore)
            {
                maxScore = winScore;
                maxChild = node;
            }
        }
        return maxChild;

    }

    public void add(Node newNode) 
    {
        if(this.childArray == null)
            this.childArray = new ArrayList<Node>();
        this.childArray.add(newNode);
    }
}