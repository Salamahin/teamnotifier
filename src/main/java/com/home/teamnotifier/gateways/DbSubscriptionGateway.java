package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.resource.auth.UserInfo;
import org.slf4j.*;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.stream.Collectors.toList;

public class DbSubscriptionGateway implements SubscriptionGateway {
  private static final Logger LOG = LoggerFactory.getLogger(DbSubscriptionGateway.class);

  private final TransactionHelper transactionHelper;

  @Inject
  public DbSubscriptionGateway(final TransactionHelper transactionHelper) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public void subscribe(final String userName, final int serverId) {
    transactionHelper.transaction(em -> {
      final UserEntity userEntity = getUserEntity(userName, em);
      final AppServerEntity appServerEntity = getAppServerEntity(serverId, em);

      SubscriptionEntity entity = new SubscriptionEntity();
      entity.setAppServer(appServerEntity);
      entity.setSubscriber(userEntity);
      entity.setTimestamp(LocalDateTime.now());

      appServerEntity.getSubscriptions().add(entity);

      em.merge(appServerEntity);
      em.merge(entity);

      return null;
    });
  }

  private UserEntity getUserEntity(final String userName, final EntityManager em) {
    final CriteriaBuilder cb = em.getCriteriaBuilder();
    final CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
    final Root<UserEntity> rootEntry = cq.from(UserEntity.class);
    final CriteriaQuery<UserEntity> selectUserQuery = cq
        .where(cb.equal(rootEntry.get("name"), userName));

    return em.createQuery(selectUserQuery).getSingleResult();
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em) {
    return em.find(AppServerEntity.class, serverId);
  }

  @Override
  public void unsubscribe(final String userName, final int serverId) {
    transactionHelper.transaction(em -> {
      final AppServerEntity serverEntity = getAppServerEntity(serverId, em);
      final UserEntity userEntity = getUserEntity(userName, em);

      final CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaDelete<SubscriptionEntity> delete = cb.createCriteriaDelete(SubscriptionEntity.class);
      final Root<SubscriptionEntity> _subscription = delete.from(SubscriptionEntity.class);

      final Predicate predicate = cb.and(
          cb.equal(_subscription.get("subscriberEntity"), userEntity),
          cb.equal(_subscription.get("appServerEntity"), serverEntity)
      );

      delete = delete.where(predicate);

      final Iterator<SubscriptionEntity> it = serverEntity.getSubscriptions().iterator();
      while (it.hasNext()) {
        if(Objects.equals(userName, it.next().getSubscriber().getName()))
          it.remove();
      }
      em.createQuery(delete).executeUpdate();
      em.merge(serverEntity);

      return null;
    });
  }

  @Override
  public void reserve(final String userName, int applicationId)
  throws AlreadyReserved {
    final boolean success = reservationSuccess(userName, applicationId);
    if (!success) { throw new AlreadyReserved(); }
  }

  private Boolean reservationSuccess(String userName, int applicationId) {
    return transactionHelper.transaction(em -> {
      final SharedResourceEntity resourceEntity = em
          .find(SharedResourceEntity.class, applicationId);
      final Optional<UserEntity> occupier = Optional
          .of(resourceEntity)
          .map(SharedResourceEntity::getOccupier);

      if (occupier.isPresent()) {
        LOG.debug("Failed to reserve resource {}:{}:{} by {}: already reserved by {}",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName,
            occupier.get().getName()
        );
        return false;
      }

      final UserEntity newOccupier = getUserEntity(userName, em);
      resourceEntity.setOccupier(newOccupier);
      resourceEntity.setOccupationStartTime(LocalDateTime.now());

      em.merge(resourceEntity);

      return true;
    });
  }

  @Override
  public void free(final String userName, int applicationId)
  throws NotReserved {
    if (!freeSuccess(userName, applicationId)) { throw new NotReserved(); }
  }

  private boolean freeSuccess(final String userName, final int applicationId) {
    return transactionHelper.transaction(em -> {
      final SharedResourceEntity resourceEntity = em
          .find(SharedResourceEntity.class, applicationId);
      final Optional<UserEntity> occupier = Optional
          .of(resourceEntity)
          .map(SharedResourceEntity::getOccupier);

      if (!occupier.isPresent()) {
        LOG.debug("Failed to free resource {}:{}:{} by {}: not reserved",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName
        );
        return false;
      }

      resourceEntity.setOccupier(null);
      resourceEntity.setOccupationStartTime(null);

      em.merge(resourceEntity);

      return true;
    });
  }
}
