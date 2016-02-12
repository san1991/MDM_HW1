

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2/8/2016.
 */
public class DynamicTree {
    public ArrayList<DynamicTreeNode> leafNodes;

    public DynamicTreeNode root;
    public ArrayList<String> listofUser;

    public int add;
    public int delete;
    public int update;
    public int total;



    public DynamicTree(){
        this.leafNodes=new ArrayList<DynamicTreeNode>();
        this.listofUser=new ArrayList<String>();
    };
    public DynamicTree(DynamicTreeNode root){
        this.leafNodes=new ArrayList<DynamicTreeNode>();
        this.listofUser=new ArrayList<String>();
        this.root=root;
    }
//Create topology tree by parsing xml file
    public void setTopo(File file){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root=document.getDocumentElement();
            //System.out.println(root.getNodeName());
            DynamicTreeNode rt=new DynamicTreeNode(root.getNodeName());
            this.root=rt;
            Node rr=root;
            setTopoHelper(this.root,rr);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }


    }
//help function setTopo to assign child nodes of the each nodes in the tree.
    public void setTopoHelper(DynamicTreeNode dn,Node n){
        if(n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int num = nl.getLength();
            for (int i = 0; i < num; i++) {
                Node node=nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    DynamicTreeNode cn = new DynamicTreeNode(node.getNodeName());
                    //System.out.println(node.getNodeName());
                    dn.addChild(cn);
                    setTopoHelper(cn, node);
                }

            }
        }
        else{

            this.leafNodes.add(dn);
            //dn.printName();
        }

    }

    public void clearDatabase(DynamicTreeNode root){
        root.database.clear();
        for(DynamicTreeNode dtn:root.children){
            clearDatabase(dtn);
        }
    }

    public void initialDatabase_Pointer(File file){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root=document.getDocumentElement();
            //System.out.println(root.getNodeName());
            if(root.hasChildNodes()) {
                NodeList leaflist = root.getChildNodes();
                for(int i=0;i<leaflist.getLength();i++){
                    Node leafnode=leaflist.item(i);
                    if (leafnode.getNodeType() == Node.ELEMENT_NODE) {
                        for (DynamicTreeNode dt : leafNodes) {
                            if (dt.getName().equals(leafnode.getNodeName())) {
                                if (leafnode.hasChildNodes()) {
                                    NodeList userlist = leafnode.getChildNodes();
                                    for (int j = 0; j < userlist.getLength(); j++) {

                                        Node user = userlist.item(j);
                                        if(user.getNodeType()==Node.ELEMENT_NODE) {
                                            String userNum=user.getFirstChild().getNodeValue();
                                            this.listofUser.add(userNum);
                                            //dt.addUsers(userNum, dt);
                                            //System.out.println(user.getFirstChild().getNodeValue());
                                            if(dt.parent!=null)
                                            updateDatabase_actualPointer(userNum,dt,dt,true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }

    public void updateDatabase_actualPointer(String user,DynamicTreeNode childNode,DynamicTreeNode parentNode,boolean up){

        if(up) {
            if (parentNode.database.containsKey(user) && !parentNode.database.get(user).equals(childNode)) {
                parentNode.database.put(user, childNode);
                //show how the database be updated (updating entry)
                //System.out.println("update node: "+parentNode.getName());


                this.update++;
                this.total=this.add+this.delete+this.update;
                if (parentNode.parent != null){
                    updateDatabase_actualPointer(user, parentNode, parentNode.parent,up);

                }
                for(DynamicTreeNode dtn:parentNode.children){
                    updateDatabase_actualPointer(user,childNode,dtn,!up);
                }


            }
            else if (!parentNode.database.containsKey(user)) {
                parentNode.database.put(user, childNode);
                this.add++;
                this.total=this.add+this.delete+this.update;

                //show how the database be updated (adding entry)
                //System.out.println("add in node: "+parentNode.getName());

                if (parentNode.parent != null)
                    updateDatabase_actualPointer(user, parentNode, parentNode.parent,up);
            }
            else if(parentNode.database.containsKey(user) && parentNode.database.get(user).equals(childNode)){
                for(DynamicTreeNode dtn:parentNode.children){
                    updateDatabase_actualPointer(user,childNode,dtn,!up);
                }
            }
        }
        else{



            if(!parentNode.equals(childNode)&&parentNode.database.containsKey(user)){
                parentNode.database.remove(user);
                this.delete++;
                this.total=this.add+this.delete+this.update;

                //show how the database be updated (deleting entry)
                //System.out.println("delete in node: "+parentNode.getName());


                for(DynamicTreeNode dtn:parentNode.children){
                    updateDatabase_actualPointer(user,childNode,dtn,up);
                }
            }
        }


    }


    public void resetCounter(){
        this.add=0;
        this.update=0;
        this.delete=0;
        this.total=0;
    }

    public void printUpdateCost(String user){
        System.out.println("The cost of moving user "+user+" are: ");
        System.out.println("The number of database which added this entry is : "+this.add);
        System.out.println("The number of database which delete this entry is : "+this.delete);
        System.out.println("The number of database which update this entry is : "+this.update);
        System.out.println("The number of database which totally modified this entry is : "+this.total);
    }

    public void initialDatabase_Value(File file){

    }








    public static void main(String[] arg) {


        DynamicTree dt = new DynamicTree();
        File tp = new File("Topology_xc.xml");
        dt.setTopo(tp);

        File userFile = new File("Users.xml");
        dt.initialDatabase_Pointer(userFile);

        //Testing moving user from one place to another.
        /*
        ((DynamicTreeNode)dt.root.database.get("2601")).printName();

        dt.resetCounter();
        System.out.println("Starting move 2601 from PA to "+dt.leafNodes.get(12).getName());
        dt.updateDatabase_actualPointer("2601",dt.leafNodes.get(12),dt.leafNodes.get(12),true);


    }

        ((DynamicTreeNode)dt.root.database.get("2601")).printName();
        dt.printUpdateCost("2601");
        dt.resetCounter();
        dt.updateDatabase_actualPointer("2601",dt.leafNodes.get(42),dt.leafNodes.get(42),true);
        dt.printUpdateCost("2601");
        */


    }

}
