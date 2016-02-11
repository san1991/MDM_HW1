
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.*;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
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

    public static void updateDatabase_actualPointer(String user,DynamicTreeNode childNode,DynamicTreeNode parentNode,boolean up){

        if(up) {
            if (parentNode.database.containsKey(user) && !parentNode.database.get(user).equals(childNode)) {
                parentNode.database.put(user, childNode);
                if (parentNode.parent != null)
                    updateDatabase_actualPointer(user, parentNode, parentNode.parent,up);

            }
            else if (!parentNode.database.containsKey(user)) {
                parentNode.database.put(user, childNode);
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
                for(DynamicTreeNode dtn:parentNode.children){
                    updateDatabase_actualPointer(user,childNode,dtn,!up);
                }
            }
        }


    }

    public void initialDatabase_Value(File file){

    }








    public static void main(String[] arg){

        DelegateTree<String, String> tree = new DelegateTree<String,String>();
        DelegateTree<DynamicTreeNode, String> t = new DelegateTree<DynamicTreeNode,String>();
        tree.setRoot("A");
        tree.addChild("A-B1", "A", "B1");
        tree.addChild("A-B2", "A", "B2");
        tree.addChild("B1-C1","B1","C1");
        tree.addChild("B1-C2","B1","C2");

        DynamicTree dt=new DynamicTree();
        File tp=new File("Topology_xc.xml");
        dt.setTopo(tp);
        //dt.root.printName();
        //dt.root.children.get(0).printName();
        //dt.leafNodes.get(0).printName();
        File userFile=new File("Users.xml");
        dt.initialDatabase_Pointer(userFile);
        ((DynamicTreeNode)dt.root.database.get("2601")).printName();

        updateDatabase_actualPointer("2601",dt.leafNodes.get(40),dt.leafNodes.get(40),true);
        ((DynamicTreeNode)dt.root.database.get("2601")).printName();


        BasicVisualizationServer<String, String> vs = new BasicVisualizationServer<String, String>(new FRLayout<String,String>(tree), new Dimension(600, 500));


        Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>() {
            public Paint transform(String i) {
                return Color.GRAY;
            }
        };



        RenderContext<String, String> renderContext = vs.getRenderContext();
        renderContext.setVertexFillPaintTransformer(vertexPaint);

        Transformer<String, String> transformer = new ToStringLabeller<String>();
        renderContext.setEdgeLabelTransformer(transformer);
        Transformer<String, String> vertexTransformer = new ToStringLabeller<String>();
        renderContext.setVertexLabelTransformer(vertexTransformer);
        vs.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);



        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
