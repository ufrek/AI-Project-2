// To play with your AI player, you need to do two things:
// 1) Add an import statement to import your AI player
// 2) Change the lines inside start that say:
//      TOP_Player =
//      BOT_Player =
//
// Note: You can replay a game from a text file by setting
//        File_Name to be a String with the name of the file

package ProjectTwoEngine;

// Here I am importing some players here.
// You can change this to import your player
import AliceSquad.AliceTheQuick;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Scanner;

public class AIGameApp extends Application
{
    // IMPORTANT: File_Name = null
    //      signals that we are NOT replaying a game from a file  
    // static final String File_Name = null;
    // static final String File_Name = "transcript.txt";
    
    Player TOP_Player;
    Player BOT_Player;
    
    Canvas test_canvas;
    Stage primary;
    GameState state;
    
    // You can raise this delay to slow down the AI moves
    final double DELAY_TIME = 1.5;

    public void start(Stage primaryStage){
	//IMPORTANT : Change these lines to change who is playing!
	TOP_Player = new AliceTheQuick();
	BOT_Player =  new RandomPlayer();

	//IMPORTANT : If there is a File_Name
	//     Then we will always display the game from the file!
	//if( File_Name != null){
	//    TOP_Player = new GameFromFilePlayer(File_Name);
	//    BOT_Player = new GameFromFilePlayer(File_Name);
	//}


	//Set up the names in the state object
	String nameTop;
	String nameBot;
	nameTop = TOP_Player.getPlayName();
	nameBot = BOT_Player.getPlayName();


	state = new GameState(nameTop, nameBot );
	TOP_Player.begin(new GameState(state, PlayerID.TOP) );
	BOT_Player.begin(new GameState(state, PlayerID.BOT) );
	
        primary = primaryStage;
	String title = "TOP: " + nameTop + " ";
	title = title + "  vs  BOT: " + nameBot;
        primaryStage.setTitle(title);
        Group root = new Group();
        test_canvas = new Canvas(1200, 900);
 
        root.getChildren().add(test_canvas);
        Scene mainScene;
        mainScene = new Scene(root, 1200, 900, Color.BEIGE);


	//Test Code
	for(Monster mon : state.getDeck()){
	    System.out.println(mon.name);
	}

	
		  
        primaryStage.setScene(mainScene);
        primaryStage.show();

	Timeline quickTimer = new Timeline(new KeyFrame(Duration.seconds(DELAY_TIME), new EventHandler<ActionEvent>() {
		
		public void handle(ActionEvent event) {
		    nextAITurn();
		}
	    }));

	
	quickTimer.setCycleCount(Timeline.INDEFINITE);
	quickTimer.play();		

	    
	GameDisplayGraphics.displayState(test_canvas, state);

    }


    void nextAITurn(){
	if (! GameRules.isGameOver(state)){
		PlayerID cur_player = state.getCurPlayer();
		PlayerID other_player = GameRules.otherPlayer(cur_player);
		GameState copy_state;
		GameState other_copy;
		Move nextMove = null;
		Move lastMove = state.getLastMove();
	    
		if( lastMove == null || lastMove instanceof PlaceMonsterMove){
		    copy_state = new GameState(state, cur_player);
		    other_copy = new GameState(state, other_player);
		    if(cur_player == PlayerID.TOP){
			nextMove = TOP_Player.getBuyMonster(copy_state);
			BOT_Player.startOppTurn(other_copy);
		    }
		    else{
			nextMove = BOT_Player.getBuyMonster(copy_state);
			TOP_Player.startOppTurn(other_copy);
		    }
		}
		else if (lastMove instanceof BuyMonsterMove){
		    other_copy = new GameState(state, other_player);
		    int price = ((BuyMonsterMove) lastMove).getPrice();
		    Monster mon = ((BuyMonsterMove) lastMove).getMonster();

		    if(cur_player == PlayerID.TOP){
			nextMove = BOT_Player.getRespond(other_copy, mon, price);
		    }
		    else{
			nextMove = TOP_Player.getRespond(other_copy, mon, price);
		    }
		}
		else if (lastMove instanceof RespondMove){
		    copy_state = new GameState(state, cur_player);
		    other_copy = new GameState(state, other_player);
		    RespondMove lastResp = (RespondMove) lastMove;
		    
		    if(cur_player == PlayerID.TOP){
			if( lastResp.isPass() ){
			    nextMove = TOP_Player.getPlace(copy_state, lastResp.getMonster());
			}
			else{
			    TOP_Player.stolenMonster(copy_state);
			    nextMove = BOT_Player.getPlace(other_copy, lastResp.getMonster());
			}
		    }
		    else{
			if( lastResp.isPass() ){
			    nextMove = BOT_Player.getPlace(copy_state, lastResp.getMonster());
			}
			else{
			    BOT_Player.stolenMonster(copy_state);
			    nextMove = TOP_Player.getPlace(other_copy, lastResp.getMonster());
			}
		    }
		}
		
		state = GameRules.makeMove(state, nextMove);
		GameDisplayGraphics.displayState(test_canvas, state);
	}
    }
}

	


	
