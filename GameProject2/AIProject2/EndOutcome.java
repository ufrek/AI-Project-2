package AIProject2;

import ProjectTwoEngine.GameState;
import ProjectTwoEngine.Player;
import ProjectTwoEngine.PlayerID;

public class EndOutcome 
{
    Node node;
    PlayerID winner;

    public EndOutcome(Node node, PlayerID winner)
    {
        this.node = node;
        this.winner = winner;
    }

    public Node getNode(){ return node;}
    public PlayerID getWinner(){return winner;}
    
}
