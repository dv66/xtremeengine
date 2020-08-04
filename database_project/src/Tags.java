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

@Entity
public class Tags {
    @PrimaryKey
    private String tagId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String tagName;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String count;




    public Tags(){}


    public Tags(String tagId, String tagName, String count) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.count = count;
    }


    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    
    public static ArrayList<Tags> parseTags(String fileDirectory){
        ArrayList<Tags> voteRecords = new ArrayList<>();
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
                    Tags newTags = new Tags(
                            nodeElement.getAttribute("Id"),nodeElement.getAttribute("TagName"),nodeElement.getAttribute("Count")
                    );
                    voteRecords.add( newTags );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }














    public static class TagsDataAccessor {
        public TagsDataAccessor() {}

        // Open the indices
        public TagsDataAccessor(EntityStore store) throws DatabaseException {



            
            tagByTagId = store.getPrimaryIndex(String.class , Tags.class);
            tagByTagName = store.getSecondaryIndex( tagByTagId, String.class, "tagName");
            tagByCount = store.getSecondaryIndex( tagByTagId, String.class, "count");
            


        }


        PrimaryIndex<String, Tags> tagByTagId;
        SecondaryIndex<String,String, Tags> tagByTagName;
        SecondaryIndex<String,String, Tags> tagByCount;
        
    }






















    public static class PutTags {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private TagsDataAccessor sda;


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
            sda = new TagsDataAccessor(store);
            ArrayList<Tags> TagsList = parseTags( directory.getXmlFileDirectory() + "\\Tags.xml");

            for(Tags Tags: TagsList){
                sda.tagByTagId.put(Tags);
            }

            shutdown();
        }
    }

    public static class GetTags {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private TagsDataAccessor sda;



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

            sda = new TagsDataAccessor(store);




            Tags readenVote = sda.tagByTagId.get("14");
            System.out.println(readenVote.getTagName());

            shutdown();
        }
    }
}



