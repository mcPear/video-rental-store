package com.maciek.ui.editor;

/**
 * Created by maciej on 06.05.17.
 */
import com.maciek.VideoRentalStoreApplication;
import com.maciek.model.RentalModel;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import com.maciek.service.DBService;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringComponent
@UIScope
public class RentalEditor extends VerticalLayout {

    private final RentalRepository rentalRepository;
    private RentalModel currEditedRentalModel;
    private final TextField customerId = new TextField("Customer id");
    private final TextField videoId = new TextField("Video id");
    final Button save = new Button("Save", FontAwesome.SAVE);
    private final Button cancel = new Button("Cancel");
    private final CssLayout actions = new CssLayout(save, cancel);
    private final Binder<RentalModel> binder = new Binder<>(RentalModel.class);
    private final DBService dbService;

    @Autowired
    public RentalEditor(RentalRepository rentalRepository, DBService dbService) {
        this.rentalRepository = rentalRepository;
        this.dbService = dbService;

        addComponents(customerId, videoId, actions);

        bindWithValidatorCustomerId();
        bindWithValidatorVideoId();

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        cancel.addClickListener(e -> setVisible(false));
        setVisible(false);
    }

    public final void openRentalModelEditor(RentalModel r) {
        currEditedRentalModel = r;
        binder.setBean(currEditedRentalModel);
        setVisible(true);
        save.focus();
        customerId.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e ->
        {
            if(fieldsAreValid()) {
                rentalRepository.save(dbService.constructRental(currEditedRentalModel));
                h.onChange();
                Notification.show("Saved successfully", Notification.Type.TRAY_NOTIFICATION);
            }
            else
                showErrorMessage();
        });
    }

    private void bindWithValidatorCustomerId(){
        String atLeastOneDigitRegex = "[\\d]+";
        binder.forField(customerId).
                withValidator(id -> id.matches(atLeastOneDigitRegex), "Invalid customer id").
                withValidator(id -> dbService.customerExists(Long.parseLong(id)), "There is no customer with this ID").
                bind(RentalModel::getCustomerId, RentalModel::setCustomerId);
    }

    private void bindWithValidatorVideoId(){
        String atLeastOneDigitRegex = "[\\d]+";
        binder.forField(videoId).
                withValidator(id -> id.matches(atLeastOneDigitRegex), "Invalid video id").
                withValidator(id -> dbService.videoExists(Long.parseLong(id)), "There is no video with this ID").
                withValidator(id -> dbService.videoIsAvailable(Long.parseLong(id)), "Video is unavailable").
                bind(RentalModel::getVideoId, RentalModel::setVideoId);
    }

    private Boolean fieldsAreValid(){
        BinderValidationStatus<RentalModel> validationStatus = binder.validate();
        return !validationStatus.hasErrors();
    }

    private void showErrorMessage(){
        BinderValidationStatus<RentalModel> validationStatus = binder.validate();
        List<ValidationResult> validationResultList = validationStatus.getValidationErrors();

            Notification.show(validationResultList.get(0).getErrorMessage(), Notification.Type.WARNING_MESSAGE);
    }

}
