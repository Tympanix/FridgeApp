package gruppe3.dtu02128.fridgeapp;

/**
 * Created by Morten on 17-Jun-15.
 */
public class ListItem {
    String name;
    int value;
    public ListItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
