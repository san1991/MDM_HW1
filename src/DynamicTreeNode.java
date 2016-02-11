/**
 * Created by Administrator on 2/8/2016.
 */
import java.util.*;
public class DynamicTreeNode {
    public DynamicTreeNode parent;
    public List<DynamicTreeNode> children;
    public HashMap<Integer, Object> database;

    private String name;


    public DynamicTreeNode(String name){
        this.name=name;
        this.children=new ArrayList<DynamicTreeNode>();
    }

    public void addChild(DynamicTreeNode child){
        this.children.add(child);
        child.setParent(this);
    }

    public void setParent(DynamicTreeNode parent){
        this.parent=parent;
    }

    public String getName(){return this.name;}
    public void printName(){System.out.println(this.name);}
    public void addUsers(int phoneNumber, int name){
        this.database.put(phoneNumber, name);
    }
}
