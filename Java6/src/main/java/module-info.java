module org.example {
    requires javafx.controls;
    requires javafx.swing;
    requires java.desktop;

    opens org.example to javafx.graphics;
}
