/*
 * Hibernate Debezium
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.debezium.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.debezium.configuration.spi.ContainerElementSelection;
import org.hibernate.debezium.configuration.spi.DebeziumIntegrationBuilder;
import org.hibernate.debezium.configuration.spi.ObservableEntity;
import org.hibernate.debezium.observer.spi.EntityReference;
import org.hibernate.debezium.observer.spi.ObservedAssociationValuePath;
import org.hibernate.debezium.observer.spi.ObservedValuePath;
import org.hibernate.debezium.runtime.spi.DebeziumIntegration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Before;
import org.junit.Test;

import org.awaitility.Duration;

public class HibernateSearchUseCaseIT extends BaseCoreFunctionalTestCase {

	private final SimulatedSearchIntegrator integrator = new SimulatedSearchIntegrator();

	@Before
	public void clearEvents() {
		integrator.clearStubCollectedEvents();
	}

	/**
	 * Inserting an entity should trigger an event.
	 */
	@Test
	public void insert() {
		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = new EntityA( 1, "value1", "value1" );
				EntityB b = new EntityB( 1 );
				a.b = b;
				session.persist( b );
				session.persist( a );
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					// There may be multiple events (one per table)
					.allSatisfy( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
					} )
					.anySatisfy( event -> {
						assertThat( event.getChangedValuesPaths() )
								.contains( integrator.entityAObserver.pathToText );
					} )
					.anySatisfy( event -> {
						assertThat( event.getChangedValuesPaths() )
								.contains( integrator.entityAObserver.pathToB );
						assertThat( event.getValuesBefore( integrator.entityAObserver.pathToB ) )
								.isEmpty();
						assertThat( event.getValuesAfter( integrator.entityAObserver.pathToB ) )
								.containsExactly( new EntityReference<>( EntityB.class, "B", 1 ) );
					} )
			);
		} );
	}

	/**
	 * Null values when inserting an entity should not be considered changed.
	 */
	@Test
	public void insert_nulls() {
		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = new EntityA( 1, "value1", "value1" );
				// Leave a.b to null
				session.persist( a );
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					// There should only be one event (on the main entity table)
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToText );
					} )
			);
		} );
	}

	/**
	 * Deleting an entity should trigger an event.
	 */
	@Test
	public void delete() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			EntityB b = new EntityB( 1 );
			a.b = b;
			session.persist( b );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = session.getReference( EntityA.class, 1 );
				session.delete( a );
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					// There may be multiple events (one per table)
					.allSatisfy( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
					} )
					.anySatisfy( event -> {
						assertThat( event.getChangedValuesPaths() )
								.contains( integrator.entityAObserver.pathToText );
					} )
					.anySatisfy( event -> {
						assertThat( event.getChangedValuesPaths() )
								.contains( integrator.entityAObserver.pathToB );
						assertThat( event.getValuesBefore( integrator.entityAObserver.pathToB ) )
								.containsExactly( new EntityReference<>( EntityB.class, "B", 1 ) );
						assertThat( event.getValuesAfter( integrator.entityAObserver.pathToB ) )
								.isEmpty();
					} )
			);
		} );
	}

	/**
	 * Null values when deleting an entity should not be considered changed.
	 */
	@Test
	public void delete_nulls() {
		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = new EntityA( 1, "value1", "value1" );
				// Leave a.b to null
				session.persist( a );
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					// There should only be one event (on the main entity table)
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToText );
					} )
			);
		} );
	}

	/**
	 * Changes to observed basic properties should trigger an event.
	 */
	@Test
	public void update_observedText() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = session.getReference( EntityA.class, 1 );
				a.observedText = "value2";
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToText );
					} )
			);
		} );
	}

	/**
	 * Changes to non-observed basic properties should not trigger an event.
	 */
	@Test
	public void update_nonObservedText() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA a = session.getReference( EntityA.class, 1 );
				a.nonObservedText = "value2";
			} );

			await().atLeast( Duration.TEN_SECONDS )
					.untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() ).isEmpty() );
		} );
	}

	/**
	 * Changes to observed @ManyToOne associations should trigger an event.
	 */
	@Test
	public void update_manyToOne() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			EntityB b1 = new EntityB( 1 );
			a.b = b1;
			session.persist( b1 );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA entityA = session.getReference( EntityA.class, 1 );
				EntityB b2 = new EntityB( 2 );
				entityA.b = b2;
				session.persist( b2 );
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToB );
						assertThat( event.getValuesBefore( integrator.entityAObserver.pathToB ) )
								.containsExactlyInAnyOrder( new EntityReference<>( EntityB.class, "B", 1 ) );
						assertThat( event.getValuesAfter( integrator.entityAObserver.pathToB ) )
								.containsExactlyInAnyOrder( new EntityReference<>( EntityB.class, "B", 2 ) );
					} )
			);
		} );
	}

	/**
	 * Removal of a value in an observed @ManyToOne associations should trigger an event.
	 */
	@Test
	public void update_manyToOne_remove() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			EntityB b1 = new EntityB( 1 );
			a.b = b1;
			session.persist( b1 );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				EntityA entityA = session.getReference( EntityA.class, 1 );
				entityA.b = null;
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToB );
						assertThat( event.getValuesBefore( integrator.entityAObserver.pathToB ) )
								.containsExactlyInAnyOrder( new EntityReference<>( EntityB.class, "B", 1 ) );
						assertThat( event.getValuesAfter( integrator.entityAObserver.pathToB ) )
								.isEmpty();
					} )
			);
		} );
	}

	/**
	 * Changes performed through JPQL should trigger an event.
	 */
	@Test
	public void update_jpql() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				session.createQuery( "update A set observedText = 'value2' where id = 1" )
						.executeUpdate();
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToText );
					} )
			);
		} );
	}

	/**
	 * Changes performed through native SQL should trigger an event.
	 */
	@Test
	public void update_native() {
		inTransaction( session -> {
			EntityA a = new EntityA( 1, "value1", "value1" );
			session.persist( a );
		} );

		withCaptureEnabled( () -> {
			inTransaction( session -> {
				session.createNativeQuery( "update A set observedText = 'value2' where id = 1" )
						.executeUpdate();
			} );

			await().untilAsserted( () -> assertThat( integrator.entityAObserver.getCollectedEvents() )
					.hasSize( 1 )
					.first().satisfies( event -> {
						assertThat( event.getChangedEntityReference() )
								.isEqualTo( new EntityReference<>( EntityA.class, "A", 1 ) );
						assertThat( event.getChangedValuesPaths() )
								.containsExactlyInAnyOrder( integrator.entityAObserver.pathToText );
					} )
			);
		} );
	}

	@Override
	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
		super.prepareBootstrapRegistryBuilder( builder );
		builder.applyIntegrator( integrator );
	}

	@Override
	protected boolean isCleanupTestDataRequired() {
		return true;
	}

	@Override
	protected void afterSessionFactoryBuilt() {
		integrator.integration.getAdminClient().configureConnector();
	}

	private void withCaptureEnabled(Runnable runnable) {
		integrator.integration.initialize();
		try {
			integrator.integration.start();
			try {
				runnable.run();
			}
			finally {
				integrator.integration.stop();
			}
		}
		finally {
			integrator.integration.close();
		}
	}

	@Entity(name = "A")
	private static class EntityA {
		@Id
		private Integer id;

		@Basic
		private String observedText;

		@Basic
		private String nonObservedText;

		@ManyToOne
		private EntityB b;

		@OneToMany(mappedBy = "a")
		private List<EntityC> c = new ArrayList<>();

		public EntityA(Integer id, String observedText, String nonObservedText) {
			this.id = id;
			this.observedText = observedText;
			this.nonObservedText = nonObservedText;
		}
	}

	@Entity(name = "B")
	private static class EntityB {
		@Id
		private Integer id;

		@OneToMany(mappedBy = "b")
		private List<EntityA> a = new ArrayList<>();

		public EntityB(Integer id) {
			this.id = id;
		}
	}

	@Entity(name = "C")
	private static class EntityC {
		@Id
		private Integer id;

		@ManyToOne
		private EntityA a;

		public EntityC(Integer id) {
			this.id = id;
		}
	}

	private static class SimulatedSearchIntegrator implements Integrator {
		private DebeziumIntegration integration;
		private EntityAObserver entityAObserver;
		private EntityBObserver entityBObserver;
		private EntityCObserver entityCObserver;

		@Override
		public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
				SessionFactoryServiceRegistry serviceRegistry) {
			DebeziumIntegrationBuilder builder = DebeziumIntegrationBuilder.create(
					metadata, sessionFactory, serviceRegistry );

			ObservableEntity entityA = builder.observeEntity( EntityA.class, () -> entityAObserver );
			ObservedValuePath entityAPathToText =
					entityA.property( "indexedTextFromA" )
							.asBasicValue()
							.observe();
			Optional<? extends ObservedAssociationValuePath<? extends EntityB>> entityAPathToBOptional =
					entityA.property( "b" )
							.asAssociationValue( EntityB.class )
							.observeIfPossible();
			Optional<? extends ObservedAssociationValuePath<? extends EntityC>> entityAPathToCOptional =
					entityA.property( "c", ContainerElementSelection.COLLECTION_ELEMENT )
							.asAssociationValue( EntityC.class )
							.observeIfPossible();

			ObservableEntity entityB = builder.observeEntity( EntityB.class, () -> entityBObserver );
			Optional<? extends ObservedAssociationValuePath<? extends EntityA>> entityBPathToAOptional =
					entityB.property( "a", ContainerElementSelection.COLLECTION_ELEMENT )
							.asAssociationValue( EntityA.class )
							.observeIfPossible();

			ObservableEntity entityC = builder.observeEntity( EntityC.class, () -> entityCObserver );
			Optional<? extends ObservedAssociationValuePath<? extends EntityA>> entityCPathToAOptional =
					entityC.property( "a" )
							.asAssociationValue( EntityA.class )
							.observeIfPossible();

			assertThat( entityAPathToText ).isNotNull();

			// The foreign key for association A <-> B is in table A
			assertThat( entityAPathToBOptional ).isNotEmpty();
			assertThat( entityBPathToAOptional ).isEmpty();

			// The foreign key for association A <-> C is in table C
			assertThat( entityAPathToCOptional ).isEmpty();
			assertThat( entityCPathToAOptional ).isNotEmpty();

			entityAObserver = new EntityAObserver( entityAPathToText, entityAPathToBOptional.get() );
			entityBObserver = new EntityBObserver();
			entityCObserver = new EntityCObserver( entityCPathToAOptional.get() );

			integration = builder.build();
		}

		@Override
		public void disintegrate(SessionFactoryImplementor sessionFactory,
				SessionFactoryServiceRegistry serviceRegistry) {
			if ( integration != null ) {
				integration.close();
			}
		}

		public void clearStubCollectedEvents() {
			entityAObserver.clearCollectedEvents();
			entityBObserver.clearCollectedEvents();
			entityCObserver.clearCollectedEvents();
		}
	}

	private static class EntityAObserver extends StubEntityObserver<EntityA> {
		private final ObservedValuePath pathToText;
		private final ObservedAssociationValuePath<? extends EntityB> pathToB;

		private EntityAObserver(ObservedValuePath pathToText,
				ObservedAssociationValuePath<? extends EntityB> pathToB) {
			this.pathToText = pathToText;
			this.pathToB = pathToB;
		}
	}

	private static class EntityBObserver extends StubEntityObserver<EntityB> {

	}

	private static class EntityCObserver extends StubEntityObserver<EntityC> {
		private final ObservedAssociationValuePath<? extends EntityA> pathToA;

		private EntityCObserver(ObservedAssociationValuePath<? extends EntityA> pathToA) {
			this.pathToA = pathToA;
		}
	}

}
