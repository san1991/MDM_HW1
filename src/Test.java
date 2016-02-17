import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.MinimumSpanningTreeDemo;
import edu.uci.ics.jung.samples.WorldMapGraphDemo;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
//import sun.plugin.dom.core.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;

/**
 * Created by azhar on 2/11/16.
 */
public class Test {

    public static void main(String arg[]){

        /*
        Test obj = new Test();
        Graph<String,String> graph=obj.createGraph();

        //Forest<String, String> my_graph_model = new DelegateForest<String, String>((DirectedOrderedSparseMultigraph<String, String>)graph);
        //BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(my_graph_model);

        BasicVisualizationServer<String, String> visualizationServer = obj.createServer(graph);

        visualizationServer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());

        JFrame frame = new JFrame();
        frame.getContentPane().add(visualizationServer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        */


        /*
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new MinimumSpanningTreeDemo());
        f.pack();
        f.setVisible(true);


        /*
        final JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        content.add(new WorldMapGraphDemo());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        */

    }



    private VisualizationImageServer<String, String> createServer(
            final Graph<String, String> aGraph) {
        final Layout<String, String> layout = new FRLayout2<String, String>(
                aGraph);

        layout.setSize(new Dimension(300, 300));
        final VisualizationImageServer<String, String> vv =
                new VisualizationImageServer<String, String>(
                        layout, new Dimension(350, 350));
        vv.getRenderContext().setVertexLabelTransformer(
                new ToStringLabeller<String>());
        return vv;
    }

    private Graph<String, String> createGraph() {
        final Graph<String, String> graph =
                new DirectedSparseMultigraph<String, String>();
        final String vertex1 = "IE";
        final String vertex2 = "P1";
        final String vertex3 = "P2";
        final String vertex4 = "P3";
        final String vertex5 = "FE";

        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);
        graph.addVertex(vertex4);
        graph.addVertex(vertex5);

        graph.addEdge("1", vertex1, vertex2, EdgeType.DIRECTED);
        graph.addEdge("2", vertex2, vertex3, EdgeType.DIRECTED);
        graph.addEdge("3", vertex3, vertex5, EdgeType.DIRECTED);
        graph.addEdge("4", vertex1, vertex4, EdgeType.DIRECTED);
        graph.addEdge("5", vertex4, vertex5, EdgeType.DIRECTED);
        graph.addEdge("6", vertex3, vertex2, EdgeType.DIRECTED);
        graph.addEdge("7", vertex2, vertex4, EdgeType.DIRECTED);
        return graph;
    }
}
