package com.home.teamnotifier.resource.auth;

import com.fasterxml.jackson.annotation.*;

@SuppressWarnings("FieldCanBeLocal") @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonAutoDetect(
    fieldVisibility=JsonAutoDetect.Visibility.ANY,
    getterVisibility=JsonAutoDetect.Visibility.NONE,
    isGetterVisibility=JsonAutoDetect.Visibility.NONE,
    setterVisibility=JsonAutoDetect.Visibility.NONE,
    creatorVisibility=JsonAutoDetect.Visibility.NONE)
@JsonTypeName("UserInfo")
public class UserInfo
{

  private final String name;

  @JsonCreator
  public UserInfo(@JsonProperty("name") String name)
  {
    this.name=name;
  }
}
