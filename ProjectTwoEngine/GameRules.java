package ProjectTwoEngine;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameRules{

    static PlayerID otherPlayer(PlayerID p){
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

    public static GameState makeMove(GameState state, Move mv){
	GameState new_state = null;

	if (mv instanceof BuyMonsterMove){
	    if ( isLegalMove(state, mv) ){
		new_state = makeMoveBuyMonster(state, (BuyMonsterMove) mv);
	    }
	    else{
		new_state = GameState.concedeState(state, state.getCurPlayer());
	    }
	}
	if (mv instanceof RespondMove){
	    if ( isLegalMove(state, mv) ){
		new_state = makeMoveRespond(state, (RespondMove) mv);
	    }
	    else{
		PlayerID o_player = otherPlayer(state.getCurPlayer());
		new_state = GameState.concedeState(state, o_player);
	    }
	}
	if (mv  instanceof PlaceMonsterMove){
	    if ( isLegalMove(state, mv) ){
		new_state = makeMovePlaceMonster(state, (PlaceMonsterMove) mv);
	    }
	    else{
		List<Move> leg_mv = getLegalMoves(state);
		PlayerID play = leg_mv.get(0).getPlayer();
		new_state = GameState.concedeState(state, play);
	    }
	}
	return new_state;
    }
    
    public static List<Move> getLegalMoves(GameState state){
	List<Move> leg_moves = null;
	Move last_move = state.getLastMove();

	if (last_move == null){
	    leg_moves = getLegalBuyMonster(state);
	}
	else if (last_move instanceof PlaceMonsterMove){
	    leg_moves = getLegalBuyMonster(state);
	}
	else if (last_move instanceof RespondMove){
	    leg_moves = getLegalPlaceMonster(state);
	}
	else if (last_move  instanceof BuyMonsterMove){
	    leg_moves = getLegalRespond(state);
	}
	return leg_moves;
    }
	
    
    public static boolean isLegalMove(GameState state, Move mv){
	boolean answer = false;
	if (mv instanceof BuyMonsterMove){
	    answer = isLegalBuyMonster(state, (BuyMonsterMove) mv);
	}
	if (mv instanceof PlaceMonsterMove){
	    answer = isLegalPlaceMonster(state, (PlaceMonsterMove) mv);
	}
	if (mv instanceof RespondMove){
	    answer = isLegalRespond(state, (RespondMove) mv);
	}
	return answer;
    }

    static boolean isLegalBuyMonster(GameState state, BuyMonsterMove mv){
	Move last_move = state.getLastMove();
	boolean answer = false;
	
	if( last_move == null || last_move instanceof PlaceMonsterMove ){
	    List<Move> legal = getLegalMoves(state);
	    for( Move leg_mov : legal ){
		if (leg_mov instanceof BuyMonsterMove){
		    BuyMonsterMove new_leg_mv = (BuyMonsterMove) leg_mov;
		    if(new_leg_mv.getPlayer() == mv.getPlayer()){
			if(new_leg_mv.getPrice() == mv.getPrice()){
			    if(new_leg_mv.getMonster() == mv.getMonster()){
				answer = true;
			    }
			}
		    }
		}
	    }
	}
	return answer;
    }

    static boolean isLegalPlaceMonster(GameState state, PlaceMonsterMove mv){
	Move last_move = state.getLastMove();
	boolean answer = false;
	
	if( last_move instanceof RespondMove ){
	    List<Move> legal = getLegalMoves(state);
	    for( Move leg_mov : legal ){
		if (leg_mov instanceof PlaceMonsterMove){
		    PlaceMonsterMove new_leg_mv = (PlaceMonsterMove) leg_mov;
		    if(new_leg_mv.getPlayer() == mv.getPlayer()){
			if(new_leg_mv.getCastle() == mv.getCastle()){
			    if(new_leg_mv.getMonster() == mv.getMonster()){
				answer = true;
			    }
			}
		    }
		}
	    }
	}
	return answer;
    }

    static boolean isLegalRespond(GameState state, RespondMove mv){
	Move last_move = state.getLastMove();
	boolean answer = false;
	
	if( last_move instanceof BuyMonsterMove ){
	    List<Move> legal = getLegalMoves(state);
	    for( Move leg_mov : legal ){
		if (leg_mov instanceof RespondMove){
		    RespondMove new_leg_mv = (RespondMove) leg_mov;
		    if(new_leg_mv.getPlayer() == mv.getPlayer()){
			if(new_leg_mv.isPass() == mv.isPass()){
			    if(new_leg_mv.getMonster() == mv.getMonster()){
				answer = true;
			    }
			}
		    }
		}
	    }
	}
	return answer;
    }
	    
    //Note: You can choose any public monster
    // Price must be less than your coins
    static List<Move> getLegalBuyMonster(GameState state){
	System.out.println("Get Legal Buy Monster");
	List<Move> leg_moves = new ArrayList<Move>();
	PlayerID play = state.getCurPlayer();
	List<Monster> pub_mons = state.getPublicMonsters();
	int coins = state.getCoins(play);

	for(Monster mon : pub_mons){
	    for(int i = 1; i<= coins; i++){
		leg_moves.add( new BuyMonsterMove(play, i, mon) );
	    }
	}
	return leg_moves;
	
    }

    //Note: You can only steal if you have enough coins to pay
    static List<Move> getLegalRespond(GameState state){
	System.out.println("Get Legal Respond");
	List<Move> leg_moves = new ArrayList<Move>();
	BuyMonsterMove last_move = (BuyMonsterMove) state.getLastMove();
	PlayerID play = otherPlayer(state.getCurPlayer());
	Monster monst = last_move.getMonster();
	
	leg_moves.add( new RespondMove(play, true, monst) );

	int price = last_move.getPrice();
	int coins = state.getCoins(play);
	if(coins >= price){
	    leg_moves.add( new RespondMove(play, false, monst) );
	}
	return leg_moves;
    }

    //Note: You can only place a monster at a castle that hasn't been won yet
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

    //Attempting to buy a monster doesn't really change anything
    static GameState makeMoveBuyMonster(GameState state, BuyMonsterMove mv){
	GameState new_state = new GameState(state);
	new_state.setLastMove(mv);

	return new_state;
    }

    //This just adjusts the coins
    static GameState makeMoveRespond(GameState state, RespondMove mv){
	GameState new_state = new GameState(state);
	PlayerID cur_play = state.getCurPlayer();
	BuyMonsterMove last_move = (BuyMonsterMove) state.getLastMove();

	int price = last_move.getPrice();
	
	new_state.setLastMove(mv);

	if(mv.isPass()){
	    int coins = state.getCoins(cur_play);
	    new_state.setCoins( cur_play, coins - price);
	}
	else {
	    int cur_coins = state.getCoins(cur_play);
	    int opp_coins = state.getCoins( otherPlayer(cur_play) );
	    new_state.setCoins( cur_play, cur_coins + price);
	    new_state.setCoins( otherPlayer(cur_play) ,  opp_coins - price);
	}

	return new_state;
    }

    //This places the monster
    // ... Does a battle if needed
    // ... And then starts the next player's turn
    static GameState makeMovePlaceMonster(GameState state, PlaceMonsterMove mv){
	GameState new_state = new GameState(state);
	Monster mon = mv.getMonster();
	CastleID cas = mv.getCastle();
	    
	PlayerID cur_play = state.getCurPlayer();
	PlayerID placing_play = mv.getPlayer();
	
	new_state.setLastMove(mv);

	//First we remove the monster from the public monsters
	new_state.removePublicMonster(mon);
	if (state.getNextMonster() != null){
	    new_state.addPublicMonster( new_state.getNextMonster() );
	}
	if (new_state.getDeck().size() != 0){
	    List<Monster> deck = new_state.getDeck();
	    Monster next_mon = deck.remove(0);
	    new_state.setDeck(deck);
	    new_state.setNextMonster(next_mon);
	}

	//Next we place the monster and do a battle if needed
	new_state.addMonster(cas, placing_play, mon);
	int num_mon = new_state.getMonsters(cas, placing_play).size();
	if(num_mon == 4){
	    new_state = doBattle(new_state, cas);
	}

	//Finally we start the next player's turn
	new_state.setCurPlayer( otherPlayer(cur_play) );
	new_state.addCoins(otherPlayer(cur_play), 3);

	return new_state;
    }

    //Add Hidden Dragons (if needed)  and do the battle
    //Ties are determined randomly
    static GameState doBattle(GameState state, CastleID cas){
	System.out.println("Battle at "+cas.name());

	if( state.getHidden(PlayerID.TOP) == cas ){
	    state.addMonster(cas, PlayerID.TOP, Monster.DRAGON);
	}
	if(state.getHidden(PlayerID.BOT) == cas){
	    state.addMonster(cas, PlayerID.BOT,  Monster.DRAGON);
	}
		
	List<Monster> top_mons = state.getMonsters(cas, PlayerID.TOP);
	List<Monster> bot_mons = state.getMonsters(cas, PlayerID.BOT);
	
	handleDragons(top_mons, bot_mons);
	handleDragons(bot_mons, top_mons);
	int top_total = 0;
	int bot_total = 0;

	for( Monster mon : top_mons){
	    top_total = top_total + mon.value;
	}
	for(Monster mon : bot_mons){
	    bot_total = bot_total + mon.value;
	}

	if (top_total > bot_total){
	    state.setCastleWon(cas, PlayerID.TOP);
	}
	if (bot_total > top_total){
	    state.setCastleWon(cas, PlayerID.BOT);
	}
	if (bot_total == top_total){
	    Random rand = new Random();
	    if (rand.nextBoolean()){
		state.setCastleWon(cas, PlayerID.TOP);
	    }
	    else{
		state.setCastleWon(cas, PlayerID.BOT);
	    }
	}

	return state;

    }

    //This function mutates the lists that it gets as input!!
    //I am changing a state by changing the monster lists -- beware!
    static void handleDragons(List<Monster> slay_mons, List<Monster> drag_mons){
	int slay_count = 0;
	
	for(Monster mon: slay_mons){
	    if (mon == Monster.SLAYER){
		slay_count = slay_count + 1;
	    }
	}

	for(int i=0; i<slay_count; i++){
	    if(drag_mons.contains(Monster.DRAGON) ){
		drag_mons.remove(Monster.DRAGON);
		drag_mons.add(Monster.DEAD);
	    }
	}
    }

}
