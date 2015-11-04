package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.resource.environment.*;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

public class DbEnvironmentGateway implements EnvironmentGateway {
  private final TransactionHelper transactionHelper;

  @Inject
  public DbEnvironmentGateway(final TransactionHelper transactionHelper) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public EnvironmentsInfo status() {
    return new EnvironmentsInfo(
        loadListFromDb().stream()
            .map(this::toEnvironment)
            .collect(Collectors.toList())
    );
  }

  private EnvironmentInfo toEnvironment(EnvironmentEntity entity) {
    return new EnvironmentInfo(
        entity.getName(),
        entity.getAppServers().stream()
            .map(this::toAppSever)
            .collect(toSet())
    );
  }

  private AppServerInfo toAppSever(final AppServerEntity entity) {
    return new AppServerInfo(
        entity.getName(),
        entity.getResources().stream()
            .map(this::toResource)
            .collect(toSet())
    );
  }

  private SharedResourceInfo toResource(final SharedResourceEntity sharedResourceEntity) {
    return new SharedResourceInfo(sharedResourceEntity.getName());
  }

  private List<EnvironmentEntity> loadListFromDb() {
    return transactionHelper.transaction(em -> {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      final CriteriaQuery<EnvironmentEntity> cq = cb.createQuery(EnvironmentEntity.class);
      final Root<EnvironmentEntity> rootEntry = cq.from(EnvironmentEntity.class);
      final CriteriaQuery<EnvironmentEntity> all = cq.select(rootEntry);
      final TypedQuery<EnvironmentEntity> allQuery = em.createQuery(all);
      return allQuery.getResultList();
    });
  }
}
