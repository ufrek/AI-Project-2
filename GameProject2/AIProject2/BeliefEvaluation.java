package AIProject2;
import java.util.ArrayList;
import java.util.List;

import ProjectTwoEngine.*;
public class BeliefEvaluation{
public List<Float> ReevaluateBelief(ArrayList<Float> beliefs, GameState gs, PlayerID player){
GameState gamestate = gs;
CastleID castle = CastleID.CastleA;
List<Float> Beliefs = beliefs;
ArrayList OpponentForces = new ArrayList<Integer>();
    for(int i = 0; i<3; i++){
        switch(i){
            case 0:
                castle=CastleID.CastleA;
                break;
            case 1:
                castle=CastleID.CastleB;
                break;
            case 2:
                castle=CastleID.CastleC;
                break;
        }
        if(player == PlayerID.TOP){
            List<Monster> Evilmonsters = gs.getMonsters(castle, PlayerID.BOT);
            int EvilProwess = 0;
            for(Monster m: Evilmonsters){
                EvilProwess+=m.value;
            }
            OpponentForces.add(EvilProwess);
            
            
        }
        else if(player == PlayerID.BOT){
            List<Monster> Evilmonsters = gs.getMonsters(castle, PlayerID.TOP);
            int EvilProwess = 0;
            for(Monster m: Evilmonsters){
                EvilProwess+=m.value;
            }
            OpponentForces.add(EvilProwess);
        }

    }
Beliefs = makeEvaluation(OpponentForces);
return Beliefs;
}
public List<Float> makeEvaluation(ArrayList<Integer> Forces){
    int AllForcesSummed = 0;
    for(int force: Forces){
        AllForcesSummed+=force;
    }
    List<Float> NewBeliefs = new ArrayList<Float>();
    for(int i = 0; i<3; i++){
        NewBeliefs.set(i, (float) NewBeliefs.get(i)/AllForcesSummed);
        NewBeliefs.set(i, 1-NewBeliefs.get(i));
    }
    return NewBeliefs;
}
}
