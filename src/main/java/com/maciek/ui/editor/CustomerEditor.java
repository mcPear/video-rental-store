package com.maciek.ui.editor;

/**
 * Created by maciej on 06.05.17.
 */

import com.maciek.entity.Customer;
import com.maciek.repository.CustomerRepository;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

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

        addComponents(firstName, lastName, emailAddress, actions);

        bindWithValidatorFirstName();
        bindWithValidatorLastName();
        bindWithValidatorEmailAddress();

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> {
            if(fieldsAreValid()) repository.save(currEditedCustomer);
            showMessage();
        });

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
        save.addClickListener(e -> {if(fieldsAreValid()) h.onChange();});
    }

    private void bindWithValidatorFirstName(){
        String onlyLettersRegex = "\\p{IsAlphabetic}*";
        binder.forField(firstName).
                withValidator(firstName -> firstName.length()>0, "Enter first name").
                withValidator(firstName -> firstName.length()<30, "Too long first name").
                withValidator(firstName -> firstName.matches(onlyLettersRegex), "Use only letters in the first name").
                bind(Customer::getFirstName, Customer::setFirstName);
    }

    private void bindWithValidatorLastName(){
        String onlyLettersRegex = "\\p{IsAlphabetic}*";
        binder.forField(lastName).
                withValidator(lastName -> lastName.length()>0, "Enter last name").
                withValidator(lastName -> lastName.length()<50, "Too long last name").
                withValidator(lastName -> lastName.matches(onlyLettersRegex), "Use only letters in the last name").
                bind(Customer::getLastName, Customer::setLastName);
    }

    private void bindWithValidatorEmailAddress(){
        binder.forField(emailAddress).
                withValidator(emailAddress -> emailAddressIsValid(emailAddress), "Invalid email").
                bind(Customer::getEmailAddress, Customer::setEmailAddress);
    }

    private Boolean fieldsAreValid(){
        BinderValidationStatus<Customer> validationStatus = binder.validate();
        return !validationStatus.hasErrors();
    }

    private Boolean emailAddressIsValid(String emailGiven){
        boolean emailIsValid = false;
        try {
            new InternetAddress(emailGiven);
            emailIsValid=true;
        }
        catch(AddressException e) {
        }
        return emailIsValid;
    }

    private void showMessage(){
        BinderValidationStatus<Customer> validationStatus = binder.validate();
        List<ValidationResult> validationResultList = validationStatus.getValidationErrors();

        if(validationResultList.size()==0)
            Notification.show("Saved successfully", Notification.Type.TRAY_NOTIFICATION);
        else
            Notification.show(validationResultList.get(0).getErrorMessage(), Notification.Type.WARNING_MESSAGE);
    }
}