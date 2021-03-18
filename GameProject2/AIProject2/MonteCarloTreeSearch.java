package GameProject2.AIProject2;

import java.util.Random;

import ProjectTwoEngine.*;
import java.util.List;

public class MonteCarloTreeSearch 
{
    static final int WINSCORE = 0;
    int level;
    PlayerID opponent;
    int searchDuration = 1000;                      //play with values
   

    public Move findNextMove(GameState state, PlayerID curPlayer)
    {
        opponent = GameRules.otherPlayer(curPlayer);
        Tree tree = new Tree(new Node(state, null, opponent));
        Node rootNode = tree.getRoot();

        long start = System.currentTimeMillis();
        long end = start + searchDuration;                        
        while (System.currentTimeMillis() < end) 
        {
             //modify selectPromisingNode for better selection
            Node promisingNode = selectPromisingNode(rootNode);    
            if (!promisingNode.isOver())
            {
                Move m = promisingNode.getState().getMove();
                if(m = null || m instanceof PlaceMonsterMove) //root case: start on the buy phase
                {
                    expandBuyNode(promisingNode);
                }
                else if(m instanceof BuyMonsterMove)
                {
                    expandRespondNode(promisingNode);
                }
                else if(m instanceof RespondMove)
                {
                    expandPlaceNode(promisingNode);
                }

            } 
                
            
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) 
            {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            PlayerID playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getMove();
    }

    private Node selectPromisingNode(Node rootNode) 
    {
        Node node = rootNode;
        while (node.getChildArray().size() != 0)
        {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }
   
    private void expandBuyNode(Node node) 
    {
        List<State> possibleStates = node.getState().getAllPossibleBuySuccessors(); 
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node, state.getCurPlayer());
            node.getChildArray().add(newNode);
        });
    }

    private void expandRespondNode(Node node) 
    {
        List<State> possibleStates = node.getState().getAllPossibleRespondSuccessors(); 
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node, state.getCurPlayer());
            node.getChildArray().add(newNode);
        });
    }

    private void expandPlaceNode(Node node) 
    {
        List<State> possibleStates = node.getState().getAllPossiblePlaceSuccessors();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node, state.getCurPlayer());
            node.getChildArray().add(newNode);
        });
    }

    private void backPropogation(Node nodeToExplore, PlayerID player) 
    {
        Node tempNode = nodeToExplore;
        while (tempNode != null) 
        {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getCurPlayer() == player)
             {
                tempNode.getState().addScore(WIN_SCORE);            //wtf is WInScore?
            }
            tempNode = tempNode.getParent();
        }
    }
    private int simulateRandomPlayout(Node node) 
    {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().checkStatus();       ///make sure this returns player id
        if (boardStatus == opponent) 
        {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (boardStatus == Board.IN_PROGRESS)                    //figure out what this does
        {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().checkStatus();
        }
        return boardStatus;
    }
    
}
