package org.jenkinsci.plugins.valgrindMassif;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.IOException;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifResultsScanner implements FilePath.FileCallable<String[]> {
    private String				pattern;

    public MassifResultsScanner(String pattern)
    {
        this.pattern = pattern;
    }

    public String[] invoke(File basedir, VirtualChannel virtualChannel) throws IOException, InterruptedException {
        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = { pattern };
        ds.setIncludes(includes);
        ds.setBasedir(basedir);
        ds.setCaseSensitive(true);
        ds.scan();

        return ds.getIncludedFiles();
    }
}
