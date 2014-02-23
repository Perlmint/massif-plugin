package org.jenkinsci.plugins.valgrindMassif.report;

import java.math.BigInteger;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifReportSnapshot {
    private Integer snapshotID;
    private BigInteger time;
    private Integer memoryHeap;
    private Integer memoryHeapExtra;
    private Integer memoryStacks;
    private Boolean isPeak;
    private Boolean isDetailed;
    private MassifReportHeapNode root;

    public String toString() {
        return toString(0);
    }

    public String toString(Integer level) {
        String heapTreeString = "";
        if (isDetailed.booleanValue()) {
            heapTreeString = "\nHeapTree : {\n" + root.toString(level + 1) + "\n}";
        }
        return "Snapshot {" + "\nID : " + snapshotID.toString() + "\nTime : " + time.toString() +
                "\nMemoryHeap : " + memoryHeap.toString() + "\nMemoryHeapExtra : " + memoryHeapExtra.toString() +
                "\nMemoryStacks : " + memoryStacks.toString() + "\nisPeak : " + isPeak.toString() +
                "\nisDetailed : " + isDetailed.toString() + heapTreeString + "\n}";
    }

    public MassifReportHeapNode getRoot() {
        return root;
    }

    public void setRoot(MassifReportHeapNode root) {
        this.root = root;
    }

    public Integer getSnapshotID() {
        return snapshotID;
    }

    public void setSnapshotID(Integer snapshotID) {
        this.snapshotID = snapshotID;
    }

    public BigInteger getTime() {
        return time;
    }

    public void setTime(BigInteger time) {
        this.time = time;
    }

    public Integer getMemoryHeap() {
        return memoryHeap;
    }

    public void setMemoryHeap(Integer memoryHeap) {
        this.memoryHeap = memoryHeap;
    }

    public Integer getMemoryHeapExtra() {
        return memoryHeapExtra;
    }

    public void setMemoryHeapExtra(Integer memoryHeapExtra) {
        this.memoryHeapExtra = memoryHeapExtra;
    }

    public Integer getMemoryStacks() {
        return memoryStacks;
    }

    public void setMemoryStacks(Integer memoryStacks) {
        this.memoryStacks = memoryStacks;
    }

    public Boolean getIsPeak() {
        return isPeak;
    }

    public void setIsPeak(Boolean isPeak) {
        this.isPeak = isPeak;
    }

    public Boolean getIsDetailed() {
        return isDetailed;
    }

    public void setIsDetailed(Boolean isDetailed) {
        this.isDetailed = isDetailed;
    }

    public Integer getTotalUsage() { return memoryHeap + memoryStacks + memoryHeapExtra; }
}
