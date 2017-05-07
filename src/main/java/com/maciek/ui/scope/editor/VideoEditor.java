package com.maciek.ui.scope.editor;

/**
 * Created by maciej on 06.05.17.
 */
import com.maciek.entity.Video;
import com.maciek.repository.VideoRepository;
import com.maciek.ui.scope.entity.RentalUI;
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
public class VideoEditor extends VerticalLayout {

    private final VideoRepository repository;
    private final RentalEditor rentalEditor;
    private Video currEditedVideo;
    private final TextField title = new TextField("Title");
    private final TextField director = new TextField("Director");
    private final TextField year = new TextField("Year");
    private final Button save = new Button("Save", FontAwesome.SAVE);
    private final Button cancel = new Button("Cancel");
    private final CssLayout actions = new CssLayout(save, cancel);
    private final Binder<Video> binder = new Binder<>(Video.class);

    @Autowired
    public VideoEditor(VideoRepository repository, RentalEditor rentalEditor, RentalUI rentalUI) {
        this.repository = repository;
        this.rentalEditor=rentalEditor;

        addComponents(title, director, year, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> repository.save(currEditedVideo));

        cancel.addClickListener(e -> setVisible(false));

        setVisible(false);
    }

    public final void openVideoEditor(Video v) {
        currEditedVideo = v;
        currEditedVideo.setRented(false);
        binder.setBean(currEditedVideo);
        setVisible(true);
        save.focus();
        title.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        rentalEditor.save.addClickListener(e -> h.onChange());
        save.addClickListener(e -> h.onChange());
    }

}