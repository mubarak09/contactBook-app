package controllers;


import models.Contact;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.CBORSerializer;
import persistence.JSONSerializer;
import persistence.XMLSerializer;
import java.io.File;
import kotlin.test.assertEquals;
import kotlin.test.assertNull;

public class contactBookAPITest {
    private var learnKotlin: Contact? = null
    private var summerHoliday: Contact? = null
    private var codeApp: Contact? = null
    private var testApp: Contact? = null
    private var swim: Contact? = null
    private var populatedContacts: contactBookAPI? = contactBookAPI(XMLSerializer(File("contacts.xml")))
    private var emptyContacts: ContactAPI? = ContactAPI(XMLSerializer(File("Contacts.xml")))

    @BeforeEach
    fun setup(){
        learnKotlin = Contact("Learning Kotlin", 5, "College", false, "09-02-2023 21:10")
        summerHoliday = Contact("Summer Holiday to France", 1, "Holiday", false, "16-03-2023 02:45")
        codeApp = Contact("Code App", 4, "Work", false, "05-01-2023 22:59")
        testApp = Contact("Test App", 4, "Work", false, "13-03-2023 14:34")
        swim = Contact("Swim - Pool", 3, "Hobby", false, "15-03-2023 15:00")

        //adding 5 Contact to the Contacts api
        populatedContacts!!.add(learnKotlin!!)
        populatedContacts!!.add(summerHoliday!!)
        populatedContacts!!.add(codeApp!!)
        populatedContacts!!.add(testApp!!)
        populatedContacts!!.add(swim!!)
    }

    @AfterEach
    fun tearDown(){
        learnKotlin = null;
        summerHoliday = null;
        codeApp = null;
        testApp = null;
        swim = null;
        populatedContacts = null;
        emptyContacts = null;
    }

    @Nested
    inner class AddContacts {
        @Test
        fun `adding a Contact to a populated list adds to ArrayList`() {
            val newContact = Contact("Study Lambdas", 1, "College", false, "11-03-2023 23:10")
            assertEquals(5, populatedContacts!!.numberOfContacts())
            assertTrue(populatedContacts!!.add(newContact))
            assertEquals(6, populatedContacts!!.numberOfContacts())
            assertEquals(newContact, populatedContacts!!.findContact(populatedContacts!!.numberOfContacts() - 1))
        }

        @Test
        fun `adding a Contact to an empty list adds to ArrayList`() {
            val newContact = Contact("Study Lambdas", 1, "College", false, "11-03-2023 23:56")
            assertEquals(0, emptyContacts!!.numberOfContacts())
            assertTrue(emptyContacts!!.add(newContact))
            assertEquals(1, emptyContacts!!.numberOfContacts())
            assertEquals(newContact, emptyContacts!!.findContact(emptyContacts!!.numberOfContacts() - 1))
        }
    }

    @Nested
    inner class SearchContacts {

        @Test
        fun `search Contacts by title returns no Contacts when no Contacts with that title exist`() {
            //Searching a populated collection for a title that doesn't exist.
            assertEquals(5, populatedContacts!!.numberOfContacts())
            val searchResults = populatedContacts!!.searchByTitle("no results expected")
            assertTrue(searchResults.isEmpty())

            //Searching an empty collection
            assertEquals(0, emptyContacts!!.numberOfContacts())
            assertTrue(emptyContacts!!.searchByTitle("").isEmpty())
        }

        @Test
        fun `search Contacts by title returns Contacts when Contacts with that title exist`() {
            assertEquals(5, populatedContacts!!.numberOfContacts())

            //Searching a populated collection for a full title that exists (case matches exactly)
            var searchResults = populatedContacts!!.searchByTitle("Code App")
            assertTrue(searchResults.contains("Code App"))
            assertFalse(searchResults.contains("Test App"))

            //Searching a populated collection for a partial title that exists (case matches exactly)
            searchResults = populatedContacts!!.searchByTitle("App")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))

            //Searching a populated collection for a partial title that exists (case doesn't match)
            searchResults = populatedContacts!!.searchByTitle("aPp")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))
        }

    }

    @Nested
    inner class ListContacts {

        @Test
        fun `listAllContacts returns No Contacts Stored message when ArrayList is empty`() {
            assertEquals(0, emptyContacts!!.numberOfContacts())
            assertTrue(emptyContacts!!.listAllContacts().lowercase().contains("no Contacts"))
        }

        @Test
        fun `listAllContacts returns Contacts when ArrayList has Contacts stored`() {
            assertEquals(5, populatedContacts!!.numberOfContacts())
            val ContactsString = populatedContacts!!.listAllContacts().lowercase()
            assertTrue(ContactsString.contains("learning kotlin"))
            assertTrue(ContactsString.contains("code app"))
            assertTrue(ContactsString.contains("test app"))
            assertTrue(ContactsString.contains("swim"))
            assertTrue(ContactsString.contains("summer holiday"))
        }

        @Test
        fun `listActiveContacts() returns Contacts that have isContactArchived set to false`(){
            assertTrue(!populatedContacts!!.listActiveContacts().contains("No Contacts stored") || !populatedContacts!!.listActiveContacts().contains("No active Contacts stored"))
        }

        @Test
        fun `listArchivedContacts() returns Contacts that have isContactArchived set to true`() {
            assertEquals("No archived Contacts stored", populatedContacts!!.listArchivedContacts())

        }

        @Test
        fun `numberOfArchivedContacts() returns the amount of Contacts that are Archived`() {
            assertEquals(0, populatedContacts!!.numberOfArchivedContacts())
            val newContact = Contact("Study Lambdas", 1, "College", true, "16-03-2023 21:23")
            assertTrue(populatedContacts!!.add(newContact))
            assertEquals(1, populatedContacts!!.numberOfArchivedContacts())
        }

        @Test
        fun `numberOfActiveContacts() returns the amount of Contacts that are Active`() {
            assertEquals(5, populatedContacts!!.numberOfActiveContacts())
            val newContact = Contact("Study Lambdas", 1, "College", false, "16-03-2023 21:18")
            assertTrue(populatedContacts!!.add(newContact))
            assertEquals(6, populatedContacts!!.numberOfActiveContacts())
        }

        @Test
        fun `listContactsBySelectedPriority() returns Contacts based on the priority`(){
            assertTrue(populatedContacts!!.listContactsBySelectedPriority(1).contains("Summer Holiday to France"))
            val newContact = Contact("Study Lambdas", 1, "College", false, "16-03-2023 20:07")
            assertTrue(populatedContacts!!.add(newContact))
            val Contacts = populatedContacts!!.listContactsBySelectedPriority(1)
            assertTrue(Contacts.contains("Summer Holiday to France"))
            assertTrue(Contacts.contains("Study Lambdas"))
        }

        @Test
        fun `numberOfContactsByPriority()returns the number of Contacts based on the priority passed`(){
            assertEquals(2, populatedContacts!!.numberOfContactsByPriority(4))
            val newContact = Contact("Study Lambdas", 4, "College", false,"16-03-2023 18:11")
            assertTrue(populatedContacts!!.add(newContact))
            assertEquals(3, populatedContacts!!.numberOfContactsByPriority(4))
        }

    }

    @Nested
    inner class DeleteContacts {

        @Test
        fun `deleting a Contact that does not exist, returns null`() {
            assertNull(emptyContacts!!.deleteContact(0))
            assertNull(populatedContacts!!.deleteContact(-1))
            assertNull(populatedContacts!!.deleteContact(5))
        }

        @Test
        fun `deleting a Contact that exists delete and returns deleted object`() {
            assertEquals(5, populatedContacts!!.numberOfContacts())
            assertEquals(swim, populatedContacts!!.deleteContact(4))
            assertEquals(4, populatedContacts!!.numberOfContacts())
            assertEquals(learnKotlin, populatedContacts!!.deleteContact(0))
            assertEquals(3, populatedContacts!!.numberOfContacts())
        }
    }

    @Nested
    inner class UpdateContacts {
        @Test
        fun `updating a Contact that does not exist returns false`(){
            assertFalse(populatedContacts!!.updateContact(6, Contact("Updating Contact", 2, "Work", false, "07-03-2023 23:14")))
            assertFalse(populatedContacts!!.updateContact(-1, Contact("Updating Contact", 2, "Work", false,"07-03-2023 21:15")))
            assertFalse(emptyContacts!!.updateContact(0, Contact("Updating Contact", 2, "Work", false, "07-03-2023 23:25")))
        }

        @Test
        fun `updating a Contact that exists returns true and updates`() {
            //check Contact 5 exists and check the contents
            assertEquals(swim, populatedContacts!!.findContact(4))
            assertEquals("Swim - Pool", populatedContacts!!.findContact(4)!!.ContactTitle)
            assertEquals(3, populatedContacts!!.findContact(4)!!.ContactPriority)
            assertEquals("Hobby", populatedContacts!!.findContact(4)!!.ContactCategory)

            //update Contact 5 with new information and ensure contents updated successfully
            assertTrue(populatedContacts!!.updateContact(4, Contact("Updating Contact", 2, "College", false, "07-03-2023 23:17")))
            assertEquals("Updating Contact", populatedContacts!!.findContact(4)!!.ContactTitle)
            assertEquals(2, populatedContacts!!.findContact(4)!!.ContactPriority)
            assertEquals("College", populatedContacts!!.findContact(4)!!.ContactCategory)
        }
    }

    @Nested
    inner class CountingMethods {

        @Test
        fun numberOfContactsCalculatedCorrectly() {
            assertEquals(5, populatedContacts!!.numberOfContacts())
            assertEquals(0, emptyContacts!!.numberOfContacts())
        }

        @Test
        fun numberOfArchivedContactsCalculatedCorrectly() {
            assertEquals(0, populatedContacts!!.numberOfArchivedContacts())
            assertEquals(0, emptyContacts!!.numberOfArchivedContacts())
        }

        @Test
        fun numberOfActiveContactsCalculatedCorrectly() {
            assertEquals(5, populatedContacts!!.numberOfActiveContacts())
            assertEquals(0, emptyContacts!!.numberOfActiveContacts())
        }

        @Test
        fun numberOfContactsByPriorityCalculatedCorrectly() {
            assertEquals(1, populatedContacts!!.numberOfContactsByPriority(1))
            assertEquals(0, populatedContacts!!.numberOfContactsByPriority(2))
            assertEquals(1, populatedContacts!!.numberOfContactsByPriority(3))
            assertEquals(2, populatedContacts!!.numberOfContactsByPriority(4))
            assertEquals(1, populatedContacts!!.numberOfContactsByPriority(5))
            assertEquals(0, emptyContacts!!.numberOfContactsByPriority(1))
        }
    }


    /*
    Inner class for testing XML persistence
     */
    @Nested
    inner class PersistenceTests {

        /*
        XML FORMAT TESTS
         */
        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
        // Saving an empty Contacts.XML file.
        val storingContacts = ContactAPI(XMLSerializer(File("Contacts.xml")))
            storingContacts.store()

        //Loading the empty Contacts.xml file into a new object
        val loadedContacts = ContactAPI(XMLSerializer(File("Contacts.xml")))
            loadedContacts.load()

        //Comparing the source of the Contacts (storingContacts) with the XML loaded Contacts (loadedContacts)
        assertEquals(0, storingContacts.numberOfContacts())
        assertEquals(0, loadedContacts.numberOfContacts())
        assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
    }

    @Test
    fun `saving and loading an loaded collection in XML doesn't loose data`() {
    // Storing 3 Contacts to the Contacts.XML file.
    val storingContacts = ContactAPI(XMLSerializer(File("Contacts.xml")))
            storingContacts.add(testApp!!)
            storingContacts.add(swim!!)
            storingContacts.add(summerHoliday!!)
            storingContacts.store()

    //Loading Contacts.xml into a different collection
    val loadedContacts = ContactAPI(XMLSerializer(File("Contacts.xml")))
            loadedContacts.load()

    //Comparing the source of the Contacts (storingContacts) with the XML loaded Contacts (loadedContacts)
    assertEquals(3, storingContacts.numberOfContacts())
    assertEquals(3, loadedContacts.numberOfContacts())
    assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
    assertEquals(storingContacts.findContact(0), loadedContacts.findContact(0))
    assertEquals(storingContacts.findContact(1), loadedContacts.findContact(1))
    assertEquals(storingContacts.findContact(2), loadedContacts.findContact(2))
}

        /*
        JSON FORMAT TESTS
         */
@Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
                // Saving an empty Contacts.json file.
                val storingContacts = ContactAPI(JSONSerializer(File("Contacts.json")))
                storingContacts.store()

                //Loading the empty Contacts.json file into a new object
                val loadedContacts = ContactAPI(JSONSerializer(File("Contacts.json")))
                loadedContacts.load()

                //Comparing the source of the Contacts (storingContacts) with the json loaded Contacts (loadedContacts)
                assertEquals(0, storingContacts.numberOfContacts())
                assertEquals(0, loadedContacts.numberOfContacts())
                assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
                }

@Test
        fun `saving and loading an loaded collection in JSON doesn't loose data`() {
                // Storing 3 Contacts to the Contacts.json file.
                val storingContacts = ContactAPI(JSONSerializer(File("Contacts.json")))
                storingContacts.add(testApp!!)
                storingContacts.add(swim!!)
                storingContacts.add(summerHoliday!!)
                storingContacts.store()

                //Loading Contacts.json into a different collection
                val loadedContacts = ContactAPI(JSONSerializer(File("Contacts.json")))
                loadedContacts.load()

                //Comparing the source of the Contacts (storingContacts) with the json loaded Contacts (loadedContacts)
                assertEquals(3, storingContacts.numberOfContacts())
                assertEquals(3, loadedContacts.numberOfContacts())
                assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
                assertEquals(storingContacts.findContact(0), loadedContacts.findContact(0))
                assertEquals(storingContacts.findContact(1), loadedContacts.findContact(1))
                assertEquals(storingContacts.findContact(2), loadedContacts.findContact(2))
                }

        /*
        CBOR FORMAT TESTS
         */

@Test
        fun `saving and loading an empty collection in CBOR doesn't crash app`() {
                // Saving an empty Contacts.CBOR file.
                val storingContacts = ContactAPI(CBORSerializer(File("Contacts.cbor")))
                storingContacts.store()

                //Loading the empty Contacts.cbor file into a new object
                val loadedContacts = ContactAPI(CBORSerializer(File("Contacts.cbor")))
                loadedContacts.load()

                //Comparing the source of the Contacts (storingContacts) with the CBOR loaded Contacts (loadedContacts)
                assertEquals(0, storingContacts.numberOfContacts())
                assertEquals(0, loadedContacts.numberOfContacts())
                assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
                }

@Test
        fun `saving and loading an loaded collection in CBOR doesn't loose data`() {
                // Storing 3 Contacts to the Contacts.CBOR file.
                val storingContacts = ContactAPI(CBORSerializer(File("Contacts.cbor")))
                storingContacts.add(testApp!!)
                storingContacts.add(swim!!)
                storingContacts.add(summerHoliday!!)
                storingContacts.store()

                //Loading Contacts.xml into a different collection
                val loadedContacts = ContactAPI(CBORSerializer(File("Contacts.cbor")))
                loadedContacts.load()

                //Comparing the source of the Contacts (storingContacts) with the XML loaded Contacts (loadedContacts)
                assertEquals(3, storingContacts.numberOfContacts())
                assertEquals(3, loadedContacts.numberOfContacts())
                assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
                assertEquals(storingContacts.findContact(0), loadedContacts.findContact(0))
                assertEquals(storingContacts.findContact(1), loadedContacts.findContact(1))
                assertEquals(storingContacts.findContact(2), loadedContacts.findContact(2))
                }
                }

@Nested
    inner class ArchiveContactTests {
    @Test
    fun `archive Contact by index`(){
        val archiveContacts = ContactAPI(JSONSerializer(File("Contacts.json")))
        archiveContacts.add(testApp!!)
        archiveContacts.add(swim!!)
        archiveContacts.add(summerHoliday!!)

        assertEquals(true, archiveContacts.archiveContactByIndex(0))
        assertEquals(true, archiveContacts.archiveContactByIndex(1))
        assertEquals(true, archiveContacts.archiveContactByIndex(2))
    }
}


}
