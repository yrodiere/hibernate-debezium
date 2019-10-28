/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.runtime.spi;

public interface DebeziumAdminClient {

	/**
	 * Create/configure the remote Debezium connectors.
	 */
	void configureConnector();

	/**
	 * Dump the required configuration of the Debezium connectors to the given object.
	 * TODO decide on an interface. We'll probably just dump to the filesystem?
	 */
	void dumpConnectorConfiguration(Object todo);

}
