package com.home.teamnotifier.resource.environment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormatter {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("HH:mm:ss dd:MM:YYYY");

  public String toString(final LocalDateTime time) {
    return formatter.format(time);
  }
}
