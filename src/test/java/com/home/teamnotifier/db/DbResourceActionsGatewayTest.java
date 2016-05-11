//package com.home.teamnotifier.db;
//
//import com.google.common.collect.Range;
//import com.home.teamnotifier.db.DbPreparer;
//import com.home.teamnotifier.core.BroadcastInformation;
//import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
//import com.home.teamnotifier.core.responses.notification.ResourceAction;
//import com.home.teamnotifier.gateways.ActionsGateway;
//import com.home.teamnotifier.gateways.ResourceDescription;
//import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
//import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
//import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.time.Instant;
//
//import static com.home.teamnotifier.db.DbPreparer.getRandomString;
//import static org.assertj.core.api.Assertions.*;
//
//public class DbResourceActionsGatewayTest {
//
//    private static ActionsTester<ResourceActionsHistory, ResourceAction> tester;
//    private static DbPreparer helper;
//    private static ActionsGateway gateway;
//    private static EnvironmentEntity environment;
//
//    @BeforeClass
//    public static void setUp() throws Exception {
//        helper = new DbPreparer();
//        gateway = new DbActionsGateway(helper.TRANSACTION_HELPER);
//
//        environment = helper.createPersistedEnvironmentWithOneServerAndOneResource(
//                getRandomString(),
//                getRandomString(),
//                getRandomString()
//        );
//
//        tester = new ActionsTester<ResourceActionsHistory, ResourceAction>(helper) {
//            @Override
//            BroadcastInformation<ResourceAction> newAction(String userName, int id, String description) {
//                return gateway.newResourceAction(userName, id, description);
//            }
//
//            @Override
//            int persistedId() {
//                return helper.anyResourceId(environment);
//            }
//
//            @Override
//            ResourceActionsHistory actionsInRange(int id, Range<Instant> range) {
//                return gateway.getActionsOnResource(id, range);
//            }
//        };
//    }
//
//    @Test
//    public void testDoesntHaveBeforeMiddle() throws Exception {
//        tester.testDoesntHaveBeforeMiddle();
//    }
//
//    @Test
//    public void testDoesntHaveAfter() throws Exception {
//        tester.testDoesntHaveAfter();
//    }
//
//    @Test
//    public void testReturnsSubscribersNamesAfterAction() throws Exception {
//        tester.testReturnsSubscribersNamesAfterAction(environment);
//    }
//
//    @Test
//    public void testActionByResourceNameAndServerName() throws Exception {
//        final String envName = "env";
//        final String srvName = "srv";
//        final String appName = "app";
//
//        helper.createPersistedEnvironmentWithOneServerAndOneResource(envName, srvName, appName);
//        final String userName = helper.createPersistedUser(getRandomString(), getRandomString()).getName();
//
//        final ResourceDescription resourceDescription = ResourceDescription.newBuilder()
//                .withResourceName(appName)
//                .withEnvironmentName(envName)
//                .withServerName(srvName)
//                .build();
//
//        assertThat(gateway.newResourceAction(userName, resourceDescription, "test")).isNotNull();
//    }
//
//    @Test(expected = NoSuchResource.class)
//    public void testNoSuchResourceWhenGetActionsOfNotPersistedEntity() {
//        tester.testNoSuchEntityWhenGetActionsOfNotPersistedEntity();
//    }
//
//    @Test(expected = EmptyDescription.class)
//    public void testEmptyDescriptionWhenNoActionDataProvided() {
//        tester.testEmptyDescriptionWhenNoActionDataProvided();
//    }
//
//    @Test(expected = NoSuchUser.class)
//    public void testNoSuchUserWhenNewActionWithNotPresentUser() {
//        tester.testNoSuchUserWhenNewActionWithNotPresentUser();
//    }
//}
