import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;
import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;
import static com.sleepycat.persist.model.Relationship.ONE_TO_ONE;

@Entity
public class PostLinks {
    @PrimaryKey
    private String postLinkId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String creationDate;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String postId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String relatedPostId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String linkTypeId;



    public PostLinks(){}


    public PostLinks(String postLinkId, String creationDate, String postId, String relatedPostId, String linkTypeId) {
        this.postLinkId = postLinkId;
        this.creationDate = creationDate;
        this.postId = postId;
        this.relatedPostId = relatedPostId;
        this.linkTypeId = linkTypeId;
    }

    public String getPostLinkId() {
        return postLinkId;
    }

    public void setPostLinkId(String postLinkId) {
        this.postLinkId = postLinkId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getRelatedPostId() {
        return relatedPostId;
    }

    public void setRelatedPostId(String relatedPostId) {
        this.relatedPostId = relatedPostId;
    }

    public String getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(String linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public static ArrayList<PostLinks> parsePostLinks(String fileDirectory){
        ArrayList<PostLinks> voteRecords = new ArrayList<>();
        try {
            File fXmlFile = new File( fileDirectory );
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDocument = dBuilder.parse(fXmlFile);
            newDocument.getDocumentElement().normalize();

            NodeList nodeList = newDocument.getElementsByTagName("row");

            for(int i = 0 ; i < nodeList.getLength(); i++){
                Node nodeItem = nodeList.item( i );
                if ( nodeItem.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) nodeItem;
                    PostLinks newPostLinks = new PostLinks(
                            nodeElement.getAttribute("Id"),nodeElement.getAttribute("CreationDate"),nodeElement.getAttribute("PostId"),
                            nodeElement.getAttribute("RelatedPostId"),nodeElement.getAttribute("LinkTypeId")
                    );
                    voteRecords.add( newPostLinks );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }














    public static class PostLinksDataAccessor {
        public PostLinksDataAccessor() {}

        // Open the indices
        public PostLinksDataAccessor(EntityStore store) throws DatabaseException {




            postLinkByPostLinkId = store.getPrimaryIndex(String.class , PostLinks.class);
            postLinkByCreationDate = store.getSecondaryIndex( postLinkByPostLinkId, String.class, "creationDate");
            postLinkByPostId = store.getSecondaryIndex( postLinkByPostLinkId, String.class, "postId");
            postLinkByRelatedPostId = store.getSecondaryIndex( postLinkByPostLinkId, String.class, "relatedPostId");
            postLinkByLinkTypeId = store.getSecondaryIndex( postLinkByPostLinkId, String.class, "linkTypeId");


        }


        PrimaryIndex<String, PostLinks> postLinkByPostLinkId;
        SecondaryIndex<String,String, PostLinks> postLinkByCreationDate;
        SecondaryIndex<String,String, PostLinks> postLinkByPostId;
        SecondaryIndex<String,String, PostLinks> postLinkByRelatedPostId;
        SecondaryIndex<String,String, PostLinks> postLinkByLinkTypeId;


    }






















    public static class PutPostLinks {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private PostLinksDataAccessor sda;


        // The setup() method opens the environment and store for us.
        public void setup() throws DatabaseException {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();
            envConfig.setAllowCreate(true);
            storeConfig.setAllowCreate(true);
            // Open the environment and entity store
            envmnt = new Environment(envHome, envConfig);
            store = new EntityStore(envmnt, "EntityStore", storeConfig);
        }


        // Close our environment and store.
        public void shutdown() throws DatabaseException {
            store.close();
            envmnt.close();
        }



        // Populate the entity store
        public void run() throws DatabaseException {
            setup();
            sda = new PostLinksDataAccessor(store);
            ArrayList<PostLinks> PostLinksList = parsePostLinks( directory.getXmlFileDirectory() + "\\PostLinks.xml");

            for(PostLinks PostLinks: PostLinksList){
                sda.postLinkByPostLinkId.put(PostLinks);
            }

            shutdown();
        }
    }

    public static class GetPostLinks {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private PostLinksDataAccessor sda;



        // The setup() method opens the environment and store for us.
        public void setup() throws DatabaseException {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();
            envConfig.setAllowCreate(true);
            storeConfig.setAllowCreate(true);

            // Open the environment and entity store
            envmnt = new Environment(envHome, envConfig);
            store = new EntityStore(envmnt, "EntityStore", storeConfig);
        }


        public void shutdown() throws DatabaseException {
            store.close();
            envmnt.close();
        }


        // Retrieve some SimpleEntityClass objects from the store.
        public void run() throws DatabaseException {
            setup();

            sda = new PostLinksDataAccessor(store);




            PostLinks readenVote = sda.postLinkByPostLinkId.get("50");
            System.out.println(readenVote.getCreationDate());

            shutdown();
        }
    }
}



