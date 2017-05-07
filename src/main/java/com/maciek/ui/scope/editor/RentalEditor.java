package com.maciek.ui.scope.editor;

/**
 * Created by maciej on 06.05.17.
 */
import com.maciek.VideoRentalStoreApplication;
import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.model.RentalModel;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

@SpringComponent
@UIScope
public class RentalEditor extends VerticalLayout {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final VideoRepository videoRepository;
    private static final Logger log = LoggerFactory.getLogger(VideoRentalStoreApplication.class);
    private RentalModel currEditedRentalModel;
    private final TextField customerId = new TextField("Customer id");
    private final TextField videoId = new TextField("Video id");
    final Button save = new Button("Save", FontAwesome.SAVE);
    private final Button cancel = new Button("Cancel");
    private final CssLayout actions = new CssLayout(save, cancel);
    private final Binder<RentalModel> binder = new Binder<>(RentalModel.class);

    @Autowired
    public RentalEditor(RentalRepository rentalRepository, CustomerRepository customerRepository, VideoRepository videoRepository) {
        this.customerRepository = customerRepository;
        this.videoRepository = videoRepository;
        this.rentalRepository = rentalRepository;

        addComponents(customerId, videoId, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e ->
        {
            rentalRepository.save(rentalModelToRental());
            Video modifiedVideo = videoRepository.findOne(Long.parseLong(currEditedRentalModel.getVideoId()));
            modifiedVideo.setRented(true);
            videoRepository.save(modifiedVideo);
        });

        cancel.addClickListener(e -> setVisible(false));
        setVisible(false);
    }

    public final void openRentalByModelEditor(RentalModel r) {
        currEditedRentalModel = r;
        binder.setBean(currEditedRentalModel);
        setVisible(true);
        save.focus();
        customerId.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
    }

    public Rental rentalModelToRental(){
        Customer customer = customerRepository.findOne(Long.parseLong(currEditedRentalModel.getCustomerId()));
        Video video = videoRepository.findOne(Long.parseLong(currEditedRentalModel.getVideoId()));
        Date date = currEditedRentalModel.getDate();
        Boolean returned = currEditedRentalModel.getReturned();
        return new Rental(customer, video, date, returned);
    }

}
