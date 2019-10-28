/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

import org.hibernate.boot.Metadata;
import org.hibernate.debezium.configuration.impl.DebeziumIntegrationBuilderImpl;
import org.hibernate.debezium.runtime.spi.DebeziumIntegration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public interface DebeziumIntegrationBuilder {

	static DebeziumIntegrationBuilder create(Metadata metadata, SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		return new DebeziumIntegrationBuilderImpl( metadata, sessionFactory, serviceRegistry );
	}

	/**
	 * Enable observation of the given entity.
	 *
	 * @param entityType The type of an entity to observe.
	 * @param observerFactory An observer factory. Will be used to create the observer when {@link #build()} is called.
	 * @param <E> The entity type.
	 * @return A representation of the entity as an observable, where the observed properties can be specified.
	 */
	<E> ObservableEntity observeEntity(Class<E> entityType, EntityObserverFactory<? super E> observerFactory);

	/**
	 * @return A {@link DebeziumIntegration}.
	 * @throws org.hibernate.debezium.util.HibernateDebeziumException if an exception is thrown by an {@link EntityObserverFactory},
	 * or if there is no {@link EntityObserverFactory} registered for an observed value
	 * (this may happen when observing associations).
	 */
	DebeziumIntegration build();

}
