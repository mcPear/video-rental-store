package com.maciek.ui.scope.entity;

import com.maciek.VideoRentalStoreApplication;
import com.maciek.entity.Video;
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

    @Autowired
    public VideoUI(VideoRepository repo, VideoEditor videoEditor, RentalUI rentalUI) {
        this.editor=videoEditor;
        this.repo=repo;
        this.grid=new Grid<>(Video.class);
        this.filter=new TextField();
        this.addNewBtn = new Button("New video", VaadinIcons.PLUS);
        this.rentalUI = rentalUI;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        addComponents(actions,grid,editor);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(500, Unit.PIXELS);
        grid.setColumns("id", "title", "director", "year");

        filter.setPlaceholder("Filter by title");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listAvailableVideos(e.getValue()));

        addNewBtn.addClickListener(e -> editor.openVideoEditor(new Video("Title", "Director", "Year", true)));

        rentalUI.returnBtn.addClickListener(e-> listAvailableVideos(filter.getValue()));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listAvailableVideos(filter.getValue());
        });

        listAvailableVideos(null);
    }

    void listAvailableVideos(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findByRentedFalse());
        }
        else {
            grid.setItems(repo.findByRentedFalseAndTitleStartsWithIgnoreCase(filterText));
        }
    }


}
