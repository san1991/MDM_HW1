
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


    public DynamicTree(){
        this.leafNodes=new ArrayList<DynamicTreeNode>();
    };
    public DynamicTree(DynamicTreeNode root){
        this.leafNodes=new ArrayList<DynamicTreeNode>();
        this.root=root;
    }

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
