package org.jenkinsci.plugins.valgrindMassif;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifPublisherConfig implements Serializable {
    private String pattern;

    @DataBoundConstructor
    public MassifPublisherConfig(String pattern) {
        this.pattern = pattern.trim();
    }

    String getPattern() {
        return this.pattern;
    }
}
