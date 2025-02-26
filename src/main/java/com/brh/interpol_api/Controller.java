package com.brh.interpol_api;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


import java.util.ArrayList;


public class Controller {
    @FXML
    private TextField nameTf;
    @FXML
    private TextField forenameTf;
    @FXML
    private TextField freeSearchTf;
    @FXML
    private TableView<Person> tableView;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> forenameColumn;
    @FXML
    private TableColumn<Person, String> birthdayColumn;
    @FXML
    private TableColumn<Person, String> imageColumn;
    @FXML
    private WebView webView;

    WebEngine engine;

    private ObservableList<Person> personList  = FXCollections.observableArrayList();


    @FXML
    public void initialize(){
        engine =  webView.getEngine();
        engine.setUserAgent( "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        nameColumn.setCellValueFactory( new PropertyValueFactory<>("name"));
        imageColumn.setCellValueFactory( new PropertyValueFactory<>("imageURL"));
        forenameColumn.setCellValueFactory( new PropertyValueFactory<>("forename"));
        birthdayColumn.setCellValueFactory( new PropertyValueFactory<>("dateOfBirth"));
    }

    @FXML
    protected void onSearchClick() {
        String name = nameTf.getText();
        String forename = forenameTf.getText();
        String freeSearch = freeSearchTf.getText();

        APIRequest request = new APIRequest();
        request.getData(name, forename, freeSearch, this::handlePersonData);

    }

    private void handlePersonData( ArrayList<Person> data){
        System.out.println("handle api data");
        personList.clear();
        personList.addAll(data);
        tableView.setItems( personList );
        tableView.refresh();
    }

    @FXML
    public void onTableClick(){
        String url =  getFocusedUrl();
        engine.loadContent("<img src="+url+">");
    }

    private String getFocusedUrl(){
        int tableRow = tableView.getFocusModel().getFocusedIndex();
        return personList.get(tableRow).getImageURL().replace("http://", "https://");
    }
}