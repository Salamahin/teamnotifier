package com.home.teamnotifier.core.responses.authentication;

import org.junit.Test;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;

public class UserInfoTest {
    @Test
    public void serializesToJSON() throws Exception {
        final UserInfo userInfo = new UserInfo("user");
        testSerializesToJson(UserInfo.class, userInfo, "fixtures/userInfo.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final UserInfo person = new UserInfo("user");
        testDeserializeFromJson(UserInfo.class, person, "fixtures/userInfo.json");
    }
}