package mattw.youtube.commentsuite.db;

/**
 * Type definition for YouTubeObjects
 *
 * @since 2018-12-30
 * @author mattwright324
 */
public enum YType {
    UNKNOWN("Unknown"),
    VIDEO("Video"),
    CHANNEL("Channel"),
    PLAYLIST("Playlist"),
    COMMENT("Comment");

    private String display;

    YType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public int id() {
        return this.ordinal() - 1;
    }
}
