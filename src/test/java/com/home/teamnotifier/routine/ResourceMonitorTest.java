package com.home.teamnotifier.routine;

import com.home.teamnotifier.resource.Environment;
import org.junit.*;
import org.mockito.*;
import java.util.*;
import static org.mockito.Mockito.*;

public class ResourceMonitorTest {

  private ResourceMonitor monitor;

  private String securityToken;

  @Before
  public void setUp()
  throws Exception {
    monitor = new ResourceMonitor();
    securityToken = UUID.randomUUID().toString();
  }

  @Test
  public void testCanGetFullStatus()
  throws Exception {
    final List<Environment> environmentList = monitor.getStatus(securityToken);
  }

  @Test
  public void testCanReserve()
  throws Exception {
    monitor.reserve(securityToken, -1);
  }

  @Test
  public void testCanSubscribe()
  throws Exception {
    monitor.subscribe(securityToken, -1);
  }

  @Test
  public void testCanUnsubscribe()
  throws Exception {
    monitor.unsubscribe(securityToken, -1);
  }

  @Ignore
  @Test
  public void testNotificationFiredWhenReservationSuccess()
  throws Exception {
    monitor = mock(ResourceMonitor.class);
    monitor.subscribe(securityToken, -1);
    monitor.reserve(securityToken, -1);

    verify(monitor, times(1)).fireNotification();
  }

  @Test(expected = AlreadyReserved.class)
  public void testReservationOnReservedResourceFails()
  throws Exception {
    final int applicationId = -1;
    monitor.reserve(securityToken, applicationId);
    monitor.reserve(securityToken, applicationId);
  }

}