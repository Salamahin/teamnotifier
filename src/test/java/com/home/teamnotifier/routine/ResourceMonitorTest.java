package com.home.teamnotifier.routine;

import com.home.teamnotifier.resource.environment.Environment;
import org.junit.*;
import java.util.*;
import static org.mockito.Mockito.*;

public class ResourceMonitorTest {

  private ResourceMonitor monitor;
  private int userId;

  @Before
  public void setUp()
  throws Exception {
    monitor = new ResourceMonitor();
    userId = -1;
  }

  @Test
  public void testCanGetFullStatus()
  throws Exception {
    final List<Environment> environmentList = monitor.getStatus(userId);
  }

  @Test
  public void testCanUnsubscribe()
  throws Exception {
    monitor.unsubscribe(userId, -1);
  }

  @Test
  public void testNotificationFiredWhenReservationSuccess()
  throws Exception {
    monitor = spy(ResourceMonitor.class);
    monitor.reserve(userId, -1);

    verify(monitor, times(1)).fireNotification();
  }

  @Test
  public void testNotificationFiredWhenFreeSuccess()
  throws Exception {
    monitor = spy(ResourceMonitor.class);
    final int applicationId = -1;
    monitor.reserve(userId, applicationId);
    monitor.free(userId, applicationId);

    verify(monitor, times(2)).fireNotification();
  }

  @Test(expected = AlreadyReserved.class)
  public void testReservationOnReservedResourceFails()
  throws Exception {
    final int applicationId = -1;
    monitor.reserve(userId, applicationId);
    monitor.reserve(userId, applicationId);
  }

  @Test
  public void testReserveResourceAfterFree()
  throws Exception {
    final int applicationId = -1;
    monitor.reserve(userId, applicationId);
    monitor.free(userId, applicationId);
    monitor.reserve(userId, applicationId);
  }

  @Test(expected = NotReserved.class)
  public void testFreeNotReservedFails()
  throws Exception {
    final int applicationId = -1;
    monitor.free(userId, applicationId);
  }
}