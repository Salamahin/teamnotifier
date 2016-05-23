package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ActionInfo;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.notification.ResourceAction;
import com.home.teamnotifier.core.responses.notification.ServerAction;
import com.home.teamnotifier.gateways.ActionsGateway;
import com.home.teamnotifier.gateways.ResourceDescription;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.home.teamnotifier.db.DbGatewayCommons.getSubscribersButUser;
import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;

public class DbActionsGateway implements ActionsGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    DbActionsGateway(
            final TransactionHelper transactionHelper
    ) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public BroadcastInformation<ResourceAction> newResourceAction(
            final String userName,
            final int resourceId,
            final String description
    ) {
        try {
            return tryPersistNewActionOnResource(userName, resourceId, description);
        } catch (Exception exc) {
            rethrowIfContainsConstraintViolation(exc);
            return null;
        }
    }

    @Override
    public BroadcastInformation<ServerAction> newServerAction(
            final String userName,
            final int serverId,
            final String description
    ) throws NoSuchServer, EmptyDescription, NoSuchUser {
        try {
            return tryPersistNewServerAction(userName, serverId, description);
        } catch (Exception exc) {
            rethrowIfContainsConstraintViolation(exc);
            return null;
        }
    }

    @Override
    public BroadcastInformation<ResourceAction> newResourceAction(
            final String userName,
            final ResourceDescription resourceDescription,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser {
        final String environmentName = resourceDescription.getEnvironmentName();
        final String resourceName = resourceDescription.getResourceName();
        final String serverName = resourceDescription.getServerName();

        try {
            return tryPersistNewActionOnResource(userName, environmentName, serverName, resourceName, description);
        } catch (NoResultException exc) {
            throw new NoSuchResource(String.format("No resource env=%s srv=%s res=%s", environmentName, serverName, resourceName));
        } catch (Exception exc) {
            rethrowIfContainsConstraintViolation(exc);
            return null;
        }
    }

    private void rethrowIfContainsConstraintViolation(final Exception exc) {
        final Optional<Throwable> firstConstraintViolation = Throwables.getCausalChain(exc).stream()
                .filter((ConstraintViolationException.class)::isInstance)
                .findFirst();

        if (firstConstraintViolation.isPresent())
            throw new EmptyDescription(firstConstraintViolation.get());
        throw Throwables.propagate(exc);
    }

    private BroadcastInformation<ServerAction> tryPersistNewServerAction(
            final String userName,
            final int serverId,
            final String description
    ) {
        return transactionHelper.transaction(em -> {
            final ServerEntity server = getServerEntity(serverId, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ServerAction action = newServerAction(userEntity, server, description, em);
            return new BroadcastInformation<>(action, getSubscribersButUser(userName, server));
        });
    }

    private BroadcastInformation<ResourceAction> tryPersistNewActionOnResource(
            final String userName,
            final int resourceId,
            final String description
    ) {
        return transactionHelper.transaction(em -> {
            final ResourceEntity resourceEntity = getResourceEntity(resourceId, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ResourceAction resourceAction = newResourceAction(
                    userEntity,
                    resourceEntity,
                    description,
                    em
            );

            return new BroadcastInformation<>(
                    resourceAction,
                    getSubscribersButUser(userName, resourceEntity.getServer())
            );
        });
    }

    private BroadcastInformation<ResourceAction> tryPersistNewActionOnResource(
            final String userName,
            final String envName,
            final String serverName,
            final String resourceName,
            final String description
    ) {
        return transactionHelper.transaction(em -> {
            final ResourceEntity resourceEntity = getResourceEntity(envName, serverName, resourceName, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ResourceAction action = newResourceAction(userEntity, resourceEntity, description, em);
            return new BroadcastInformation<>(action, getSubscribersButUser(userName, resourceEntity.getServer()));
        });
    }

    private ServerAction newServerAction(
            final UserEntity userEntity,
            final ServerEntity server,
            final String description,
            final EntityManager em
    ) {
        final ServerActionEntity action = new ServerActionEntity(
                userEntity,
                server,
                description
        );
        em.persist(action);

        return new ServerAction(userEntity, server, description);
    }

    private ResourceAction newResourceAction(
            final UserEntity userEntity,
            final ResourceEntity resourceEntity,
            final String description,
            final EntityManager em
    ) {
        final ResourceActionEntity action = new ResourceActionEntity(
                userEntity,
                resourceEntity,
                description
        );
        em.persist(action);

        return new ResourceAction(userEntity, resourceEntity, description);
    }

    private ServerEntity getServerEntity(final int serverId, final EntityManager em) {
        final ServerEntity entity = em.find(ServerEntity.class, serverId);
        if (entity == null)
            throw new NoSuchServer(String.format("No server with id %d", serverId));

        return entity;
    }

    private ResourceEntity getResourceEntity(final int resourceId, final EntityManager em) {
        final ResourceEntity entity = em.find(ResourceEntity.class, resourceId);
        if (entity == null)
            throw new NoSuchResource(String.format("No resource with id %d", resourceId));

        return entity;
    }

    private ResourceEntity getResourceEntity(
            final String envName,
            final String serverName,
            final String resourceName,
            final EntityManager em
    ) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ResourceEntity> cqRes = cb.createQuery(ResourceEntity.class);
        Subquery<ServerEntity> srvByName = cqRes.subquery(ServerEntity.class);
        Subquery<EnvironmentEntity> envByName = srvByName.subquery(EnvironmentEntity.class);

        final Root<ResourceEntity> rootRes = cqRes.from(ResourceEntity.class);
        final Root<ServerEntity> rootSrv = srvByName.from(ServerEntity.class);
        final Root<EnvironmentEntity> rootEnv = envByName.from(EnvironmentEntity.class);

        envByName = envByName.select(rootEnv).where(cb.equal(rootEnv.get("name"), envName));

        srvByName = srvByName.select(rootSrv).where(cb.and(
                cb.equal(rootSrv.get("name"), serverName),
                cb.equal(rootSrv.get("environment"), envByName)
        ));

        cqRes.select(rootRes).where(cb.and(
                cb.equal(rootRes.get("name"), resourceName),
                cb.equal(rootRes.get("server"), srvByName)
        ));

        return em
                .createQuery(cqRes)
                .getSingleResult();
    }

    @Override
    public ResourceActionsHistory getActionsOnResource(final int resourceId, final Range<Instant> range) {
        return transactionHelper.transaction(em -> {
            final ResourceEntity resource = getResourceEntity(resourceId, em);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<ResourceActionEntity> cq = cb.createQuery(ResourceActionEntity.class);
            final Root<ResourceActionEntity> rootEntry = cq.from(ResourceActionEntity.class);

            final Path<Instant> time = rootEntry.get("timestamp");

            final Predicate actionTimeInRange = getPredicateForRange(range, cb, rootEntry);
            final Predicate isEqualToProvidedResource = cb.equal(rootEntry.get("resource"), resource);

            final CriteriaQuery<ResourceActionEntity> allInRange = cq
                    .select(rootEntry)
                    .where(cb.and(actionTimeInRange, isEqualToProvidedResource))
                    .orderBy(cb.desc(time));

            final TypedQuery<ResourceActionEntity> allQuery = em.createQuery(allInRange);

            return new ResourceActionsHistory(resource, range, toActionInfos(allQuery.getResultList()));
        });
    }

    @Override
    public ServerActionsHistory getActionsOnServer(int serverId, Range<Instant> range) throws NoSuchResource {
        return transactionHelper.transaction(em -> {
            final ServerEntity server = getServerEntity(serverId, em);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<ServerActionEntity> cq = cb.createQuery(ServerActionEntity.class);
            final Root<ServerActionEntity> rootEntry = cq.from(ServerActionEntity.class);

            final Path<Instant> time = rootEntry.get("timestamp");

            final Predicate actionTimeInRange = getPredicateForRange(range, cb, rootEntry);
            final Predicate isEqualToProvidedResource = cb.equal(rootEntry.get("server"), server);

            final CriteriaQuery<ServerActionEntity> allInRange = cq
                    .select(rootEntry)
                    .where(cb.and(actionTimeInRange, isEqualToProvidedResource))
                    .orderBy(cb.desc(time));

            final TypedQuery<ServerActionEntity> allQuery = em.createQuery(allInRange);

            return new ServerActionsHistory(server, range, toActionInfos(allQuery.getResultList()));
        });
    }

    private <T extends ActionEntity> List<ActionInfo> toActionInfos(List<T> actions) {
        return actions.stream()
                .map(a -> new ActionInfo(
                                a.getActor().getName(),
                                a.getTimestamp(),
                                a.getDetails()
                        )
                )
                .collect(Collectors.toList());
    }

    private <T extends ActionEntity> Predicate getPredicateForRange(
            final Range<Instant> range,
            final CriteriaBuilder cb,
            final Root<T> root
    ) {
        final Path<Instant> time = root.get("timestamp");
        final List<Predicate> predicates = new ArrayList<>();

        if (range.hasLowerBound()) {
            final Instant lowerEndpoint = range.lowerEndpoint();
            if (range.lowerBoundType() == BoundType.CLOSED) {
                predicates.add(cb.greaterThanOrEqualTo(time, lowerEndpoint));
            } else {
                predicates.add(cb.greaterThan(time, lowerEndpoint));
            }
        }

        if (range.hasUpperBound()) {
            final Instant upperEndpoint = range.upperEndpoint();
            if (range.lowerBoundType() == BoundType.CLOSED) {
                predicates.add(cb.lessThanOrEqualTo(time, upperEndpoint));
            } else {
                predicates.add(cb.lessThan(time, upperEndpoint));
            }
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
