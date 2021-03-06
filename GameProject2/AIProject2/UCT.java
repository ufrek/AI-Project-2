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
    static List<Float> beliefs;
    static boolean isDragonAlreadyRevealed = false;
    static float beliefThreshold = .75f;
    public UCT()
    {
        beliefs = null;
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
  
    public static Node findBestNodeWithUCT(Node node, boolean smart) 
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
        if(sortedChildren.size() == 0 || !smart)                      //crappy bandaid to cover up the bug on getHIdden returning null
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
        int oppCoins = state.getCoins(MonteCarloTreeSearch.otherPlayer(state.getCurPlayer()));
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

        //if we strongly believe a dragon is at a ceretain spot, prioritize buying a slayer
        if(state.getHidden(state.getCurPlayer()) == null && beliefs  != null)
        {
            for(Float f : beliefs)
            {
                if(f > beliefThreshold && available.contains(Monster.SLAYER)) 
                {
                    if(fairValue.get(Monster.SLAYER) <= coins)
                    {
                        second = choice;
                        choice = Monster.SLAYER;
                        
                    }
                   
                }
            }
        }
        //if we think a monster
        if(choice.value == 6) //only prioritize if first choice is either dragon or gryphon, otherwise power priority is random
        {
            second = Monster.GRYPHON;
            third = Monster.GIANT;
      
        }
        boolean hasBestMove = false;
        for(Node n : node.getChildArray())   //could add more logic to check castles out. The mind is willing but clock is not.
        {
            Move m = n.getState().getMove();
            BuyMonsterMove b = (BuyMonsterMove)m;
            if(b.getMonster().equals(choice))
            {
                if(!hasBestMove && oppCoins == 0 && b.getPrice() == 1) //buys for when when opponent can't steal
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                else if(!hasBestMove && coins > oppCoins && choice.value >= 3 && b.getPrice() == oppCoins + 1 )   //if have more coins, prioritize outbidding
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                else if(fairValue.get(choice) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
                
            }
            else if(b.getMonster().equals(second))
            {
                if(!hasBestMove && oppCoins == 0 && b.getPrice() == 1) //buys for 1 when when opponent can't steal
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                 //if have more coins, prioritize outbidding giants and up
                else if(!hasBestMove && coins > oppCoins && second.value >= 3 && b.getPrice() == oppCoins + 1 )  
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                else if(fairValue.get(second) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
            }
            else if(b.getMonster().equals(third))
            {
                if(!hasBestMove && oppCoins == 0 && b.getPrice() == 1) //buys for 1 when when opponent can't steal
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                 //if have more coins, prioritize outbidding giants and up
                else if(!hasBestMove && coins > oppCoins && third.value >= 3 && b.getPrice() == oppCoins + 1 )  
                {
                    goodMoves.add(0,n);
                    hasBestMove = true;
                }
                else if(fairValue.get(third) <= coins)
                {
                    goodMoves.add(n);             //add more expensive moves to end
                }
            }
            else
                badMoves.add(n);
        }
        //Collections.shuffle(goodMoves);
        Collections.shuffle(badMoves);  //shuffles up the bad moves as last priority move checks
        goodMoves.addAll(badMoves);
        
        
        return goodMoves;

    }

    static List<Node> sortRespondMove(Node node)    //could implement a closeness metric for castles to see if it's worth stealing...TIME....
    {
        GameState state = node.getState().getGs();
        
        int coins = state.getCoins(state.getCurPlayer()); 
        int oppCoins = state.getCoins(MonteCarloTreeSearch.otherPlayer(state.getCurPlayer()));
        BuyMonsterMove m = (BuyMonsterMove)state.getLastMove();
        int price = m.getPrice();
        Monster mon = m.getMonster();

        List<Node> goodMoves = new ArrayList<Node>();
        List<Node> badMoves = new ArrayList<Node>();
        
        boolean willPass = true;
        //steal if opponent is buying for 70& of fair value or less
        if(price <= 0.65 * fairValue.get(mon) && coins >= price)
        {

           willPass = false;
        }
     
        //prioritizes stealing potentially troublesome monsters, makes sure you still have some coins left over
        // and don't give too many to the opponent
        if((mon.value >= 3) && coins >= price + 2 && oppCoins + price < 10)
        {
            willPass = false;
        }

        if(price < coins * .7 && mon.value >= 3)    
        {
            willPass = false;
        }

        //if beliefs of Dragon location are strong enough, steal a slayer
        if(state.getHidden(state.getCurPlayer()) == null &&  m.getMonster() == Monster.SLAYER && beliefs != null)
        {
            for(Float f : beliefs)
            {
                if(f > beliefThreshold) 
                {
                    if(price <= 2 * fairValue.get(mon) && coins >= price)
                    {

                         willPass = false;
                    }
                    
                }
            }
            
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
        float closestScore = 0;
     
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
    
    private static  float castleScore(CastleID cstle, GameState state)
    {
        int score = 0;
        
        int friendDragons = 0;
        int enemyDragons = 0;
       
        int friendDragonSlayers = 0;
        int enemyDragonSlayers = 0;
        
       

        //beliefEvaluation code
        int dragonScore = 6;
        BeliefEvaluation b = new BeliefEvaluation();
        if(beliefs == null)
        {
            List<Monster> badMons = new ArrayList<Monster>();
            for(CastleID cas : CastleID.values())
            {
                List<Monster> temp = state.getMonsters(cas, MonteCarloTreeSearch.otherPlayer(state.getCurPlayer()));
                badMons.addAll(temp);
            }
           
            ArrayList<Integer> monValues = new ArrayList<Integer>();
            for(Monster m : badMons)
                monValues.add(m.value);
            beliefs = b.makeEvaluation(monValues); 
            int curCas = cstle.ordinal();
            if(curCas == 0)
                    score -= dragonScore * beliefs.get(0); 
            else if(curCas == 1)
                    score -= dragonScore * beliefs.get(1);
            else if(curCas == 2)
                    score -= dragonScore * beliefs.get(2);
            else
                    System.out.println("Belif eval is messed up when initialiizing belliefs");
            
            
        }

        else
        {
            //if we don't know the location, use beliefevaluation
            if(state.getHidden(state.getCurPlayer()) == null)   
                {
                   
                    List<Monster> badMons = new ArrayList<Monster>();
                    for(CastleID cas : CastleID.values())
                    {
                        List<Monster> temp = state.getMonsters(cas, MonteCarloTreeSearch.otherPlayer(state.getCurPlayer()));
                        badMons.addAll(temp);
                    }
                    
                    ArrayList<Integer> monValues = new ArrayList<Integer>();
                    for(Monster m : badMons)
                        monValues.add(m.value);
                    beliefs = b.ReevaluateBelief(beliefs, state, state.getCurPlayer());

                    //checks if we have a slayer and place it where we think the hidden dragon is
                    Move move = state.getLastMove();
                    RespondMove r = (RespondMove)move;
                    if(r.getMonster() == Monster.SLAYER)
                    {
                        float maxBelief = 0;
                        int castleIndex = 0;
                        for(float f : beliefs)
                        {
                            if(f > maxBelief)
                            {
                                castleIndex = beliefs.indexOf(f);
                                maxBelief = f;
                            }
                        }

                        CastleID maxCas = CastleID.CastleA;
                        switch (castleIndex)
                         {
                            case 0:
                                maxCas = CastleID.CastleA;
                                break;
                            case 1:
                                maxCas = CastleID.CastleB;
                                break;
                            case 2:
                                maxCas = CastleID.CastleC;
                                break;

                            default:
                                System.out.println("Didn't find a max Castle Index");
                                break;
                        }

                        if(cstle == maxCas)
                            return 0;
                    }
                    
                    int curCastle = cstle.ordinal();
                   if(curCastle == 0)
                            score -= dragonScore * beliefs.get(0); 
                    else if(curCastle == 1)
                            score -= dragonScore * beliefs.get(1);
                            
                    else if(curCastle == 2)
                            score -= dragonScore * beliefs.get(2);
                    else
                            System.out.println("Belief evaluation is messed up. Line 402");
                           
                    
                }
            //we do know the location
            else
            {  
                if(!isDragonAlreadyRevealed)
                {
                    beliefs.clear();
                    CastleID dragLocation = state.getHidden(state.getCurPlayer());
                    int dragLoc = dragLocation.ordinal();
                    if(dragLoc == 0)
                    {
                        beliefs.add(0, 1f);
                        beliefs.add(1, 0f);
                        beliefs.add(2,0f);
                    }
                   else if(dragLoc == 1)
                   {
                    beliefs.add(0, 0f);
                    beliefs.add(1, 1f);
                    beliefs.add(2,0f);
                   }
                    else if (dragLoc == 2)   
                     {
                        beliefs.add(0, 0f);
                        beliefs.add(1, 0f);
                        beliefs.add(2,1f);
                     }
                    else
                        System.out.println("dragon Location broken");

                    isDragonAlreadyRevealed = true;
                }
                int curCastle = cstle.ordinal();
                System.out.println(curCastle);
               if(curCastle == 0)
                        score -= dragonScore * beliefs.get(0); 
              else if(curCastle == 1)
                        score -= dragonScore * beliefs.get(1);
                else if(curCastle == 2)
                        score -= dragonScore * beliefs.get(2);
                else
                    System.out.println("Belief evaluation is messed up Line 446");
              
            }
        }

        //back to greedy logic
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
