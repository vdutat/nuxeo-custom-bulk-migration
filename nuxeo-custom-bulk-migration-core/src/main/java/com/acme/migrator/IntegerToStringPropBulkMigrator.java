package com.acme.migrator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.migrator.AbstractBulkMigrator;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.migration.MigrationDescriptor;

public class IntegerToStringPropBulkMigrator extends AbstractBulkMigrator {

    public static final String MIGRATION_ID = "basic-bulk-migration";

    public static final String MIGRATION_BEFORE_STATE = "before";

    public static final String MIGRATION_AFTER_STATE = "after";

    public static final String DEFAULT_MIGRATION_NXQL = "SELECT * FROM Document WHERE "+NXQL.ECM_ISVERSION+"=0 AND "+NXQL.ECM_PRIMARYTYPE+"='MyDoc'";

    private static final String DEFAULT_INTEGER_PROP_NAME = "mydoc:intprop";

    private static final String DEFAULT_STRING_PROP_NAME = "mydoc:stringprop";

    public IntegerToStringPropBulkMigrator(MigrationDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    public void notifyStatusChange() {
        // nothing to do
    }

    @Override
    public void compute(CoreSession session, List<String> ids, Map<String, Serializable> properties) {
        boolean saveSession = false;
        for (var id : ids) {
            if (migrateDocument(session, id)) {
                saveSession = true;
            }
        }
        if (saveSession) {
            session.save();
        }
    }

    protected boolean migrateDocument(CoreSession session, String id) {
        var doc = session.getDocument(new IdRef(id));
        var intValue = (Long) doc.getPropertyValue(getIntegerPropertyName());
        if (intValue != null) {
            doc.setPropertyValue(getStringPropertyName(), intValue.toString());
            session.saveDocument(doc);
            return true;
        }
        return false;
    }

    @Override
    protected String getNXQLScrollQuery() {
        return Framework.getProperty("acme.migrator."+MIGRATION_ID+".nxql", DEFAULT_MIGRATION_NXQL);
    }

    @Override
    protected String probeSession(CoreSession session) {
        return session.queryProjection(getNXQLScrollQuery(), 1, 0).isEmpty() ? MIGRATION_AFTER_STATE
                : MIGRATION_BEFORE_STATE;
    }
    
    protected String getIntegerPropertyName() {
        return Framework.getProperty("acme.migrator."+MIGRATION_ID+".intergerPropName", DEFAULT_INTEGER_PROP_NAME);
    }
    
    protected String getStringPropertyName() {
        return Framework.getProperty("acme.migrator."+MIGRATION_ID+".stringPropName", DEFAULT_STRING_PROP_NAME);
    }

}
