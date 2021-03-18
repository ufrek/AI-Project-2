package GameProject2.AIProject2;

import java.util.Random;

import ProjectTwoEngine.*;
import java.util.List;

public class MonteCarloTreeSearch 
{
    static final int WINSCORE = -1;
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
                expandNode(promisingNode);
            
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) 
            {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
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
   
    private void expandNode(Node node) 
    {
        List<State> possibleStates = node.getState().getAllPossibleStates(); //maybe do the steal randomly
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node, GameRules.otherPlayer(node.getState().curPlayer));
            node.getChildArray().add(newNode);
        });
    }

    private void backPropogation(Node nodeToExplore, PlayerID player) 
    {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getCurPlayer() == player)
             {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }
    private int simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().checkStatus();
        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().checkStatus();
        }
        return boardStatus;
    }
    
}
