package AIProject2;

import ProjectTwoEngine.*;

import java.security.interfaces.RSAPrivateCrtKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.lang.model.util.ElementScanner14;

public class State
{
    Move lastMove;
    GameState gs;
    int visitCount = 0;
    PlayerID curPlayer;
    double winScore;
    Random rand;

    static GreedyPlayer greedPlayer;
    public State(Move m, GameState gs, PlayerID curPlayer)   //visitCount
    {
        this.lastMove = m;
        this.gs = new GameState(gs);
        this.curPlayer = curPlayer;
        rand = new Random();

       if(greedPlayer == null)
       {
           greedPlayer = new GreedyPlayer();
       }
    }

    public State(State s)
    {
        this.lastMove = s.getMove();
        this.gs = s.getGs();
        this.curPlayer = s.getCurPlayer();
        rand = new Random();

        if(greedPlayer == null)
       {
           greedPlayer = new GreedyPlayer();
       }

    }

    public void incrementVisit()
    {
        visitCount += 1;
    }

//-----------------------------Getting all possible states for each phase, Buy, Respond, and Place
//these all need to pass in parameters by copy or it eats the deck
    public List<State> getAllPossibleBuySuccessors(GameState gs)                
    {
        List<State> successorStates = new ArrayList<State>();
        GameState gsCopy = new GameState(gs);
        //System.out.println("Stuck");
        List<Move> buyMoves = GameRules.getLegalMoves(gsCopy);
        
        for (Move buyMove : buyMoves) 
        {
        
            GameState buyPhase = GameRules.makeMove(gsCopy, (BuyMonsterMove)buyMove);
            State state = new State(buyMove, buyPhase, buyPhase.getCurPlayer());
            successorStates.add(state);
        }
          
        return successorStates;
    }

    public List<State> getAllPossibleRespondSuccessors(GameState gs)
    {
        List<State> successorStates = new ArrayList<State>();
        GameState gsCopy = new GameState(gs);
        List<Move> respondMoves = GameRules.getLegalMoves(gs);
        System.out.println((gs.getDeckSize()));//////////////////////////////////////////Not Working.....not sure why
        for(Move respondMove : respondMoves)
        {
            GameState respondPhase = GameRules.makeMove(gs, (RespondMove)respondMove);
            
            State state = new State(respondMove, respondPhase, respondPhase.getCurPlayer());
            successorStates.add(state);
        }
        
        return successorStates;
    }

    public  List<State> getAllPossiblePlaceSuccessors(GameState gs)
    {
        List<State> successorStates = new ArrayList<State>();
        GameState gsCopy = new GameState(gs);
        List<Move> placeMoves = GameRules.getLegalMoves(gsCopy);

        for(Move placeMove : placeMoves)
        {
            
            GameState placePhase = GameRules.makeMove(gsCopy, (PlaceMonsterMove)placeMove);
            State state = new State(placeMove, placePhase, placePhase.getCurPlayer());
            successorStates.add(state);
        }
        
        return successorStates;
    }
    
//----------------Random Play Functions----------------------------------------
    public GameState randomPlay(GameState state)
    {
        Move m = state.getLastMove();
        if(m == null || m instanceof PlaceMonsterMove) //root case: start on the buy phase
        {
            return RandomBuyMonster(state);
        }
        else if(m instanceof BuyMonsterMove)
        {
            return RandomResponse(state);
        }
        else if (m instanceof RespondMove)
        {
            return RandomPlaceMonster(state);
        }
        else
        {
            System.out.println("Problem in Random Play Method");
            return null;
        }
           
    }

    public GameState  RandomBuyMonster(GameState state)
    {
        System.out.println(state.getPublicMonsters().size());
        List<Move> leg_moves = GameRules.getLegalMoves(state);
      
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (BuyMonsterMove) leg_moves.get(i));
        
     
         //return GameRules.makeMove(state, greedPlayer.getBuyMonster(state));
    }
    
    public GameState  RandomResponse(GameState state)
    {
       List<Move> leg_moves = GameRules.getLegalMoves(state);
        
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (RespondMove) leg_moves.get(i));
        //BuyMonsterMove m = (BuyMonsterMove)state.getLastMove();
        //Monster mon = m.getMonster();
        //int price = m.getPrice();
         //return GameRules.makeMove(state, greedPlayer.getRespond(state, mon, price));
    }

    public GameState  RandomPlaceMonster(GameState state)
    {
        List<Move> leg_moves = GameRules.getLegalMoves(state);
        
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (PlaceMonsterMove) leg_moves.get(i));

        // RespondMove m = (RespondMove)state.getLastMove();
         //Monster mon = m.getMonster();
         //return GameRules.makeMove(state, greedPlayer.getPlace(state, mon));
    }

    public Move getMove() 
    {
        return lastMove;
    }
     
    public int getVisitCount()
    {
        return visitCount;
    }

    public double getWinScore() 
    {
        return winScore;
    }

    public PlayerID getCurPlayer() 
    {
        return curPlayer;
    }

    public GameState getGs()
    {
        return gs;
    }

    //returns null if game is still playing, otherwise returns winner/draw
    public PlayerID checkStatus(PlayerID curPlayer)
    {
        if(MonteCarloTreeSearch.isGameOver(this.gs))
        {
            int curPlayerCastles = 0;
            int oppPlayerCastles = 0;
            for(CastleID cas : CastleID.values())
            {
                if(this.gs.getCastleWon(cas) == curPlayer)
                    curPlayerCastles += 1;
                else
                    oppPlayerCastles += 1;
            }   

            if(curPlayerCastles > oppPlayerCastles)
                return curPlayer;
            else if(oppPlayerCastles > curPlayerCastles)
                return MonteCarloTreeSearch.otherPlayer(curPlayer);
            else
                return MonteCarloTreeSearch.otherPlayer(curPlayer);  //draws are a loss
        }
        else
            return null;

        
      
    }

    public void setWinScore(int value) 
    {
        this.winScore = value;
    }

    public void addScore(int amount)
    {
        this.winScore += amount;
    }
}
    
