package ProjectTwoEngine;

public enum Monster{
    DRAGON("Dragon", 6),
    GRYPHON("Gryphon", 5),
    GIANT("Giant", 4),
    WARLION("War Lion", 3),
    WOLF("Wolf", 2),
    SLAYER("Slayer", 1),
    DEAD("Dead Dragon", 0);

    public final String name;
    public final int value;

    private Monster(String n, int v){
	this.name = n;
	this.value = v;
    }
}
