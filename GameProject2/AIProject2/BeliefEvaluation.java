package AIProject2;
import ProjectTwoEngine.*;
public class BeliefEvaluation{
public List<Float> ReevaluateBelief(GameState gs){
GameState gamestate = gs;

List<Float> Beliefs = new List<Float>(3);
    if(gs.isTopTurn()){
        for(int i = 0; i < 3; i++){
            List Evilmonsters = gs.getMonsters(i, PlayerID.BOT);
            int EvilProwess = 0;
            for(Monster m: Evilmonsters){
                EvilProwess+=m.value;
            }
            List Fairmonsters = gs.getMonsters(i, PlayerID.TOP);
            int FairProwess = 0;
            for(Monster m: Fairmonsters){
                FairProwess+=m.value;
            }
            if(FairProwess>EvilProwess){
                if(FairProwess>EvilProwess+6){
                    Beliefs[i] = 1.0;
                }
                else{
                    Beliefs[i] = 0.5;
                }
            }
            if(FairProwess<EvilProwess){
                if(FairProwess+6<EvilProwess && gamestate.getHidden(PlayerID.TOP) != i){
                    Beliefs[i] = 0.0;
                }
                else{
                    Beliefs[i] = 0.5; 
                }
            }
            

    }
    }
    else(){
        for(int i = 0; i < 3; i++){
            List Evilmonsters = gs.getMonsters(i, PlayerID.Top);
            int EvilProwess = 0;
            for(Monster m: Evilmonsters){
                EvilProwess+=m.value;
            }
            List Fairmonsters = gs.getMonsters(i, PlayerID.BOT);
            int FairProwess = 0;
            for(Monster m: Fairmonsters){
                FairProwess+=m.value;
            }
            if(FairProwess>EvilProwess){
                if(FairProwess>EvilProwess+6){
                    Beliefs[i] = 1.0;
                }
                else{
                    Beliefs[i] = 0.5;
                }
            }
            if(FairProwess<EvilProwess){
                if(FairProwess+6<EvilProwess && gamestate.getHidden(PlayerID.BOT) != i){
                    Beliefs[i] = 0.0;
                }
                else{
                    Beliefs[i] = 0.5; 
                }
            }
            

    }
    }
return Beliefs;
}

}