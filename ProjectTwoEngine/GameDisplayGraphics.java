package ProjectTwoEngine;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.shape.ArcType;
import java.util.List;

class GameDisplayGraphics{
    public static int SIZE_TALL = 900;
    public static int SIZE_WIDE = 1200;
    public static int MARGIN = 20;
    public static int BOX_TALL = 80;
    public static int BOX_WIDE = 150;
    public static double LINE_WIDE = 4;
    static int FONT_SIZE = 30;
    static GraphicsContext gc;


    static void displayState(Canvas can, GameState state){
	int x, y, num_stones;
	
	gc = can.getGraphicsContext2D();

	gc.setStroke(Color.BLACK);
	gc.setLineWidth(LINE_WIDE);
	
	gc.clearRect(0,0,SIZE_WIDE,SIZE_TALL);

	// Print the Players' Names
	//gc.setStroke(Color.BLUE);
	//gc.setFont(Font.font("Helvetica", FONT_SIZE));
	//gc.setLineWidth(1.5);
	//gc.strokeText(state.getTopName(), MARGIN + BOX_WIDE, MARGIN + 0.5*BOX_TALL);
	//gc.strokeText(state.getBotName(), MARGIN + BOX_WIDE, MARGIN + 7.75*BOX_TALL);
	//gc.setStroke(Color.BLACK);
	// End Players Names

	// Figuring out whether there is a price
	Move last_move = state.getLastMove();
	boolean show_price = false;
	int price = -1;
	Monster price_mon = null;
	if(last_move instanceof BuyMonsterMove){
	    BuyMonsterMove last_buy = (BuyMonsterMove) last_move;
	    show_price = true;
	    price = last_buy.getPrice();
	    price_mon = last_buy.getMonster();
	}

	// Figuring out whether there is a steal
	boolean show_pass = false;
	boolean show_steal = false;
	Monster resp_mon = resp_mon = null;
	if(last_move instanceof RespondMove){
	    RespondMove last_resp = (RespondMove) last_move;
	    resp_mon = last_resp.getMonster();
	    if(last_resp.isPass()){
		show_pass = true;
	    }
	    else{
		show_steal = true;
	    }
	}
	
	// Write Public Monsters and maybe the price
	int i = 0;
	PlayerID cur_play = state.getCurPlayer();
	gc.setStroke(Color.BLUE);
	gc.setFont(Font.font("Helvetica", FONT_SIZE));
	gc.setLineWidth(1.5);
	gc.strokeText(cur_play.name() + " Player Turn", 300, 50);

	
	gc.strokeText("Public Monsters:", 900, 50);
	gc.setFill(Color.RED);
	
	for(Monster mon : state.getPublicMonsters() ){
	    i = i+1;
	    if(mon == resp_mon && (show_pass || show_steal)){
		PlayerID opp = GameRules.otherPlayer(cur_play);
		if (show_pass){
		    gc.setFill(Color.BLACK);
		    gc.fillText("Pass By "+opp.name(), 900, 50 + i*50);
		    gc.setFill(Color.RED);
		    show_pass = false;
		}
		if (show_steal){
		    gc.setFill(Color.BLACK);
		    gc.fillText("Steal By "+opp.name(), 900, 50 + i*50);
		    gc.setFill(Color.RED);
		    show_steal = false;
		}
	    }   
	    else{
		gc.fillText(d_str(mon), 900, 50 + i*50);
	    }
		
	    if(show_price && price_mon == mon){
		show_price = false;
		gc.setFill(Color.BLACK);
		gc.fillText("Coin "+price, 790, 50 + i*50);
		gc.setFill(Color.RED);
	    }
	}
	gc.strokeText("Next Monster:", 900, 125 + i*50);
	Monster next_mon = state.getNextMonster();
	gc.fillText(d_str(next_mon), 900, 125 + (i+1)*50);
	

	// Write Coins
	gc.setStroke(Color.BLUE);
	gc.strokeText("TOP: " + state.top_name, 875, 225 + (i+1) * 50);
	gc.strokeText("BOT: " + state.bot_name, 875, 225 + (i+3) * 50);

	gc.setFill(Color.BLACK);
	String coin_str = String.valueOf(state.getCoins(PlayerID.TOP));
	gc.fillText(coin_str + " Coins", 900, 225 + (i+2)*50);
	coin_str = String.valueOf(state.getCoins(PlayerID.BOT));
	gc.fillText(coin_str + " Coins", 900, 225 + (i+4)*50);

	displayCastle(state, CastleID.CastleA, 25);
	displayCastle(state, CastleID.CastleB, 300);
	displayCastle(state, CastleID.CastleC, 575);
    }

    static void displayCastle(GameState state, CastleID cas, int X){
	boolean do_hid_top = false;
	boolean do_hid_bot = false;
	if (state.getCastleWon(cas) == null){
	    gc.setFill(Color.GREEN);
	    gc.setStroke(Color.GREEN);
	    gc.strokeRect(X, 400, 250, 75);
	    gc.fillText(cas.name(), X+25, 450);
	}
	else{
	    PlayerID winner = state.getCastleWon(cas);
	    gc.setStroke(Color.BLACK);
	    gc.setFill(Color.BLACK);
	    gc.strokeRect(X, 400, 250, 75);
	    gc.fillText(winner.name(), X+25, 450);
	    if(state.top_hidden == cas){
		do_hid_top = true;
	    }
	    if(state.bot_hidden == cas){
		do_hid_bot = true;
	    }
	}

	List<Monster> top_mons = state.getMonsters(cas, PlayerID.TOP);
	List<Monster> bot_mons = state.getMonsters(cas, PlayerID.BOT);

	gc.setFill(Color.RED);
	int i = 0;
	for(Monster mon : top_mons){
	    i = i+1;
	    if((do_hid_top)&&(mon == Monster.DRAGON || mon == Monster.DEAD)){
		gc.setFill(Color.PURPLE);
		gc.fillText(d_str(mon), X+25, 450 - 15 - i*50);
		gc.setFill(Color.RED);
		do_hid_top = false;
	    }
	    else{
		gc.fillText(d_str(mon), X+25, 450 - 15 - i*50);
	    }
	}
	
	i = 0;
	for(Monster mon : bot_mons){
	    i = i+1;
	    if((do_hid_bot)&&(mon == Monster.DRAGON || mon == Monster.DEAD)){
		gc.setFill(Color.PURPLE);
		gc.fillText(d_str(mon), X+25, 450 +10 + i*50);
		gc.setFill(Color.RED);
		do_hid_bot = false;
	    }
	    else{
		gc.fillText(d_str(mon), X+25, 450 +10 + i*50);
	    }
	}
    }
    
    static String d_str( Monster m){
	String answer = m.name;
	return answer + "  " + m.value;
    }
}
