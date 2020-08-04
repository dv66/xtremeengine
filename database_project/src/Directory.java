/**
 * Created by User on 7/9/2017.
 */
public class Directory {



    private String databaseEnnvironmentDirectory;
    private String xmlFileDirectory;




    private static Directory newDirectory = new Directory();
    private Directory(){
        databaseEnnvironmentDirectory = "G:\\10101010101010\\L3T1\\MY COURSE\\" +
                "no_sql_database_project\\database_project";
        xmlFileDirectory = "G:\\10101010101010\\L3T1\\" +
                "MY COURSE\\no_sql_database_project\\database_project_kits\\XML DATASET\\DataScience";
    }

    public static Directory getInstance(){ return newDirectory;}

    public String getDatabaseEnnvironmentDirectory() {
        return databaseEnnvironmentDirectory;
    }

    public String getXmlFileDirectory() {
        return xmlFileDirectory;
    }

    public void setDatabaseEnnvironmentDirectory(String databaseEnnvironmentDirectory) {
        this.databaseEnnvironmentDirectory = databaseEnnvironmentDirectory;
    }

    public void setXmlFileDirectory(String xmlFileDirectory) {
        this.xmlFileDirectory = xmlFileDirectory;
    }
}
