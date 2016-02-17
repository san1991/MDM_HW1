

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.util.ArrayList;

import java.util.HashMap;

/**
 * Created by Administrator on 2/8/2016.
 */

public class DynamicTree {
    public ArrayList<DynamicTreeNode> leafNodes;
    public HashMap<String,DynamicTreeNode> testDatabase;

    public DynamicTreeNode root;
    public ArrayList<String> listofUser;

    public int add;
    public int delete;
    public int update;
    public int total;

    public int size;

    private HashMap<String, Integer > user_mobility_count;
    private HashMap<String, HashMap<DynamicTreeNode, Integer> > user_call_count;

    public DynamicTree() {
        this.leafNodes = new ArrayList<DynamicTreeNode>();
        this.listofUser = new ArrayList<String>();
        this.size=0;
        this.testDatabase=new HashMap<String, DynamicTreeNode>();
        this.user_mobility_count= new HashMap<>();
        this.user_call_count= new HashMap<>();

    }


    public DynamicTree(DynamicTreeNode root) {
        this.leafNodes = new ArrayList<DynamicTreeNode>();
        this.listofUser = new ArrayList<String>();
        this.root = root;
        this.size=0;

        this.testDatabase=new HashMap<String, DynamicTreeNode>();

        this.user_mobility_count= new HashMap<>();
        this.user_call_count= new HashMap<>();
    }


    public void updateUserCallMetric(String MH, DynamicTreeNode mobileHost, Integer i){

        HashMap<DynamicTreeNode, Integer> hostList= user_call_count.get(MH);

        if (hostList==null){
            hostList= new HashMap<DynamicTreeNode, Integer>();
            hostList.put(mobileHost,0);
            user_call_count.put(MH, hostList);
        }

        hostList.put(mobileHost,(hostList.get(mobileHost)+i));

    }

    public void updateUserMobilityMetric(String MH, Integer i){
        user_mobility_count.put(MH,user_mobility_count.get(MH)+i);

    }
    //Create topology tree by parsing xml file
    public void setTopo(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            //System.out.println(root.getNodeName());
            DynamicTreeNode rt = new DynamicTreeNode(root.getNodeName());
            this.root = rt;
            Node rr = root;
            this.size++;
            setTopoHelper(this.root, rr);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

    //help function setTopo to assign child nodes of the each nodes in the tree.
    public void setTopoHelper(DynamicTreeNode dn, Node n) {
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int num = nl.getLength();
            for (int i = 0; i < num; i++) {
                Node node = nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    DynamicTreeNode cn = new DynamicTreeNode(node.getNodeName());
                    //System.out.println(node.getNodeName());
                    dn.addChild(cn);
                    this.size++;
                    setTopoHelper(cn, node);
                }

            }
        } else {

            this.leafNodes.add(dn);
            //dn.printName();
        }

    }

    public void clearDatabase(DynamicTreeNode root) {
        root.database.clear();
        for (DynamicTreeNode dtn : root.children) {
            clearDatabase(dtn);
        }
    }

    public void initialDatabase_Pointer(File file) {

        clearDatabase(this.root);
        this.testDatabase.clear();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            //System.out.println(root.getNodeName());
            if (root.hasChildNodes()) {
                NodeList leaflist = root.getChildNodes();
                for (int i = 0; i < leaflist.getLength(); i++) {
                    Node leafnode = leaflist.item(i);
                    if (leafnode.getNodeType() == Node.ELEMENT_NODE) {
                        for (DynamicTreeNode dt : leafNodes) {
                            if (dt.getName().equals(leafnode.getNodeName())) {
                                if (leafnode.hasChildNodes()) {
                                    NodeList userlist = leafnode.getChildNodes();
                                    for (int j = 0; j < userlist.getLength(); j++) {

                                        Node user = userlist.item(j);
                                        if (user.getNodeType() == Node.ELEMENT_NODE) {
                                            String userNum = user.getFirstChild().getNodeValue();
                                            this.listofUser.add(userNum);
                                            //dt.addUsers(userNum, dt);
                                            //System.out.println(user.getFirstChild().getNodeValue());
                                            if (dt.parent != null)
                                                updateDatabase_actualPointer(userNum, dt, dt, true,new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());
                                                this.testDatabase.put(userNum,dt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }



    public void resetCounter() {
        this.add = 0;
        this.update = 0;
        this.delete = 0;
        this.total = 0;
    }

    public void printUpdateCost(String user) {
        System.out.println("The cost of moving user " + user + " are: ");
        System.out.println("The number of database which added this entry is : " + this.add);
        System.out.println("The number of database which delete this entry is : " + this.delete);
        System.out.println("The number of database which update this entry is : " + this.update);
        System.out.println("The number of database which totally modified this entry is : " + this.total);
    }

    public void initialDatabase_Value(File file) {

        clearDatabase(this.root);
        this.testDatabase.clear();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            //System.out.println(root.getNodeName());
            if (root.hasChildNodes()) {
                NodeList leaflist = root.getChildNodes();
                for (int i = 0; i < leaflist.getLength(); i++) {
                    Node leafnode = leaflist.item(i);
                    if (leafnode.getNodeType() == Node.ELEMENT_NODE) {
                        for (DynamicTreeNode dt : leafNodes) {
                            if (dt.getName().equals(leafnode.getNodeName())) {
                                if (leafnode.hasChildNodes()) {
                                    NodeList userlist = leafnode.getChildNodes();
                                    for (int j = 0; j < userlist.getLength(); j++) {

                                        Node user = userlist.item(j);
                                        if (user.getNodeType() == Node.ELEMENT_NODE) {
                                            String userNum = user.getFirstChild().getNodeValue();
                                            this.listofUser.add(userNum);
                                            //dt.addUsers(userNum, dt);
                                            //System.out.println(user.getFirstChild().getNodeValue());
                                            if (dt.parent != null)
                                                updateDatabase_databseValue(userNum, dt, dt, false,new ArrayList<DynamicTreeNode>(), new ArrayList<DynamicTreeNode>());
                                                this.testDatabase.put(userNum,dt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

    public DynamicTreeNode getNodeByName(String nodeName){
        for(DynamicTreeNode dtn:leafNodes){
            if(dtn.getName().equals(nodeName))return dtn;
        }
        return null;
    }


    // put code for gui

    public void updateDatabase_actualPointer(String user, DynamicTreeNode childNode, DynamicTreeNode parentNode, boolean up,ArrayList<DynamicTreeNode> updatedNodes,ArrayList<DynamicTreeNode> deletedNodes) {

        if (up) {
            if (parentNode.database.containsKey(user) && !parentNode.database.get(user).equals(childNode)) {
                parentNode.database.put(user, childNode);
                //show how the database be updated (updating entry)
                //System.out.println("update node: "+parentNode.getName());


                this.update++;
                this.total = this.add + this.delete + this.update;
                if (parentNode.parent != null) {
                    updateDatabase_actualPointer(user, parentNode, parentNode.parent, up,updatedNodes,deletedNodes);

                }
                for (DynamicTreeNode dtn : parentNode.children) {
                    updateDatabase_actualPointer(user, childNode, dtn, !up,updatedNodes,deletedNodes);
                }


            } else if (!parentNode.database.containsKey(user)) {
                parentNode.database.put(user, childNode);
                this.add++;
                this.total = this.add + this.delete + this.update;

                //show how the database be updated (adding entry)
                //System.out.println("add in node: "+parentNode.getName());

                if (parentNode.parent != null)
                    updateDatabase_actualPointer(user, parentNode, parentNode.parent, up,updatedNodes,deletedNodes);
            } else if (parentNode.database.containsKey(user) && parentNode.database.get(user).equals(childNode)) {
                for (DynamicTreeNode dtn : parentNode.children) {
                    updateDatabase_actualPointer(user, childNode, dtn, !up,updatedNodes,deletedNodes);
                }
            }
        } else {


            if (!parentNode.equals(childNode) && parentNode.database.containsKey(user)) {
                parentNode.database.remove(user);
                this.delete++;
                this.total = this.add + this.delete + this.update;

                //show how the database be updated (deleting entry)
                //System.out.println("delete in node: "+parentNode.getName());


                for (DynamicTreeNode dtn : parentNode.children) {
                    updateDatabase_actualPointer(user, childNode, dtn, up,updatedNodes,deletedNodes);
                }
            }
        }


    }


    // change gui in here
    public void updateDatabase_databseValue(String user, DynamicTreeNode childNode, DynamicTreeNode parentNode, boolean isDelete,ArrayList<DynamicTreeNode> updatedNodes,ArrayList<DynamicTreeNode> deletedNodes) {

        if(!isDelete){
            if (!parentNode.database.containsKey(user)) {
                parentNode.database.put(user, childNode);
                this.add++;
                this.total = this.add + this.delete + this.update;

                //show how the database be updated (adding entry)
                //System.out.println("add in node: "+parentNode.getName());

                if (parentNode.parent != null)
                    updateDatabase_databseValue(user, childNode, parentNode.parent, isDelete,updatedNodes,deletedNodes);
            }
            else if(parentNode.database.containsKey(user)&&!parentNode.database.get(user).equals(childNode)){



                DynamicTreeNode previousLocation=(DynamicTreeNode) parentNode.database.get(user);
                String previousLocationName=previousLocation.getName();
                //System.out.println(previousLocationName);
                parentNode.database.put(user, childNode);
                this.update++;
                this.total = this.add + this.delete + this.update;
                //show how the database be updated (updating entry)
                //System.out.println("update node: "+parentNode.getName());

                if (parentNode.parent != null) {
                    updateDatabase_databseValue(user, childNode, parentNode.parent, isDelete,updatedNodes,deletedNodes);

                }
                DynamicTreeNode pl=getNodeByName(previousLocationName);
                if(pl.database.containsKey(user)) {
                    updateDatabase_databseValue(user, pl, pl, !isDelete,updatedNodes,deletedNodes);
                }
            }
        }
        else {
            if(parentNode.database.containsKey(user)) {
                if (parentNode.database.get(user).equals(childNode)) {
                    parentNode.database.remove(user);


                    this.delete++;
                    this.total = this.add + this.delete + this.update;
                    //show how the database be updated (deleting entry)
                    //System.out.println("delete in node: " + parentNode.getName());

                    if (parentNode.parent != null) {
                        updateDatabase_databseValue(user, childNode, parentNode.parent, isDelete,updatedNodes,deletedNodes);

                    }
                }
            }
        }


    }

    public DynamicTreeNode getCallerLocation(String phoneNumber){
        if(this.testDatabase.containsKey(phoneNumber)){
            return this.testDatabase.get(phoneNumber);
        }else return null;
    }

    public void updateDatabase_forwarding_address(){


    }

    public void updateDatabase_forwarding_pointer(String user, int level, DynamicTreeNode oldLocation,DynamicTreeNode newLocation){

        DynamicTreeNode addingEnd=newLocation;
        DynamicTreeNode addingChild=newLocation;
        DynamicTreeNode deletingEnd=oldLocation;
        DynamicTreeNode deletingChild=oldLocation;
        for(int i=0;i<level;i++){
            addingEnd.database.put(user,addingChild);
            String child=addingEnd.getName();
            addingEnd=addingEnd.parent;
            addingChild=getNodeByName(child);

            deletingEnd.database.remove(user);
            


        }



    }


    public static void main(String[] arg) {


        DynamicTree dt = new DynamicTree();
        File tp = new File("Topology_xc.xml");
        dt.setTopo(tp);

        File userFile = new File("Users.xml");
        dt.initialDatabase_Pointer(userFile);

        //Testing moving user from one place to another.
       // /*
        ((DynamicTreeNode) dt.root.database.get("2601")).printName();

        dt.resetCounter();
        System.out.println("Starting move 2601 from PA to " + dt.leafNodes.get(4).getName());
        dt.updateDatabase_actualPointer("2601", dt.leafNodes.get(4), dt.leafNodes.get(4), true,null,null);


        ((DynamicTreeNode) dt.root.database.get("2601")).printName();
        dt.printUpdateCost("2601");
        dt.resetCounter();
        dt.updateDatabase_actualPointer("2601", dt.leafNodes.get(42), dt.leafNodes.get(42), true,null,null);
        dt.printUpdateCost("2601");
        //*/
///*

        dt.initialDatabase_Value(userFile);
        ((DynamicTreeNode) dt.root.database.get("2602")).printName();
        dt.resetCounter();
        System.out.println("Starting move 2602 from PA to " + dt.leafNodes.get(4).getName());
        dt.updateDatabase_databseValue("2602", dt.leafNodes.get(4), dt.leafNodes.get(4), false,null,null);
        ((DynamicTreeNode) dt.root.database.get("2602")).printName();

        dt.printUpdateCost("2602");
        dt.resetCounter();
        dt.updateDatabase_databseValue("2602", dt.leafNodes.get(42), dt.leafNodes.get(42), false,null,null);
        dt.printUpdateCost("2602");

//*/

    }



}