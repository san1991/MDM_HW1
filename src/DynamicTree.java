
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

/**
 * Created by Administrator on 2/8/2016.
 */
public class DynamicTree {
    public DynamicTreeNode root;


    public DynamicTree(DynamicTreeNode root){
        this.root=root;
    }

    public static void main(String[] arg){

        DelegateTree<String, String> tree = new DelegateTree<String,String>();
        DelegateTree<DynamicTreeNode, String> t = new DelegateTree<DynamicTreeNode,String>();
        tree.setRoot("A");
        tree.addChild("A-B1", "A", "B1");
        tree.addChild("A-B2", "A", "B2");
        tree.addChild("B1-C1","B1","C1");
        tree.addChild("B1-C2","B1","C2");




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
