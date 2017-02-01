/**
 * Created by Administrator on 2/8/2016.
 */
import java.util.*;
public class DynamicTreeNode {
    public DynamicTreeNode parent;
    public List<DynamicTreeNode> children;
    public HashMap<String, Object> database;
    public int nodeColor;
    private String name;


    public DynamicTreeNode(String name){
        this.name=name;
        this.children=new ArrayList<DynamicTreeNode>();
        this.database=new HashMap<String, Object>();
        this.nodeColor=Constants.NODE_COLOR_DEFAULT;
    }

    public void addChild(DynamicTreeNode child){
        this.children.add(child);
        child.setParent(this);
    }

    @Override
    public String toString(){
        return name;
    }
    public void setParent(DynamicTreeNode parent){
        this.parent=parent;
    }

    public String getName(){return this.name;}
    public void printName(){System.out.println(this.name);}
    public void addUsers(String phoneNumber, Object node){
        this.database.put(phoneNumber, node);
    }

    public boolean isInLine(DynamicTreeNode leafnode){

        boolean result =false;

        while(leafnode.parent!=null){
            if(this.equals(leafnode))result= true;
            leafnode=leafnode.parent;
        }
        return result;
    }


}
