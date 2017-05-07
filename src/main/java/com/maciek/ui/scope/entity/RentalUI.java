package com.maciek.ui.scope.entity;

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
    final Button returnBtn;
    private boolean currentRentalsListed;

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
        this.videoRepository = videoRepository;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, currentBtn, archiveBtn, returnBtn);
        addComponents(actions,grid,editor);

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

        addNewBtn.addClickListener(e -> editor.openRentalByModelEditor(new RentalModel("0", "0" , new Date(new java.util.Date().getTime()),false)));

        archiveBtn.addClickListener(e -> listArchiveRentals(""));

        currentBtn.addClickListener(e -> listCurrentRentals(""));

        returnBtn.addClickListener(e ->
        {
            SingleSelectionModel<Rental> singleSelect = (SingleSelectionModel<Rental>) grid.getSelectionModel();
            Optional<Rental> rentalOptional = singleSelect.getSelectedItem();
            if (rentalOptional.isPresent()){
                Rental rental = rentalOptional.get();
                rental.setReturned(true);
                repo.save(rental);
                listCurrentRentals(filter.getValue());
                listArchiveRentals(filter.getValue());

                Video video = rental.getVideo();
                video.setRented(false);
                videoRepository.save(video);
            }
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCurrentRentals(filter.getValue());
        });

        listCurrentRentals(null);
    }

    void listCurrentRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findByReturnedFalse());
        }
        else {
            grid.setItems(repo.findByReturnedFalseAndCustomerLastNameStartsWithIgnoreCase(filterText));
        }
        currentRentalsListed=true;
    }

    void listArchiveRentals(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findByReturnedTrue());
        }
        else {
            grid.setItems(repo.findByReturnedTrueAndCustomerLastNameStartsWithIgnoreCase(filterText));
        }
        currentRentalsListed=false;
    }

}