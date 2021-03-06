package test;

import impl.ContactImpl;
import impl.ContactManagerImpl;
import org.junit.Before;
import org.junit.Test;
import spec.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ContactManagerTest {

    private ContactManager contactManager;
    private Calendar nowDate;
    private Calendar pastDate;
    private Calendar futureDate;


    @Before
    public void setUp() {
        contactManager = new ContactManagerImpl();
        nowDate = Calendar.getInstance();
        pastDate = Calendar.getInstance();
        pastDate.add(Calendar.YEAR, -1);
        futureDate = Calendar.getInstance();
        futureDate.add(Calendar.YEAR, 1);
    }

    @Test
    public void testAddNewContactNullNameThrowsException() {
        try {
            contactManager.addNewContact(null, "notes");
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testAddNewContactNullNotesThrowsException() {
        try {
            contactManager.addNewContact("name", null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testGetContactsFromName() {
        contactManager.addNewContact("mike", "notes");
        contactManager.addNewContact("kevin", "notes");

        Set<Contact> expectMike = contactManager.getContacts("mike");
        Set<Contact> expectBoth = contactManager.getContacts("ke");
        Set<Contact> expectNone = contactManager.getContacts("sue");

        assertEquals(1, expectMike.size());
        assertEquals("mike", ((Contact) expectMike.toArray()[0]).getName());

        assertEquals(2, expectBoth.size());
        assertEquals(0, expectNone.size());
    }

    @Test
    public void testGetContactsFromNameThrowsException() {
        try {
            contactManager.getContacts((String) null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testGetContactsFromIds() {
        contactManager.addNewContact("mollie", "notes");
        contactManager.addNewContact("mike", "notes");

        Set<Contact> expectedContacts = contactManager.getContacts("m");

        Iterator<Contact> it = expectedContacts.iterator();
        int firstId = it.next().getId();
        int secondId = it.next().getId();

        Set<Contact> returnedContacts = contactManager.getContacts(firstId, secondId);

        assertEquals(expectedContacts, returnedContacts);
    }

    @Test
    public void testGetContactsFromIdsNotFoundThrowsException() {
        contactManager.addNewContact("mike", "notes");
        try {
            contactManager.getContacts(3, 8);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

//#### Future Meetings ####

    @Test
    public void testAddFutureMeetingContactNotFoundThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> outsideContactSet = new HashSet<Contact>();
        outsideContactSet.add(new ContactImpl("sue", "notes"));

        try {
            contactManager.addFutureMeeting(outsideContactSet, futureDate);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testAddFutureMeetingPastDateThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mike = contactManager.getContacts("mike");
        try {
            contactManager.addFutureMeeting(mike, pastDate);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetFutureMeetingListContactNotFoundThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        contactManager.addFutureMeeting(mikeSet, futureDate);

        Contact sue = new ContactImpl("sue", "notes");
        try {
            contactManager.getFutureMeetingList(sue);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetFutureMeetingListReturnsMeeting() {
        contactManager.addNewContact("mike", "notes");
        contactManager.addNewContact("sue", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Set<Contact> sueSet = contactManager.getContacts("sue");
        Set<Contact> bothSet = contactManager.getContacts("");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addFutureMeeting(sueSet, futureDate);
        contactManager.addFutureMeeting(bothSet, futureDate);
        List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);

        assertEquals(1, returnedMeetings.size());
        assertTrue(returnedMeetings.get(0).getContacts().contains(mike));
    }

    @Test
    public void testGetFutureMeetingListSorted() {
        Calendar oneYearDate = Calendar.getInstance();
        oneYearDate.add(Calendar.YEAR, 1);
        Calendar twoYearDate = Calendar.getInstance();
        twoYearDate.add(Calendar.YEAR, 2);
        Calendar threeYearDate = Calendar.getInstance();
        threeYearDate.add(Calendar.YEAR, 3);

        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addFutureMeeting(mikeSet, threeYearDate);
        contactManager.addFutureMeeting(mikeSet, oneYearDate);
        contactManager.addFutureMeeting(mikeSet, twoYearDate);
        List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);

        assertEquals(oneYearDate, returnedMeetings.get(0).getDate());
        assertEquals(twoYearDate, returnedMeetings.get(1).getDate());
        assertEquals(threeYearDate, returnedMeetings.get(2).getDate());
    }

    @Test
    public void testAddFutureDateReturnsId() {
        Contact mike = new ContactImpl("mike", "notes");
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");

        int mikeMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
        List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);

        assertEquals(mikeMeetingId, returnedMeetings.get(0).getId());
    }

    // #### Past Meetings ####

    @Test
    public void testAddNewPastMeetingContactNotFoundThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> outsideContactSet = new HashSet<Contact>();
        outsideContactSet.add(new ContactImpl("sue", "notes"));

        try {
            contactManager.addNewPastMeeting(outsideContactSet, pastDate, "text");
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testAddNewPastMeetingContactNullContactThrowsException() {
        try {
            contactManager.addNewPastMeeting(null, pastDate, "text");
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testAddNewPastMeetingContactNullDateThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mike = contactManager.getContacts("mike");
        try {
            contactManager.addNewPastMeeting(mike, null, "text");
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testAddNewPastMeetingContactNullTextThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mike = contactManager.getContacts("mike");
        try {
            contactManager.addNewPastMeeting(mike, pastDate, null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testGetPastMeetingListContactNotFoundThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        contactManager.addNewPastMeeting(mikeSet, pastDate, "text");

        Contact sue = new ContactImpl("sue", "notes");
        try {
            contactManager.getPastMeetingListFor(sue);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetPastMeetingListReturnsMeeting() {
        contactManager.addNewContact("mike", "notes");
        contactManager.addNewContact("sue", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Set<Contact> sueSet = contactManager.getContacts("sue");
        Set<Contact> bothSet = contactManager.getContacts("");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(sueSet, pastDate, "text");
        contactManager.addNewPastMeeting(bothSet, pastDate, "text");
        List<PastMeeting> returnedMeetings = contactManager.getPastMeetingListFor(mike);

        assertEquals(1, returnedMeetings.size());
        assertTrue(returnedMeetings.get(0).getContacts().contains(mike));
    }

    @Test
    public void testGetPastMeetingListSorted() {
        Calendar oneYearAgoDate = Calendar.getInstance();
        oneYearAgoDate.add(Calendar.YEAR, -1);
        Calendar twoYearAgoDate = Calendar.getInstance();
        twoYearAgoDate.add(Calendar.YEAR, -2);
        Calendar threeYearAgoDate = Calendar.getInstance();
        threeYearAgoDate.add(Calendar.YEAR, -3);

        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, threeYearAgoDate, "text");
        contactManager.addNewPastMeeting(mikeSet, oneYearAgoDate, "text");
        contactManager.addNewPastMeeting(mikeSet, twoYearAgoDate, "text");
        List<PastMeeting> returnedMeetings = contactManager.getPastMeetingListFor(mike);

        assertEquals(threeYearAgoDate, returnedMeetings.get(0).getDate());
        assertEquals(twoYearAgoDate, returnedMeetings.get(1).getDate());
        assertEquals(oneYearAgoDate, returnedMeetings.get(2).getDate());
    }

    // ### General Meeting Return

    @Test
    public void testGetFutureMeetingListByDateReturnsFutureMeetings() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "past1");
        contactManager.addNewPastMeeting(mikeSet, pastDate, "past2");
        contactManager.addNewPastMeeting(mikeSet, pastDate, "past3");
        contactManager.addFutureMeeting(mikeSet, futureDate);
        contactManager.addFutureMeeting(mikeSet, futureDate);
        contactManager.addFutureMeeting(mikeSet, futureDate);

        List<Meeting> futureMeetings = contactManager.getFutureMeetingList(mike);
        List<Meeting> returnedFutureMeetings = contactManager.getMeetingListOn(futureDate);

        assertEquals(3, returnedFutureMeetings.size());
        assertTrue(returnedFutureMeetings.contains(futureMeetings.get(0)));
        assertTrue(returnedFutureMeetings.contains(futureMeetings.get(1)));
        assertTrue(returnedFutureMeetings.contains(futureMeetings.get(2)));
    }

    @Test
    public void testGetFutureMeetingListByDateReturnsPastMeetings() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "past1");
        contactManager.addNewPastMeeting(mikeSet, pastDate, "past2");
        contactManager.addNewPastMeeting(mikeSet, pastDate, "past3");
        contactManager.addFutureMeeting(mikeSet, futureDate);
        contactManager.addFutureMeeting(mikeSet, futureDate);
        contactManager.addFutureMeeting(mikeSet, futureDate);

        List<PastMeeting> pastMeetings = contactManager.getPastMeetingListFor(mike);
        List<Meeting> returnedPastMeetings = contactManager.getMeetingListOn(pastDate);

        assertEquals(3, returnedPastMeetings.size());
        assertTrue(returnedPastMeetings.contains((Meeting) pastMeetings.get(0)));
        assertTrue(returnedPastMeetings.contains((Meeting) pastMeetings.get(1)));
        assertTrue(returnedPastMeetings.contains((Meeting) pastMeetings.get(2)));
    }

    // ### Get Meeting by Id ###

    @Test
    public void testGetPastMeetingByIdReturnsNullIfNone() {
        PastMeeting returnedMeeting = contactManager.getPastMeeting(100);

        assertThat(returnedMeeting, is(nullValue()));
    }

    @Test
    public void testGetPastMeetingByIdThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);

        try {
            contactManager.getPastMeeting(futureMeetingId);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetPastMeetingByIdReturn() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");

        List<PastMeeting> pastMeetingList = contactManager.getPastMeetingListFor(mike);
        PastMeeting pastMeeting = pastMeetingList.get(0);
        int pastMeetingId = pastMeeting.getId();

        PastMeeting returnedMeeting = contactManager.getPastMeeting(pastMeetingId);

        assertEquals(returnedMeeting, pastMeeting);
    }

    @Test
    public void testGetFutureMeetingByIdReturnsNullIfNone() {
        FutureMeeting returnedMeeting = contactManager.getFutureMeeting(100);

        assertThat(returnedMeeting, is(nullValue()));
    }

    @Test
    public void testGetFutureMeetingByIdThrowsException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");

        List<PastMeeting> pastMeetingList = contactManager.getPastMeetingListFor(mike);
        int pastMeetingId = pastMeetingList.get(0).getId();

        try {
            contactManager.getFutureMeeting(pastMeetingId);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetFutureMeetingByIdReturn() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);

        List<Meeting> futureMeetingList = contactManager.getFutureMeetingList(mike);
        FutureMeeting futureMeeting = (FutureMeeting) futureMeetingList.get(0);

        FutureMeeting returnedMeeting = contactManager.getFutureMeeting(futureMeetingId);

        assertEquals(returnedMeeting, futureMeeting);
    }

    @Test
    public void testGetMeetingByIdReturnsNullIfNone() {
        Meeting returnedMeeting = contactManager.getMeeting(100);

        assertThat(returnedMeeting, is(nullValue()));
    }

    @Test
    public void testGetMeetingByIdReturnsPastMeeting() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
        contactManager.addFutureMeeting(mikeSet, futureDate);

        List<PastMeeting> pastMeetingList = contactManager.getPastMeetingListFor(mike);
        Meeting pastMeeting = (Meeting) pastMeetingList.get(0);
        int pastMeetingId = pastMeeting.getId();

        Meeting returnedMeeting = contactManager.getMeeting(pastMeetingId);

        assertEquals(returnedMeeting, pastMeeting);
    }

    @Test
    public void testGetMeetingByIdReturnsFutureMeeting() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
        contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");

        List<Meeting> futureMeetingList = contactManager.getFutureMeetingList(mike);
        Meeting futureMeeting = futureMeetingList.get(0);

        FutureMeeting returnedMeeting = contactManager.getFutureMeeting(futureMeetingId);

        assertEquals(returnedMeeting, futureMeeting);
    }

    // ### add Notes ###

    @Test
    public void testAddMeetingNotesThrowsNullPointerException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
        List<PastMeeting> pastMeetingList = contactManager.getPastMeetingListFor(mike);
        int pastMeetingId = pastMeetingList.get(0).getId();

        try {
            contactManager.addMeetingNotes(pastMeetingId, null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testAddMeetingNotesThrowsIllegalStateException() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");

        int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);

        try {
            contactManager.addMeetingNotes(futureMeetingId, "notes");
            fail();
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void testAddMeetingNotesThrowsIllegalArgumentException() {
        try {
            contactManager.addMeetingNotes(100, "notes");
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testAddMeetingNotesAddNotesToPastMeeting() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");
        Iterator<Contact> it = mikeSet.iterator();
        Contact mike = it.next();

        contactManager.addNewPastMeeting(mikeSet, pastDate, "Notes 1");
        List<PastMeeting> pastMeetingList = contactManager.getPastMeetingListFor(mike);
        int pastMeetingId = pastMeetingList.get(0).getId();

        contactManager.addMeetingNotes(pastMeetingId, "Notes 2");

        PastMeeting pastMeeting = contactManager.getPastMeeting(pastMeetingId);

        assertTrue(pastMeeting.getNotes().contains("Notes 1"));
        assertTrue(pastMeeting.getNotes().contains("Notes 2"));
    }

    @Test
    public void testAddMeetingNotesConvertsFutureMeetingToPastMeetingAndAddsNotes() {
        contactManager.addNewContact("mike", "notes");
        Set<Contact> mikeSet = contactManager.getContacts("mike");

        Calendar halfSecondAwayDate = Calendar.getInstance();
        halfSecondAwayDate.add(Calendar.MILLISECOND, 500);

        try {
            int convertMeetingId = contactManager.addFutureMeeting(mikeSet, halfSecondAwayDate);
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                fail("600ms sleep interupted");
            }
            try {
                contactManager.addMeetingNotes(convertMeetingId, "Convert Meeting Notes");
            } catch (IllegalStateException e) {
                fail("addMeetingNotes() asserts that meeting is in the future, even though date is the past.");
            }
            try {
                PastMeeting pastMeeting = contactManager.getPastMeeting(convertMeetingId);
                assertTrue(pastMeeting.getNotes().contains("Convert Meeting Notes"));
            } catch (IllegalArgumentException e) {
                fail("Couldn't find converted meeting as past meeting, meeting not converted.");
            }
        } catch (IllegalArgumentException e) {
            fail("addFutureMeeting() took longer than 500ms to be called, hence meeting was in the 'past'.");
        }
    }


    @Test
    public void testConstructorLoadsFromFile() {
        contactManager.addNewContact("mike", "mike notes");
        contactManager.addNewContact("sue", "sue notes");
        contactManager.addNewContact("kevin", "kevin notes");
        Contact kevin = contactManager.getContacts("kevin").iterator().next();

        ObjectOutputStream encode = null;
        try {
            encode = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream("specific_test.txt")));
        } catch (FileNotFoundException e) {
            System.err.println("encoding... " + e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            encode.writeObject(contactManager);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            encode.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        ContactManager reconstructedCM = new ContactManagerImpl();

        assertEquals(3, reconstructedCM.getContacts("").size());
        assertEquals(1, reconstructedCM.getContacts("kevin").size());
        Contact reconstructedKevin = reconstructedCM.getContacts("kevin").iterator().next();
        assertEquals(reconstructedKevin.getId(), kevin.getId());
        assertEquals(reconstructedKevin.getName(), kevin.getName());
        assertEquals(reconstructedKevin.getNotes(), kevin.getNotes());
        (new File("specific_test.txt")).delete();
    }
}
