package gruppe3.dtu02128.fridgeapp;

import java.util.Date;

/**
 * Created by Morten on 17-Jun-15.
 */
public class ListItem {
    String name;
    long dat;
    public ListItem(String name, long date) {
        this.name = name;
        this.dat = date;
    }

    public long getValue() {
        return dat;
    }

    public String getName() {
        return name;
    }
}
