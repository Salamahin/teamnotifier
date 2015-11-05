package com.home.teamnotifier.routine;

import com.home.teamnotifier.gateways.AlreadyReserved;
import com.home.teamnotifier.gateways.NotReserved;
import com.home.teamnotifier.resource.environment.EnvironmentInfo;
import org.junit.*;
import java.util.*;
import static org.mockito.Mockito.*;

public class ResourceMonitorTest {

  private ResourceMonitor monitor;
  private String userName;

  @Before
  public void setUp()
  throws Exception {
    monitor = new ResourceMonitor();
    userName= "userName";
  }

  @Test
  public void testCanGetFullStatus()
  throws Exception {
    final List<EnvironmentInfo> environmentList = monitor.getStatus(userName);
  }

  @Test
  public void testCanUnsubscribe()
  throws Exception {
    monitor.unsubscribe(userName, -1);
  }

  @Test
  public void testNotificationFiredWhenReservationSuccess()
  throws Exception {
    monitor = spy(ResourceMonitor.class);
    monitor.reserve(userName, -1);

    verify(monitor, times(1)).fireNotification();
  }

  @Test
  public void testNotificationFiredWhenFreeSuccess()
  throws Exception {
    monitor = spy(ResourceMonitor.class);
    final int applicationId = -1;
    monitor.reserve(userName, applicationId);
    monitor.free(userName, applicationId);

    verify(monitor, times(2)).fireNotification();
  }

  @Test(expected = AlreadyReserved.class)
  public void testReservationOnReservedResourceFails()
  throws Exception {
    final int applicationId = -1;
    monitor.reserve(userName, applicationId);
    monitor.reserve(userName, applicationId);
  }

  @Test
  public void testReserveResourceAfterFree()
  throws Exception {
    final int applicationId = -1;
    monitor.reserve(userName, applicationId);
    monitor.free(userName, applicationId);
    monitor.reserve(userName, applicationId);
  }

  @Test(expected = NotReserved.class)
  public void testFreeNotReservedFails()
  throws Exception {
    final int applicationId = -1;
    monitor.free(userName, applicationId);
  }
}