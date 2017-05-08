package com.maciek.ui.scope.table;

import com.maciek.VideoRentalStoreApplication;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import com.maciek.ui.scope.editor.VideoEditor;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by maciej on 06.05.17.
 */

@SpringComponent
@UIScope
public class VideoUI extends VerticalLayout {

    private final VideoRepository repo;
    private final VideoEditor editor;
    private final Grid<Video> grid;
    private final TextField filter;
    private final Button addNewBtn;
    private static final Logger log = LoggerFactory.getLogger(VideoRentalStoreApplication.class);
    private final RentalUI rentalUI;
    private final RentalRepository rentalRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public VideoUI(VideoRepository repo, VideoEditor videoEditor, RentalUI rentalUI, RentalRepository rentalRepository, VideoRepository videoRepository) {
        this.editor=videoEditor;
        this.repo=repo;
        this.grid=new Grid<>(Video.class);
        this.filter=new TextField();
        this.addNewBtn = new Button("New video", VaadinIcons.PLUS);
        this.rentalUI = rentalUI;
        this.rentalRepository = rentalRepository;
        this.videoRepository=videoRepository;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        addComponents(actions,grid,editor);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(700, Unit.PIXELS);
        grid.setColumns("id", "title", "director", "year");

        filter.setPlaceholder("Filter by title");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listAvailableVideos(e.getValue()));

        addNewBtn.addClickListener(e -> editor.openVideoEditor(new Video("Title", "Director", "Year")));

        rentalUI.returnBtn.addClickListener(e-> listAvailableVideos(filter.getValue()));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listAvailableVideos(filter.getValue());
        });

        listAvailableVideos(null);
    }

    private void listAvailableVideos(String filterText) {
        List<Rental> notReturnedRentals = rentalRepository.findByReturnedFalse();
        List<Video> unavailableVideos = notReturnedRentals.stream().map(Rental::getVideo).collect(Collectors.toList());
        if (StringUtils.isEmpty(filterText)) {
            List<Video> availableVideos = videoRepository.findAll();
            for(Video uv:unavailableVideos){
                availableVideos = availableVideos.stream().filter(v -> v.getId()!=uv.getId()).collect(Collectors.toList());
            }
            grid.setItems(availableVideos);
        }
        else {
            List<Video> availableFilteredVideos = videoRepository.findByTitleStartsWithIgnoreCase(filterText);
            for(Video uv:unavailableVideos){
                availableFilteredVideos = availableFilteredVideos.stream().filter(v -> v.getId()!=uv.getId()).collect(Collectors.toList());
            }
            grid.setItems(availableFilteredVideos);
        }
    }


}
