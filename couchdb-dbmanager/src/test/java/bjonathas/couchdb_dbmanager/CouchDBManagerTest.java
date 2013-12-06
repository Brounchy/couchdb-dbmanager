package bjonathas.couchdb_dbmanager;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.DesignDocument;
import org.junit.Test;

public class CouchDBManagerTest {

	private final static String SERVER_LOCATION = "http://localhost:5984/";
	private final static String MIGRATE_TEST_DB_SOURCE = "couchDBManagerMigrateTestSource";
	private final static String MIGRATE_TEST_DB_CIBLE = "couchDBManagerMigrateTestCible";
	private final static String MIGRATE_TEST_DESIGN_DOC_ID = "_design/couchDBManagermigrateTestDesignDocId";
	private final CouchDBManager couchDBManager = new CouchDBManager();
	
	@Test
	public void testMigrate(){
		
		HttpClient authenticatedHttpClient = null;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(
					SERVER_LOCATION).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CouchDbInstance dbInstance = new StdCouchDbInstance(
				authenticatedHttpClient);
		
		CouchDbConnector dbSource = dbInstance.createConnector(MIGRATE_TEST_DB_SOURCE, true);
		
		CouchDbConnector dbCible = dbInstance.createConnector(MIGRATE_TEST_DB_CIBLE, true);		
		
		DesignDocument d = new DesignDocument(MIGRATE_TEST_DESIGN_DOC_ID);
		
		dbSource.update(d);
		
		checkNotNull(dbSource.get(DesignDocument.class,MIGRATE_TEST_DESIGN_DOC_ID),"The design doc has not been created");
		
		couchDBManager.migrateDesignDocs(dbSource,dbCible);
		
		checkNotNull(dbSource.get(DesignDocument.class,MIGRATE_TEST_DESIGN_DOC_ID),"The design doc has not been migrated");
	
		dbInstance.deleteDatabase(MIGRATE_TEST_DB_SOURCE);
		dbInstance.deleteDatabase(MIGRATE_TEST_DB_CIBLE);
	
	}
	
}
