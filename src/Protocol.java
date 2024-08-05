import java.io.*;
import java.util.ArrayList;

/** The protocol class for the structure of the server. Its idea is based on the Knock knock protocol which
 *  has states for every different action. It also has the database stored both internally and externally. This means
 *  that in order for the database to have easy and fast access to the database, it makes a copy of it. But it also has
 *  to be refreshed from times to times because i.e. from the actions of another client, the database has new data in
 *  it.
 *  The way the data is stored externally is simple. The database is an ArrayList of Accounts, and in their turn,
 *  the Accounts have An arraylist of Emails and some other fields. Every other field is serializable, and so is the
 *  Class Email, so serialization meets no problem. The more technical part of the storing is analyzed in the function
 *  responsible for this specific action. The class has some other variables stored, such as the states of the program
 *  as practically constants (static final), an array of the default messages for each state, a variable for the
 *  current State of the program, an account that the client connects to and finally an email (either for the client
 *  to read or to send to another user). The states are self explanatory, so the only thing to point out is that some
 *  actions have phases, so there can be many states numbered appropriately for a specific action i.e. to send an Email.
 *  All the processing is happening in the function processingInput, and from there, with the help of other inside
 *  functions of course, the output is produced. Each state has its own function.
 *
 */

public class Protocol {

    private static final int STARTUP_MENU= 0;
    private static final int LOGIN1= 1;
    private static final int LOGIN2= 2;
    private static final int REGISTER1 = 3;
    private static final int REGISTER2 = 4;
    private static final int MAIN_MENU= 5;
    private static final int NEW_EMAIL1= 6;
    private static final int NEW_EMAIL2= 7;
    private static final int NEW_EMAIL3= 8;
    private static final int SHOW_EMAILS= 9;
    private static final int WAITING= 10;
    private static final int READ_EMAIL= 11;
    private static final int DELETE_EMAIL= 12;
    private static final int EXIT= 13;
    private int currentState;
    private final String[] outputs;
    private ArrayList<Account> accounts;
    private Email email;
    private Account currentAccount;

    /** Default and the only constructor where the accounts from the database load to the variable accounts
     * the default outputs for every state are initialized and the current state starts from the main menu.
     *
     *
     */
    public Protocol() {
        currentState= 0;
        accounts= new ArrayList<>();
        loadAccounts();
        outputs= new String[] { "\n >>Login (1)\n >>Register (2)\n >>Exit (3)\n" ,
                "Enter your username (enter 0 to go back):\n",
                "Enter your password (enter 0 to cancel):\n",
                "Select a username (enter 0 to go back):\n",
                "Select a password (enter 0 to cancel):\n",
                ">> New Email (1)\n>> Show Emails (2)\n>> Read Email (3)\n>> Delete Email (4)\n>> Log out (5)\n>> Exit (6)",
                "Select a receiver:\n",
                "Enter the Subject: (enter 0 to cancel)\n",
                "Type the body of the Email:\n",
                "These are your Emails:\n",
                "Press any key to continue...\n",
                "Type the id of the Email you want to read:\n",
                "Type the id of the Email you want to delete:\n",
                "GoodBye!\n"};
        email= null;
        currentAccount= null;
    }

    /** Function where, depending from the input from the client and the current state of the program, the
     * output to be sent to the client is produced. Each time the function starts, the function loadAccounts is
     * called because then, the database is renewed where it may contain new information i.e. a new Email to an account
     *
     * @param input input from the client
     * @return the output to give to the client
     */
    public String processingInput(String input) {
        loadAccounts();
        String output="*********\n";
        if(currentState ==STARTUP_MENU) {
            output+= startUpMenu(input);
        } else if(currentState ==LOGIN1) {
            output+= login1(input);
        } else if(currentState ==LOGIN2) {
            output+= login2(input);
        } else if(currentState == REGISTER1) {
            output+= register1(input);
        } else if(currentState == REGISTER2) {
            output+= register2(input);
        } else if(currentState ==MAIN_MENU) {
            output+= mainMenu(input);
        } else if(currentState == NEW_EMAIL1) {
            output+= newEmail1(input);
        } else if(currentState == NEW_EMAIL2) {
            output+= newEmail2(input);
        } else if(currentState == NEW_EMAIL3) {
            output+= newEmail3(input);
        } else if(currentState == SHOW_EMAILS) {
            output+= showEmails();
        } else if (currentState == WAITING) {
            output+= waiting();
        } else if(currentState == READ_EMAIL) {
            output+= readEmail(input);
        } else if(currentState == DELETE_EMAIL) {
            output+= deleteEmail(input);
        }
        return output;
    }

    /** Function for the state STARTUP_MENU
     * When in this state, the user is shown a menu with 3 choices, one to login to their account, one to
     * create a new account and a final one to exit the program. So, depending on the choice of the user,
     * the program proceeds to the state of the equivalent case. There is also a case that is only selected when
     * the program starts (case 0) so the first message is part of the output. If the input is wrong (not a number) or is not a
     * choice, then an indicative message is part of the output.
     *
     *
     * @param input the choice of the user
     * @return the output to be shown to the user
     */
    public String startUpMenu(String input) {
        try {
            int op = Integer.parseInt(input);
            switch (op) {
                case 0: //start of program
                    return "Welcome!\n" + outputs[currentState];
                case 1:
                    currentState = LOGIN1;
                    break;
                case 2:
                    currentState = REGISTER1;
                    break;
                case 3:
                    currentState = EXIT;
                    break;
                default:
                    return "Wrong input\n" + outputs[currentState];

            }
        } catch (NumberFormatException exception) {
            return "Wrong input\n" + outputs[currentState];
        }
        return outputs[currentState];
    }

    /** Function for state LOGIN1 (first phase of action login)
     *  User is shown a message telling them to enter their username or to press 0 to go back (state STARTUP_MENU). If
     *  the input is not 0, then the function findAccount is called to check if the username exists in the database. If
     *  yes, then the state goes to the next phase of the action login. The currentAccount is the account of the username,
     *  but the user still has to confirm (in the next phase) if they know the password of the account. If the account
     *  does not exist, then an indicative message is part of the output.
     *
     * @param username the username given by the user
     * @return the output to be shown to the user
     */
    public String login1(String username) {
        if (username.equals("0")){
            currentState =STARTUP_MENU;
            return outputs[currentState];
        } else if((currentAccount=findAccount(username)) != null ) {
            currentState =LOGIN2;
            return outputs[currentState];
        }  else {
            return "The username does not exist\n" + outputs[currentState];
        }
    }

    /** Function for state LOGIN2 (final phase of action login)
     *  In this state, the user has entered a username that does exist in the database, and is told to enter the
     *  password or press 0 to go back to main menu. Since the account is already connected, the check is easy with
     *  a simple .equals. If the password is the correct one, the state goes to MAIN_MENU with a welcoming message to the
     *  user. But if it not the correct, then an indicative message is shown and the state remains the same for the
     *  user to try again to enter the correct password.
     *  If the user decides to go back, it is important that the currentAccount goes back to null.
     *
     * @param password the password entered from the user
     * @return the output to be shown to the user
     */
    public String login2(String password) {
        if(currentAccount.getPassword().equals(password)) {
            currentState =MAIN_MENU;
            return "Welcome back " + currentAccount.getUsername() + "!\n" + outputs[currentState];

        } else if (password.equals("0")) { //given up on password so goes back on main menu
            currentAccount=null; //because the account is the one that the user tries to get access
            // from the previous faction
            currentState =STARTUP_MENU;
            return outputs[currentState];

        } else {
            return "Wrong password, try again\n" + outputs[currentState];
        }
    }

    /** Function for state REGISTER1 (first phase of action register)
     *  In this state, the user has chosen from the fist menu to sign up with a new account. So they are asked to enter
     *  the username they want to have (or press 0 to cancel). Usernames are unique, so with the help of the function
     *  findAccount, the username is searched in the database. If the username already exists, an indicative message
     *  is shown and then asks the user to try a different username. Else, the first phase with the partial creation
     *  of the new account is complete and the state proceeds to the next phase.
     *
     * @param username the username the user picked for their email account
     * @return the output to be shown to the user
     */
    public String register1(String username) {
        if(username.equals("")) {
            return "you cannot have an empty username\n" + outputs[currentState];
        } else if(username.equals("0")) {
            currentState =STARTUP_MENU;
            return outputs[currentState];
        }
        if(findAccount(username)!= null) {
            return "This username is already taken from another user, try another one\n" + outputs[currentState];
        } else {
            currentAccount= new Account(username);
            currentState = REGISTER2;
            return outputs[currentState];
        }
    }

    /** Function for state REGISTER2 (final phase of action register)
     *  In this state ,the user has picked a username for their new account and they have been asked to type the
     *  password they want to use (or press 0 to cancel). The only password the users cannot pick is an empty one and
     *  0, because this is what they type to cancel the creation of the new account. In any other case, the password
     *  is set, and the account is added to the database. As part of the output is a message for the successful
     *  creation of the new account.
     *
     * @param password the password the user picked
     * @return the output to be shown to the user
     */
    public String register2(String password) {
        if(password.equals("")) {
            return "You cannot have an empty password\n" + outputs[currentState];
        } else if (password.equals("0")) {
            currentAccount=null;
            currentState =STARTUP_MENU;
            return  outputs[currentState];
        } else {
            currentAccount.setPassword(password);
            accounts.add(currentAccount);
            refreshAccounts();
            currentState = MAIN_MENU;
            return "Your account was created successfully, welcome " + currentAccount.getUsername() + "!\n" + outputs[currentState];
        }
    }

    /** Function for state MAIN_MENU
     *  In this state, the user has connected to an account and has been shown a menu with 6 choices. Send a new email
     *  See all their Emails, read an Email, delete an Email, log out or exit. Each of the choices has a number-id for
     *  the user to choose. If the input is a valid one, the state goes to the one the user has chosen. If not, they
     *  are asked to give a correct input and are shown the menu again. The only action that isn't a state is the one
     *  that shows all the Emails. This goes to the state WAITING and returns as output the function showEmails.
     *
     * @param input the choice of the user from the main menu
     * @return the output to be shown to the user
     */
    public String mainMenu(String input) {
        try {
            int op = Integer.parseInt(input);
            switch (op) {
                case 1:
                    currentState =NEW_EMAIL1;
                    break;
                case 2:
                    currentState = SHOW_EMAILS;
                    return showEmails();
                case 3:
                    currentState = READ_EMAIL;
                    break;
                    case 4:
                    currentState = DELETE_EMAIL;
                    break;
                case 5:
                    currentAccount=null;
                    currentState = STARTUP_MENU;
                    return "Log out successful\n" + outputs[currentState];
                case 6:
                    currentState = EXIT;
                    break;
                default:
                    return "Not a choice\n" + outputs[currentState];

            }
        } catch (NumberFormatException exception) {
            return "Wrong input\n" + outputs[currentState];
        }
        return outputs[currentState];

    }

    /** Function for state NEW_EMAIL1 (first phase of the action new email)
     *  In this state, the user has chosen to send a new email, so the first thing is to choose the receiver of the e
     *  email. The receiver has to be in the database, so if the username of the receiver does not exist, the user is
     *  informed and the state goes back to the main menu. If the username exists, then the new Email is created
     *  with the sender and receiver already filled. The state goes to the next phase of the action new Email.
     *
     * @param receiverUsername the receiver the user wants to send the new Email
     * @return the output to be shown to the user
     */
    public String newEmail1(String receiverUsername) {
        if(findAccount(receiverUsername) == null) {
            currentState =MAIN_MENU;
            return "There is not a receiver with that username\n" + outputs[currentState];
        } else {
            if(receiverUsername.equals("0")) {
                currentState= MAIN_MENU;
                return outputs[currentState];
            }
            currentState =NEW_EMAIL2;
            email = new Email(currentAccount.getUsername(),receiverUsername);
            return outputs[currentState];
        }

    }

    /** Function for state NEW_EMAIL2 (second phase of action mew email)
     * In this state, the user has been asked to enter the subject of the new Email (or press 0 to cancel)
     * Then the state goes to the next and final phase of the action New Email.
     *
     * @param subject the subject of the new Email
     * @return the output to be shown to the user
     */
    public String newEmail2(String subject) {
        if(subject.equals("0")) {
            email=null;
            currentState= MAIN_MENU;
            return outputs[currentState];
        }
        email.setSubject(subject);
        currentState =NEW_EMAIL3;
        return outputs[currentState];
    }

    /** Function for state NEW_EMAIL3 (final phase of the action new email)
     *  In this phase, the user has to type the main body of te new email (or press 0 to cancel)
     *  After the Email has been completed, then it is send to the receiver by adding it to their mailbox.
     *  Then, the field email becomes null again and the database is renewed. The state goes back to main menu
     *  and the user is informed for the successful completion of their action.
     *
     * @param mainBody the main body of the new email
     * @return the output to be shown to the user
     */
    public String newEmail3(String mainBody) {
        currentState =MAIN_MENU;
        if(mainBody.equals("0")) {
            email=null;
            return outputs[currentState];
        }
        email.setMainBody(mainBody);
        findAccount(email.getReceiver()).addEmail(email);
        email =null;
        refreshAccounts();
        return "Message sent successfully\n" + outputs[currentState];
    }

    /** Function to show the emails of an account (action show emails)
     *  This function is called when in the main menu, the user chooses the second choice, to see a preview of their
     *  Emails. If the user has no emails, they are informed properly, else they are shown the list of their Emails
     *  where for every Email, its ID, if it is read or not, its sender and its subject is shown. Of course, the user
     *  has to take some time to check the list, so the next state is the waiting one, so the user has all the time they
     *  need to see the list.
     *
     * @return the output to be shown to the user
     */
    public String showEmails() {
        if(currentAccount.isMailBoxEmpty()) {
            currentState= MAIN_MENU;
            return "You  have no Emails\n" + outputs[currentState];
        } else {
            currentState=WAITING;
            StringBuilder str= new StringBuilder("ID       FROM");
            for (int i=0; i < currentAccount.size(); i++) {
                Email email= currentAccount.getEmail(i);
                str.append("\n").append(i+1).append(". ");
                if (email.IsEmailNew()){
                    str.append("[New] ");
                } else {
                    str.append("      ");
                }
                str.append(email.getSender()).append("   S: ").append(email.getSubject());
            }
            return outputs[SHOW_EMAILS] + "\n" + str.toString() + "\n"  + outputs[currentState];
        }
    }

    /** Function for state WAITING (not an action)
     *  Sometimes, when the user is shown some data, the want to process them on their own time, so after these actions
     *  the current state becomes the WAITING where they are shown a message to press a key to continue.
     *  When they press the key, this function is called where it just moves to the main menu.
     *
     *
     * @return the output to be shown to the user
     */
    public String waiting() {
        currentState =MAIN_MENU;
        return outputs[currentState];
    }

    /** Function for state READ_EMAIL (action read Email)
     *  In this state, the user has chosen from the main menu to read a specific email, and has been asked to give the
     *  Id if the email they want to read. The ids work like a stack (because the database is implemented with an
     *  ArrayList), which means that the smallest id is the one from the most resent Email. If the id is valid, then
     *  the user is shown the whole Email (Sender, Subject and main body) and the current state moves to the WAITING
     *  for the user to read the email and then move on. If the id in not a valid one or there isn't an email with this
     *  id, the appropriate message is shown to the user.
     *
     * @param EmailId the id of the Email the user wants to read
     * @return the output to be shown to the user
     */
    public String readEmail(String EmailId) {
        try {
             email = currentAccount.getEmail(Integer.parseInt(EmailId) - 1);  //to -1 giati einai 0-indexed
        } catch (NumberFormatException exception) {
            email=null;
            currentState =MAIN_MENU;
            return "Wrong input\n" + outputs[currentState];
        }
        if(email !=null) {
            email.setNew(false);
            refreshAccounts();
            String emailOutput= email.toString();
            email =null;
            currentState =WAITING;
            return emailOutput + "\n\n" + outputs[currentState];
        } else {
            currentState =MAIN_MENU;
            return "There is no Email with this id\n" + outputs[currentState];
        }
    }

    /** Function for state DELETE_EMAIL (action delete email)
     *  In this state, the user has chosen from the main menu the option to delete an Email and has been shown a
     *  message asking the id of the to-be-deleted Email. Similarly with the action read email, if the id is not valid
     *  then an indicative message is shown. If the id is valid, then the email is deleted and the database is refreshed
     *  with the function refresh accounts.
     *
     *
     * @param id the id of the Email the user wants to delete
     * @return the output to be shown to the user
     */
    public String deleteEmail(String id) {
        currentState =MAIN_MENU;
        try {
            currentAccount.deleteEmail(Integer.parseInt(id) -1);

        } catch (NumberFormatException e) {
            return "Wrong input\n" + outputs[currentState];

        } catch ( IndexOutOfBoundsException e) {
            return "There is no email with such id\n" + outputs[currentState];
        }
        refreshAccounts();
        return "Message deleted successfully\n" + outputs[currentState];
    }

    /** This function is called after every action made from the user in order to always have the most recent data from
     *  the database. As explained in the beginning, the whole database is serialized and stored in
     *  a file, named database.ser. If the file previously was empty, that means that the database was empty as well, so
     *  the variable accounts is initialized. iF not, then the variable accounts gets the data stored in the file.
     *  An important note is that the current account (if the user has logged in) must be renewed too in case some new
     *  data have come i.e. a new email. The only times this doesn't happen is if the user is not logged in or when they
     *  haven't finished their sign up because the information they have typed (username) will be lost because the
     *  account has yet to be stored in the database.
     *
     */
    public void loadAccounts() {
        try {
            FileInputStream fileIn = new FileInputStream("database.ser"); //file with serialized accounts
            ObjectInputStream in = new ObjectInputStream(fileIn);
            accounts = (ArrayList) in.readObject();
            in.close();
            fileIn.close();
        } catch (EOFException | NullPointerException e) { // for empty txt file
            accounts= new ArrayList<>();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            return;
        }
        //When a client is logged in and the server needs to refresh the accounts
        //the client needs to refresh his current account eg a new email may come to them
        if(currentAccount!=null && currentState != REGISTER2) {
            currentAccount = findAccount(currentAccount.getUsername());
        }
    }

    /** Function that searches a username in the database and returns it if it exists, or else returns null
     *
     * @param username a username to check it there is in the database
     * @return the Account of the username or null if the account doesn't exist
     */
    public Account findAccount(String username) {
        for(Account anAccount: accounts) {
            if(anAccount.getUsername().equals(username)) {
                return anAccount;
            }
        }
        return null;
    }

    /** Function that refreshes the database and is called every time a chance is made i.e. new email was sent, an email
     *  was deleted, an email has changed its state from not read to read.
     *  The file that contains the data (database.ser) is opened where it previously was either
     *  empty or had the previous data. In either case, the serialized data is written in the file (deleting any
     *  previous data).
     */
    public void refreshAccounts() {
        try {
            FileOutputStream fileOut = new FileOutputStream("database.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(accounts);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
