/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package org.jetbrains.idea.devkit.inspections;

import com.intellij.openapi.module.Module;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.devkit.DevKitBundle;
import org.jetbrains.idea.devkit.inspections.quickfix.CreateHtmlDescriptionFix;
import org.jetbrains.idea.devkit.inspections.quickfix.CreatePostfixTemplateHtmlDescriptionFix;


public class PostfixTemplateDescriptionNotFoundInspection extends DescriptionNotFoundInspectionBase {

  private static final String POSTFIX_TEMPLATES = "postfixTemplates";

  @NotNull
  @Override
  protected PsiDirectory[] getDescriptionsDirs(@NotNull Module module) {
    return getPostfixTemplateDirectories(module);
  }

  @Override
  protected CreateHtmlDescriptionFix getFix(Module module, String descriptionDir) {
    return new CreatePostfixTemplateHtmlDescriptionFix(descriptionDir, module);
  }

  @NotNull
  public static PsiDirectory[] getPostfixTemplateDirectories(Module module) {
    final PsiPackage aPackage = JavaPsiFacade.getInstance(module.getProject()).findPackage(POSTFIX_TEMPLATES);
    if (aPackage != null) {
      return aPackage.getDirectories(GlobalSearchScope.moduleWithDependenciesScope(module));
    }
    else {
      return PsiDirectory.EMPTY_ARRAY;
    }
  }

  @NotNull
  @Override
  protected String getHasNotDescriptionError() {
    return "Postfix template does not have a description";
  }

  @NotNull
  @Override
  protected String getHasNotBeforeAfterError() {
    return "Postfix template must have 'before.*.template' and 'after.*.template' beside 'description.html'";
  }

  @NotNull
  @Override
  protected String getClassName() {
    return "com.intellij.codeInsight.template.postfix.templates.PostfixTemplate";
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return DevKitBundle.message("inspections.component.postfix.template.not.found.description.name");
  }

  @NotNull
  @Override
  public String getShortName() {
    return "PostfixTemplateDescriptionNotFound";
  }
}
