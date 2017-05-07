package com.maciek.ui.scope.table;

import com.maciek.entity.Customer;
import com.maciek.repository.CustomerRepository;
import com.maciek.ui.scope.editor.CustomerEditor;
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
public class CustomerUI extends VerticalLayout {

    private final CustomerRepository repo;
    private final CustomerEditor editor;
    private final Grid<Customer> grid;
    private final TextField filter;
    private final Button addNewBtn;

    @Autowired
    public CustomerUI(CustomerRepository repo, CustomerEditor editor) {
        this.editor=editor;
        this.repo=repo;
        this.grid=new Grid<>(Customer.class);
        this.filter=new TextField();
        this.addNewBtn = new Button("New customer", VaadinIcons.PLUS);

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        addComponents(actions,grid,editor);

        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(700, Unit.PIXELS);
        grid.setColumns("id", "lastName", "firstName", "emailAddress");

        filter.setPlaceholder("Filter by last name");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        addNewBtn.addClickListener(e -> editor.openCustomerEditor(new Customer("First name", "Second name", "email@address.ex")));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        listCustomers(null);
    }

    private void listCustomers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        }
        else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
        }
    }

}
