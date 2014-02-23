package org.jenkinsci.plugins.valgrindMassif;

import org.jenkinsci.plugins.valgrindMassif.report.MassifReportSnapshot;

import java.util.List;
import java.util.Vector;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifReport {
    private String description;
    private String command;
    private String timeUnit;
    private List<Integer> detailedSnapshotIndices;
    private Integer peakSnapshotIndex;
    private List<MassifReportSnapshot> snapshots;

    MassifReport() {
        detailedSnapshotIndices = new Vector<Integer>();
        snapshots = new Vector<MassifReportSnapshot>();
    }

    public String toString() {
        return this.toString(0);
    }

    public String toString(Integer level) {
        String stringfiedSnapshot = new String("[");
        for (MassifReportSnapshot snapshot : getSnapshots()) {
            stringfiedSnapshot = stringfiedSnapshot + "\n" + snapshot.toString(level + 1);
        }
        stringfiedSnapshot = stringfiedSnapshot + "\n]";
        return "Report {\n" + "Description : " + description + "\nCommand : " + command + "\ntimeUnit" + timeUnit +
                "\nPeak Snapshot : " + peakSnapshotIndex.toString() + "Snapshots : " + stringfiedSnapshot + "\n}";
    }

    public void appendDetailedSnapshotIndex(Integer index) {
        getDetailedSnapshotIndices().add(index);
    }

    public String getDescription() {
        return description;
    }

    public void appendSnapshot(MassifReportSnapshot snapshot) {
        getSnapshots().add(snapshot);
    }

    public MassifReportSnapshot getSnapshot(Integer index) {
        return getSnapshots().get(index);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public List<Integer> getDetailedSnapshotIndices() {
        return detailedSnapshotIndices;
    }

    public Integer getPeakSnapshotIndex() {
        return peakSnapshotIndex;
    }

    public void setPeakSnapshotIndex(Integer peakSnapshotIndex) {
        this.peakSnapshotIndex = peakSnapshotIndex;
    }

    public List<MassifReportSnapshot> getSnapshots() {
        return snapshots;
    }

    public Integer getSnapshotsCount() {
        return snapshots.size();
    }

    public Integer getPeakUsage() {
        if (peakSnapshotIndex == null) {
            return null;
        }

        MassifReportSnapshot peakSnapshot = getSnapshot(peakSnapshotIndex);
        return peakSnapshot.getMemoryHeap() + peakSnapshot.getMemoryStacks() + peakSnapshot.getMemoryHeapExtra();
    }
}
