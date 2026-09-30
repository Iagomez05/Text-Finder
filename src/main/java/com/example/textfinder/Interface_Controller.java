package com.example.textfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Interface_Controller implements Initializable {
    private final LinkedListLibrary<File> library = new LinkedListLibrary<>();
    private final LinkedListLibrary<File> results = new LinkedListLibrary<>();
    private final Indizador indexer = new Indizador();

    @FXML
    private MenuButton btn_Sort;
    @FXML
    private ListView<String> listview_biblioteca;
    @FXML
    private ListView<String> listview_results;
    @FXML
    private TextFlow textFlow;
    @FXML
    private TextField TextFieldBuscar;
    @FXML
    private Label libraryStatus;
    @FXML
    private Label resultStatus;
    @FXML
    private Label indexStatus;
    @FXML
    private Button openDocumentButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listview_biblioteca.setPlaceholder(new Label("Add a TXT, PDF, or DOCX document"));
        listview_results.setPlaceholder(new Label("Search results will appear here"));
        openDocumentButton.setDisable(true);

        listview_results.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            openDocumentButton.setDisable(index < 0);
            if (index >= 0 && index < results.size()) {
                showDocument(results.get(index));
            }
        });
        updateLibraryStatus();
        updateResultStatus();
    }

    @FXML
    public void btn_anadir_doc(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add document");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported documents", "*.txt", "*.pdf", "*.docx"),
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("PDF documents", "*.pdf"),
                new FileChooser.ExtensionFilter("Word documents", "*.docx")
        );
        addFile(chooser.showOpenDialog(listview_biblioteca.getScene().getWindow()));
    }

    @FXML
    public void btn_anadir_carpeta(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Add documents from folder");
        File directory = chooser.showDialog(listview_biblioteca.getScene().getWindow());
        if (directory == null) {
            return;
        }

        File[] files = directory.listFiles(DocumentParser::supports);
        if (files != null) {
            for (File file : files) {
                addFile(file);
            }
        }
    }

    @FXML
    public void btn_actualizar_doc(ActionEvent event) {
        int index = listview_biblioteca.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            setIndexStatus("Select a document to refresh");
            return;
        }
        File selected = library.get(index);
        if (!selected.isFile()) {
            removeLibraryItem(index);
            setIndexStatus("Missing document removed");
            return;
        }
        indexer.markDirty();
        setIndexStatus("Document refreshed · index needs rebuild");
    }

    @FXML
    public void btn_eliminar_doc(ActionEvent event) {
        int index = listview_biblioteca.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            removeLibraryItem(index);
            setIndexStatus("Library changed · index needs rebuild");
        }
    }

    @FXML
    public void btn_IndizarArchivos(ActionEvent event) {
        if (library.isEmpty()) {
            setIndexStatus("Add documents before building the index");
            return;
        }
        try {
            int count = indexer.rebuild(library);
            setIndexStatus(count + (count == 1 ? " document indexed" : " documents indexed"));
        } catch (IOException exception) {
            showError("Indexing failed", exception.getMessage());
        }
    }

    @FXML
    public void btn_Buscar_Archivos(ActionEvent event) {
        String query = TextFieldBuscar.getText().trim();
        results.clear();
        listview_results.getItems().clear();
        textFlow.getChildren().clear();

        if (query.isEmpty()) {
            resultStatus.setText("Enter a word or phrase to search");
            return;
        }
        if (library.isEmpty()) {
            resultStatus.setText("Add documents before searching");
            return;
        }

        try {
            if (!indexer.isIndexed()) {
                indexer.rebuild(library);
                setIndexStatus(library.size() + (library.size() == 1 ? " document indexed" : " documents indexed"));
            }

            if (query.matches("[\\p{L}\\p{N}]+")) {
                addIndexedResults(query);
            } else {
                addPhraseResults(query);
            }
            refreshResultList();
        } catch (IOException exception) {
            showError("Search failed", exception.getMessage());
        }
    }

    @FXML
    public void btn_Abrir_Doc1(ActionEvent event) {
        int index = listview_results.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= results.size()) {
            return;
        }

        File selected = results.get(index);
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new IOException("Opening files is not supported on this system.");
            }
            Desktop.getDesktop().open(selected);
        } catch (IOException exception) {
            showError("Could not open document", exception.getMessage());
        }
    }

    @FXML
    public void Sort_by_Name(ActionEvent event) {
        QuickSort.sort(results);
        refreshResultList();
        btn_Sort.setText("Name");
    }

    @FXML
    public void Sort_by_Date(ActionEvent event) {
        BubbleSort.sort(results);
        refreshResultList();
        btn_Sort.setText("Modified");
    }

    @FXML
    public void Sort_by_size(ActionEvent event) {
        RadixSort.sort(results);
        refreshResultList();
        btn_Sort.setText("Size");
    }

    private void addFile(File file) {
        if (file == null || !DocumentParser.supports(file) || library.contains(file)) {
            return;
        }
        library.add(file);
        listview_biblioteca.getItems().add(file.getName());
        indexer.markDirty();
        updateLibraryStatus();
        setIndexStatus("Library changed · index needs rebuild");
    }

    private void removeLibraryItem(int index) {
        File file = library.get(index);
        library.remove(file);
        listview_biblioteca.getItems().remove(index);
        indexer.markDirty();
        updateLibraryStatus();
    }

    private void addIndexedResults(String query) {
        for (String path : indexer.searchDocuments(query)) {
            File match = new File(path);
            if (match.isFile()) {
                results.add(match);
            }
        }
    }

    private void addPhraseResults(String query) throws IOException {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        for (File file : library) {
            String content = DocumentParser.parse(file).toLowerCase(Locale.ROOT);
            if (content.contains(normalizedQuery)) {
                results.add(file);
            }
        }
    }

    private void refreshResultList() {
        listview_results.getItems().clear();
        for (File file : results) {
            listview_results.getItems().add(file.getName());
        }
        updateResultStatus();
    }

    private void showDocument(File file) {
        try {
            highlightMatches(DocumentParser.parse(file), TextFieldBuscar.getText().trim());
        } catch (IOException exception) {
            showError("Preview unavailable", exception.getMessage());
        }
    }

    private void highlightMatches(String content, String query) {
        textFlow.getChildren().clear();
        if (query.isEmpty()) {
            textFlow.getChildren().add(new Text(content));
            return;
        }

        String lowerContent = content.toLowerCase(Locale.ROOT);
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        int cursor = 0;
        while (cursor < content.length()) {
            int match = lowerContent.indexOf(lowerQuery, cursor);
            if (match < 0) {
                textFlow.getChildren().add(new Text(content.substring(cursor)));
                break;
            }
            if (match > cursor) {
                textFlow.getChildren().add(new Text(content.substring(cursor, match)));
            }
            Text highlighted = new Text(content.substring(match, match + query.length()));
            highlighted.getStyleClass().add("search-highlight");
            textFlow.getChildren().add(highlighted);
            cursor = match + query.length();
        }
    }

    private void updateLibraryStatus() {
        libraryStatus.setText(library.size() + (library.size() == 1 ? " document" : " documents"));
    }

    private void updateResultStatus() {
        resultStatus.setText(results.size() + (results.size() == 1 ? " result" : " results"));
    }

    private void setIndexStatus(String message) {
        indexStatus.setText(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null || message.isBlank() ? "Unexpected document processing error." : message);
        alert.showAndWait();
    }
}
