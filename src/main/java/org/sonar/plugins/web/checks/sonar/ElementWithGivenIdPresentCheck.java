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
package org.sonar.plugins.web.checks.sonar;

import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.NoSqale;
import org.sonar.squidbridge.annotations.RuleTemplate;

@Rule(
  key = "S1436",
  priority = Priority.MAJOR,
  name = "An element with a specific \"id\" attribute should be present in each HTML page",
  tags = {RuleTags.CONVENTION})
@RuleTemplate
@NoSqale
public class ElementWithGivenIdPresentCheck extends AbstractPageCheck {

  @RuleProperty(
    key = "id",
    description = "Value of the \"id\" attribute expected to be present on every page")
  public String id = "";

  private boolean foundId;

  @Override
  public void startDocument(List<Node> nodes) {
    foundId = false;
  }

  @Override
  public void startElement(TagNode node) {
    if (id.equals(node.getAttribute("id"))) {
      foundId = true;
    }
  }

  @Override
  public void endDocument() {
    if (!id.isEmpty() && !foundId) {
      createViolation(0, "The ID \"" + id + "\" is missing from this page and should be added.");
    }
  }

}
