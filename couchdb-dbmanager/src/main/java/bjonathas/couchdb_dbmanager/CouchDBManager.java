package bjonathas.couchdb_dbmanager;

import java.util.Collection;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.DesignDocument;

import bjonathas.couchdb_dbmanager.contants.CouchDBConstants;

import com.google.common.collect.Lists;

public class CouchDBManager {

	public Collection<DesignDocument> getDesignDocs(CouchDbConnector dbConnector) {
		ViewQuery q = new ViewQuery().allDocs().includeDocs(true)
				.startKey(CouchDBConstants.DESIGN_DOC_START_KEY)
				.endKey(CouchDBConstants.DESIGN_DOC_END_KEY);
		return dbConnector.queryView(q, DesignDocument.class);
	}

	public void migrateDesignDocs(CouchDbConnector dbConnectorSource,
			CouchDbConnector dbConnectorCible) {

		Collection<DesignDocument> designsToPost = Lists.newArrayList();

		for (DesignDocument designFromSource : getDesignDocs(dbConnectorSource)) {
			DesignDocument designToPost = new DesignDocument(designFromSource.getId());
			for (String viewname : designFromSource.getViews().keySet()) {
				designToPost.addView(viewname, designFromSource.getViews().get(viewname));
			}
			designsToPost.add(designToPost);
		}
		
		dbConnectorCible.executeBulk(designsToPost);
	}
}