package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

final class DbGatewayCommons
{
  private DbGatewayCommons() {
    throw new AssertionError();
  }

  static UserEntity getUserEntity(final String userName, final EntityManager em)
  {
    final CriteriaBuilder cb=em.getCriteriaBuilder();
    final CriteriaQuery<UserEntity> cq=cb.createQuery(UserEntity.class);
    final Root<UserEntity> rootEntry=cq.from(UserEntity.class);
    final CriteriaQuery<UserEntity> selectUserQuery=cq
        .where(cb.equal(rootEntry.get("name"), userName));

    return em.createQuery(selectUserQuery).getSingleResult();
  }
}
