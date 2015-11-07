package com.home.teamnotifier.core;

import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.gateways.AlreadyReserved;
import com.home.teamnotifier.db.DbEnvironmentGateway;
import com.home.teamnotifier.db.DbSubscriptionGateway;
import com.home.teamnotifier.gateways.NotReserved;
import com.home.teamnotifier.web.socket.ClientManager;
import org.junit.*;

import static org.mockito.Mockito.*;

@Ignore
public class ResourceMonitorTest
{

  private ResourceMonitor monitor;

  private String userName;

  @Test
  public void testCanGetFullStatus()
      throws Exception
  {
    monitor.status();
  }

  @Test
  public void testCanUnsubscribe()
      throws Exception
  {
    monitor.unsubscribe(userName, -1);
  }

  @Test
  public void testNotificationFiredWhenReservationSuccess()
      throws Exception
  {
    monitor=spy(ResourceMonitor.class);
    monitor.reserve(userName, -1);

    verify(monitor, times(1)).fireNotification();
  }

  @Test
  public void testNotificationFiredWhenFreeSuccess()
      throws Exception
  {
    monitor=spy(ResourceMonitor.class);
    final int applicationId=-1;
    monitor.reserve(userName, applicationId);
    monitor.free(userName, applicationId);

    verify(monitor, times(2)).fireNotification();
  }

  @Test(expected=AlreadyReserved.class)
  public void testReservationOnReservedResourceFails()
      throws Exception
  {
    final int applicationId=-1;
    monitor.reserve(userName, applicationId);
    monitor.reserve(userName, applicationId);
  }

  @Test
  public void testReserveResourceAfterFree()
      throws Exception
  {
    final int applicationId=-1;
    monitor.reserve(userName, applicationId);
    monitor.free(userName, applicationId);
    monitor.reserve(userName, applicationId);
  }

  @Test(expected=NotReserved.class)
  public void testFreeNotReservedFails()
      throws Exception
  {
    final int applicationId=-1;
    monitor.free(userName, applicationId);
  }

  @Before
  public void setUp()
      throws Exception
  {
    final TransactionHelper helper=new TransactionHelper();
    final ClientManager manager=mock(ClientManager.class);

    monitor=new ResourceMonitor(
        new DbEnvironmentGateway(helper),
        new DbSubscriptionGateway(helper),
        manager
    );
    userName="userName";
  }
}