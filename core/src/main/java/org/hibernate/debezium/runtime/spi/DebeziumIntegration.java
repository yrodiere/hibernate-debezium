/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.runtime.spi;

import org.hibernate.debezium.observer.spi.EntityChangeEvent;

public interface DebeziumIntegration {

	/**
	 * @return A client for administration operations.
	 */
	DebeziumAdminClient getAdminClient();

	/**
	 * Allocate resources (threads and connections)
	 */
	void initialize();

	/**
	 * Start processing events and {@link org.hibernate.debezium.observer.spi.EntityObserver#notify(EntityChangeEvent) notifying the observers}.
	 */
	void start();

	/**
	 * Stop processing events and {@link org.hibernate.debezium.observer.spi.EntityObserver#notify(EntityChangeEvent) notifying the observers}.
	 */
	void stop();

	/**
	 * Release resources (threads and connections).
	 */
	void close();

}
