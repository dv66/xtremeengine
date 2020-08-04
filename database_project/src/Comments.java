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
public class Comments {
    @PrimaryKey
    private String commentId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String postId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String score;
    @SecondaryKey(relate=ONE_TO_ONE)
    private String text;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String creationDate;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String userId;





    public Comments(){}


    public Comments(String commentId, String postId, String score, String text, String creationDate, String userId) {
        this.commentId = commentId;
        this.postId = postId;
        this.score = score;
        this.text = text;
        this.creationDate = creationDate;
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static ArrayList<Comments> parseComments(String fileDirectory){
        ArrayList<Comments> voteRecords = new ArrayList<>();
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
                    Comments newComments = new Comments(
                            nodeElement.getAttribute("Id"),nodeElement.getAttribute("PostId"),nodeElement.getAttribute("Score"),
                            nodeElement.getAttribute("Text"),nodeElement.getAttribute("CreationDate"),nodeElement.getAttribute("UserId")
                    );
                    voteRecords.add( newComments );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }














    public static class CommentsDataAccessor {
        public CommentsDataAccessor() {}

        // Open the indices
        public CommentsDataAccessor(EntityStore store) throws DatabaseException {




            commentByCommentId = store.getPrimaryIndex(String.class , Comments.class);
            commentByPostId = store.getSecondaryIndex( commentByCommentId, String.class, "postId");
            commentByScore = store.getSecondaryIndex( commentByCommentId, String.class, "score");
            commentByText = store.getSecondaryIndex( commentByCommentId, String.class, "text");
            commentByCreationDate = store.getSecondaryIndex( commentByCommentId, String.class, "creationDate");
            commentByUserId = store.getSecondaryIndex( commentByCommentId, String.class, "userId");

        }


        PrimaryIndex<String, Comments> commentByCommentId;
        SecondaryIndex<String,String, Comments> commentByPostId;
        SecondaryIndex<String,String, Comments> commentByScore;
        SecondaryIndex<String,String, Comments> commentByText;
        SecondaryIndex<String,String, Comments> commentByCreationDate;
        SecondaryIndex<String,String, Comments> commentByUserId;



    }






















    public static class PutComments {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private CommentsDataAccessor sda;


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
            sda = new CommentsDataAccessor(store);
            ArrayList<Comments> CommentsList = parseComments( directory.getXmlFileDirectory() + "\\Comments.xml");

            for(Comments Comments: CommentsList){
                sda.commentByCommentId.put(Comments);
            }

            shutdown();
        }
    }

    public static class GetComments {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private CommentsDataAccessor sda;



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

            sda = new CommentsDataAccessor(store);




            Comments readenVote = sda.commentByCommentId.get("14");
            System.out.println(readenVote.getText());

            shutdown();
        }
    }
}



