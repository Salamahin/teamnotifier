package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class DbUserGateway implements UserGateway
{

  private final TransactionHelper transactionHelper;

  @Inject
  public DbUserGateway(final TransactionHelper transactionHelper)
  {
    this.transactionHelper=transactionHelper;
  }

  @Override public UserCredentials userCredentials(final String userName)
  {
    final UserEntity entity=getEntityByName(userName);
    if (entity != null)
      return new UserCredentials(entity.getName(), entity.getPassHash());
    else
      return null;
  }

  private UserEntity getEntityByName(String name)
  {
    return transactionHelper.transaction(em -> {
      final CriteriaBuilder cb=em.getCriteriaBuilder();
      final CriteriaQuery<UserEntity> cq=cb.createQuery(UserEntity.class);
      final Root<UserEntity> rootEntry=cq.from(UserEntity.class);
      final CriteriaQuery<UserEntity> selectUserQuery=cq
          .where(cb.equal(rootEntry.get("name"), name));

      return em.createQuery(selectUserQuery).getSingleResult();
    });
  }
}
