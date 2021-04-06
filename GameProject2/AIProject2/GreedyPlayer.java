package AIProject2;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import ProjectTwoEngine.*;
public class GreedyPlayer implements Player
{
    
    PlayerID playID;
    PlayerID enemyID;

    
    GameState currentState;
    
    HashMap<Monster, Integer> fairValue;
    
    
    public GreedyPlayer()
    {
        fairValue = new HashMap<Monster, Integer>();
        
        fairValue.put(Monster.DRAGON, 6);
        fairValue.put(Monster.GRYPHON, 5);
        fairValue.put(Monster.GIANT, 4);
        fairValue.put(Monster.WARLION, 3);
        fairValue.put(Monster.WOLF, 2);
        fairValue.put(Monster.SLAYER, 4);
        fairValue.put(Monster.DEAD, 0);
    }

    
    public void begin(GameState init_state)
    {
        currentState = init_state;
        
        //Are we top or bot?
        if(currentState.getTopName().equals(getPlayName()))
        {
            playID = PlayerID.TOP;
            enemyID = PlayerID.BOT;
        }
        else
        {
            playID = PlayerID.BOT;
            enemyID = PlayerID.TOP;
        }
    }
    
    
    public BuyMonsterMove getBuyMonster(GameState state)
    {
        currentState = state;
        
        int coins = state.getCoins(playID);
        List<Monster> available = state.getPublicMonsters();
        
        Monster choice = available.get(0);
        for(Monster m : available)
        {
            
            //buy the most expensive monster that can be afforded
            if(fairValue.get(m) > fairValue.get(choice) && fairValue.get(m) <= coins)
            {
                choice = m;
            }
        }
        
        
        //buy for fair value
        if(fairValue.get(choice) <= coins)
        {
            return new BuyMonsterMove(playID, fairValue.get(choice), choice);
        }
        
        //if that's not possible, buy with all remaining coins
        return new BuyMonsterMove(playID, coins, choice);
    }
    
    
    public void startOppTurn(GameState state)
    {
        currentState = state;
    }
    
    
    public RespondMove getRespond(GameState state, Monster mon, int price)
    {
        currentState = state;
        
        int coins = state.getCoins(playID); 
        
        //steal if opponent is buying for half of fair value or less
        if(price <= 0.5 * fairValue.get(mon) && coins >= price)
        {
            return new RespondMove(playID, false, mon);
        }
        
        
        return new RespondMove(playID, true, mon);
    }
    
    
    public void stolenMonster(GameState state)
    {
        currentState = state;
    }
    
    
    public PlaceMonsterMove getPlace(GameState state, Monster mon)
    {
        currentState = state;
        
        CastleID bestCastle = null;
        int closestScore = 0;
        
        
        for(Move m : MonteCarloTreeSearch.getLegalPlaceMonster(state))
        {
            PlaceMonsterMove m2 = (PlaceMonsterMove)m;
            CastleID castle = m2.getCastle();
            
            if(bestCastle == null || (closestScore > Math.abs(castleScore(castle, state)) && (!mon.equals(Monster.DRAGON) || !isDragonTrap(castle))))
            {
                
                bestCastle = castle;
                closestScore = Math.abs(castleScore(castle, state));
               
            }
        }
        
        
    
        return new PlaceMonsterMove(playID, bestCastle, mon); 
    }
    
    
    
    public String getPlayName()
    {
        return "Greedy Player";
    }
    
    
    
    
    private int castleScore(CastleID cstle, GameState state)
    {
        int score = 0;
        
        int friendDragons = 0;
        int enemyDragons = 0;
        
        int friendDragonSlayers = 0;
        int enemyDragonSlayers = 0;
       if(state.getMonsters(cstle, playID) != null)
       {
        for (Monster m : state.getMonsters(cstle, playID))
        {
            score += m.value;
            
            if(m.equals(Monster.DRAGON))
            {
                friendDragons++;
            }
            if(m.equals(Monster.SLAYER))
            {
                friendDragonSlayers++;
            }
        }
        for (Monster m : currentState.getMonsters(cstle, enemyID))
        {
            score -= m.value;
            
            if(m.equals(Monster.DRAGON))
            {
                enemyDragons++;
            }
            if(m.equals(Monster.SLAYER))
            {
                enemyDragonSlayers++;
            }
        }
       } 
      
        
        if(state.getHidden(playID) != null && state.getHidden(playID).equals(cstle))
        {
            score += Monster.DRAGON.value;
            friendDragons++;
        }
        
        
        for(int i = 0; i < Math.min(friendDragons, enemyDragonSlayers); i++)
        {
            score -= 6;
        }
        for(int i = 0; i < Math.min(enemyDragons, friendDragonSlayers); i++)
        {
            score += 6;
        }
        
        return score;
    }
    
    private boolean isDragonTrap(CastleID castle)
    {
        int dragonIndex = 0;
        
        for (Monster m : currentState.getMonsters(castle, playID))
        {
            if(m.equals(Monster.DRAGON))
            {
                dragonIndex++;
            }
        }
        for (Monster m : currentState.getMonsters(castle, enemyID))
        {
            if(m.equals(Monster.SLAYER))
            {
                dragonIndex--;
            }
        }
        
        if(currentState.getHidden(playID).equals(castle))
        {
            dragonIndex++;
        }
        
        return dragonIndex < 0;
    }
}
