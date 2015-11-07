package com.home.teamnotifier.db;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.core.environment.*;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DbSharedResourceActionsGateway implements SharedResourceActionsGateway {
  private final TransactionHelper transactionHelper;

  @Inject
  public DbSharedResourceActionsGateway(
      final TransactionHelper transactionHelper
  ) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public BroadcastInformation newAction(String userName, int resourceId, String description) {
    return transactionHelper.transaction(em -> {
      final SharedResourceEntity resourceEntity = em.find(SharedResourceEntity.class, resourceId);
      final UserEntity userEntity = DbGatewayCommons.getUserEntity(userName, em);

      final ActionOnSharedResourceEntity action =
          new ActionOnSharedResourceEntity(userEntity, resourceEntity, description);
      em.merge(action);

      final LocalDateTime time = action.getActionTime();

      final List<String> usersToNotify = DbGatewayCommons
          .getSubscribersButUser(userName, resourceEntity.getAppServer());

      return new BroadcastInformation(
          String.format("%s: %s", userName, description),
          time,
          usersToNotify
      );
    });
  }

  @Override
  public ActionsInfo getActions(Range<LocalDateTime> range) {
    final List<ActionOnSharedResourceEntity> actions = transactionHelper.transaction(em -> {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      final CriteriaQuery<ActionOnSharedResourceEntity> cq = cb
          .createQuery(ActionOnSharedResourceEntity.class);
      final Root<ActionOnSharedResourceEntity> rootEntry = cq
          .from(ActionOnSharedResourceEntity.class);
      final Path<LocalDateTime> time = rootEntry.get("actionTime");

      final CriteriaQuery<ActionOnSharedResourceEntity> allInRange =
          cq.select(rootEntry).where(getPredicateForRange(range, cb, rootEntry))
              .orderBy(cb.desc(time));
      final TypedQuery<ActionOnSharedResourceEntity> allQuery = em.createQuery(allInRange);

      return allQuery.getResultList();
    });

    final List<ActionInfo> actionInfos = actions.stream()
        .map(a -> new ActionInfo(
                a.getActor().getName(),
                a.getActionTime().toString(),
                a.getDetails()
            )
        )
        .collect(Collectors.toList());

    return new ActionsInfo(actionInfos);
  }

  private Predicate getPredicateForRange(Range<LocalDateTime> range, CriteriaBuilder cb,
      Root<ActionOnSharedResourceEntity> root) {
    final Path<LocalDateTime> time = root.get("actionTime");
    final List<Predicate> predicates = new ArrayList<>();

    if (range.hasLowerBound()) {
      final LocalDateTime lowerEndpoint = range.lowerEndpoint();
      if (range.lowerBoundType() == BoundType.CLOSED) {
        predicates.add(cb.greaterThanOrEqualTo(time, lowerEndpoint));
      } else { predicates.add(cb.greaterThan(time, lowerEndpoint)); }
    }

    if (range.hasUpperBound()) {
      final LocalDateTime upperEndpoint = range.lowerEndpoint();
      if (range.lowerBoundType() == BoundType.CLOSED) {
        predicates.add(cb.lessThanOrEqualTo(time, upperEndpoint));
      } else { predicates.add(cb.lessThan(time, upperEndpoint)); }
    }

    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
  }
}
