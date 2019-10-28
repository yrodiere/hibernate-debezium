/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.runtime.impl;

import org.hibernate.debezium.runtime.spi.DebeziumAdminClient;
import org.hibernate.debezium.runtime.spi.DebeziumIntegration;

public class DebeziumIntegrationImpl implements DebeziumIntegration {

	@Override
	public DebeziumAdminClient getAdminClient() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public void initialize() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}
}
