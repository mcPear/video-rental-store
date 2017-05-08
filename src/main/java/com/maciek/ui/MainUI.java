package com.maciek.ui;

import com.maciek.ui.table.CustomerUI;
import com.maciek.ui.table.RentalUI;
import com.maciek.ui.table.VideoUI;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by maciej on 06.05.17.
 */

@SpringUI
@Theme("valo")
public class MainUI extends UI {

    private final CustomerUI customerUI;
    private final VideoUI videoUI;
    private final RentalUI rentalUI;

    @Autowired
    public MainUI(CustomerUI customerUI, VideoUI videoUI, RentalUI rentalUI) {
        this.customerUI=customerUI;
        this.videoUI=videoUI;
        this.rentalUI=rentalUI;
    }

    @Override
    protected void init(VaadinRequest request) {
        HorizontalLayout videoCustomerLayout = new HorizontalLayout(videoUI, customerUI);
        VerticalLayout mainLayout = new VerticalLayout(videoCustomerLayout, rentalUI);
        setContent(mainLayout);

    }



}
