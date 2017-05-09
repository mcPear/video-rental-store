package com.maciek.ui.editor;

/**
 * Created by maciej on 06.05.17.
 */
import com.maciek.entity.Video;
import com.maciek.repository.VideoRepository;
import com.maciek.ui.table.RentalUI;
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

import java.time.Year;
import java.util.List;

@SpringComponent
@UIScope
public class VideoEditor extends VerticalLayout {

    private Video currEditedVideo;
    private final TextField title = new TextField("Title");
    private final TextField director = new TextField("Director");
    private final TextField year = new TextField("Year");
    private final Button save = new Button("Save", FontAwesome.SAVE);
    private final Button cancel = new Button("Cancel");
    private final CssLayout actions = new CssLayout(save, cancel);
    private final Binder<Video> binder = new Binder<>(Video.class);

    @Autowired
    public VideoEditor(VideoRepository repository) {

        addComponents(title, director, year, actions);

        bindWithValidatorDirector();
        bindWithValidatorTitle();
        bindWithValidatorYear();

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> {
            if(fieldsAreValid()) repository.save(currEditedVideo);
            showMessage();
        });

        cancel.addClickListener(e -> setVisible(false));

        setVisible(false);
    }

    public final void openVideoEditor(Video v) {
        currEditedVideo = v;
        binder.setBean(currEditedVideo);
        setVisible(true);
        save.focus();
        title.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> {if(fieldsAreValid()) h.onChange();});
    }

    private void bindWithValidatorTitle(){
        String onlyLettersAndSpacesRegex = "[\\s\\w\\p{IsAlphabetic}]*";
        binder.forField(title).
                withValidator(title -> title.length()>0, "Enter title").
                withValidator(title -> title.length()<150, "Too long title").
                withValidator(title -> title.matches(onlyLettersAndSpacesRegex), "Use only letters, numbers and spaces in the title").
                bind(Video::getTitle, Video::setTitle);
    }

    private void bindWithValidatorDirector(){
        String onlyLettersAndSpacesRegex = "[\\s\\p{IsAlphabetic}]*";
        binder.forField(director).
                withValidator(director -> director.length()>0, "Enter director").
                withValidator(director -> director.length()<50, "Too long director name").
                withValidator(director -> director.matches(onlyLettersAndSpacesRegex), "Use only letters and spaces in the director name").
                bind(Video::getDirector, Video::setDirector);
    }

    private void bindWithValidatorYear(){
        String yearRegex = "\\d{4}";
        binder.forField(year).
                withValidator(year -> year.matches(yearRegex), "Invalid year").
                withValidator(year -> Integer.parseInt(year)<= Year.now().getValue(), "Invalid year").
                bind(Video::getYear, Video::setYear);
    }

    private Boolean fieldsAreValid(){
        BinderValidationStatus<Video> validationStatus = binder.validate();
        return !validationStatus.hasErrors();
    }

    private void showMessage(){
        BinderValidationStatus<Video> validationStatus = binder.validate();
        List<ValidationResult> validationResultList = validationStatus.getValidationErrors();

        if(validationResultList.size()==0)
            Notification.show("Saved successfully", Notification.Type.TRAY_NOTIFICATION);
        else
            Notification.show(validationResultList.get(0).getErrorMessage(), Notification.Type.WARNING_MESSAGE);
    }

}