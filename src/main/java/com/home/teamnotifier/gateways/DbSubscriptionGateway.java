package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.SubscriptionEntity;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

public class DbSubscriptionGateway implements SubscriptionGateway
{
  private final TransactionHelper transactionHelper;

  @Inject
  public DbSubscriptionGateway(final TransactionHelper transactionHelper)
  {
    this.transactionHelper=transactionHelper;
  }

  @Override public void subscribe(final String userName, final int serverId)
  {
    transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);

      SubscriptionEntity entity=new SubscriptionEntity();
      entity.setAppServerId(serverId);
      entity.setSubscriberId(userEntity.getId());
      em.persist(entity);

      return null;
    });
  }

  private UserEntity getUserEntity(final String userName, final EntityManager em)
  {
    final CriteriaBuilder cb=em.getCriteriaBuilder();
    final CriteriaQuery<UserEntity> cq=cb.createQuery(UserEntity.class);
    final Root<UserEntity> rootEntry=cq.from(UserEntity.class);
    final CriteriaQuery<UserEntity> selectUserQuery=cq
        .where(cb.equal(rootEntry.get("name"), userName));

    return em.createQuery(selectUserQuery).getSingleResult();
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em)
  {
    return em.find(AppServerEntity.class, serverId);
  }

  @Override public void unsubscribe(final String userName, final int serverId)
  {
    transactionHelper.transaction(em -> {
      final AppServerEntity serverEntity=getAppServerEntity(serverId, em);
      final UserEntity userEntity=getUserEntity(userName, em);

      final CriteriaBuilder cb = em.getCriteriaBuilder();
      final CriteriaDelete<SubscriptionEntity> delete = cb.createCriteriaDelete(SubscriptionEntity.class);
      final Root<SubscriptionEntity> rootEntry = delete.from(SubscriptionEntity.class);

      final Predicate predicate=cb.and(cb.equal(rootEntry.get("subscriber"), userEntity), cb.equal(rootEntry.get("appServer"), serverEntity));
      delete.where(predicate);

      return null;
    });
  }

  @Override public void reserve(final String userName, int applicationId)
  {

  }

  @Override public void free(final String userName, int applicationId)
  {

  }
}
