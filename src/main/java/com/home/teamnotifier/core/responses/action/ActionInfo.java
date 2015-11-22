package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;
import com.home.teamnotifier.utils.Iso8601DateTimeHelper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.home.teamnotifier.utils.Iso8601DateTimeHelper.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ActionInfo")
public class ActionInfo implements Serializable {
  private final String userName;

  private final String timestamp;

  private final String description;

  @JsonCreator
  private ActionInfo(
      @JsonProperty("userName") final String userName,
      @JsonProperty("timestamp") final String timestamp,
      @JsonProperty("description") final String description
  ) {
    this.userName = userName;
    this.timestamp = timestamp;
    this.description = description;
  }

  public ActionInfo(
          final String userName,
          final LocalDateTime timestamp,
          final String description
  ) {
    this.userName = userName;
    this.timestamp = toIso8601String(timestamp);
    this.description = description;
  }


  public String getUserName() {
    return userName;
  }

  public LocalDateTime getTimestamp() {
    return parseTimestamp(timestamp);
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, timestamp, description);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final ActionInfo that = (ActionInfo) o;
    return Objects.equals(userName, that.userName) &&
        Objects.equals(timestamp, that.timestamp) &&
        Objects.equals(description, that.description);
  }
}
