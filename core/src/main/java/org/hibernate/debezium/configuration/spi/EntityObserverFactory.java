/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

import org.hibernate.debezium.observer.spi.EntityObserver;

/**
 * @param <E> The entity type.
 */
public interface EntityObserverFactory<E> {

	EntityObserver<E> createEntityObserver();

}
