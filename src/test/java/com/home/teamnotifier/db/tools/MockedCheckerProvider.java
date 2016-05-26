package com.home.teamnotifier.db.tools;

import com.home.teamnotifier.core.ServerAvailabilityChecker;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class MockedCheckerProvider {
    private MockedCheckerProvider() {
        throw new AssertionError();
    }

    public static ServerAvailabilityChecker getMockedChecker() {
        final ServerAvailabilityChecker c = mock(ServerAvailabilityChecker.class);
        doReturn(emptyMap()).when(c).getAvailability();
        return c;
    }
}
