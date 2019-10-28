/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.observer.spi;

/**
 * @param <E> The entity type.
 */
public interface EntityObserver<E> {

	void notify(EntityChangeEvent<E> event);

}
