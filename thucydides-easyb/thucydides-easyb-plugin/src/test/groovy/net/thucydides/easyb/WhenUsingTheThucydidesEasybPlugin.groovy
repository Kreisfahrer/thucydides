package net.thucydides.easyb;


import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.hamcrest.Matchers.*
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.StepListener
import net.thucydides.core.webdriver.WebDriverFactory

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

public class WhenUsingTheThucydidesEasybPlugin {

    ThucydidesPlugin plugin
    Binding binding


    class BrowserlessThucydidesPlugin extends ThucydidesPlugin {

        @Override
        protected WebDriverFactory getDefaultWebDriverFactory() {
            return new WebDriverFactory() {
                protected WebDriver newFirefoxDriver() {
                    return new MockWebDriver();
                }

            }
        }

        public getCloseCount() {
            return closeCount;
        }
    }

    class MockedThucydidesPlugin extends ThucydidesPlugin {

        @Override
        protected WebDriverFactory getDefaultWebDriverFactory() {
            return new WebDriverFactory() {

                protected FirefoxDriver newFirefoxDriver() {
                    return Mockito.mock(FirefoxDriver.class);
                }

            }
        }

        public getCloseCount() {
            return closeCount;
        }
    }
    @Before
    public void initMocks() {
        plugin = new BrowserlessThucydidesPlugin();
        plugin.resetConfiguration();
        binding = new Binding();
        binding.setVariable("sourceFile", "TestStory.story")
    }

    @After
    public void clearSystemProperties() {
        System.setProperty("webdriver.base.url", "");
    }

    @Test
    public void the_plugin_should_answer_to_the_name_of_thucydides() {
        assert plugin.name == "thucydides"
    }

    @Test
    public void the_plugin_should_use_a_normal_WebDriverFactory_by_default() {
        WebDriverFactory factory = plugin.getDefaultWebDriverFactory();
        assert factory != null
    }


    @Test
    public void the_plugin_should_inject_a_webdriver_instance_into_the_story_context() {

        plugin.configuration.uses_default_base_url "http://www.google.com"

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver != null
    } 

    @Test
    public void the_plugin_should_inject_a_Pages_object_into_the_story_context() {

        plugin.configuration.uses_default_base_url "http://www.google.com"

        runStories(plugin, binding);

        Pages pages = (Pages) binding.getVariable("pages");
        assert pages != null
    }


    @Test
    public void plugin_configuration_is_available_via_a_property_called_thucydides() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        assert config != null
    }

    @Test
    public void the_plugin_should_let_the_user_define_the_default_base_url() {

        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        driver.shouldHaveOpenedAt("http://www.google.co.nz")
    }

    @Test
    public void the_plugin_should_open_the_browser_to_the_system_defined_default_url() {

        System.setProperty("webdriver.base.url", "http://www.google.com");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        driver.shouldHaveOpenedAt("http://www.google.com")
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_story() {

        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver.wasClosed();
        assert driver.closedCount == 1;
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_scenario_if_requested() {

        ThucydidesPlugin plugin = new MockedThucydidesPlugin();

        plugin.getConfiguration().use_new_broswer_for_each_scenario()
        plugin.getConfiguration().uses_default_base_url("http://www.google.com")
        plugin.beforeStory(binding);
        WebDriver driver = (WebDriver) binding.getVariable("driver");

        runScenarios(plugin, binding);

        plugin.afterStory(binding);

        verify(driver, times(2)).get("http://www.google.com");
    }

    @Test
    public void the_plugin_should_obtain_the_user_story_name_from_the_easyb_source_file() {
        
        ThucydidesPlugin plugin = new MockedThucydidesPlugin();
        Binding binding = new Binding();
        binding.setVariable "sourceFile", "my/working/directory/EasybStory.story"
        plugin.stepListener = mock(StepListener)

        runStories(plugin, binding);
        
        verify(plugin.stepListener).testRunStarted("EasybStory")

    }

    @Test
    public void the_plugin_should_also_work_with_easyb_stories_with_a_groovy_suffix() {
        
        ThucydidesPlugin plugin = new MockedThucydidesPlugin();
        Binding binding = new Binding();
        binding.setVariable "sourceFile", "my/working/directory/EasybStory.groovy"
        plugin.stepListener = mock(StepListener)
        
        runStories(plugin, binding);
        
        verify(plugin.stepListener).testRunStarted("EasybStory")
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none()
        
    @Test
    public void the_plugin_should_fail_to_initialize_if_no_source_file_is_defined() {
        expectedException.expect IllegalArgumentException
        expectedException.expectMessage containsString("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")

        ThucydidesPlugin plugin = new MockedThucydidesPlugin();
        Binding binding = new Binding();
        plugin.stepListener = mock(StepListener)
        
        runStories(plugin, binding);
    }
    
    private void runStories(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeStory(binding);
        runScenarios(plugin, binding);
        plugin.afterStory(binding);
    }

    private void runScenarios(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeScenario(binding);
        plugin.beforeGiven(binding);
        plugin.beforeWhen(binding);
        plugin.beforeThen(binding);
        plugin.afterScenario(binding);
        plugin.beforeScenario(binding);
        plugin.beforeGiven(binding);
        plugin.beforeWhen(binding);
        plugin.beforeThen(binding);
        plugin.afterScenario(binding);
    }


}
