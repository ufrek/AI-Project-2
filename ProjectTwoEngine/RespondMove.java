package ProjectTwoEngine;

//This move is used when your opponent is the active player
//It indicates whether you want to pass ...
//... or whether you want to pay the price to steal the active monster

//The PlayerID is the player who is responding
// ... NOT the player whose is actively taking their turn

public class RespondMove implements Move{
    boolean is_pass;
    PlayerID play;
    Monster monst;
    
    public boolean isPass(){
	return is_pass;
    }

    public boolean isSteal() {
	return (! is_pass);
    }

    public Monster getMonster(){
	return monst;
    }
    
    public PlayerID getPlayer(){
	return play;
    }

    public RespondMove(PlayerID id, boolean pass, Monster m){
	play = id;
        is_pass = pass;
	monst = m;
    }
}
