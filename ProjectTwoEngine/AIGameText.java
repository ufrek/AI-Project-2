//This class allows you to play a game between two AI without JavaFX
//The moves for the game are sent to a text file
package ProjectTwoEngine;

import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class AIGameText{
    // Change this file name to change where the moves are written
    static final String FILE_NAME = "transcript.txt";
    
    static Player TOP_Player;
    static Player BOT_Player;
    
    static GameState state;
    
    public static void main(String[] args) throws IOException {
	//IMPORTANT : Change these lines to change who is playing!
	TOP_Player = new RandomPlayer();
	BOT_Player = new RandomPlayer();	
      
	state = new GameState(TOP_Player.getPlayName(), BOT_Player.getPlayName());

	BufferedWriter file_out = new BufferedWriter(new FileWriter( FILE_NAME ));
	file_out.write("Top Player is: ");
	file_out.write(TOP_Player.getPlayName());
	file_out.newLine();
	file_out.write("Bot Player is: ");
	file_out.write(BOT_Player.getPlayName());
	file_out.newLine();
	

	System.out.println("File created successfully");

	//Call the Start Game Functions
	TOP_Player.begin( new GameState(state, PlayerID.TOP) );
	BOT_Player.begin( new GameState(state, PlayerID.BOT) );

	while (! GameRules.isGameOver(state)){
		PlayerID cur_player = state.getCurPlayer();

		file_out.newLine();
		file_out.newLine();
		file_out.write("----------------------");
		file_out.newLine();
		file_out.write("Player " + cur_player.name() + " Start Turn");
		file_out.newLine();
		file_out.write("TOP has " + state.getCoins(PlayerID.TOP) + " coins");
		file_out.newLine();
		file_out.write("BOT has " + state.getCoins(PlayerID.BOT) + " coins");
		file_out.newLine();
		file_out.write("Publicy Available Monsters: ");
		file_out.newLine();
		for(Monster mon: state.getPublicMonsters()){
		    file_out.write("--- " + mon.name + " " +  mon.value);
		    file_out.newLine();
		}
	    

		//Active Player decides what to buy
		BuyMonsterMove buy_move = null;
		if (cur_player == PlayerID.TOP){
		    GameState copy_state = new GameState(state, PlayerID.TOP);
		    buy_move = TOP_Player.getBuyMonster(copy_state);

		    copy_state = new GameState(state, PlayerID.BOT);
		    BOT_Player.startOppTurn(copy_state);
		}
		else{
		    GameState copy_state = new GameState(state, PlayerID.BOT);
		    buy_move = BOT_Player.getBuyMonster(copy_state);

		    copy_state = new GameState(state, PlayerID.TOP);
		    TOP_Player.startOppTurn(copy_state);
		}


		//Opponent Responds
		state = GameRules.makeMove(state, buy_move);
		RespondMove resp_move = null;
		if (cur_player == PlayerID.TOP){
		    GameState copy_state = new GameState(state, PlayerID.BOT);
		    resp_move = BOT_Player.getRespond(copy_state, buy_move.getMonster(), buy_move.getPrice());
		}
		else{
		    GameState copy_state = new GameState(state, PlayerID.TOP);
		    resp_move = TOP_Player.getRespond(copy_state, buy_move.getMonster(), buy_move.getPrice());
		}

		//Place The Monster
		state = GameRules.makeMove(state, resp_move);
		PlaceMonsterMove place_move = null;
		if (resp_move.isPass()){
		    if (cur_player == PlayerID.TOP){
			GameState copy_state = new GameState(state, PlayerID.TOP);
			place_move = TOP_Player.getPlace(copy_state, buy_move.getMonster());
		    }
		    else{
			GameState copy_state = new GameState(state, PlayerID.BOT);
			place_move = BOT_Player.getPlace(copy_state, buy_move.getMonster());
		    }
		}
		else{
		    if (cur_player == PlayerID.TOP){
			GameState copy_state = new GameState(state, PlayerID.TOP);
			TOP_Player.stolenMonster(copy_state);

			copy_state = new GameState(state, PlayerID.BOT);
			place_move = BOT_Player.getPlace(copy_state, buy_move.getMonster());
		    }
		    else{
			GameState copy_state = new GameState(state, PlayerID.BOT);
			BOT_Player.stolenMonster(copy_state);

			copy_state = new GameState(state, PlayerID.TOP);
			place_move = TOP_Player.getPlace(copy_state, buy_move.getMonster());
		    }
		}
		
		state = GameRules.makeMove(state, place_move);
		
		file_out.write("Attempt to Buy:");
		file_out.newLine();
		file_out.write("--- " + buy_move.getMonster().name + " " + buy_move.getMonster().value);
		file_out.newLine();
		file_out.write("--- For the price of: " + buy_move.getPrice());
		file_out.newLine();
		if(resp_move.isPass()){
		    file_out.write("Opponent Passes.");
		    file_out.newLine();
		    file_out.write("Player " + cur_player.name() + " Gets to Place");
		    file_out.newLine();
		}
		else{
		    file_out.write("Opponent Steals.");
		    file_out.newLine();
		    file_out.write("Player " + GameRules.otherPlayer(cur_player).name() + " Gets to Place");
		    file_out.newLine();
		}
		file_out.write("--- " +  buy_move.getMonster().name + " is placed at " + place_move.getCastle().name());
		file_out.newLine();
			
	}

	List<Monster> top_mons;
	List<Monster> bot_mons;
	file_out.newLine();
	file_out.write("*************************");
	file_out.newLine();
	file_out.write("Game Over");
	file_out.newLine();

	for(CastleID cas : CastleID.values()){
	    file_out.newLine();
	    file_out.write(cas.name() + " Won By "+ state.getCastleWon(cas).name());
	    file_out.newLine();
	    top_mons = state.getMonsters(cas, PlayerID.TOP);
	    bot_mons = state.getMonsters(cas, PlayerID.BOT);

	    file_out.write("Top Monsters Are: ");
	    file_out.newLine();
	    for(Monster mon : top_mons){
		file_out.write("--- " + mon.name + " " +  mon.value);
		file_out.newLine();
	    }
	    file_out.write("Bot Monsters Are: ");
	    file_out.newLine();
	    for(Monster mon : bot_mons){
		file_out.write("--- " + mon.name + " " +  mon.value);
		file_out.newLine();
	    }

	}

	
	file_out.close();
	       
    }

	
}

	
