package net.thucydides.easyb.samples

import net.thucydides.core.annotations.Step
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps

class SampleSteps extends ScenarioSteps {

    SampleSteps(Pages pages){
        super(pages)
    }

    @Step
    def step1() {}

    @Step
    def step2() {}

    @Step
    def step3() {}

    @Step
    def failingStep() {
        assert 1 == 2
    }
}
