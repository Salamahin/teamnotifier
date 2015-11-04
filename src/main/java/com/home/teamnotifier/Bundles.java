package com.home.teamnotifier;

import com.home.teamnotifier.db.*;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;

final class Bundles {
  private Bundles() {
    throw new AssertionError();
  }

//  public static final HibernateBundle<NotifierConfiguration> HIBERNATE =
//      new HibernateBundle<NotifierConfiguration>(
//          AppServer.class,
//          Environment.class,
//          SharedResource.class,
//          Subscription.class,
//          User.class
//      ) {
//        @Override
//        public PooledDataSourceFactory getDataSourceFactory(final NotifierConfiguration conf) {
//          return conf.getDataSourceFactory();
//        }
//      };

  public static final GuiceBundle<NotifierConfiguration> GUICE =
      GuiceBundle.<NotifierConfiguration>newBuilder()
          .addModule(new NotifierModule())
          .build();

  public static final AssetsBundle ASSETS =
      new AssetsBundle("/assets", "/");
}
