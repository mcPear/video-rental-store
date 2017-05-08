package com.maciek.ui.table;

import com.maciek.VideoRentalStoreApplication;
import com.maciek.entity.Video;
import com.maciek.service.DBService;
import com.maciek.ui.editor.VideoEditor;
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

    private final VideoEditor editor;
    private final Grid<Video> grid;
    private final TextField filter;
    private final Button addNewBtn;
    private static final Logger log = LoggerFactory.getLogger(VideoRentalStoreApplication.class);
    private final DBService dbService;

    @Autowired
    public VideoUI(VideoEditor videoEditor, RentalUI rentalUI, DBService dbService) {
        this.editor=videoEditor;
        this.grid=new Grid<>(Video.class);
        this.filter=new TextField();
        this.addNewBtn = new Button("New video", VaadinIcons.PLUS);
        this.dbService=dbService;

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
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(dbService.findAvailableVideos());
        }
        else {
            grid.setItems(dbService.findAvailableVideosStartsWithIgnoreCase(filterText));
        }
    }


}
