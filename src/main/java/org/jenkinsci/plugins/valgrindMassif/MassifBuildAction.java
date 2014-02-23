package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.*;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifBuildAction extends MassifAbstractBuildAction {
    private MassifResult result;
    private MassifPublisherConfig config;

    public MassifBuildAction(AbstractBuild<?, ?> owner, MassifResult result,
                             MassifPublisherConfig config) {
        super(owner);
        this.result = result;
        this.config = config;
    }

    public HealthReport getBuildHealth() {
        return new HealthReport();
    }

    public String getIconFileName() {
        return "/plugin/valgrindMassif/icons/massif-48.png";
    }

    public String getUrlName() {
        return "massifResult";
    }

    public String getDisplayName() {
        return "Valgrind Massif Result";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public Object getTarget() {
        return getResult();
    }

    public MassifResult getResult() {
        return result;
    }
}
