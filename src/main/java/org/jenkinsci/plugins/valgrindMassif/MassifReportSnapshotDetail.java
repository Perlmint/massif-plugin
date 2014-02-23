package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.valgrindMassif.report.MassifReportSnapshot;

/**
 * Created by frakafra on 2014. 2. 23..
 */
public class MassifReportSnapshotDetail {
    private MassifReportSnapshot snapshot;
    private AbstractBuild<?, ?> owner;
    private Integer index;

    public MassifReportSnapshotDetail(AbstractBuild<?, ?> owner, MassifReportSnapshot snapshot, Integer index) {
        this.owner = owner;
        this.snapshot = snapshot;
        this.index = index;
    }

    public MassifReportSnapshot getSnapshot() {
        return snapshot;
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public Integer getIndex() {
        return index;
    }
}
