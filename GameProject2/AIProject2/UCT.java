package AIProject2;

import java.util.Random;

import javax.lang.model.util.ElementScanner14;

import ProjectTwoEngine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class UCT 
{
    static HashMap<Monster, Integer> fairValue;

    public UCT()
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
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) 
    {
        if (nodeVisit == 0) 
        {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit) 
        + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }
  
    public static Node findBestNodeWithUCT(Node node) 
    {
        int parentVisit = node.getState().getVisitCount();
        Move m = node.getState().getMove();
        List<Node> sortedChildren = new ArrayList<Node>();
        if(m == null || m instanceof PlaceMonsterMove) //root case: start on the buy phase
        {
            sortedChildren = SortBuyMove(node);
        }
        else if(m instanceof BuyMonsterMove)
        {
            sortedChildren = sortRespondMove(node);
        }
        else if (m instanceof RespondMove)
        {
            sortedChildren = sortPlaceMoves(node);
        }
        else
        {
            System.out.println("Problem in UCT Sort");
            sortedChildren = null;
        }
        if(sortedChildren.size() == 0)                      //crappy bandaid to cover up the bug on getHIdden returning null
        {                                                   //gives an unsorted list when sort breaks down
            sortedChildren = node.getChildArray();          //line 192, sortPLaceMoves isDragon Trap is the problem, don't know why
        }
        return Collections.max(sortedChildren,
                               Comparator.comparing(c -> uctValue(parentVisit, 
                                c.getState().getWinScore(), c.getState().getVisitCount())));
    }

    static List<Node> SortBuyMove(Node node)   //sorted greedily
    {
        List<Node> goodMoves = new ArrayList<Node>();
        List<Node> badMoves = new ArrayList<Node>();
        GameState state = node.getState().getGs();
        
        int coins = state.getCoins(state.getCurPlayer());
        List<Monster> available = state.getPublicMonsters();
        Random rand = new Random();
       
        Monster choice = available.get(0);
        int rnd = rand.nextInt(available.size());
        Monster second = available.get(rnd);        //put second and third choices as random
        rnd = rand.nextInt(available.size());
        Monster third = available.get(rnd);

        for(Monster m : available)
        {
            
            //buy the most expensive monster that can be afforded
            if(fairValue.get(m) > fairValue.get(choice) && fairValue.get(m) <= coins)
            {
                choice = m;
            }
        }
        if(choice.value == 6) //only prioritize if first choice is either dragon or gryphon, otherwise power priority is random
        {
            second = Monster.GRYPHON;
            third = Monster.GIANT;
      
        }
        for(Node n : node.getChildArray())
        {
            Move m = n.getState().getMove();
            BuyMonsterMove b = (BuyMonsterMove)m;
            if(b.getMonster().equals(choice))
            {
                if(fairValue.get(choice) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
                
            }
            else if(b.getMonster().equals(second))
            {
                if(fairValue.get(second) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
            }
            else if(b.getMonster().equals(third))
            {
                if(fairValue.get(third) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
            }
            else
                badMoves.add(n);
        }
        Collections.shuffle(goodMoves);
        Collections.shuffle(badMoves);  //shuffles up the bad moves as last priority move checks
        goodMoves.addAll(badMoves);
        
        //if that's not possible, buy with all remaining coins
        return goodMoves;

    }

    static List<Node> sortRespondMove(Node node)
    {
        GameState state = node.getState().getGs();
        
        int coins = state.getCoins(state.getCurPlayer()); 
        BuyMonsterMove m = (BuyMonsterMove)state.getLastMove();
        int price = m.getPrice();
        Monster mon = m.getMonster();

        List<Node> goodMoves = new ArrayList<Node>();
        List<Node> badMoves = new ArrayList<Node>();
        
        boolean willPass = true;
        //steal if opponent is buying for 70& of fair value or less
        if(price <= 0.6 * fairValue.get(mon) && coins >= price)
        {

           willPass = false;
        }
        for(Node n : node.getChildArray())
        {
            Move move = n.getState().getMove();
            RespondMove r = (RespondMove)move;
            if(r.isPass() == willPass)
                goodMoves.add(n);
            else 
                badMoves.add(n);
        }
        Collections.shuffle(badMoves);
        goodMoves.addAll(badMoves);
        return goodMoves;
    }

    static List<Node> sortPlaceMoves(Node node)
    {
        GameState state = node.getState().getGs();
        List<Node> goodMoves = new ArrayList<Node>();
        List<Node> badMoves = new ArrayList<Node>();
        
        CastleID bestCastle = null;
        int closestScore = 0;
     
        for(Node n : node.getChildArray())
        {
            Move m = n.getState().getMove();
            PlaceMonsterMove m2 = (PlaceMonsterMove)m;
            CastleID castle = m2.getCastle();
            Monster mon = m2.getMonster();
            
            if(bestCastle == null || (closestScore > Math.abs(castleScore(castle, state)) && (!mon.equals(Monster.DRAGON) || !isDragonTrap(castle, state))))
            {
                bestCastle = castle;
                closestScore = Math.abs(castleScore(castle, state));
            }
            if(m2.getCastle().equals(bestCastle))
            {
                goodMoves.add(n);
            }
            else
            {
                badMoves.add(n);
            }

        }
        Collections.shuffle(badMoves);
        goodMoves.addAll(badMoves);
        
        
    
        return goodMoves;
    }
    
    private static  int castleScore(CastleID cstle, GameState state)
    {
        int score = 0;
        
        int friendDragons = 0;
        int enemyDragons = 0;
        
        int friendDragonSlayers = 0;
        int enemyDragonSlayers = 0;
       if(state.getMonsters(cstle, state.getCurPlayer()) != null)
       {
        for (Monster m : state.getMonsters(cstle, state.getCurPlayer()))
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
        for (Monster m : state.getMonsters(cstle, MonteCarloTreeSearch.otherPlayer(state.getCurPlayer())))
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
      
        
        if(state.getHidden(state.getCurPlayer()) != null && state.getHidden(state.getCurPlayer()).equals(cstle))
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

    private static boolean isDragonTrap(CastleID castle, GameState state)
    {
        int dragonIndex = 0;
        PlayerID playID = state.getCurPlayer();
        for (Monster m : state.getMonsters(castle, playID))
        {
            if(m.equals(Monster.DRAGON))
            {
                dragonIndex++;
            }
        }
        for (Monster m : state.getMonsters(castle, MonteCarloTreeSearch.otherPlayer(playID)))
        {
            if(m.equals(Monster.SLAYER))
            {
                dragonIndex--;
            }
        }
        
        if(state.getHidden(playID) != null && state.getHidden(playID).equals(castle))
        {
            dragonIndex++;
        }
        
        return dragonIndex < 0;
    }
    
}
