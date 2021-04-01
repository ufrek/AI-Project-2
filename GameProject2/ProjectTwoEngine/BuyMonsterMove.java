package ProjectTwoEngine;

public class BuyMonsterMove implements Move{
    int price;
    Monster mon;
    PlayerID play;

    public int getPrice(){
	return price;
    }

    public Monster getMonster(){
	return mon;
    }

    public PlayerID getPlayer(){
	return play;
    }

    public BuyMonsterMove(PlayerID id, int p, Monster m){
	play = id;
	price = p;
	mon = m;
    }
}
