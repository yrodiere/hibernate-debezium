/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.observer.spi;

import java.util.Objects;

/**
 * A reference to an entity instance.
 * @param <E> The entity type.
 */
public class EntityReference<E> {

	private final Class<?> type;

	private final String name;

	private final Object id;

	public EntityReference(Class<?> type, String name, Object id) {
		this.type = type;
		this.name = name;
		this.id = id;
	}

	/**
	 * @return The type of the referenced entity.
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @return The name of the referenced entity in the Hibernate ORM mapping.
	 * @see javax.persistence.Entity#name()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The identifier of the referenced entity,
	 */
	public Object getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || obj.getClass() != getClass() ) {
			return false;
		}
		EntityReference other = (EntityReference) obj;
		return name.equals( other.name ) && Objects.equals( id, other.id );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name, id );
	}

	@Override
	public String toString() {
		// Apparently this is the usual format for references to Hibernate ORM entities.
		return name + "#" + id;
	}
}
