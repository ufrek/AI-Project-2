package GameProject2.AIProject2;

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
    }

    public Node(Node node) //copy constructor
    {
        this.state = node.getState();
        this.parent = node.getParent();
        this.curPlayer = node.getCurPlayer;
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
        return  GameRules.isGameOver(state.getGs());
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
}