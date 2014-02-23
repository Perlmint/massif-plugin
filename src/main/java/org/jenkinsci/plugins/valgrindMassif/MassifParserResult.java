package org.jenkinsci.plugins.valgrindMassif;

import hudson.FilePath;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifParserResult implements FilePath.FileCallable<List<MassifReport>> {
    private String pattern;
    private AbstractBuild<?, ?> owner;

    public MassifParserResult( String pattern, AbstractBuild<?, ?> owner )
    {
        this.pattern = pattern;
        this.owner = owner;
    }

    public List<MassifReport> invoke(File basedir, VirtualChannel virtualChannel) throws IOException, InterruptedException {
        List<MassifReport> reports = new Vector<MassifReport>();

        MassifLogger.logFine("looking for massif files in '" + basedir.getAbsolutePath() + "' with pattern '" + pattern + "'");
        for ( String fileName : findMassifReports(basedir) )
        {
            MassifLogger.logFine("parsing " + fileName + "...");
            try
            {
                MassifReport report = new MassifParser().parse( new File(basedir, fileName) );
                reports.add(report);
            }
            catch (Exception e)
            {
                MassifLogger.logWarn("failed to parse " + fileName + ": " + e.getMessage() + " at ");
                e.printStackTrace(MassifLogger.getLOGGER());
            }
        }

        return reports;
    }

    private String[] findMassifReports(File parentPath)
    {
        FileSet fs = Util.createFileSet(parentPath, this.pattern);
        DirectoryScanner ds = fs.getDirectoryScanner();
        return ds.getIncludedFiles();
    }
}
