package AIProject2;

import java.util.Random;




import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import ProjectTwoEngine.*;
import java.util.List;


public class MontePythonAI implements Player 
{
    List<Monster> gameDeck;
    int[] monsterCounts;        //in order, this counts how many monsters are still in deck i
                                //Giants, Warlions, Wolf, Gryphon, Dragon, Slayer


    Random rand;
    MonteCarloTreeSearch montey = new MonteCarloTreeSearch();

    public MontePythonAI(){}

    //This function is called when the game starts
   public void begin(GameState init_state)
    {                                            //implement initialize Hidden Beliefs
	    rand = new Random();                    //probably no longer needed
        gameDeck = DeckFactory.createDeck();     // 5 giant, 5 warlion, 4 wolf, 4 gryphon, 2 dragon, 2 slayer


    }


    //This function is called when the player must select a monster to buy
    public BuyMonsterMove getBuyMonster(GameState state)
    {

	   return (BuyMonsterMove) montey.findNextMove(new GameState(state), state.getCurPlayer());
    }

    //This function is called at the start of your opponent's turn
    public void startOppTurn(GameState state){}

    //This function is called when your opponent tried to buy a monster
    //If you steal, you will get the chosen monster
    //... but hand your opponent the price in coins
    public RespondMove getRespond(GameState state, Monster mon, int price)
    {
        
        return (RespondMove) montey.findNextMove(new GameState(state), state.getCurPlayer());
    }
    
    
    //This function is called when the opponent pays the price to steal
    // ... the monster chosen by the player
    public void stolenMonster(GameState state){}

    //This function is called when the player successfully buys a monster
    //... and needs to place the monster at a castle
    public PlaceMonsterMove getPlace(GameState state, Monster mon)
    {
       
        return (PlaceMonsterMove) montey.findNextMove(new GameState(state), state.getCurPlayer());
    }

    public String getPlayName()
    {
	    return "MontePython Searcher";
    }

     
     public static List<Monster> generateDeck(GameState state)
     {
         List<Monster> outDeck = DeckFactory.createDeck();
 
         List<Monster> pubMons = state.getPublicMonsters();
         for (Monster monster : pubMons) 
         {
             outDeck.remove(monster);
         }
        
         for(CastleID cas : CastleID.values())
         {
             List<Monster> topMon = new ArrayList<Monster>(state.getMonsters(cas, PlayerID.TOP));
 
             List<Monster> botMon = new ArrayList<Monster>(state.getMonsters(cas, PlayerID.BOT));
 
             //if the castle is won, remove the extra dragon before counting monsters that came out of the deck
             if(state.getCastleWon(cas) != null) //not properly counting the player's 
             {
                 if(state.getHidden(PlayerID.TOP) == cas)
                 {
                     topMon.remove(Monster.DRAGON);
                 }
                 if(state.getHidden(PlayerID.BOT) == cas)
                 {
                     botMon.remove(Monster.DRAGON);
                 }
                 
             }
 
             for (Monster monster : topMon) 
             {
                 outDeck.remove(monster);   
             }
             for (Monster monster : botMon) 
             {
                 outDeck.remove(monster);    
             }      
         }
         System.out.println("After catle removal: " + outDeck.size());
         Collections.shuffle(outDeck);
         return outDeck;
     }
 
     public static int[] getInitialMonsterCounts()
     {
         int []monCounts = new int[6];
         //in order for the sake of readability
         int giantsAvailable = 5;
         int warlionsAvailable = 5;
         int wolfAvailable = 4;
         int gryphonAvailable = 4;
         int dragonAvailable = 2;
         int slayerAvailable = 2;
         int possibleExtraDragons = 2;
 
         monCounts[0] = giantsAvailable;
         monCounts[1] = warlionsAvailable;
         monCounts[2] = wolfAvailable;
         monCounts[3] = gryphonAvailable;
         monCounts[4] = dragonAvailable;
         monCounts[5] = slayerAvailable;
     //monCounts[6] = possibleExtraDragons;          //not sure about using this
 
         return monCounts;
     }
 
     //counts how many of each type of monster still in the deck
     //monster counts in order: Giant, Warlion, Wolf, Gryphon, Dragon, Slayer
     public static int[] getMonstersInDeckCounts(GameState state)
     {
         int[] outMonCount = new int[6];
 
         List<Monster> curDeck = generateDeck(state);
         for (Monster monster : curDeck) 
         {
             int index = getMonsterCountIndex(monster);
             outMonCount[index] += 1;     
         }
         return outMonCount;
     }
 
     public static int[] getMonstersInDeckCounts(List<Monster> curDeck)
     {
         int[] outMonCount = new int[6];
 
         for (Monster monster : curDeck) 
         {
             int index = getMonsterCountIndex(monster);
             outMonCount[index] += 1;     
         }
         return outMonCount;
     }
 
     //gets the correct index of the array to count monsters
     static int getMonsterCountIndex(Monster mon)
     {
         //Giant, Warlion, Wolf, Gryphon, Dragon, Slayer
         switch (mon.name)
         {
             case "Giant":
                 return 0;
             case "War Lion":
                 return 1;
             case "Wolf":
                 return 2;
             case "Gryphon":
                 return 3;
             case "Dragon":
                 return 4;
             case "Dragon Slayer":
                 return 5;
             default:
                 System.out.println("Invalid Monster Type");
                 return -1;
         }
     }
    
}
