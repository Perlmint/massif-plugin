package org.jenkinsci.plugins.valgrindMassif;

import hudson.model.*;
import org.kohsuke.stapler.StaplerProxy;

/**
 * Created by frakafra on 2014. 2. 23..
 */
public abstract class MassifAbstractBuildAction extends Actionable implements Action, HealthReportingAction, StaplerProxy {
    protected AbstractBuild<?, ?> owner;

    public MassifAbstractBuildAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    @SuppressWarnings("unchecked")
    public <T extends MassifAbstractBuildAction> T getPreviousResult() {
        AbstractBuild<?, ?> build = owner;
        while (true) {
            build = build.getPreviousBuild();
            if (build == null)
                return null;
            if (build.getResult() == Result.FAILURE)
                continue;
            MassifAbstractBuildAction ret = build.getAction(this.getClass());
            if (ret != null)
                return (T) ret;
        }
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }
}
