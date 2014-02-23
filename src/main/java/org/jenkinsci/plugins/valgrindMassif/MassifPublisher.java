package org.jenkinsci.plugins.valgrindMassif;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifPublisher extends Recorder {
    private MassifPublisherConfig config;

    @DataBoundConstructor
    public MassifPublisher(String resultPattern) {
        this.setConfig(new MassifPublisherConfig(resultPattern));
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        build.getResult();

        FilePath baseFileFrom = build.getWorkspace();
        FilePath baseFileTo =  new FilePath(build.getRootDir());
        MassifResultsScanner scanner = new MassifResultsScanner(this.getConfig().getPattern());
        String[] files = baseFileFrom.act(scanner);

        for (int i = 0; i < files.length; i++) {
            FilePath fileFrom = new FilePath(baseFileFrom, files[i]);
            FilePath fileTo = new FilePath(baseFileTo, "massif-plugin/massif-results/" + files[i]);
            MassifLogger.log(listener, "Copying " + files[i] + " to " + fileTo.getRemote());
            fileFrom.copyTo(fileTo);
        }

        MassifLogger.setLogger(listener);
        MassifLogger.log(listener, "Pattern : " + config.getPattern());

        MassifParserResult parser = new MassifParserResult("massif-plugin/massif-results/" + config.getPattern(), build);
        MassifResult result = new MassifResult(build, parser);
        List<MassifReport> reportBook = result.getReports();

        MassifBuildAction buildAction = new MassifBuildAction(build, result, config);
        build.addAction(buildAction);

        return true;
    }

    @Extension
    public static final MassifPublisherDescriptor DESCRIPTOR = new MassifPublisherDescriptor();

    public MassifPublisherConfig getConfig() {
        return config;
    }

    public void setConfig(MassifPublisherConfig config) {
        this.config = config;
    }

    public static final class MassifPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        public MassifPublisherDescriptor() {
            super(MassifPublisher.class);
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return FreeStyleProject.class.isAssignableFrom(jobType)
                    || MatrixProject.class.isAssignableFrom(jobType);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }


        @Override
        public String getDisplayName() {
            return "Publish Valgrind Massif Result";
        }
    }
}
