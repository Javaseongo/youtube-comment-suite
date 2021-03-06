package mattw.youtube.commentsuite.fxml;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import mattw.youtube.commentsuite.FXMLSuite;
import mattw.youtube.commentsuite.db.CommentDatabase;
import mattw.youtube.commentsuite.db.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import static javafx.application.Platform.runLater;

/**
 * This modal allows the user to delete the entire Group of its ManageGroupsManager. This deletes only the Group
 * object from the database and does not clean up its GroupItems, Videos, Comments, or Channels unless the
 * vacuum option is selected before deletion. Alternatively, the user could do a manual vacuum from {@link Settings}
 * if they did not select vacuum when deleting.
 *
 * @see ManageGroupsManager
 * @since 2018-12-30
 * @author mattwright324
 */
public class MGMVDeleteGroupModal extends VBox {

    private static Logger logger = LogManager.getLogger(MGMVDeleteGroupModal.class.getName());

    private CommentDatabase database;

    private @FXML CheckBox doVacuum;
    private @FXML Button btnClose;
    private @FXML Button btnDelete;

    private Group group;

    public MGMVDeleteGroupModal(Group group) {
        this.group = group;

        database = FXMLSuite.getDatabase();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MGMVDeleteGroupModal.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();

            btnDelete.setOnAction(ae -> new Thread(() -> {
                runLater(() -> {
                    btnDelete.setDisable(true);
                    btnClose.setDisable(true);
                });

                try {
                    logger.warn(String.format("Deleting Group[id=%s,name=%s]", group.getId(), group.getName()));
                    database.deleteGroup(this.group);

                    logger.warn(String.format("Cleaning up after group delete [id=%s,name=%s]", group.getId(), group.getName()));
                    database.cleanUp();
                    database.commit();
                    if(doVacuum.isSelected()) {
                        database.vacuum();
                    }
                    database.refreshGroups();
                } catch (SQLException e) {
                    logger.error("Failed to delete group.", e);
                }
                runLater(() -> {
                    btnDelete.setDisable(false);
                    btnClose.setDisable(false);
                });
            }).start());
        } catch (IOException e) { logger.error(e); }
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }
}
