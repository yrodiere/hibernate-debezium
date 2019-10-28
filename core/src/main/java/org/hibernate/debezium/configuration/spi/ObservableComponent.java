/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

/**
 * A component in the entity model, i.e. an entity or an embedded element.
 *
 * @param <E> The type of the entity this component is a part of.
 */
public interface ObservableComponent<E> {

	/**
	 * Navigate to a property of this component.
	 *
	 * @param name The name of the property to navigate to.
	 */
	ObservableUntypedElement property(String name);

	/**
	 * @param name The name of the property to navigate to.
	 * @param elementSelection A selection of the container element to navigate to
	 * (collection elements, map keys, map values, ...).
	 */
	ObservableUntypedElement property(String name, ContainerElementSelection elementSelection);

}
