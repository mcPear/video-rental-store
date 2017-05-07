package com.maciek.ui.scope.table;

import com.maciek.VideoRentalStoreApplication;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.model.RentalModel;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import com.maciek.ui.scope.editor.RentalEditor;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.Optional;

/**
 * Created by maciej on 06.05.17.
 */

@SpringComponent
@UIScope
public class RentalUI extends VerticalLayout {

    private final Grid<Rental> grid;
    private final TextField filter;
    private final Button addNewBtn;
    private final Button archiveBtn;
    private final Button currentBtn;
    private final CssLayout chooseTimeBtns;
    final Button returnBtn;
    private boolean currentRentalsListed;
    private static final Logger log = LoggerFactory.getLogger(VideoRentalStoreApplication.class);
    private final RentalRepository repo;
    private final RentalEditor editor;
    private final VideoRepository videoRepository;

    @Autowired
    public RentalUI(RentalRepository repo, RentalEditor editor, VideoRepository videoRepository) {
        this.editor=editor;
        this.repo=repo;
        this.grid=new Grid<>(Rental.class);
        this.filter=new TextField();
        this.addNewBtn = new Button("New rental", VaadinIcons.PLUS);
        this.archiveBtn = new Button("Archive", VaadinIcons.ARCHIVE);
        this.currentBtn = new Button("Current", VaadinIcons.CLOCK);
        this.returnBtn = new Button("Return video", VaadinIcons.FILM);
        this.chooseTimeBtns = new CssLayout(currentBtn, archiveBtn);
        this.videoRepository = videoRepository;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, chooseTimeBtns, returnBtn);
        addComponents(actions,grid,editor);
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

        addNewBtn.addClickListener(e -> editor.openRentalModelEditor(new RentalModel("0", "0" , new Date(new java.util.Date().getTime()),false)));

        archiveBtn.addClickListener(e -> {
            listArchiveRentals("");
            changeArchiveBtnToPrimary();
        });

        currentBtn.addClickListener(e -> {
            listCurrentRentals("");
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
                repo.save(rental);
                listArchiveRentals(filter.getValue());
                listCurrentRentals(filter.getValue());

                Video video = rental.getVideo();
                video.setRented(false);
                videoRepository.save(video);
                Notification.show("Returned successfully");
            }
            else{
                Notification.show("Select a rental first");
            }
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCurrentRentals(filter.getValue());
        });

        listCurrentRentals(null);
    }

    private void listCurrentRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findByReturnedFalse());
        }
        else {
            grid.setItems(repo.findByReturnedFalseAndCustomerLastNameStartsWithIgnoreCase(filterText));
        }
        currentRentalsListed=true;
    }

    private void listArchiveRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findByReturnedTrue());
        }
        else {
            grid.setItems(repo.findByReturnedTrueAndCustomerLastNameStartsWithIgnoreCase(filterText));
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