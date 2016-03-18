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
    public DbActionsGateway(
            final TransactionHelper transactionHelper
    ) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public BroadcastInformation<ResourceAction> newActionOnSharedResource(
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
    public BroadcastInformation<ServerAction> newActionOnAppSever(
            final String userName,
            final int serverId,
            final String description
    ) throws NoSuchServer, EmptyDescription, NoSuchUser {
        try {
            return tryPersistNewActionOnAppServer(userName, serverId, description);
        } catch (Exception exc) {
            rethrowIfContainsConstraintViolation(exc);
            return null;
        }
    }

    @Override
    public BroadcastInformation<ResourceAction> newActionOnSharedResource(
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

    private BroadcastInformation<ServerAction> tryPersistNewActionOnAppServer(
            final String userName,
            final int appServerId,
            final String description
    ) {
        return transactionHelper.transaction(em -> {
            final AppServerEntity appServer = getAppServerEntity(appServerId, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ServerAction action = newActionOnAppServer(userEntity, appServer, description, em);
            return new BroadcastInformation<>(action, getSubscribersButUser(userName, appServer));
        });
    }

    private BroadcastInformation<ResourceAction> tryPersistNewActionOnResource(
            final String userName,
            final int resourceId,
            final String description
    ) {
        return transactionHelper.transaction(em -> {
            final ResourceEntity resourceEntity = getSharedResourceEntity(resourceId, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ResourceAction resourceAction = newActionOnSharedResource(
                    userEntity,
                    resourceEntity,
                    description,
                    em
            );

            return new BroadcastInformation<>(
                    resourceAction,
                    getSubscribersButUser(userName, resourceEntity.getAppServer())
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
            final ResourceEntity resourceEntity = getSharedResourceEntity(envName, serverName, resourceName, em);
            final UserEntity userEntity = getUserEntity(userName, em);

            final ResourceAction action = newActionOnSharedResource(userEntity, resourceEntity, description, em);
            return new BroadcastInformation<>(action, getSubscribersButUser(userName, resourceEntity.getAppServer()));
        });
    }

    private ServerAction newActionOnAppServer(
            final UserEntity userEntity,
            final AppServerEntity appServer,
            final String description,
            final EntityManager em
    ) {
        final ActionOnAppServerEntity action = new ActionOnAppServerEntity(
                userEntity,
                appServer,
                description
        );
        em.persist(action);

        return new ServerAction(userEntity, appServer, description);
    }

    private ResourceAction newActionOnSharedResource(
            final UserEntity userEntity,
            final ResourceEntity resourceEntity,
            final String description,
            final EntityManager em
    ) {
        final ActionOnSharedResourceEntity action = new ActionOnSharedResourceEntity(
                userEntity,
                resourceEntity,
                description
        );
        em.persist(action);

        return new ResourceAction(userEntity, resourceEntity, description);
    }

    private AppServerEntity getAppServerEntity(final int appServerId, final EntityManager em) {
        final AppServerEntity entity = em.find(AppServerEntity.class, appServerId);
        if (entity == null)
            throw new NoSuchServer(String.format("No server with id %d", appServerId));

        return entity;
    }

    private ResourceEntity getSharedResourceEntity(final int resourceId, final EntityManager em) {
        final ResourceEntity entity = em.find(ResourceEntity.class, resourceId);
        if (entity == null)
            throw new NoSuchResource(String.format("No resource with id %d", resourceId));

        return entity;
    }

    private ResourceEntity getSharedResourceEntity(
            final String envName,
            final String serverName,
            final String resourceName,
            final EntityManager em
    ) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ResourceEntity> cqRes = cb.createQuery(ResourceEntity.class);
        Subquery<AppServerEntity> srvByName = cqRes.subquery(AppServerEntity.class);
        Subquery<EnvironmentEntity> envByName = srvByName.subquery(EnvironmentEntity.class);

        final Root<ResourceEntity> rootRes = cqRes.from(ResourceEntity.class);
        final Root<AppServerEntity> rootSrv = srvByName.from(AppServerEntity.class);
        final Root<EnvironmentEntity> rootEnv = envByName.from(EnvironmentEntity.class);

        envByName = envByName.select(rootEnv).where(cb.equal(rootEnv.get("name"), envName));

        srvByName = srvByName.select(rootSrv).where(cb.and(
                cb.equal(rootSrv.get("name"), serverName),
                cb.equal(rootSrv.get("environment"), envByName)
        ));

        cqRes.select(rootRes).where(cb.and(
                cb.equal(rootRes.get("name"), resourceName),
                cb.equal(rootRes.get("appServer"), srvByName)
        ));

        return em
                .createQuery(cqRes)
                .getSingleResult();
    }

    @Override
    public ResourceActionsHistory getActionsOnResource(final int resourceId, final Range<Instant> range) {
        final List<ActionOnSharedResourceEntity> actions = transactionHelper.transaction(em -> {
            final ResourceEntity resource = getSharedResourceEntity(resourceId, em);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<ActionOnSharedResourceEntity> cq = cb.createQuery(ActionOnSharedResourceEntity.class);
            final Root<ActionOnSharedResourceEntity> rootEntry = cq.from(ActionOnSharedResourceEntity.class);

            final Path<Instant> time = rootEntry.get("actionTime");

            final Predicate actionTimeInRange = getPredicateForRange(range, cb, rootEntry);
            final Predicate isEqualToProvidedResource = cb.equal(rootEntry.get("resource"), resource);

            final CriteriaQuery<ActionOnSharedResourceEntity> allInRange = cq
                    .select(rootEntry)
                    .where(cb.and(actionTimeInRange, isEqualToProvidedResource))
                    .orderBy(cb.desc(time));

            final TypedQuery<ActionOnSharedResourceEntity> allQuery = em.createQuery(allInRange);

            return allQuery.getResultList();
        });

        return new ResourceActionsHistory(toActionInfos(actions));
    }

    @Override
    public ServerActionsHistory getActionsOnServer(int serverId, Range<Instant> range) throws NoSuchResource {
        final List<ActionOnAppServerEntity> actions = transactionHelper.transaction(em -> {
            final AppServerEntity resource = getAppServerEntity(serverId, em);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<ActionOnAppServerEntity> cq = cb.createQuery(ActionOnAppServerEntity.class);
            final Root<ActionOnAppServerEntity> rootEntry = cq.from(ActionOnAppServerEntity.class);

            final Path<Instant> time = rootEntry.get("actionTime");

            final Predicate actionTimeInRange = getPredicateForRange(range, cb, rootEntry);
            final Predicate isEqualToProvidedResource = cb.equal(rootEntry.get("appServer"), resource);

            final CriteriaQuery<ActionOnAppServerEntity> allInRange = cq
                    .select(rootEntry)
                    .where(cb.and(actionTimeInRange, isEqualToProvidedResource))
                    .orderBy(cb.desc(time));

            final TypedQuery<ActionOnAppServerEntity> allQuery = em.createQuery(allInRange);

            return allQuery.getResultList();
        });

        return new ServerActionsHistory(toActionInfos(actions));
    }

    private <T extends ActionEntity> List<ActionInfo> toActionInfos(List<T> actions) {
        return actions.stream()
                .map(a -> new ActionInfo(
                                a.getActor().getName(),
                                a.getActionTime(),
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
        final Path<Instant> time = root.get("actionTime");
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
