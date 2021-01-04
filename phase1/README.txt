===Instruction===
To run the program, open the Main class in src and run the main() method. Please have accounts.txt open for credentials
that you can use to login and interact with the textUI and check phase1 requirements.
Username starts with "a" is an Attendee account, "o" for Organizer, and "s" for Speaker (We made you as a speaker for
the CSC207 review event!).
We have popularized some events and messages in events.csv and message.txt that you can check in the system.
Thank you for your time and feedback :)

===Notes===
- Some of us have github emails instead of utoronto email for commit lines, but our name labeled beside the
commit lines should indicate who we are. Please let us know if that's a problem.
- We have added unit tests under the test folder. You may need to add JUnit4 jar to successfully compile.
***IMPORTANT***
- We noticed that file.delete() does not work properly on Windows/linux system.
(https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4722539)
Our main project does not rely on file.delete(). However, some parts of our test cases uses file.delete() heavily.
If you are using windows/linux system, you may expect some of our test cases to fail because the saved files do not get
cleaned properly. Sorry for this inconvenience as you might need to run the test cases individually and manually clean
the file in between each test cases (in EventDataProviderTest, UserAccountDataProvider and MessageDataProviderTest),
if you want to test them. For MessageControllerTest, you may need to run test cases one by one, and only manually clear
the testMessages.txt file after certain test cases which contain file.delete() (same applies to readNoFileExistTest in
UserAccountDataProvider for testAccounts.txt).

===Question===
1. Could we have a static variable constant when reads the file for splitting?
2. Our test cases are not completely independent of each other
(e.g. see messaging_system_test.MessageControllerTest.java). Hence, we have to keep the testMessages.txt file
undeleted after running all the test cases, and manually clear it before running a new round of test cases.
Is there a way to automate this process if we want to delete the file only after we execute all the test cases
in order? (Could we use JUnit5.4?)
3. We are brainstorming for new features to be implemented in phase2, and we wonder if supporting another language
(such as Mandarin) counts as a new feature for requirement 3, or does it have to be French?
4. Can we use a Markdown file for the README file for phase2?


===Clean Architecture Layer===
(classes labeled with (I) implemented its own interface)
Entity:         Event, User, Message
Use Case Class: EventServiceEngine(I), UserServiceEngine(I), MessageServiceEngine(I)
Gateway:        EventDataProvider(I), UserAccountDataProvider(I), MessageFileDataProvider(I)
Presenter:      EventPresenter, LoginPresenter, MessagePresenter, MenuPresenter
Controller:     EventController(I), LoginController(I), MessageController(I), MessageAuthorizer(I)
(for UI)        MessageSystem, EventSchedulingSystem, EventSignUpSystem, ConferenceSystem


===Assumptions===
event_system
- Cannot use the following characters: ",|ε".
- Event names must be unique.
- Implemented phase2 features that an event can have multiple speakers (at least one speaker) and no
length restriction to the event time.
- There can be 0-2 attendees in an event.
- There must be at most 1 speaker.

user_system / login_system
- Username starts with "a" => Attendee.
- Username starts with "o" => Organizer.
- Username starts with "s" => Speaker.
- When creating a new speaker, username must start with the letter 's'.
- New user accounts created must only contain alphanumeric characters.
- User accounts read in from an external file are only valid logins if they begin with
either 'a', 'o', or 's', otherwise the user account is ignored.
- The txt file that stores the user account information must contain a header (i.e. Name, Password)
and contain one user account per row.
- usernames are case sensitive, i.e. a user with the username bob123 and a user with username BoB123 are considered
two different users.

messaging_system
- Messages presented on the screen have the following format: <senderId> (<timestamp>): <message content> @<receiverId>
- Used symbol "ç" to split messages in txt file. Do not include ç in your messages.
- Attendee can message users who are not an Organizer.
- It is assumed that an attendee will only want to interact with those who have already signed up for an event or whose
usernames are known to the attendee.
They can get in touch with other attendees by checking attendee Id list using the event service.
- When organizers multi-message attendees or speakers, they will message all registered attendees or speakers in the
system.
- When the speaker multi-message to attendees registered in different talks at the same time, separate
talk names with comma (,) in between.
- When the speaker multi-message all attendees registered in his/her talks, if some attendee registers for
more than one event, he/she will only receive one copy of the message.
- When the speaker has multiple events, for example, EventA, EventB, and EventC, and he/she wants to message
attendees in EventA and EventB, then attendees registered in both EventA and EventB will receive two messages
which are differentiated using an auto-generated tag (#EventA, #EventB). For example:
<speakerId> (<timestamp>): #EventA: <message content> @<attendeeId>
<speakerId> (<timestamp>): #EventB: <message content> @<attendeeId>
- It is assumed that when attendees want to message another attendee, they know the username of the receiver.
- If attendee A does not know attendee B's username and attendee B is not signed up for any event, then
there is no way for attendee A to get in touch with attendee B.