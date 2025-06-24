module org.example.pooprojeto {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.pooprojeto to javafx.fxml;
    exports org.example.pooprojeto;
}