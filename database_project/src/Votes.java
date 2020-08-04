
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

/**
 * Created by User on 5/23/2017.
 */
@Entity
public class Votes {
    @PrimaryKey
    private String voteId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String postId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String voteTypeId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String creationDate;



    public Votes(String voteId , String postId , String voteTypeId , String creationDate){
        this.voteId = voteId;
        this.postId = postId;
        this.voteTypeId = voteTypeId;
        this.creationDate = creationDate;
    }

    public Votes() {}

    public void setVoteId(String voteId){ this.voteId = voteId;}
    public void setPostId(String postId){ this.postId = postId;}
    public void setVoteTypeId(String voteTypeId){ this.voteTypeId = voteTypeId;}
    public void setCreationDate(String creationDate){ this.creationDate = creationDate;}

    public String getVoteId(){return voteId;}
    public String getPostId(){ return postId;}
    public String getVoteTypeId(){return voteTypeId;}
    public String getCreationDate(){return creationDate;}





    public  ArrayList<Votes> parseVotes(String fileDirectory){
        ArrayList<Votes> voteRecords = new ArrayList<>();
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
                    Votes newVotes = new Votes(
                      nodeElement.getAttribute("Id"),nodeElement.getAttribute("PostId"),
                      nodeElement.getAttribute("VoteTypeId"),nodeElement.getAttribute("CreationDate")
                    );
                    voteRecords.add( newVotes );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private   class votesDataAccessor {
        public votesDataAccessor() {}
    
        // Open the indices
        public votesDataAccessor(EntityStore store) throws DatabaseException {
    
    
            // Primary key for SimpleEntityClass classes
            voteByVoteId = store.getPrimaryIndex( String.class, Votes.class);
            voteByPostId = store.getSecondaryIndex( voteByVoteId, String.class, "postId");
            voteByVoteTypeId = store.getSecondaryIndex(voteByVoteId, String.class, "voteTypeId");
            voteByCreationDate = store.getSecondaryIndex(voteByVoteId, String.class , "creationDate");
        }
    
    
        // Index Accessors
    
        PrimaryIndex<String, Votes> voteByVoteId;
        SecondaryIndex<String,String, Votes> voteByPostId;
        SecondaryIndex<String,String, Votes> voteByVoteTypeId;
        SecondaryIndex<String,String, Votes> voteByCreationDate;
    }

  private   class PutVotes {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private votesDataAccessor sda;
    
    
    //    public  void main(String args[]) {
    //        putVotes ssp = new putVotes();
    //        try {
    //            ssp.run();
    //        } catch (DatabaseException dbe) {
    //            System.err.println("putVotes: " + dbe.toString());
    //            dbe.printStackTrace();
    //        } catch (Exception e) {
    //            System.out.println("Exception: " + e.toString());
    //            e.printStackTrace();
    //        }
    //        System.out.println("All done.");
    //    }
    
    
    
    
    
    
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
        private void run() throws DatabaseException {
            setup();
            sda = new votesDataAccessor(store);
            ArrayList<Votes> votesList = parseVotes( directory.getXmlFileDirectory() + "\\Votes.xml");
    
            for(Votes votes: votesList){
                sda.voteByVoteId.put(votes);
            }
    
            shutdown();
        }
    }

    private class GetVotes {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private votesDataAccessor sda;
    
    
    
    
        public  void main(String args[]) {
            GetVotes ssg = new GetVotes();
    
    
            try {
                ssg.run();
            } catch (DatabaseException dbe) {
                System.err.println("SimpleStoreGet: " + dbe.toString());
                dbe.printStackTrace();
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
                e.printStackTrace();
            }
    
            System.out.println("All done.");
        }
    
    
    
    
    
    
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
        private void run() throws DatabaseException {
            setup();
    
            sda = new votesDataAccessor(store);
    
    
    
    
            Votes readenVote = sda.voteByVoteId.get("12205");
    
    
            System.out.println("VoteId: "  +readenVote.getVoteId()+
                    "\nPostId: " + readenVote.getPostId() +
                    "\nVoteTypeId: " + readenVote.getVoteTypeId() +
                    "\nCreationDate: " + readenVote.getCreationDate()
            );
    
            shutdown();
        }
    }
}



