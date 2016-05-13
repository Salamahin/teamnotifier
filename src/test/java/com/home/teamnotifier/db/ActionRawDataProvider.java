package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class ActionRawDataProvider {
    private final String tableName;
    private final String targetColumnIdName;

    public ActionRawDataProvider(String tableName, String targetColumnIdName) {
        this.tableName = tableName;
        this.targetColumnIdName = targetColumnIdName;
    }

    public IDataSet newActionPerHour(final int userId, final int targetId, final Range<Instant> timeRange) throws DataSetException {
        final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        final DataSetBuilder b = new DataSetBuilder(false);

        Instant timestamp = timeRange.lowerEndpoint();
        int idGenerator = 500000;
        do {
            final String timestampStr = timestampFormatter.format(timestamp);
            b
                    .newRow(tableName)
                    .with("actor_id",  userId)
                    .with(targetColumnIdName, targetId)
                    .with("details", "action")
                    .with("timestamp", timestampStr)
                    .with("id", idGenerator++)
                    .add();

            timestamp = timestamp.plus(1, ChronoUnit.HOURS);
        } while (timestamp.isBefore(timeRange.upperEndpoint()));

        return b.build();
    }
}
