package sokoban.cells;

public interface Actable {

    void act();

    void actedOn(Actable acter);
}
