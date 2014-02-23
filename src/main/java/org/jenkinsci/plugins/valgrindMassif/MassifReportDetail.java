package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.AbstractBuild;
import hudson.util.DataSetBuilder;
import org.jenkinsci.plugins.valgrindMassif.report.MassifReportSnapshot;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * Created by frakafra on 2014. 2. 23..
 */
public class MassifReportDetail {
    private MassifReport report;
    private AbstractBuild<?, ?> owner;

    public MassifReportDetail(AbstractBuild<?, ?> build, MassifReport report) {
        this.report = report;
        this.owner = build;
    }

    public MassifReport getReport() {
        return report;
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public MassifReportSnapshotDetail getDynamic(final String l, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        final String[] s = l.split("/");
        final String data = s[s.length -1];

        if (!data.startsWith("snapshot_")) {
            return null;
        }

        Integer index = Integer.parseInt(data.substring(9, data.length()));
        return new MassifReportSnapshotDetail(owner, report.getSnapshot(index), index);
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException, InterruptedException {
        MassifGraph graph = new MassifGraph(getOwner(), getDataSetBuilder().build(), "Memory Usage(bytes)", MassifGraph.DEFAULT_CHART_WIDTH, MassifGraph.DEFAULT_CHART_HEIGHT);
        graph.doPng(req, rsp);
    }

    private DataSetBuilder<String, String> getDataSetBuilder() throws IOException, InterruptedException
    {
        DataSetBuilder<String, String> dsb = new DataSetBuilder<String, String>();
        for (MassifReportSnapshot snapshot : report.getSnapshots()) {
            String label = "#" + snapshot.getSnapshotID().toString();
            //dsb.add(snapshot.getTotalUsage(), "Total", label);
            dsb.add(snapshot.getMemoryHeap(), "Heap", label);
            dsb.add(snapshot.getMemoryHeapExtra(), "Heap Extra", label);
            dsb.add(snapshot.getMemoryStacks(), "Stacks", label);
        }
        return dsb;
    }
}
