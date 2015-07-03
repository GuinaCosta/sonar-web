/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.plugins.web.checks.WebRule;

public class CheckClassesTest {

  @Test(expected=IllegalAccessException.class)
  public void create() throws Exception {
    CheckClasses.class.newInstance();
  }

  /**
   * Enforces that each check declared in list.
   */
  @Test
  public void count() {
    int count = 0;
    List<File> files = (List<File>) FileUtils.listFiles(new File("src/main/java/org/sonar/plugins/web/checks/"), new String[]{"java"}, true);
    for (File file : files) {
      if (file.getName().endsWith("Check.java") && (!file.getName().endsWith("AbstractPageCheck.java"))) {
        count++;
      }
    }
    assertThat(CheckClasses.getCheckClasses().size()).isEqualTo(count);
  }

  /**
   * Enforces that each check has test, name and description.
   */
  @Test
  public void test() {
    List<Class> checks = CheckClasses.getCheckClasses();

    for (Class cls : checks) {
      String testName = '/' + cls.getName().replace('.', '/') + "Test.class";
      assertThat(getClass().getResource(testName))
        .overridingErrorMessage("No test for " + cls.getSimpleName())
        .isNotNull();
      assertThat(AnnotationUtils.getClassAnnotation(cls, WebRule.class) != null)
        .overridingErrorMessage("Add @WebRule to " + cls.getSimpleName())
        .isTrue();
    }


    WebRulesDefinition rulesDefinition = new WebRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository("Web");

    for (RulesDefinition.Rule rule : repository.rules()) {
      assertThat(rule.htmlDescription())
        .overridingErrorMessage("Description of " + rule.key() + " should be in separate HTML file")
        .isNotNull();
    }
  }

}
