/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.configuration.spi;

/**
 * An element of unknown type in the entity model.
 */
public interface ObservableUntypedElement {

	/**
	 * Access this element as an observable embedded
	 * ({@link javax.persistence.Embedded},
	 * {@link javax.persistence.Embeddable} element of an @{@link javax.persistence.ElementCollection},
	 * ...).
	 *
	 * @throws org.hibernate.debezium.util.HibernateDebeziumException if the element is not an embedded.
	 */
	ObservableComponent asEmbedded();

	/**
	 * Access this element as an observable basic value:
	 * a property of {@link javax.persistence.Basic} type,
	 * an element of basic type in an {@link javax.persistence.ElementCollection},
	 * ...
	 *
	 * @throws org.hibernate.debezium.util.HibernateDebeziumException if the element is not a basic value.
	 */
	ObservableBasicValue asBasicValue();

	/**
	 * Access this element as an observable association value:
	 * a property representing a {@link javax.persistence.ManyToOne} or {@link javax.persistence.OneToOne} association,
	 * an element of a collection property representing
	 * a {@link javax.persistence.ManyToMany} or {@link javax.persistence.OneToMany} association,
	 * ...
	 *
	 * @param entitySuperType The expected (raw) supertype of target entities.
	 *
	 * @throws org.hibernate.debezium.util.HibernateDebeziumException if the element is not an association value,
	 * or if the target entity type does not extend {@code entity type}.
	 */
	<T> ObservableAssociationValue<? extends T> asAssociationValue(Class<T> entitySuperType);

}
