package ProjectTwoEngine;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class DeckFactory{

    public static List<Monster> createDeck(){
	List<Monster> deck = new ArrayList<Monster>();

	for(int i=0; i<5; i++){
	    deck.add(Monster.GIANT);
	    deck.add(Monster.WARLION);
	}

	for(int i=0; i<4; i++){
	    deck.add(Monster.WOLF);
	    deck.add(Monster.GRYPHON);
	}

	for(int i=0; i<2; i++){
	    deck.add(Monster.DRAGON);
	    deck.add(Monster.SLAYER);
	}

	Collections.shuffle(deck);
	return deck;
    }

}
