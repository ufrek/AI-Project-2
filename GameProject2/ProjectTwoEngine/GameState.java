package ProjectTwoEngine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class GameState {
    Map<CastleID, List<Monster> > top_monsters;
    Map<CastleID, List<Monster> > bot_monsters;

    Map<CastleID, PlayerID> castle_won;

    //public monsters are currently available to buy
    //The next mosnster will be available next turn (it is on top of the deck)
    List<Monster> public_monsters;
    Monster next_monster;
    List<Monster> deck_monsters;
    
    int top_coins;
    int bot_coins;

    //This is where the hidden dragons will attack
    CastleID top_hidden;
    CastleID bot_hidden;

    boolean top_turn;
    boolean game_over;

    String top_name;
    String bot_name;

    Move last_move;
    
    static public GameState concedeState(GameState old, PlayerID play){
	GameState state = new GameState(old);

	if(play == PlayerID.TOP){
	    state.setCastleWon(CastleID.CastleA, PlayerID.BOT);
	    state.setCastleWon(CastleID.CastleB, PlayerID.BOT);
	    state.setCastleWon(CastleID.CastleC, PlayerID.BOT);
	}
	else{
	    state.setCastleWon(CastleID.CastleA, PlayerID.TOP);
	    state.setCastleWon(CastleID.CastleB, PlayerID.TOP);
	    state.setCastleWon(CastleID.CastleC, PlayerID.TOP);
	}
	state.game_over = true;
	
	return state;
    }

    //NOTE: Next Monster is the top Monster on the Deck
    //This monster will become a public monster next turn
    public Monster getNextMonster(){
	return next_monster;
    }

    public void setNextMonster(Monster mon){
	next_monster = mon;
    }
    
    //This returns the number of monsters left in the deck (INCLUDES next monster)
    public int getDeckSize(){
	return deck_monsters.size();
    }

    //This will return null if you don't have access to this information
    public List<Monster> getDeck(){
	return deck_monsters;
    }

    public void setDeck(List<Monster> d){
	deck_monsters = d;
    }

    public void addPublicMonster(Monster mon){
	public_monsters.add(mon);
    }

    public void removePublicMonster(Monster mon){
	public_monsters.remove(mon);
    }

    public List<Monster> getPublicMonsters(){
	return public_monsters;
    }
    
    //This will return null if you don't have access to this information
    public CastleID getHidden(PlayerID play){
	CastleID answer = null;

	if(play == PlayerID.TOP){
	    return top_hidden;
	}
	if(play == PlayerID.BOT){
	    return bot_hidden;
	}
	return answer;
    }

    public void setHidden(PlayerID play, CastleID cas){
	if(play == PlayerID.TOP){
	    top_hidden = cas;
	}
	if(play == PlayerID.BOT){
	    bot_hidden = cas;
	}
    }
	

    public List<Monster> getMonsters(CastleID cas, PlayerID play){
	ArrayList<Monster> mons = null;
	    
	if (play == PlayerID.TOP){
	    mons = new ArrayList<Monster>( top_monsters.get(cas) );
	}
	if (play == PlayerID.BOT){
	    mons = new ArrayList<Monster>( bot_monsters.get(cas) );
	}
	return mons;
    }

    void addMonster(CastleID cas, PlayerID play, Monster mon){
	if (play == PlayerID.TOP){
	    top_monsters.get(cas).add(mon);
	}
	if (play == PlayerID.BOT){
	    bot_monsters.get(cas).add(mon);
	}
    }

    void removeMonster(CastleID cas, PlayerID play, Monster mon){
	if (play == PlayerID.TOP){
	    top_monsters.get(cas).remove(mon);
	}
	if (play == PlayerID.BOT){
	    bot_monsters.get(cas).remove(mon);
	}
    }
    
    public PlayerID getCastleWon(CastleID cas){
       
	return castle_won.get(cas);
    }

    void setCastleWon(CastleID cas, PlayerID play){
	castle_won.put(cas, play);
    }
    
    public int getCoins(PlayerID play_id){
	int answer = -1;
	
	if (play_id == PlayerID.TOP){
	    answer = top_coins;
	}
	if (play_id == PlayerID.BOT){
	    answer = bot_coins;
	}
	return answer;
    }

    void setCoins(PlayerID play_id, int c){
	if (play_id == PlayerID.TOP){
	    top_coins = c;
	}
	if (play_id == PlayerID.BOT){
	    bot_coins = c;
	}
    }

    void addCoins(PlayerID play_id, int extra_coins){
	if (play_id == PlayerID.TOP){
	    top_coins = top_coins + extra_coins;
	}
	if (play_id == PlayerID.BOT){
	    bot_coins = bot_coins + extra_coins;
	}
    }
    
    public String getTopName(){
	return top_name;
    }

    public String getBotName(){
	return bot_name;
    }

    public String getPlayName(PlayerID play){
	if( play == PlayerID.TOP ){
	    return top_name;
	}
	else{
	    return bot_name;
	}
    }

    public boolean isTopTurn(){
	return top_turn;
    }

    public boolean isGameOver(){
	return game_over;
    }

    void makeGameOver(){
	game_over = true;
    }

    public PlayerID getCurPlayer(){
	if (top_turn){
	    return PlayerID.TOP;
	}
	else{
	    return PlayerID.BOT;
	}
    }

    void setCurPlayer(PlayerID play){
	if (play == PlayerID.TOP){
	    top_turn = true;
	}
	else{
	    top_turn = false;
	}
    }

    public Move getLastMove(){
	return last_move;
    }

    public void setLastMove(Move m){
	last_move = m;
    }
    
    public GameState(){
	this("Human Player", "Human Player");
    }

    public GameState(String nameT, String nameB){
	top_monsters = new HashMap<CastleID, List<Monster> >();
	bot_monsters = new HashMap<CastleID, List<Monster> >();

	top_monsters.put(CastleID.CastleA, new ArrayList<Monster>() );
	top_monsters.put(CastleID.CastleB, new ArrayList<Monster>() );
	top_monsters.put(CastleID.CastleC, new ArrayList<Monster>() );
	bot_monsters.put(CastleID.CastleA, new ArrayList<Monster>() );
	bot_monsters.put(CastleID.CastleB, new ArrayList<Monster>() );
	bot_monsters.put(CastleID.CastleC, new ArrayList<Monster>() );	

	castle_won = new HashMap<CastleID, PlayerID>();
	castle_won.put(CastleID.CastleA, null);
	castle_won.put(CastleID.CastleB, null);
	castle_won.put(CastleID.CastleC, null);
	
	top_name = nameT;
	bot_name = nameB;

	top_coins = 4;
	bot_coins = 6;

	//Select random castles for the hidden dragons
	Random rand = new Random();
	List<CastleID> castles = new ArrayList<CastleID>();
	castles.add(CastleID.CastleA);
	castles.add(CastleID.CastleB);
	castles.add(CastleID.CastleC);
	top_hidden = castles.get( rand.nextInt(3) );
	bot_hidden = castles.get( rand.nextInt(3) );

	top_turn = false;
	game_over = false;

	deck_monsters = DeckFactory.createDeck();
	public_monsters = new ArrayList<Monster>();
	for(int i=0; i<4; i++){
	    public_monsters.add(deck_monsters.remove(0));
	}
	next_monster = deck_monsters.remove(0);

	last_move = null;
	
    }
    

    // Deep Copy Constructor
    public GameState(GameState state){
	top_monsters = new HashMap<CastleID, List<Monster> >();
	bot_monsters = new HashMap<CastleID, List<Monster> >();

	List<Monster> temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleA, PlayerID.TOP));
	top_monsters.put(CastleID.CastleA, temp_mon );
	
	temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleB, PlayerID.TOP));
	top_monsters.put(CastleID.CastleB, temp_mon );

	temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleC, PlayerID.TOP));
	top_monsters.put(CastleID.CastleC, temp_mon );
	
	temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleA, PlayerID.BOT));
        bot_monsters.put(CastleID.CastleA, temp_mon );
	
	temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleB, PlayerID.BOT));
	bot_monsters.put(CastleID.CastleB, temp_mon );

	temp_mon = new ArrayList<Monster>(state.getMonsters(CastleID.CastleC, PlayerID.BOT));
	bot_monsters.put(CastleID.CastleC, temp_mon );

	castle_won = new HashMap<CastleID, PlayerID>();
	castle_won.put(CastleID.CastleA, state.getCastleWon(CastleID.CastleA) );
	castle_won.put(CastleID.CastleB, state.getCastleWon(CastleID.CastleB));
	castle_won.put(CastleID.CastleC, state.getCastleWon(CastleID.CastleC));
	
	top_name = state.getPlayName(PlayerID.TOP);
	bot_name = state.getPlayName(PlayerID.BOT);

	top_coins = state.getCoins(PlayerID.TOP);
	bot_coins = state.getCoins(PlayerID.BOT);
	top_hidden = state.getHidden(PlayerID.TOP);
	bot_hidden = state.getHidden(PlayerID.BOT);

        top_turn = state.isTopTurn();
	game_over = state.isGameOver();

	public_monsters = new ArrayList<Monster>(state.getPublicMonsters());
	next_monster = state.getNextMonster();
	deck_monsters = state.getDeck();

	last_move = state.getLastMove();
    }

    // Copies a state and removes hidden information
    // This is used before handing a state to a player
    public GameState(GameState old_state, PlayerID play){
	this(old_state);

	if(play == PlayerID.TOP){
	    deck_monsters = null;

	    CastleID opp_hide = old_state.getHidden(PlayerID.BOT);
	    if (old_state.getCastleWon( opp_hide) == null){ 
		bot_hidden = null;
	    }
	    else{
		bot_hidden = opp_hide;
	    }
	}
	if(play == PlayerID.BOT){
	    deck_monsters = null;

	    CastleID opp_hide = old_state.getHidden(PlayerID.TOP);
	    if (old_state.getCastleWon( opp_hide) == null){ 
		bot_hidden = null;
	    }
	    else{
		bot_hidden = opp_hide;
	    }
	}
    }
				
}
