package client.gui;

/**
 * The observer interface that holds an update method for the GUi and model
 * @author Angela Hudal
 * @author Ryan Vay
 * @param <Subject> The subject being updated
 */
public interface Observer<Subject> {
    void update(Subject subject);
}
