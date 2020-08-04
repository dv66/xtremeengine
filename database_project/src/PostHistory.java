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
public class PostHistory {
    @PrimaryKey
    private String postHistoryId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String postHistoryTypeId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String postId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String revisionGUID;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String creationDate;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String userId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PostHistory(){}


    public PostHistory(String postHistoryId, String postHistoryTypeId, String postId, String revisionGUID,
                       String creationDate, String userId) {
        this.postHistoryId = postHistoryId;
        this.postHistoryTypeId = postHistoryTypeId;
        this.postId = postId;
        this.revisionGUID = revisionGUID;
        this.creationDate = creationDate;
        this.userId = userId;

    }

    public String getPostHistoryId() {
        return postHistoryId;
    }

    public void setPostHistoryId(String postHistoryId) {
        this.postHistoryId = postHistoryId;
    }

    public String getPostHistoryTypeId() {
        return postHistoryTypeId;
    }

    public void setPostHistoryTypeId(String postHistoryTypeId) {
        this.postHistoryTypeId = postHistoryTypeId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getRevisionGUID() {
        return revisionGUID;
    }

    public void setRevisionGUID(String revisionGUID) {
        this.revisionGUID = revisionGUID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }



    public static ArrayList<PostHistory> parsePostHistory(String fileDirectory){
        ArrayList<PostHistory> voteRecords = new ArrayList<>();
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
                    PostHistory newPostHistory = new PostHistory(
                            nodeElement.getAttribute("Id"),nodeElement.getAttribute("PostHistoryTypeId"),nodeElement.getAttribute("PostId"),
                            nodeElement.getAttribute("RevisionGUID"),nodeElement.getAttribute("CreationDate"),nodeElement.getAttribute("UserId")
                    );
                    voteRecords.add( newPostHistory );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }














    public static class PostHistoryDataAccessor {
        public PostHistoryDataAccessor() {}

        // Open the indices
        public PostHistoryDataAccessor(EntityStore store) throws DatabaseException {




            postHistoryByPostHistoryId = store.getPrimaryIndex(String.class , PostHistory.class);
            postHistoryByPostHistoryTypeId = store.getSecondaryIndex( postHistoryByPostHistoryId, String.class, "postHistoryTypeId");
            postHistoryByPostId = store.getSecondaryIndex( postHistoryByPostHistoryId, String.class, "postId");
            postHistoryByRevisionGUID = store.getSecondaryIndex( postHistoryByPostHistoryId, String.class, "revisionGUID");
            postHistoryByCreationDate = store.getSecondaryIndex( postHistoryByPostHistoryId, String.class, "creationDate");
            postHistoryByUserId = store.getSecondaryIndex( postHistoryByPostHistoryId, String.class, "userId");


        }


        PrimaryIndex<String, PostHistory> postHistoryByPostHistoryId;
        SecondaryIndex<String,String, PostHistory> postHistoryByPostHistoryTypeId;
        SecondaryIndex<String,String, PostHistory> postHistoryByPostId;
        SecondaryIndex<String,String, PostHistory> postHistoryByRevisionGUID;
        SecondaryIndex<String,String, PostHistory> postHistoryByCreationDate;
        SecondaryIndex<String,String, PostHistory> postHistoryByUserId;



    }






















    public static class PutPostHistory {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private PostHistoryDataAccessor sda;


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
            sda = new PostHistoryDataAccessor(store);
            ArrayList<PostHistory> PostHistoryList = parsePostHistory( directory.getXmlFileDirectory() + "\\PostHistory.xml");

            for(PostHistory PostHistory: PostHistoryList){
                sda.postHistoryByPostHistoryId.put(PostHistory);
            }

            shutdown();
        }
    }

    public static class GetPostHistory {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private PostHistoryDataAccessor sda;



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

            sda = new PostHistoryDataAccessor(store);




            PostHistory readenVote = sda.postHistoryByPostHistoryTypeId.get("14");
            System.out.println(readenVote.getPostHistoryTypeId());

            shutdown();
        }
    }
}



