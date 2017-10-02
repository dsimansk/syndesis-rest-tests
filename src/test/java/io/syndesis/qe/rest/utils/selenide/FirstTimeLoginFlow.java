package io.syndesis.qe.rest.utils.selenide;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import org.openqa.selenium.By;

/**
 * Sep 3, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
public class FirstTimeLoginFlow {

	public static void githubLogin(String githubUname, String githubPassword) {

		open("https://github.com/login");
		$(By.id("login_field")).setValue(githubUname);
		$(By.id("password")).setValue(githubPassword);
		$(By.name("commit")).click();
		$(".loading_progress").should(disappear);
	}

	public static void openShiftLogin(String osName, String osPass, String openShiftUrl) {

		open(openShiftUrl);
		$(By.id("inputUsername")).setValue(osName);
		$(By.id("inputPassword")).setValue(osPass);
		$("button").shouldBe(enabled).click();
		$(".loading_progress").should(disappear);
	}

	public static void syndesisLogin(String osName, String osPass, String syndesisUrl) {

		open(syndesisUrl);
		$(By.id("inputUsername")).setValue(osName);
		$(By.id("inputPassword")).setValue(osPass);
		$("button").shouldBe(enabled).click();
		if ($(By.name("approve")).isDisplayed()) {
			$(By.name("approve")).click();
		}
		$(".loading_progress").should(disappear);
	}
}
