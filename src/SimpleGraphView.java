import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * Created by azhar on 2/11/16.
 */
public class SimpleGraphView {

    public SimpleGraphView() {
        // Create a graph with Integer vertices and String edges
        DelegateTree<Integer, String> g = new DelegateTree<Integer,String>();

        g.setRoot(1);
        g.addChild("A-B1", 1 , 2);
        g.addChild("A-B2", 1, 3);
        g.addChild("B1-C1",2,4);
        g.addChild("B1-C2",2,5);
        g.addEdge("test",2,6);



        // Layout implements the graph drawing logic
        //Layout<Integer, String> layout = new CircleLayout<Integer, String>(g);

        TreeLayout<Integer, String> layout = new TreeLayout<Integer, String>(g,150,150);
//        layout.setSize(new Dimension(300,300));

        // VisualizationServer actually displays the graph
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(1500,800)); //Sets the viewing area size

        // Transformer maps the vertex number to a vertex property
        Transformer<Integer,Paint> vertexColor = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                if(i == 1) return Color.GREEN;
                return Color.RED;
            }
        };
        Transformer<Integer,Shape> vertexSize = new Transformer<Integer,Shape>(){
            public Shape transform(Integer i){
                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
                // in this case, the vertex is twice as large
                return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
                //else return circle;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());


        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new SimpleGraphView();
    }
}
