package com.maciek.ui.scope.editor;

/**
 * Created by maciej on 06.05.17.
 */

import com.maciek.entity.Customer;
import com.maciek.repository.CustomerRepository;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

    private final CustomerRepository repository;
    private Customer currEditedCustomer;
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField emailAddress = new TextField("email address");
    private final Button save = new Button("Save", FontAwesome.SAVE);
    private final Button cancel = new Button("Cancel");
    private final CssLayout actions = new CssLayout(save, cancel);
    private final Binder<Customer> binder = new Binder<>(Customer.class);

    @Autowired
    public CustomerEditor(CustomerRepository repository) {
        this.repository = repository;

        addComponents(firstName, lastName, emailAddress, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> repository.save(currEditedCustomer));

        cancel.addClickListener(e -> setVisible(false));
        setVisible(false);
    }

    public final void openCustomerEditor(Customer c) {
        currEditedCustomer = c;
        binder.setBean(currEditedCustomer);
        setVisible(true);
        save.focus();
        firstName.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
    }

}