/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.debezium.observer.spi.EntityChangeEvent;
import org.hibernate.debezium.observer.spi.EntityObserver;

class StubEntityObserver<E> implements EntityObserver<E> {
	private final List<EntityChangeEvent<E>> collectedEvents = Collections.synchronizedList( new ArrayList<>() );

	@Override
	public void notify(EntityChangeEvent<E> event) {
		collectedEvents.add( event );
	}

	public void clearCollectedEvents() {
		collectedEvents.clear();
	}

	public List<EntityChangeEvent<E>> getCollectedEvents() {
		// Copy the list to avoid concurrent access issues
		return new ArrayList<>( collectedEvents );
	}
}
