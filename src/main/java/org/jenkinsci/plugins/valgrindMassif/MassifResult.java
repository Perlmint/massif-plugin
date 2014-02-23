package org.jenkinsci.plugins.valgrindMassif;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.valgrindMassif.report.MassifReportSnapshot;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifResult {
    private MassifParserResult parser;
    private List<MassifReport> reports;
    private AbstractBuild<?, ?> owner;

    public MassifResult( AbstractBuild<?, ?> build, MassifParserResult parser)
    {
        this.parser = parser;
        this.owner = build;
        this.reports = null;
    }

    public List<MassifReport> getReports() throws IOException, InterruptedException {
        if (reports != null) {
            return reports;
        }
        FilePath file = new FilePath(getOwner().getRootDir());
        reports = file.act(parser);
        return reports;
    }

    public Object getDynamic(final String l, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        final String[] s = l.split("/");
        final String data = s[s.length -1];

        if (!data.startsWith("report_")) {
            return null;
        }

        Integer index = Integer.parseInt(data.substring(7, data.length()));
        return new MassifReportDetail(owner, getReports().get(index));
    }

    public Integer getAverageMemoryUsage() throws IOException, InterruptedException {
        Integer count = 0;
        Integer total = 0;
        for (MassifReport report : getReports()) {
            Integer peakIndex = report.getPeakSnapshotIndex();
            if (peakIndex == null) {
                continue;
            }
            count += 1;
            MassifReportSnapshot snapshot = report.getSnapshot(peakIndex);
            total += snapshot.getMemoryHeap() + snapshot.getMemoryHeapExtra() + snapshot.getMemoryStacks();
        }
        return total / count;
    }

    public int getReportsCount() {
        try {
            if (getReports() == null) {
                return 0;
            }
            return getReports().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getSummary() throws IOException, InterruptedException {
        StringBuilder summary = new StringBuilder();
        Integer averageUsage = getAverageMemoryUsage();

        summary.append("<a href=\"valgrindResult\">");

        summary.append("Average Usage : " + averageUsage.toString());

        summary.append("</a>");

        return summary.toString();
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }
}
