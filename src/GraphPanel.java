/**
 * Created by azhar on 2/10/16.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayDeque;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

public class GraphPanel {


    public static void main(String arg[]){

        DynamicTree dt = new DynamicTree();
        File tp = new File("Topology_xc.xml");
        dt.setTopo(tp);

        File userFile = new File("Users.xml");
        dt.initialDatabase_Pointer(userFile);



        Graph<String,Number> graph=createGraph(dt);
        Forest<String,Number> tree;

        VisualizationViewer<String,Number> vv2;
        VisualizationViewer<String,Number> vv0;


        ///*
        Dimension preferredSize = new Dimension(1500,800);
        Dimension preferredLayoutSize = new Dimension(1500,800);
        Dimension preferredSizeRect = new Dimension(1500,800);
        //*/
        /*
        Dimension preferredSize = new Dimension(300,300);
        Dimension preferredLayoutSize = new Dimension(400,400);
        Dimension preferredSizeRect = new Dimension(500,250);
        */
        MinimumSpanningForest2<String,Number> prim =
                new MinimumSpanningForest2<String,Number>(graph,
                        new DelegateForest<String,Number>(), DelegateTree.<String,Number>getFactory(),
                        new ConstantTransformer(1.0));


        tree = prim.getForest();

        // create two layouts for the one graph, one layout for each model


        Layout<String,Number> layout0 = new KKLayout<String,Number>(graph);
        layout0.setSize(preferredLayoutSize);
        Layout<String,Number> layout1 = new TreeLayout<String,Number>(tree,30,100);
        Layout<String,Number> layout2 = new StaticLayout<String,Number>(graph, layout1);


        VisualizationModel<String,Number> vm0 =
                new DefaultVisualizationModel<String,Number>(layout0, preferredSize);
        VisualizationModel<String,Number> vm2 = new DefaultVisualizationModel<String,Number>(layout2, preferredSizeRect);


        // adding transformer for fixing vertex size

        Transformer<String,Shape> vertexSize = new Transformer<String,Shape>(){
            public Shape transform(String i){
                Ellipse2D circle = new Ellipse2D.Double(-7, -7, 14, 14);
                // in this case, the vertex is twice as large
                return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
                //else return circle;
            }
        };



        vv0 = new VisualizationViewer<String,Number>(vm0, preferredSize);
        vv2 = new VisualizationViewer<String,Number>(vm2, preferredSizeRect);


        vv2.getRenderContext().setMultiLayerTransformer(vv0.getRenderContext().getMultiLayerTransformer());
        vv2.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv2.getRenderContext().setVertexShapeTransformer(vertexSize);

        Color back = Color.decode("0xffffbb");

        vv2.setBackground(back);

        vv2.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        //vv2.setForeground(Color.darkGray);


        vv2.setLayout(new BorderLayout());

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv2);
        frame.pack();
        frame.setVisible(true);


    }

    private static Graph<String, Number> createGraph(DynamicTree dt) {
        Graph<String, Number> graph =   new DirectedOrderedSparseMultigraph<String, Number>();

        //Graph<String, Number> graph =   new DirectedSparseGraph<String, Number>();

        String[] vertex=new String[dt.size];
        ArrayDeque<DynamicTreeNode> nodeQue=new ArrayDeque<DynamicTreeNode>();
        nodeQue.add(dt.root);

        int j=0;
        while(!nodeQue.isEmpty()){
            DynamicTreeNode dtn=nodeQue.poll();
            DynamicTreeNode dtnParent=nodeQue.poll();
            graph.addVertex(dtn.getName());

            if(dtnParent!=null){
                graph.addEdge(j,dtnParent.getName(),dtn.getName());
                j++;
            }

            for(DynamicTreeNode children:dtn.children){
                nodeQue.add(children);
                nodeQue.add(dtn);
            }
        }


        return graph;
    }


}