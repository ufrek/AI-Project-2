import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
        
        //choose the castle with the closest score that isn't already full
        if(GameRules.isLegalPlaceMonster(state, new PlaceMonsterMove(playID, CastleID.CastleA, mon)))
        {
            closestScore = Math.abs(castleScore(CastleID.CastleA));
            bestCastle = CastleID.CastleA;
        }
        if(GameRules.isLegalPlaceMonster(state, new PlaceMonsterMove(playID, CastleID.CastleB, mon)))
        {
            if(bestCastle == null || closestScore > Math.abs(castleScore(CastleID.CastleB)))
            {
                closestScore = Math.abs(castleScore(CastleID.CastleB));
                bestCastle = CastleID.CastleB;
            }
        }
        if(GameRules.isLegalPlaceMonster(state, new PlaceMonsterMove(playID, CastleID.CastleC, mon)))
        {
            if(bestCastle == null || closestScore > Math.abs(castleScore(CastleID.CastleC)))
            {
                bestCastle = CastleID.CastleC;
            }
        }
    
        return new PlaceMonsterMove(playID, bestCastle, mon); 
    }
    
    
    
    public String getPlayName()
    {
        return "Greedy Player";
    }
    
    
    
    
    private int castleScore(CastleID cstle)
    {
        int score = 0;
        
        int friendDragons = 0;
        int enemyDragons = 0;
        
        int friendDragonSlayers = 0;
        int enemyDragonSlayers = 0;
        
        for (Monster m : currentState.getMonsters(cstle, playID))
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
        
        if(currentState.getHidden(playID).equals(cstle))
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
}