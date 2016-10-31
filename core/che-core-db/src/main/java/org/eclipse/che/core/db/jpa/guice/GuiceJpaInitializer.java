package org.eclipse.che.core.db.jpa.guice;

import com.google.inject.persist.PersistService;

import org.eclipse.che.core.db.jpa.JpaInitializer;

import javax.inject.Inject;

/**
 * Should be bound as eager singleton.
 * See <a href="https://github.com/google/guice/wiki/JPA">doc</a>
 *
 * @author Yevhenii Voevodin
 * @author Anton Korneta
 */
public class GuiceJpaInitializer implements JpaInitializer {

    @Inject
    private PersistService persistService;

    public void init() {
        persistService.start();
    }
}
