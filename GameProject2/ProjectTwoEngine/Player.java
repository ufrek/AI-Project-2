package ProjectTwoEngine;

public interface Player{

    //This function is called when the game starts
    public void begin(GameState init_state);

    //This function is called when the player must select a monster to buy
    public BuyMonsterMove getBuyMonster(GameState state);

    //This function is called at the start of your opponent's turn
    public void startOppTurn(GameState state);

    //This function is called when your opponent tried to buy a monster
    //If you steal, you will get the chosen monster
    //... but hand your opponent the price in coins
    public RespondMove getRespond(GameState state, Monster mon, int price);
    
    //This function is called when the opponent pays the price to steal
    // ... the monster chosen by the player
    public void stolenMonster(GameState state);

    //This function is called when the player successfully buys a monster
    //... and needs to place the monster at a castle
    public PlaceMonsterMove getPlace(GameState state, Monster mon);

    public String getPlayName();
}

   
