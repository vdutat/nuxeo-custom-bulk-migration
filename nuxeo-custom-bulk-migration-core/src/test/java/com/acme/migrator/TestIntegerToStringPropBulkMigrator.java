package com.acme.migrator;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.Assert.assertEquals;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.SYSTEM_USERNAME;
import static org.nuxeo.ecm.core.migrator.AbstractBulkMigrator.PARAM_MIGRATION_ID;
import static org.nuxeo.ecm.core.migrator.AbstractBulkMigrator.PARAM_MIGRATION_STEP;

import java.util.Objects;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.bulk.BulkService;
import org.nuxeo.ecm.core.bulk.message.BulkStatus;
import org.nuxeo.ecm.core.migrator.AbstractBulkMigrator.MigrationAction;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.migration.MigrationService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy("com.acme.migrator.nuxeo-custom-bulk-migration-core:OSGI-INF/custombulkmigrator-contrib.xml")
@Deploy("com.acme.migrator.nuxeo-custom-bulk-migration-core.test:OSGI-INF/test-basic-bulk-migrator.xml")
public class TestIntegerToStringPropBulkMigrator {

    @Inject
    protected CoreSession session;

    @Inject
    protected BulkService bulkService;

    @Inject
    protected MigrationService migrationService;

    @Inject
    protected TransactionalFeature txFeature;

    @Before
    public void setup() {
        for (var i = 0; i < 20; i++) {
            var doc = session.createDocumentModel("/", String.format("MyDoc%03d", i), "MyDoc");
            doc.setPropertyValue("mydoc:intprop", i);
            session.createDocument(doc);
        }
        session.save();
        txFeature.nextTransaction();
    }

    //TODO
    @Ignore 
    @Test
    public void testBulkMigration() {
        // assert before state (ie: there are documents with dc:title = 'Content to migrate')
        var beforeState = migrationService.probeAndSetState(IntegerToStringPropBulkMigrator.MIGRATION_ID);
        assertEquals(IntegerToStringPropBulkMigrator.MIGRATION_BEFORE_STATE, beforeState);

        // run the migration
        migrationService.runStep(IntegerToStringPropBulkMigrator.MIGRATION_ID, "before-to-after");

        // await its end
        await().atMost(ONE_MINUTE).until(() -> !migrationService.getStatus(IntegerToStringPropBulkMigrator.MIGRATION_ID).isRunning());

        // assert after state (ie: there are no documents with empty mydoc:stringprop)
        var afterState = migrationService.probeAndSetState(IntegerToStringPropBulkMigrator.MIGRATION_ID);
        // TODO this fails, why?
        assertEquals(IntegerToStringPropBulkMigrator.MIGRATION_AFTER_STATE, afterState);
    }

    @Test
    public void testBulkActionFrameworkBinding() {
        // run the migration
        migrationService.probeAndRun(IntegerToStringPropBulkMigrator.MIGRATION_ID);

        // retrieve the bulk status for migration action, that will assert the migration is running on top of BAF
        var bulkStatus = await().atMost(ONE_MINUTE)
                                .until(() -> bulkService.getStatuses(SYSTEM_USERNAME)
                                                        .stream()
                                                        .filter(s -> MigrationAction.ACTION_NAME.equals(s.getAction()))
                                                        .findFirst()
                                                        .orElse(null),
                                        Objects::nonNull);
        // assert command
        var bulkCommand = bulkService.getCommand(bulkStatus.getId());
        assertEquals(IntegerToStringPropBulkMigrator.DEFAULT_MIGRATION_NXQL, bulkCommand.getQuery());
        assertEquals(IntegerToStringPropBulkMigrator.MIGRATION_ID, bulkCommand.getParam(PARAM_MIGRATION_ID));
        assertEquals("before-to-after", bulkCommand.getParam(PARAM_MIGRATION_STEP));

        // await its end
        await().atMost(ONE_MINUTE).until(() -> !migrationService.getStatus(IntegerToStringPropBulkMigrator.MIGRATION_ID).isRunning());

        // refresh the status
        bulkStatus = bulkService.getStatus(bulkStatus.getId());
        assertEquals(BulkStatus.State.COMPLETED, bulkStatus.getState());
        assertEquals(20, bulkStatus.getProcessed());
    }
}
