/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

import java.util.Optional;

import org.hibernate.debezium.observer.spi.EntityChangeEvent;
import org.hibernate.debezium.observer.spi.ObservedAssociationValuePath;

/**
 * An observable association value in the entity model:
 * a property representing a {@link javax.persistence.ManyToOne} or {@link javax.persistence.OneToOne} association,
 * an element of a collection property representing
 * a {@link javax.persistence.ManyToMany} or {@link javax.persistence.OneToMany} association,
 * ...
 *
 * @param <T> The target entity type.
 */
public interface ObservableAssociationValue<T> {

	/**
	 * Observe changes to this value,
	 * i.e. make sure that changes to this value
	 * will trigger a call to {@link org.hibernate.debezium.observer.spi.EntityObserver#notify(EntityChangeEvent)}
	 * on the observers bound to the root entity.
	 *
	 * @return An optional containing the path to the association value,
	 * to be used to detect changes together with {@link EntityChangeEvent#getChangedValuesPaths()}
	 * or to retrieve before/after change value with {@link EntityChangeEvent#getValuesBefore(ObservedAssociationValuePath)}
	 * and {@link EntityChangeEvent#getValuesAfter(ObservedAssociationValuePath)}.
	 * The optional is empty if the association cannot be observed from this side,
	 * in which case you should try observing the association from the owning side.
	 */
	Optional<? extends ObservedAssociationValuePath<T>> observeIfPossible();

}
