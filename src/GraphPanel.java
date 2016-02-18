
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

import com.sun.javaws.Main;
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


    public static int edgeName=0;
    public static void main(String arg[]){

        DynamicTree dt = new DynamicTree();

        dt.setTopo(Constants.TOPOLOGY_FILE);

        Graph<DynamicTreeNode,Number> graph=createGraph(dt);



        Forest<DynamicTreeNode,Number> tree;

        VisualizationViewer<DynamicTreeNode,Number> vv2;
        VisualizationViewer<DynamicTreeNode,Number> vv0;


        Dimension preferredSize = new Dimension(1500,800);
        Dimension preferredLayoutSize = new Dimension(1500,800);
        Dimension preferredSizeRect = new Dimension(1500,800);


        MinimumSpanningForest2<DynamicTreeNode,Number> prim =
                new MinimumSpanningForest2<DynamicTreeNode,Number>(graph,
                        new DelegateForest<DynamicTreeNode,Number>(), DelegateTree.<DynamicTreeNode,Number>getFactory(),
                        new ConstantTransformer(1.0));


        tree = prim.getForest();

        // create two layouts for the one graph, one layout for each model


        Layout<DynamicTreeNode,Number> layout0 = new KKLayout<DynamicTreeNode,Number>(graph);
        layout0.setSize(preferredLayoutSize);
        Layout<DynamicTreeNode,Number> layout1 = new TreeLayout<DynamicTreeNode,Number>(tree,33,140);
        Layout<DynamicTreeNode,Number> layout2 = new StaticLayout<DynamicTreeNode,Number>(graph, layout1);


        VisualizationModel<DynamicTreeNode,Number> vm0 =
                new DefaultVisualizationModel<DynamicTreeNode,Number>(layout0, preferredSize);
        VisualizationModel<DynamicTreeNode,Number> vm2 = new DefaultVisualizationModel<DynamicTreeNode,Number>(layout2, preferredSizeRect);

        // adding transformer for fixing vertex size

        Transformer<DynamicTreeNode,Shape> vertexSize = new Transformer<DynamicTreeNode,Shape>(){
            public Shape transform(DynamicTreeNode i){
                Ellipse2D circle = new Ellipse2D.Double(-7, -7, 14, 14);
                // in this case, the vertex is twice as large
                return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
                //else return circle;
            }
        };

        Transformer<DynamicTreeNode,Paint> vertexColor = new Transformer<DynamicTreeNode,Paint>() {
            public Paint transform(DynamicTreeNode h) {
                if(h.nodeColor==Constants.NODE_COLOR_DEFAULT)
                    return Color.YELLOW;
                else if (h.nodeColor==Constants.NODE_COLOR_UPDATED)
                    return Color.GREEN;
                else    // if (h.nodeColor==Constants.NODE_COLOR_DELETED)
                    return Color.RED;

            }
        };


        vv0 = new VisualizationViewer<DynamicTreeNode,Number>(vm0, preferredSize);
        vv2 = new VisualizationViewer<DynamicTreeNode,Number>(vm2, preferredSizeRect);


        vv2.getRenderContext().setVertexFillPaintTransformer(vertexColor);
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




        //code for mode selection and user interaction

        ControlUI controlUI = new ControlUI(dt,vv2);
        controlUI.addBaseStationsInDropdown();
        controlUI.setVisible(true);


        // code for mode simulation


        //for actual pointers



    }

    private static Graph<DynamicTreeNode, Number> createGraph(DynamicTree dt) {
        Graph<DynamicTreeNode, Number> graph =   new DirectedOrderedSparseMultigraph<DynamicTreeNode, Number>();

        //Graph<String, Number> graph =   new DirectedSparseGraph<String, Number>();

        String[] vertex=new String[dt.size];
        ArrayDeque<DynamicTreeNode> nodeQue=new ArrayDeque<DynamicTreeNode>();
        nodeQue.add(dt.root);


        while(!nodeQue.isEmpty()){
            DynamicTreeNode dtn=nodeQue.poll();
            DynamicTreeNode dtnParent=nodeQue.poll();
            graph.addVertex(dtn);

            if(dtnParent!=null){
                graph.addEdge(edgeName,dtnParent,dtn);
                edgeName++;
            }

            for(DynamicTreeNode children:dtn.children){
                nodeQue.add(children);
                nodeQue.add(dtn);
            }
        }


        return graph;
    }


}

