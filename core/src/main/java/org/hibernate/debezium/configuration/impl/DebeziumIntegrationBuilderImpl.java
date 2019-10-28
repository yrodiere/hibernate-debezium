/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.impl;

import org.hibernate.boot.Metadata;
import org.hibernate.debezium.configuration.spi.DebeziumIntegrationBuilder;
import org.hibernate.debezium.configuration.spi.EntityObserverFactory;
import org.hibernate.debezium.configuration.spi.ObservableEntity;
import org.hibernate.debezium.runtime.impl.DebeziumIntegrationImpl;
import org.hibernate.debezium.runtime.spi.DebeziumIntegration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class DebeziumIntegrationBuilderImpl implements DebeziumIntegrationBuilder {

	public DebeziumIntegrationBuilderImpl(Metadata metadata, SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		super();
	}

	@Override
	public <E> ObservableEntity observeEntity(Class<E> entityType, EntityObserverFactory<? super E> observerFactory) {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public DebeziumIntegration build() {
		return new DebeziumIntegrationImpl();
	}
}
