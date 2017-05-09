package com.maciek.ui.table;

import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import com.maciek.service.DBService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created by maciej on 06.05.17.
 */

@SpringComponent
@UIScope
public class RentalUI extends VerticalLayout {

    private final Grid<Rental> grid=new Grid<>(Rental.class);
    private final TextField filter=new TextField();
    private final Button addNewBtn = new Button("New rental", VaadinIcons.PLUS);
    private final Button archiveBtn = new Button("Archive", VaadinIcons.ARCHIVE);
    private final Button currentBtn = new Button("Current", VaadinIcons.CLOCK);
    private final CssLayout chooseTimeBtns = new CssLayout(currentBtn, archiveBtn);
    final Button returnBtn = new Button("Return video", VaadinIcons.FILM);
    private boolean currentRentalsListed;
    private final RentalRepository rentalRepository;
    CustomerUI customerUI;
    VideoUI videoUI;

    @Autowired
    public RentalUI(RentalRepository rentalRepository, VideoRepository videoRepository, DBService dbService) {
        this.rentalRepository = rentalRepository;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, chooseTimeBtns, returnBtn);
        addComponents(actions,grid);
        chooseTimeBtns.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(1500, Unit.PIXELS);
        grid.setColumns("id", "customer", "video", "date");

        filter.setPlaceholder("Filter by last name");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> {
            if(currentRentalsListed)
                listCurrentRentals(e.getValue());
            else
                listArchiveRentals(e.getValue());
            });

        addNewBtn.addClickListener(e -> {
            SingleSelectionModel selectedVideoModel = (SingleSelectionModel<Video>) videoUI.getGrid().getSelectionModel();
            SingleSelectionModel selectedCustomerModel = (SingleSelectionModel<Customer>) customerUI.getGrid().getSelectionModel();
            Optional<Video> videoOptional = selectedVideoModel.getFirstSelectedItem();
            Optional<Customer> customerOptional = selectedCustomerModel.getFirstSelectedItem();
            if(videoOptional.isPresent() && customerOptional.isPresent()){
                dbService.saveNewRental(customerOptional.get(), videoOptional.get());
                Notification.show("Saved successfully", Notification.Type.TRAY_NOTIFICATION);
            }
            else{
                Notification.show("Select a customer and a video first", Notification.Type.WARNING_MESSAGE);
            }
            listCurrentRentals(null);
            videoUI.refreshGrid();
        });

        archiveBtn.addClickListener(e -> {
            listArchiveRentals(null);
            changeArchiveBtnToPrimary();
        });

        currentBtn.addClickListener(e -> {
            listCurrentRentals(null);
            changeCurrentBtnToPrimary();
        });
        currentBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);

        returnBtn.addClickListener(e ->
        {
            SingleSelectionModel<Rental> singleSelect = (SingleSelectionModel<Rental>) grid.getSelectionModel();
            Optional<Rental> rentalOptional = singleSelect.getSelectedItem();
            if (rentalOptional.isPresent()&&!rentalOptional.get().getReturned()){
                Rental rental = rentalOptional.get();
                rental.setReturned(true);
                rentalRepository.save(rental);
                listArchiveRentals(filter.getValue());
                listCurrentRentals(filter.getValue());

                Video video = rental.getVideo();
                videoRepository.save(video);
                Notification.show("Returned successfully", Notification.Type.TRAY_NOTIFICATION);
            }
            else{
                Notification.show("Select a rental first", Notification.Type.WARNING_MESSAGE);
            }
        });

        listCurrentRentals(null);
    }

    private void listCurrentRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(rentalRepository.findByReturnedFalse());
        }
        else {
            grid.setItems(rentalRepository.findByReturnedFalseAndCustomerLastNameStartsWithIgnoreCase(filterText));
        }
        currentRentalsListed=true;
    }

    private void listArchiveRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(rentalRepository.findByReturnedTrue());
        }
        else {
            grid.setItems(rentalRepository.findByReturnedTrueAndCustomerLastNameStartsWithIgnoreCase(filterText));
        }
        currentRentalsListed=false;
    }

    private void changeArchiveBtnToPrimary(){
        archiveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        currentBtn.removeStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    private void changeCurrentBtnToPrimary(){
        currentBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        archiveBtn.removeStyleName(ValoTheme.BUTTON_PRIMARY);
    }

}