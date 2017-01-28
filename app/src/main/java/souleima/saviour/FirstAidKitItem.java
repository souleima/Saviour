package souleima.saviour;

/**
 * Created by pc on 1/27/2017.
 */

public class FirstAidKitItem {
    private String itemName;
    public FirstAidKitItem(String itemName){
        this.itemName=itemName;
    }
    public String getItemName(){
        return this.itemName;
    }
    public void setItemName(String itemName){
        this.itemName=itemName;
    }
}
