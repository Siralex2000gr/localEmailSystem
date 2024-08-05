/** Class Email
 * The fields are the sender, the receiver of the email, its subject, its main body
 * and finally if the message has been read or not from the receiver
 * The class implements java.io.Serializable in order for the database from the server to be stored
 * in a serializable way. This can happen easily since its fields are
 * the boolean primitive data type and String which is a Serializable Class.
 */

public class Email implements java.io.Serializable{
    private String sender;
    private String receiver;
    private String subject;
    private String mainbody;
    private boolean isNew;

    /** Default constructor for Email
     * In the default we consider the email to be unread, and the other info to be blank
     *
     */
    public Email() {
        this.isNew= true;
        sender= "";
        receiver= "";
        subject= "";
        mainbody = "";
    }

    /** Constructor with sender and receiver
     *
     * @param aSender the sender of the email
     * @param aReceiver the receiver of the email
     */
    public Email(String aSender, String aReceiver) {
        this();
        sender= aSender;
        receiver= aReceiver;

    }

    /** Setters and getters where there are nessesary to exist
     *
     */
    public void setNew (boolean isNew){ this.isNew=isNew; }

    public boolean IsEmailNew(){ return isNew; }

    public String getSender() { return sender; }

    public String getReceiver(){ return receiver; }

    public void setSubject(String aSubject) { this.subject=aSubject; }

    public String getSubject() { return subject; }
    public  String getMainbody() {return mainbody; }

    public void setMainBody(String aMainBody) { this.mainbody =aMainBody; }

    /** Overridden toString that is
     *
     * @return the way the Email is shown to the receiver
     */
    public String toString() {
        return "From: " + sender + "\nSubject: " + subject + "\n" +
                mainbody;
    }
}
