package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.AppServerDataObject;
import org.junit.*;

public class AppServerDAOTest {

  private AppServerDAO dao;

  @Before
  public void setUp() throws Exception {
    dao = new AppServerDAO();
  }

  @Test
  public void testCreate() throws Exception {
    AppServerDataObject server = new AppServerDataObject();
    server.setName("server");
    server = dao.createOrUpdate(server);
  }
}
