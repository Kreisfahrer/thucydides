package net.thucydides.easyb

import net.thucydides.core.steps.ScenarioSteps

public class PluginConfiguration {

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    private static final String OUTPUT_DIRECTORY_PROPERTY = "thucydides.outputDirectory";

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    private static ThreadLocal<PluginConfiguration> configuration = new ThreadLocal<PluginConfiguration>();

    public static synchronized reset() {
        configuration.remove();
    }

    public static synchronized PluginConfiguration getInstance() {
        if (configuration.get() == null) {
            configuration.set(new PluginConfiguration());
        }
        return configuration.get();
    }

    def defaultBaseUrl;

    def registeredSteps = [];

    def resetBrowserInEachScenario = false

    /**
     * Define the base URL to be used for this story.
     */
    public void uses_default_base_url(String defaultBaseUrl) {
        setDefaultBaseUrl(defaultBaseUrl);
    }

    public void uses_steps_from(Class<ScenarioSteps> stepsClass) {
        registeredSteps += stepsClass
    }


    public void setDefaultBaseUrl(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getDefaultBaseUrl() {
        return defaultBaseUrl;
    }


    public void use_new_broswer_for_each_scenario() {
        resetBrowserInEachScenario = true;
    }

    public boolean isResetBrowserInEachScenario() {
        return resetBrowserInEachScenario;
    }

}
