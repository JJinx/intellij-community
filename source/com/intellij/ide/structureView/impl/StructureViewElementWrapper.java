package com.intellij.ide.structureView.impl;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cdr
 */
public class StructureViewElementWrapper<V extends PsiElement> implements StructureViewTreeElement {
  private final StructureViewTreeElement myTreeElement;
  private final PsiFile myMainFile;

  public StructureViewElementWrapper(@NotNull StructureViewTreeElement treeElement, @NotNull PsiFile mainFile) {
    myTreeElement = treeElement;
    myMainFile = mainFile;
  }

  public V getValue() {
    return (V)myTreeElement.getValue();
  }

  public StructureViewTreeElement[] getChildren() {
    TreeElement[] baseChildren = myTreeElement.getChildren();
    List<StructureViewTreeElement> result = new ArrayList<StructureViewTreeElement>();
    for (TreeElement element : baseChildren) {
      StructureViewTreeElement wrapper = new StructureViewElementWrapper((StructureViewTreeElement)element, myMainFile);

      result.add(wrapper);
    }
    return result.toArray(new StructureViewTreeElement[result.size()]);
  }

  public ItemPresentation getPresentation() {
    return myTreeElement.getPresentation();
  }

  public void navigate(final boolean requestFocus) {
    Navigatable navigatable = getNavigatableInTemplateLanguageFile();
    if (navigatable != null) {
      navigatable.navigate(requestFocus);
    }
  }

  @Nullable
  private Navigatable getNavigatableInTemplateLanguageFile() {
    PsiElement element = (PsiElement)myTreeElement.getValue();
    int offset = element.getTextRange().getStartOffset();
    final Language dataLanguage = ((TemplateLanguageFileViewProvider)myMainFile.getViewProvider()).getTemplateDataLanguage();
    final PsiFile dataFile = myMainFile.getViewProvider().getPsi(dataLanguage);
    if (dataFile == null) return null;

    PsiElement tlElement = dataFile.findElementAt(offset);
    while(true) {
      if (tlElement == null || tlElement.getTextRange().getStartOffset() != offset) break;
      if (tlElement instanceof Navigatable) {
        return (Navigatable)tlElement;
      }
      tlElement = tlElement.getParent();
    }
    return null;
  }

  public boolean canNavigate() {
    return getNavigatableInTemplateLanguageFile() != null;
  }

  public boolean canNavigateToSource() {
    return canNavigate();
  }
}
