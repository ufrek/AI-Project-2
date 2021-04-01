package AIProject2;

import java.util.Random;

import javax.lang.model.util.ElementScanner14;

import ProjectTwoEngine.*;
import java.util.List;

public class MonteCarloTreeSearch 
{

  
    static final int winScore = 10;
    int level;
    PlayerID opponent;
    int searchDuration = 500;                      //play with values
   

    public Move findNextMove(GameState state, PlayerID curPlayer)
    {
            //replaces the null deck with a hypotetical deck
        if(state.getDeck() == null)
        {
            List<Monster> curDeck = MontePythonAI.generateDeck(state);
            state.setDeck(curDeck);
        }
        
     
        opponent = otherPlayer(curPlayer);
        Tree tree = new Tree(new Node(state, null, opponent));
        Node rootNode = tree.getRoot();
        //figure out how to expand the node ------can clean this up a bit I suppose
        expandNode(rootNode);
        
       
        long start = System.currentTimeMillis();
        long end = start + searchDuration;                        
        while (System.currentTimeMillis() < end) 
        {
             //modify selectPromisingNode for better selection
            Node promisingNode = selectPromisingNode(rootNode);    
            if (!promisingNode.isOver())
            {
                expandNode(promisingNode);
               
            } 
                
            
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) 
            {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            PlayerID playoutResult = simulateRandomPlayout(nodeToExplore, curPlayer);
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);

        GameState ga  = winnerNode.getState().getGs();
        System.out.println("Winner node" + ga.getDeckSize());
        return winnerNode.getState().getMove();
    }

    private Node selectPromisingNode(Node rootNode) 
    {
        Node node = new Node(rootNode);
    
        while (node.getChildArray().size() != 0) 
        {
            node = UCT.findBestNodeWithUCT(node);
        }
        System.out.println("Node Selected");
        return node;
    }
   
    private void expandNode(Node node) 
    {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        Move lastMove = tempState.getMove();
        List<State> possibleStates;
        if (lastMove == null || lastMove instanceof PlaceMonsterMove)
        {

            possibleStates = tempState.getAllPossibleBuySuccessors(tempState.getGs()); 
           
        }
        else if(lastMove instanceof BuyMonsterMove)
        {
            System.out.println("Picking Response");
            GameState temp = node.getState().getGs();
            System.out.println(temp.getDeckSize());
            possibleStates = tempState.getAllPossibleRespondSuccessors(tempState.getGs()); 
        }
        else if (lastMove instanceof RespondMove)
        {
            
            possibleStates = tempState.getAllPossiblePlaceSuccessors(tempState.getGs()); 
        }
        else
        {
            possibleStates = null;
            System.out.println("Problem in Root Node Expansion Block");
        }
            
       
        for(State state : possibleStates) 
        {
            Node newNode = new Node(state, node, state.getCurPlayer());
            GameState gs = node.getState().getGs();
            
            node.add(newNode);
        }
        
    }


    private void backPropogation(Node nodeToExplore, PlayerID player) 
    {
        Node tempNode = nodeToExplore;
        while (tempNode != null) 
        {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getCurPlayer() == player)
             {
                tempNode.getState().addScore(winScore);           
            }
            tempNode = tempNode.getParent();
        }
    }
    private PlayerID simulateRandomPlayout(Node node, PlayerID curPlayer) 
    {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
       

        PlayerID endGameVictor = tempState.checkStatus(curPlayer);       ///make sure this returns player id
        if (endGameVictor == opponent) 
        {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return endGameVictor;
        }
        GameState gs = tempState.getGs();                               //could maybe play off of the temp node?
        List<Monster> newDeck = MontePythonAI.generateDeck(gs);
        gs.setDeck(newDeck);
        while (!isGameOver(gs) && endGameVictor == null)                    
        {
            gs = tempState.randomPlay(gs);
            Move m = gs.getLastMove();//tempState.getMove();
            tempState = new State(m, gs, gs.getCurPlayer());
            endGameVictor = tempState.checkStatus(curPlayer);
        }
        System.out.print(endGameVictor);
        return endGameVictor;
    }

    public static PlayerID otherPlayer(PlayerID p)
    {
        if (p == PlayerID.TOP){
            return PlayerID.BOT;
        }
        else{
            return PlayerID.TOP;
        }
        }

        static boolean isGameOver(GameState state){
            boolean done = true;
            if (state.getCastleWon(CastleID.CastleA) == null){
                done = false;
            }
            if (state.getCastleWon(CastleID.CastleB) == null){
                done = false;
            }
            if (state.getCastleWon(CastleID.CastleC) == null){
                done = false;
            }
            return done;
            }
    
}
