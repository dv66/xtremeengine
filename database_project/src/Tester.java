import com.sleepycat.je.DatabaseException;

/**
 * Created by User on 7/9/2017.
 */
public class Tester {
    public static void main(String args[]) {
        PostLinks.GetPostLinks ssp = new PostLinks.GetPostLinks();
        try {
            ssp.run();
        } catch (DatabaseException dbe) {
            System.err.println("PutTags: " + dbe.toString());
            dbe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("All done.");
    }

}
