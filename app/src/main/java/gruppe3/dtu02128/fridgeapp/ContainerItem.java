package gruppe3.dtu02128.fridgeapp;

/**
 * Created by Marcus on 19-06-2015.
 */
public class ContainerItem {
    String name;
    String type;

    public ContainerItem(String name, String type){
        this.name = name;
        this.type = type;
    }

    public ContainerItem(){
        this.name = "";
        this.type = "";
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setType(String type){
        this.type = type;
    }
        
}
