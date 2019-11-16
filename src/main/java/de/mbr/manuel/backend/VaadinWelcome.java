package de.mbr.manuel.backend;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;


public class VaadinWelcome extends Composite<Div> implements HasComponents {


public VaadinWelcome() {
   add(new Image("reindeer.jpeg", "whatever"));
}


}
