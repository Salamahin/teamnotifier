package com.home.teamnotifier.db;

import java.time.LocalDateTime;

public final class ReservationData
{
  private final UserEntity occupier;
  private final LocalDateTime occupationTime;

  ReservationData(final UserEntity occupier, final LocalDateTime occupationTime)
  {
    this.occupier=occupier;
    this.occupationTime=occupationTime;
  }

  public UserEntity getOccupier()
  {
    return occupier;
  }

  public LocalDateTime getOccupationTime()
  {
    return occupationTime;
  }
}
