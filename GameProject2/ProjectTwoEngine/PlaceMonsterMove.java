package ProjectTwoEngine;

public class PlaceMonsterMove implements Move{
    Monster mon;
    CastleID cas;
    PlayerID play;

    public CastleID getCastle(){
	return cas;
    }

    public Monster getMonster(){
	return mon;
    }

    public PlayerID getPlayer(){
	return play;
    }

    public PlaceMonsterMove(PlayerID id, CastleID c, Monster m){
	play = id;
        cas = c;
	mon = m;
    }
}
