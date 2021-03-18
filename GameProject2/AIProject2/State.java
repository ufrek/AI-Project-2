package GameProject2.AIProject2;

import ProjectTwoEngine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class State
{
    Move lastMove;
    GameState gs;
    int visitCount = 0;
    PlayerID curPlayer;
    double winScore;
    Random rand;

    public State(Move m, GameState gs, PlayerID curPlayer)   //visitCount
    {
        this.lastMove = m;
        this.gs = gs;
        this.curPlayer = curPlayer;
        rand = new Random();
    }

    public void incrementVisit()
    {
        visitCount += 1;
    }

//-----------------------------Getting all possible states for each phase, Buy, Respond, and Place
    public List<State> getAllPossibleBuySuccessors(GameState gs)
    {
        List<State> successorStates = new ArrayList<State>();

        GameState gsCopy = new GameState(gs);
        List<Move> buyMoves = GameRules.getLegalMoves(gsCopy);
        
        for (Move buyMove : buyMoves) 
        {
        
            GameState buyPhase = GameRules.makeMove(gsCopy, (BuyMonsterMove)buyMove);
            State state = new State(buyMove, buyPhase, curPlayer);
            successorStates.add(state);
        }
          
        return successorStates;
    }

    public List<State> getAllPossibleRespondSuccessors(GameState gs)
    {
        List<State> successorStates = new ArrayList<State>();
        GameState gsCopy = new GameState(gs);
        List<Move> respondMoves = GameRules.getLegalMoves(gsCopy);

        for(Move respondMove : respondMoves)
        {
            GameState respondPhase = GameRules.makeMove(gsCopy, (RespondMove)respondMove);
            State state = new State(respondMove, respondPhase, curPlayer);
            successorStates.add(state);
        }
        
        return successorStates;
    }

    public List<State> getAllPossiblePlaceSuccessors(GameState gs)
    {
        List<State> successorStates = new ArrayList<State>();
        GameState gsCopy = new GameState(gs);
        List<Move> placeMoves = GameRules.getLegalMoves(gsCopy);

        for(Move placeMove : placeMoves)
        {
            
            GameState placePhase = GameRules.makeMove(gsCopy, (PlaceMonsterMove)placeMove);
            State state = new State(placeMove, placePhase, GameRules.otherPlayer(curPlayer));
            successorStates.add(state);
        }
        
        return successorStates;
    }
    
//----------------Random Play Functions----------------------------------------
    public GameState  RandomBuyMonster(GameState state)
    {
        List<Move> leg_moves = GameRules.getLegalMoves(state);
        
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (BuyMonsterMove) leg_moves.get(i));
    }
    
    public GameState  RandomResponse(GameState state)
    {
        List<Move> leg_moves = GameRules.getLegalMoves(state);
        
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (RespondMove) leg_moves.get(i));
    }

    public GameState  RandomPlaceMonster(GameState state)
    {
        List<Move> leg_moves = GameRules.getLegalMoves(state);
        
        int i = rand.nextInt(leg_moves.size());
         return GameRules.makeMove(state, (PlaceMonsterMove) leg_moves.get(i));
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
}
    
