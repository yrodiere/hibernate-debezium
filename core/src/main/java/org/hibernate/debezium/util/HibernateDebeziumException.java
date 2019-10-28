/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.util;

public class HibernateDebeziumException extends RuntimeException {
	public HibernateDebeziumException(String message) {
		super( message );
	}

	public HibernateDebeziumException(String message, Throwable cause) {
		super( message, cause );
	}
}
