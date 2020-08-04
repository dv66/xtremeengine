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
public class Badges {
    @PrimaryKey
    private String badgeId;

    @SecondaryKey(relate=MANY_TO_ONE)
    private String userId;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String badgeName;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String date;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String badgeClass;
    @SecondaryKey(relate=MANY_TO_ONE)
    private String tagBased;


    public Badges(String badgeId, String userId, String badgeName, String date, String badgeClass, String tagBased) {
        this.badgeId = badgeId;
        this.userId = userId;
        this.badgeName = badgeName;
        this.date = date;
        this.badgeClass = badgeClass;
        this.tagBased = tagBased;
    }

    public Badges() {}

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public String getTagBased() {
        return tagBased;
    }

    public void setTagBased(String tagBased) {
        this.tagBased = tagBased;
    }

    public static ArrayList<Badges> parseBadges(String fileDirectory){
        ArrayList<Badges> voteRecords = new ArrayList<>();
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
                    Badges newBadges = new Badges(
                            nodeElement.getAttribute("Id"),nodeElement.getAttribute("UserId"),nodeElement.getAttribute("Name"),
                            nodeElement.getAttribute("Date"),nodeElement.getAttribute("Class"),nodeElement.getAttribute("TagBased")
                    );
                    voteRecords.add( newBadges );
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return voteRecords;
    }














    public static   class BadgesDataAccessor {
        public BadgesDataAccessor() {}

        // Open the indices
        public BadgesDataAccessor(EntityStore store) throws DatabaseException {



            badgeByBadgeId = store.getPrimaryIndex(String.class , Badges.class);
            badgeByUserId = store.getSecondaryIndex( badgeByBadgeId, String.class, "userId");
            badgeByBadgeName = store.getSecondaryIndex( badgeByBadgeId, String.class, "badgeName");
            badgeByDate = store.getSecondaryIndex( badgeByBadgeId, String.class, "date");
            badgeByClass = store.getSecondaryIndex( badgeByBadgeId, String.class, "badgeClass");
            badgeByTagBased = store.getSecondaryIndex( badgeByBadgeId, String.class, "tagBased");


        }


        // Index Accessors

        PrimaryIndex<String, Badges> badgeByBadgeId;
        SecondaryIndex<String,String, Badges> badgeByUserId;
        SecondaryIndex<String,String, Badges> badgeByBadgeName;
        SecondaryIndex<String,String, Badges> badgeByDate;
        SecondaryIndex<String,String, Badges> badgeByClass;
        SecondaryIndex<String,String, Badges> badgeByTagBased;
    }






















    public static  class PutBadges {
        private Directory directory = Directory.getInstance();
        private  File envHome = new File( directory.getDatabaseEnnvironmentDirectory() );
        private Environment envmnt;
        private EntityStore store;
        private BadgesDataAccessor sda;


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
            sda = new BadgesDataAccessor(store);
            ArrayList<Badges> BadgesList = parseBadges( directory.getXmlFileDirectory() + "\\Badges.xml");

            for(Badges Badges: BadgesList){
                sda.badgeByBadgeId.put(Badges);
            }

            shutdown();
        }
    }

    public static class GetBadges {
        private Directory directory = Directory.getInstance();
        private File envHome = new File(directory.getDatabaseEnnvironmentDirectory());
        private Environment envmnt;
        private EntityStore store;
        private BadgesDataAccessor sda;



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

            sda = new BadgesDataAccessor(store);




            Badges readenVote = sda.badgeByBadgeId.get("12205");


            shutdown();
        }
    }
}



