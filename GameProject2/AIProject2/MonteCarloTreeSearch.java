package AIProject2;

import java.util.Random;

import javax.lang.model.util.ElementScanner14;

import ProjectTwoEngine.*;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloTreeSearch 
{

  
    static final int winScore = 10;
    int level;
    PlayerID opponent;
    int searchDuration = 5000;                      //play with values
    
   

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
            EndOutcome result = simulateRandomPlayout(nodeToExplore, curPlayer);
            backPropogation(result, curPlayer);                             //this is the issue
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


    private void backPropogation(EndOutcome result, PlayerID curPLayer) 
    {
        Node tempNode = result.getNode();
        if(tempNode.getState().getWinScore() == Integer.MIN_VALUE)
            return;
        while (tempNode != null) 
        {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getCurPlayer() == curPLayer)
             {
                tempNode.getState().addScore(winScore);           
            }
            tempNode = tempNode.getParent();
        }
    }
    private EndOutcome simulateRandomPlayout(Node node, PlayerID curPlayer) 
    {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
       

        PlayerID endGameVictor = tempState.checkStatus(curPlayer);      
        if (endGameVictor == opponent) 
        {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);

            return new EndOutcome(tempNode, endGameVictor);
        }
        GameState gs = tempState.getGs();                              
        List<Monster> newDeck = MontePythonAI.generateDeck(gs);
        gs.setDeck(newDeck);
        
        while (!isGameOver(gs) && endGameVictor == null)                    
        {
            gs = tempState.randomPlay(gs);
            Move m = gs.getLastMove();//tempState.getMove();
            tempState = new State(m, gs, gs.getCurPlayer());
            tempNode = new Node(tempState, tempNode, gs.getCurPlayer());       //not sure if this line works
            endGameVictor = tempState.checkStatus(curPlayer);
            newDeck = MontePythonAI.generateDeck(gs);
            gs.setDeck(newDeck);
        }
        System.out.print("Lookahead Winner" + endGameVictor);
        return new EndOutcome(tempNode, endGameVictor);
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
            static List<Move> getLegalPlaceMonster(GameState state){
                System.out.println("Get Legal Place Monster");
                List<Move> leg_moves = new ArrayList<Move>();
                RespondMove last_move = (RespondMove) state.getLastMove();
                Monster monst = last_move.getMonster();
                PlayerID play = null;
            
                
                if(last_move.isPass()){
                    play = state.getCurPlayer();
                }
                else{
                    play = otherPlayer( state.getCurPlayer() );
                }
            
            
                if( state.getCastleWon(CastleID.CastleA) == null){
                    leg_moves.add( new PlaceMonsterMove(play, CastleID.CastleA, monst) );
                }
                if( state.getCastleWon(CastleID.CastleB) == null){
                    leg_moves.add( new PlaceMonsterMove(play, CastleID.CastleB, monst) );
                }
                if( state.getCastleWon(CastleID.CastleC) == null){
                    leg_moves.add( new PlaceMonsterMove(play, CastleID.CastleC, monst) );
                }
            
                return leg_moves;
    }

}
