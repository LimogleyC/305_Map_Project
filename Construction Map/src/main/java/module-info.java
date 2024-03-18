module org.example.constructionmap {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.constructionmap to javafx.fxml;
    exports org.example.constructionmap;
}