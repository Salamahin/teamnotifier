package com.home.teamnotifier.gateways;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.User;
import com.home.teamnotifier.db.*;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Optional;

public class DbUserGateway implements UserGateway {

  private final TransactionHelper transactionHelper;

  @Inject
  public DbUserGateway(final TransactionHelper transactionHelper) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public User userById(final int userId) {
    final UserEntity storedUserEntity =
        transactionHelper.transaction(em -> em.find(UserEntity.class, userId));
    Preconditions.checkNotNull(storedUserEntity, "User with id %s not found", userId);
    return new User(storedUserEntity.getId(), storedUserEntity.getName());
  }

  @Override
  public String getPasswordHash(final String userName) {
    return transactionHelper.transaction(em -> {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      final CriteriaQuery<String> cq = cb.createQuery(String.class);
      final Root<UserEntity> rootEntry = cq.from(UserEntity.class);
      final CriteriaQuery<String> hash = cq
          .select(rootEntry.get("passHash"))
          .where(cb.equal(rootEntry.get("name"), userName));

      return em.createQuery(hash).getSingleResult();
    });
  }
}
