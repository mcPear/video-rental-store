package com.maciek.ui.table;

import com.maciek.entity.Video;
import com.maciek.service.DBService;
import com.maciek.ui.editor.VideoEditor;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Created by maciej on 06.05.17.
 */

@SpringComponent
@UIScope
public class VideoUI extends HorizontalLayout {

    private final VideoEditor editor;
    private final Grid<Video> grid=new Grid<>(Video.class);
    private final TextField filter=new TextField();
    private final Button addNewBtn = new Button("New video", VaadinIcons.PLUS);
    private final DBService dbService;

    @Autowired
    public VideoUI(VideoEditor videoEditor, RentalUI rentalUI, DBService dbService) {
        this.editor=videoEditor;
        this.dbService=dbService;

        rentalUI.videoUI=this;

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout actionsAndGridLayout = new VerticalLayout(actions, grid);
        addComponents(actionsAndGridLayout, editor);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(700, Unit.PIXELS);
        grid.setColumns("id", "title", "director", "year");

        filter.setPlaceholder("Filter by title");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listAvailableVideos(e.getValue()));

        addNewBtn.addClickListener(e -> editor.openVideoEditor(new Video(null, "Title", "Director", "Year", null)));

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

    void refreshGrid(){
        listAvailableVideos(filter.getValue());
    }

    public Grid<Video> getGrid(){
        return grid;
    }

}
