import java.util.ArrayList;

/** Class Account
 * This class represents an account of a user for our email system
 * Its fields are the username of the user, their password and a mailbox, which is
 * a List with their emails. The class implements java.io.Serializable in
 * order for the database from the server to be stored in a serializable way. This can
 * happen since its fields are String which is a Serializable Class, and an ArrayList with
 * Emails, which both the Email and also the ArrayList implements java.io.Serializable.
 *
 */
public class Account implements java.io.Serializable {
    private String username;
    private String password;
    private ArrayList<Email> mailbox;

    /** Default constructor with empty username, password and mailbox
     *
     */
    public Account() {
        username= "";
        password= "";
        mailbox= new ArrayList<>();
    }

    /** Constructor with the username given
     *
     * @param aUsername the username of the user
     */
    public Account(String aUsername) {
        this();
        username= aUsername;

    }

    /** necessary getters and setters
     *
     */
    public String getUsername() { return username; }

    public void setPassword(String APassword) {this.password= APassword; }

    public String getPassword() { return  password; }

    /**
     *
     * @return if the mailbox is empty of not
     */
    public boolean isMailBoxEmpty() {
        return mailbox.isEmpty();
    }

    /** Function to return a specific Email
     *
     *
     * @param index the index of the wanted Email
     * @return the Email if the index is in bounds, or else null
     */
    public Email getEmail (int index) {
        try {
            return mailbox.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /** Function for the size of the mailbox
     *
     * @return the size of te mailbox
     */
    public int size() {return mailbox.size(); }

    /** Function to add an Email to the mailbox
     *
     * @param anEmail the to-be-added Email
     */
    public void addEmail (Email anEmail) {
        mailbox.add(anEmail);
    }

    /** Function to Delete an Email
     *
     * @param index the index of the to-be-deleted Email
     */
    public void deleteEmail (int index) {
        mailbox.remove(index);
         }
}

