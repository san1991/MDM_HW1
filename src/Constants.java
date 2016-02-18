import java.io.File;

/**
 * Created by azhar on 2/16/16.
 */
public class Constants {
    public static String MODE_DATABASE_VALUES="Database Values";
    public static String MODE_ACTUAL_POINTERS="Actual Pointers";
    public static String MODE_DATABASE_FORWARD_POINTERS="Database with Forwarding Pointers";
    public static String MODE_POINTER_FORWARD_POINTERS="Actual Pointer with Forwarding Pointers";
    public static String MODE_REPLICATION_DATABASE="Database with Replication";
    public static String MODE_REPLICATION_POINTER="Actual Pointer Replication";


    public static File TOPOLOGY_FILE = new File("Topology_xc.xml");
    public static File USER_FILE = new File("Users.xml");

    public static String CURRENT_MODE;

    //for node coloring
    public static int NODE_COLOR_DEFAULT=0;
    public static int NODE_COLOR_DELETED=1;
    public static int NODE_COLOR_UPDATED=2;

}
