/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

import org.hibernate.debezium.observer.spi.EntityChangeEvent;
import org.hibernate.debezium.observer.spi.ObservedValuePath;

/**
 * An observable basic value in the entity model: a property of {@link javax.persistence.Basic} type,
 * an element of basic type in an {@link javax.persistence.ElementCollection},
 * ...
 */
public interface ObservableBasicValue {

	/**
	 * Observe changes to this value,
	 * i.e. make sure that changes to this value
	 * will trigger a call to {@link org.hibernate.debezium.observer.spi.EntityObserver#notify(EntityChangeEvent)}
	 * on the observers bound to the root entity.
	 *
	 * @return The path to the association value,
	 * to be used to detect changes together with {@link EntityChangeEvent#getChangedValuesPaths()}.
	 */
	ObservedValuePath observe();

}
