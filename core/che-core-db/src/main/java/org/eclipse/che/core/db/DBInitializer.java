package org.eclipse.che.core.db;

import org.eclipse.che.core.db.jpa.JpaInitializer;
import org.eclipse.che.core.db.jpa.eclipselink.GuiceEntityListenerInjectionManager;
import org.eclipse.che.core.db.schema.SchemaInitializationException;
import org.eclipse.che.core.db.schema.SchemaInitializer;
import org.eclipse.persistence.sessions.server.ServerSession;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;

/**
 * Initializes database components.
 *
 * <p>Those components which require any persistence operations of their bootstrap
 * have to depend on this component. For example:
 * <pre>
 * class StackExistsChecker {
 *     &#064;PostConstruct
 *     public void check(DBInitializer ignored) {
 *         ....
 *     }
 * }
 * </pre>
 * In this way it is guaranteed that all database related components
 * will be appropriately initialized before {@code check} method is executed.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class DBInitializer {

    @Inject
    public void initialize(SchemaInitializer schemaInitializer, JpaInitializer jpaInitializer) throws SchemaInitializationException {
        // schema must be initialized before any other component
        // which may interact with database
        schemaInitializer.init();

        // jpa initialization goes next
        jpaInitializer.init();
    }

    @PostConstruct
    public void setUpInjectionManager(GuiceEntityListenerInjectionManager injManager, EntityManagerFactory emFactory) {
        final ServerSession session = emFactory.unwrap(ServerSession.class);
        session.setEntityListenerInjectionManager(injManager);
    }
}
