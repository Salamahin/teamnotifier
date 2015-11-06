package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonAutoDetect(
    fieldVisibility=JsonAutoDetect.Visibility.ANY,
    getterVisibility=JsonAutoDetect.Visibility.NONE,
    isGetterVisibility=JsonAutoDetect.Visibility.NONE,
    setterVisibility=JsonAutoDetect.Visibility.NONE,
    creatorVisibility=JsonAutoDetect.Visibility.NONE)
@JsonTypeName("ActionInfo")
public class ActionInfo
{
  private final String userName;
  private final String timestamp;
  private final String description;

  @JsonCreator
  public ActionInfo(
      @JsonProperty("userName") final String userName,
      @JsonProperty("timestamp") final String timestamp,
      @JsonProperty("description") final String description
  )
  {
    this.userName=userName;
    this.timestamp=timestamp;
    this.description=description;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getTimestamp()
  {
    return timestamp;
  }

  public String getDescription()
  {
    return description;
  }
}
