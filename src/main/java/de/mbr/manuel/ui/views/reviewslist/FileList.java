/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mbr.manuel.ui.views.reviewslist;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.ModelItem;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.Encode;
import com.vaadin.flow.templatemodel.TemplateModel;

import de.mbr.manuel.backend.File;
import de.mbr.manuel.backend.Review;
import de.mbr.manuel.backend.ReviewService;
import de.mbr.manuel.backend.service.FileService;
import de.mbr.manuel.ui.MainLayout;
import de.mbr.manuel.ui.common.AbstractEditorDialog;
import de.mbr.manuel.ui.encoders.LocalDateToStringEncoder;
import de.mbr.manuel.ui.encoders.LongToStringEncoder;


/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 *
 * Implemented using a simple template.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Review List")
@Tag("reviews-list")
@HtmlImport("frontend://src/views/reviewslist/reviews-list.html")
public class FileList extends PolymerTemplate<FileList.FileModel> {

   @Id("search")
   private TextField search;
   @Id("newReview")
   private Button    addReview;
   @Id("header")
   private H2        header;
   private ReviewEditorDialog reviewForm = new ReviewEditorDialog(this::saveUpdate, this::deleteUpdate);

   private List<String> availableFileSets = FileService.listFileLists();

   public List<String> getAvailableFileSets() {
      return availableFileSets;
   }

   public FileList() {
      search.setPlaceholder("Search files");
      search.addValueChangeListener(e -> updateList());
      search.setValueChangeMode(ValueChangeMode.EAGER);
      search.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

      addReview.addClickListener(e -> openForm(new Review(), AbstractEditorDialog.Operation.ADD));
        /*
            This is a fall-back method:
            '+' is not a event.code (DOM events), so as a fall-back shortcuts
            will perform a character-based comparison. Since Key.ADD changes
            locations frequently based on the keyboard layout's language, we
            opted to use a character instead.
         */
      addReview.addClickShortcut(Key.of("+"));

      // Set review button and edit button text from Java
      getElement().setProperty("reviewButtonText", "New review");
      getElement().setProperty("editButtonText", "Edit");

      ComboBox<String> fileSetsDropdown = new ComboBox<>("Select a fileSet");
      fileSetsDropdown.setItems(FileService.listFileLists());
//      add(new Div(fileSetsDropdown));
      fileSetsDropdown.addValueChangeListener(new HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>>() {

         @Override
         public void valueChanged( AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> comboBoxStringComponentValueChangeEvent ) {
            if ( !StringUtils.isEmpty(comboBoxStringComponentValueChangeEvent.getValue()) ) {

            }
         }
      });

      updateList();

   }

   public void deleteUpdate( Review review ) {
      ReviewService.getInstance().deleteReview(review);
      updateList();
      Notification.show("Beverage successfully deleted.", 3000, Position.BOTTOM_START);
   }

   public void saveUpdate( Review review, AbstractEditorDialog.Operation operation ) {
      ReviewService.getInstance().saveReview(review);
      updateList();
      Notification.show("Beverage successfully " + operation.getNameInText() + "ed.", 3000, Position.BOTTOM_START);
   }

   @EventHandler
   private void edit( @ModelItem Review review ) {
      openForm(review, AbstractEditorDialog.Operation.EDIT);
   }

   private void openForm( Review review, AbstractEditorDialog.Operation operation ) {
      // Add the form lazily as the UI is not yet initialized when
      // this view is constructed
      if ( reviewForm.getElement().getParent() == null ) {
         getUI().ifPresent(ui -> ui.add(reviewForm));
      }
      reviewForm.open(review, operation);
   }

   private void updateList() {
      List<Path> paths = FileService.listFiles();

      List<File> files = new ArrayList<>(paths.size());

      for ( Path path : paths ) {
         File file = new File();
         file.setPath(path.toString());
         file.setName(path.getFileName().toString());
         files.add(file);
      }

      if ( search.isEmpty() ) {
         header.setText("Files");
         header.add(new Span(files.size() + " in total"));
      } else {
         header.setText("Search for “" + search.getValue() + "”");
         if ( !files.isEmpty() ) {
            header.add(new Span(files.size() + " results"));
         }
      }
      getModel().setFiles(files);
   }

   public interface FileModel extends TemplateModel {

      void setFiles( List<File> files );
   }

}
