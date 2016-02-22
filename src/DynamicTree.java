

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.util.ArrayDeque;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import java.util.*;


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

    public void sortLeafNodes(){
        Collections.sort(this.leafNodes,new Comparator<DynamicTreeNode>() {
            @Override
            public int compare(DynamicTreeNode o1, DynamicTreeNode o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
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
        if(!this.user_mobility_count.containsKey(MH))this.user_mobility_count.put(MH,i);
        else
        this.user_mobility_count.put(MH,user_mobility_count.get(MH)+i);

    }

    public void initialUserCallMetric(String MH){
        HashMap<DynamicTreeNode, Integer> hostList= user_call_count.get(MH);

        if (hostList==null){
            hostList= new HashMap<DynamicTreeNode, Integer>();
            for(DynamicTreeNode leaf:this.leafNodes){
                hostList.put(leaf,0);
            }

            user_call_count.put(MH, hostList);
        }


    }

    public void initialUserMobilityMetric(String MH){
        this.user_mobility_count.put(MH,1);
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


                                            updateDatabase_actualPointer(userNum, dt, dt, true,new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());
                                            initialUserMobilityMetric(userNum);
                                            initialUserCallMetric(userNum);

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

                                            updateDatabase_databseValue(userNum, dt, dt, false,new ArrayList<DynamicTreeNode>(), new ArrayList<DynamicTreeNode>());
                                            this.testDatabase.put(userNum,dt);
                                            initialUserMobilityMetric(userNum);
                                            initialUserCallMetric(userNum);
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
                updatedNodes.add(parentNode);
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
                updatedNodes.add(parentNode);
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
                deletedNodes.add(parentNode);
                //show how the database be updated (deleting entry)
                //System.out.println("delete in node: "+parentNode.getName());


                for (DynamicTreeNode dtn : parentNode.children) {
                    updateDatabase_actualPointer(user, childNode, dtn, up,updatedNodes,deletedNodes);
                }
            }
        }


    }


    // change gui in here
    public void updateDatabase_databseValue(String user, DynamicTreeNode childNode, DynamicTreeNode parentNode, boolean isDelete,
                                            ArrayList<DynamicTreeNode> updatedNodes,ArrayList<DynamicTreeNode> deletedNodes) {

        if(!isDelete){
            if (!parentNode.database.containsKey(user)) {
                parentNode.database.put(user, childNode);
                this.add++;
                this.total = this.add + this.delete + this.update;
                updatedNodes.add(parentNode);
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
                updatedNodes.add(parentNode);
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
                    deletedNodes.add(parentNode);
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

    public void updateDatabase_forwarding_address(String user, int level, DynamicTreeNode oldLocation,DynamicTreeNode newLocation,
                                                  ArrayList<DynamicTreeNode> updatedNodes,ArrayList<DynamicTreeNode> deletedNodes){

        DynamicTreeNode addingEnd=newLocation;
        DynamicTreeNode addingChild=newLocation;
        String child = addingEnd.getName();
        DynamicTreeNode deletingEnd=oldLocation;
        DynamicTreeNode deletingChild=oldLocation;
        String dChild = deletingEnd.getName();
        for(int i=0;i<level;i++){
            if(!addingEnd.parent.equals(deletingEnd.parent)) {
                System.out.println("adding in node: "+addingEnd.getName());
                addingEnd.database.put(user, addingChild);
                //
                updatedNodes.add(addingEnd);

                addingEnd = addingEnd.parent;
                addingChild = getNodeByName(child);
                this.add++;
                this.total = this.add + this.delete + this.update;
                System.out.println("deleting in node: "+deletingEnd.getName());
                deletingEnd.database.remove(user);
                deletedNodes.add(deletingEnd);

                deletingEnd = deletingEnd.parent;

                this.delete++;
                this.total = this.add + this.delete + this.update;
            }else break;
        }


        addingChild = getNodeByName(child);
        if(level==0){
            addingChild.database.put(user,addingChild);
            updatedNodes.add(addingChild);
            System.out.println("adding in node: "+addingChild.getName());
            this.add++;
        }
        deletingChild = getNodeByName(dChild);
        deletingChild.database.put(user,addingChild);

        updatedNodes.add(deletingChild);

        System.out.println("updating in node: "+deletingChild.getName());

        this.update++;

        this.total = this.add + this.delete + this.update;

    }

    public void updateDatabase_forwarding_pointer(String user, int level, DynamicTreeNode oldLocation,DynamicTreeNode newLocation,
                                                  ArrayList<DynamicTreeNode> updatedNodes,ArrayList<DynamicTreeNode> deletedNodes){

        DynamicTreeNode addingEnd=newLocation;
        DynamicTreeNode addingChild=newLocation;
        DynamicTreeNode deletingEnd=oldLocation;
        DynamicTreeNode deletingChild=oldLocation;
        for(int i=0;i<level;i++){
            if(!addingEnd.parent.equals(deletingEnd.parent)) {
                System.out.println("adding in node: "+addingEnd.getName());
                addingEnd.database.put(user, addingChild);
                //
                updatedNodes.add(addingEnd);
                String child = addingEnd.getName();
                addingEnd = addingEnd.parent;
                addingChild = getNodeByName(child);
                this.add++;
                this.total = this.add + this.delete + this.update;
                System.out.println("deleting in node: "+deletingEnd.getName());
                deletingEnd.database.remove(user);
                deletedNodes.add(deletingEnd);
                String dChild = deletingEnd.getName();
                deletingEnd = deletingEnd.parent;
                deletingChild = getNodeByName(dChild);
                this.delete++;
                this.total = this.add + this.delete + this.update;
            }else break;
        }

        if(level==0) {
            addingEnd.database.put(user, addingChild);
            updatedNodes.add(addingEnd);
            System.out.println("adding in node: " + addingEnd.getName());
            this.add++;
        }
        deletingEnd.database.put(user,addingEnd);
        updatedNodes.add(deletingEnd);
        System.out.println("updating in node: "+deletingEnd.getName());
        this.update++;
        this.total = this.add + this.delete + this.update;


    }

    public int getCMR(String user, DynamicTreeNode node){

        return this.user_call_count.get(user).get(node)/this.user_mobility_count.get(user);
    }

    public int getAggCMR(String user,DynamicTreeNode node){
        ArrayDeque<DynamicTreeNode> n=new ArrayDeque<DynamicTreeNode>();
        n.add(node);
        int aggCMR=0;
        while(!n.isEmpty()){
            DynamicTreeNode current=n.poll();
            if(current.children.size()==0){
                aggCMR+=getCMR(user,current);
            }else{
                for(DynamicTreeNode cc:current.children){
                    n.add(cc);
                }
            }
        }
        return aggCMR;
    }

    public int findForwardingLevel(String user,DynamicTreeNode oldLocation){
        int level=0;
        int result=level;
        int max=getCMR(user,oldLocation);
        int lastLevel=max;
        while(oldLocation.parent!=null){
            level++;
            oldLocation=oldLocation.parent;

            int aggCMR=getAggCMR(user,oldLocation);
            if(aggCMR-lastLevel>max){
                max=aggCMR-lastLevel;
                result=level;
            }
            lastLevel=aggCMR;
        }
        System.out.println(result);
        return result;
    }

    public void replication(String user, int maxRepli,int maxLevel,int Smax,int Smin,DynamicTreeNode current,ArrayList<DynamicTreeNode> levelNodes,
                            ArrayList<DynamicTreeNode> addReplication,ArrayList<DynamicTreeNode> deleteReplication){

        if(maxLevel>=0) {
            ArrayList<DynamicTreeNode> checking = new ArrayList<DynamicTreeNode>();

            for (DynamicTreeNode dtn : levelNodes) {
                if (!dtn.isInLine(current) && dtn.database.containsKey(user)) {
                    dtn.database.remove(user);
                    System.out.println("delete " + user + "'s information from " + dtn.getName());
                    deleteReplication.add(dtn);
                }
                if (this.getAggCMR(user, dtn) > Smax) {
                    checking.add(dtn);
                }
            }
            if (checking.size() != 0) {
                final String u = user;
                Collections.sort(checking, new Comparator<DynamicTreeNode>() {
                    @Override
                    public int compare(DynamicTreeNode o1, DynamicTreeNode o2) {
                        if (getAggCMR(u, o1) == getAggCMR(u, o2))
                            return 0;
                        return getAggCMR(u, o1) < getAggCMR(u, o2) ? -1 : 1;
                    }
                });

                if (maxRepli >= checking.size()) {
                    for (DynamicTreeNode ln : checking) {
                        ln.database.put(user, current);
                        System.out.println("Replicating " + user + "'s information from " + current.getName() + " to node: " + ln.getName());
                        addReplication.add(ln);
                    }
                    ArrayList<DynamicTreeNode> nextLevel = new ArrayList<DynamicTreeNode>();
                    for (DynamicTreeNode levelNode : levelNodes) {
                        if (levelNode.parent != null) {
                            if (!nextLevel.contains(levelNode.parent)) nextLevel.add(levelNode.parent);
                        }
                    }

                    replication(user, maxRepli - checking.size(), maxLevel - 1, Smax, Smin, current, nextLevel, addReplication, deleteReplication);


                } else {
                    for (int i = 0; i < maxRepli; i++) {
                        checking.get(i).database.put(user, current);
                        System.out.println("Replicating " + user + "'s information from " + current.getName() + " to node: " + checking.get(i).getName());
                        addReplication.add(checking.get(i));
                    }
                }

            }
        }
    }


    public void makeCall(DynamicTreeNode srcBaseStation, String callee, ArrayList<DynamicTreeNode> baseStations){

        baseStations.add(srcBaseStation);

        if (srcBaseStation.database.containsKey(callee) && !(srcBaseStation.database.get(callee).equals(srcBaseStation))){
            makeCall((DynamicTreeNode)srcBaseStation.database.get(callee), callee,baseStations);
        }else if (!srcBaseStation.database.containsKey(callee) && srcBaseStation.parent!=null){
            makeCall(srcBaseStation.parent,callee,baseStations);
        }

    }



    public static void main(String[] arg) {


        DynamicTree dt = new DynamicTree();
        File tp = new File("Topology_xc.xml");
        dt.setTopo(tp);

        File userFile = new File("Users.xml");
        dt.initialDatabase_Pointer(userFile);

        //System.out.println(dt.user_call_count.get("2203").get(dt.getCallerLocation("2203")));
        //Testing moving user from one place to another.
       // /*
        ((DynamicTreeNode) dt.root.database.get("2601")).printName();

        dt.resetCounter();
        System.out.println("Starting move 2601 from PA to " + dt.leafNodes.get(4).getName());
        dt.updateDatabase_actualPointer("2601", dt.leafNodes.get(4), dt.leafNodes.get(4), true,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());


        ((DynamicTreeNode) dt.root.database.get("2601")).printName();
        dt.printUpdateCost("2601");
        dt.resetCounter();
        dt.updateDatabase_actualPointer("2601", dt.leafNodes.get(42), dt.leafNodes.get(42), true,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());
        dt.printUpdateCost("2601");
        //*/
///*

        dt.initialDatabase_Value(userFile);
        ((DynamicTreeNode) dt.root.database.get("2602")).printName();
        dt.resetCounter();
        System.out.println("Starting move 2602 from PA to " + dt.leafNodes.get(4).getName());
        dt.updateDatabase_databseValue("2602", dt.leafNodes.get(4), dt.leafNodes.get(4), false,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());
        ((DynamicTreeNode) dt.root.database.get("2602")).printName();

        dt.printUpdateCost("2602");
        dt.resetCounter();
        dt.updateDatabase_databseValue("2602", dt.leafNodes.get(42), dt.leafNodes.get(42), false,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());
        dt.printUpdateCost("2602");

//*/

        dt.initialDatabase_Pointer(userFile);
        dt.resetCounter();
        System.out.println("Starting move 2603 from PA to " + dt.leafNodes.get(34).getName());
        DynamicTreeNode callerLocation=dt.getCallerLocation("2603");
        dt.updateUserCallMetric("2603",dt.leafNodes.get(4),50);
        dt.updateDatabase_forwarding_pointer("2603",dt.findForwardingLevel("2603",callerLocation),callerLocation,dt.leafNodes.get(34),
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());

        dt.printUpdateCost("2603");


        dt.initialDatabase_Pointer(userFile);
        dt.resetCounter();
        System.out.println("Testing replication for user 3601");
        System.out.println("increase call number from node: "+ dt.leafNodes.get(8).getName());
        dt.updateUserCallMetric("3601",dt.leafNodes.get(8),100);

        dt.replication("3601",3,1,10,5,dt.getCallerLocation("3601"),dt.leafNodes,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());

        System.out.println("increase call number from node: "+ dt.leafNodes.get(23).getName());
        dt.updateUserCallMetric("3601",dt.leafNodes.get(23),100);
        dt.replication("3601",3,1,10,5,dt.getCallerLocation("3601"),dt.leafNodes,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());

        System.out.println("increase call number from node: "+ dt.leafNodes.get(38).getName());
        dt.updateUserCallMetric("3601",dt.leafNodes.get(38),100);
        dt.replication("3601",3,1,10,5,dt.getCallerLocation("3601"),dt.leafNodes,
                new ArrayList<DynamicTreeNode>(),new ArrayList<DynamicTreeNode>());

    }



}