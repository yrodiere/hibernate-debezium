/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.observer.spi;

import java.util.List;
import java.util.Set;

public interface EntityChangeEvent<E> {

	/**
	 * @return A reference to the changed entity.
	 */
	EntityReference<E> getChangedEntityReference();

	/**
	 * @return References to the changed values.
	 */
	Set<ObservedValuePath> getChangedValuesPaths();

	/**
	 * @param path A path to an observed association value.
	 * @param <T> The (raw) type of the target entity.
	 * @return References to targeted entities before the change.
	 * <strong>Targeted entities that were not affected by the change may be omitted.</strong>
	 */
	<T> List<EntityReference<T>> getValuesBefore(ObservedAssociationValuePath<T> path);

	/**
	 * @param path A path to an observed association value.
	 * @param <T> The (raw) type of the target entity.
	 * @return References to targeted entities after the change.
	 * <strong>Targeted entities that were not affected by the change may be omitted.</strong>
	 */
	<T> List<EntityReference<T>> getValuesAfter(ObservedAssociationValuePath<T> path);

}
