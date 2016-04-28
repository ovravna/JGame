package game;

import java.util.ArrayList;
import java.util.List;

public abstract class InputManager {

    private static List<InputObject> inputObjects = new ArrayList<>();
    protected static InputHandler input;


    public static void addInputObject(InputObject inputObject) {
        inputObjects.add(inputObject);
        inputObject.setInputHandler(input);

    }


    protected void setInput(InputHandler input) {
        this.input = input;

        for (InputObject inputObject : inputObjects) {
            inputObject.setInputHandler(this.input);
        }
    }
}
