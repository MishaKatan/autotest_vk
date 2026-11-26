package com.github.mishakatan.tests;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Smoke-тесты")
@SelectPackages("com.github.mishakatan.tests")
@IncludeTags("smoke")
class SmokeSuite {
}