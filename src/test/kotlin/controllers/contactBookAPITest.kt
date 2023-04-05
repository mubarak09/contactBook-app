package controllers

import models.Contact
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import persistence.XMLSerializer
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class contactBookAPITest {

    private var learnKotlin: Contact? = null
    private var summerHoliday: Contact? = null
    private var codeApp: Contact? = null
    private var testApp: Contact? = null
    private var swim: Contact? = null
    private var populatedContacts: contactBookAPI? = contactBookAPI(XMLSerializer(File("contacts.xml")))
    private var emptyContacts: contactBookAPI? = contactBookAPI(XMLSerializer(File("contacts.xml")))

    @BeforeEach
    fun setup(){
        learnKotlin = Contact("Learning Kotlin", 5, "College", false, "0")
        summerHoliday = Contact("Summer Holiday to France", 1, "Holiday", false, "0")
        codeApp = Contact("Code App", 4, "Work", false, "0")
        testApp = Contact("Test App", 4, "Work", false, "0")
        swim = Contact("Swim - Pool", 3, "Hobby", false, "0")

        //adding 5 Note to the notes api
        populatedContacts!!.add(learnKotlin!!)
        populatedContacts!!.add(summerHoliday!!)
        populatedContacts!!.add(codeApp!!)
        populatedContacts!!.add(testApp!!)
        populatedContacts!!.add(swim!!)
    }

    @AfterEach
    fun tearDown(){
        learnKotlin = null
        summerHoliday = null
        codeApp = null
        testApp = null
        swim = null
        populatedContacts = null
        emptyContacts = null
    }

    @Test
    fun `adding a Contact to a populated list adds to ArrayList`(){
        val newContact = Contact("\"Study Lambdas\", 1, \"College\", false", 4, "College",false, "1")
        assertEquals(5, populatedContacts!!.numberOfContacts())
        assertTrue(populatedContacts!!.add(newContact))
        assertEquals(6, populatedContacts!!.numberOfContacts())
        assertEquals(newContact, populatedContacts!!.findContact(populatedContacts!!.numberOfContacts() - 1))
    }

    @Test
    fun `adding a Contact to an empty list adds to ArrayList`(){
        val newContact = Contact("\"Study Lambdas\", 1, \"College\", false", 4, "College",false, "1")
        assertEquals(0, emptyContacts!!.numberOfContacts())
        assertTrue(emptyContacts!!.add(newContact))
        assertEquals(1, emptyContacts!!.numberOfContacts())
        assertEquals(newContact, emptyContacts!!.findContact(emptyContacts!!.numberOfContacts() - 1))
    }

    @Test
    fun `listAllContact returns No Contacts Stored message when ArrayList is empty`() {
        assertEquals(0, emptyContacts!!.numberOfContacts())
        assertTrue(emptyContacts!!.listAllContacts().lowercase().contains("no notes"))
    }

    @Test
    fun `listAllContacts returns Contacts when ArrayList has notes stored`() {
        assertEquals(5, populatedContacts!!.numberOfContacts())
        val contactString = populatedContacts!!.listAllContacts().lowercase()
        assertTrue(contactString.contains("learning kotlin"))
        assertTrue(contactString.contains("code app"))
        assertTrue(contactString.contains("test app"))
        assertTrue(contactString.contains("swim"))
        assertTrue(contactString.contains("summer holiday"))
    }
    @Nested
    inner class DeleteNotes {

        @Test
        fun `deleting a Contact that does not exist, returns null`() {
            assertNull(emptyContacts!!.deleteContact(0))
            assertNull(populatedContacts!!.deleteContact(-1))
            assertNull(populatedContacts!!.deleteContact(5))
        }

        @Test
        fun `deleting a contact that exists delete and returns deleted object`() {
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
        fun `updating a contact that does not exist returns false`(){
            assertFalse(populatedContacts!!.updateContact(6, Contact("Updating Contact", 2, "Work", false, "2")))
            assertFalse(populatedContacts!!.updateContact(-1, Contact("Updating Contact", 2, "Work", false, "2")))
            assertFalse(emptyContacts!!.updateContact(0, Contact("Updating Contact", 2, "Work", false, "2")))
        }

        @Test
        fun `updating a contact that exists returns true and updates`() {
            //check contact 5 exists and check the contents
            assertEquals(swim, populatedContacts!!.findContact(4))
            assertEquals("Swim - Pool", populatedContacts!!.findContact(4)!!.contactTitle)
            assertEquals(3, populatedContacts!!.findContact(4)!!.contactPriority)
            assertEquals("Hobby", populatedContacts!!.findContact(4)!!.contactCategory)

            //update contact 5 with new information and ensure contents updated successfully
            assertTrue(populatedContacts!!.updateContact(4, Contact("Updating Contact", 2, "College", false, "2")))
            assertEquals("Updating Contact", populatedContacts!!.findContact(4)!!.contactTitle)
            assertEquals(2, populatedContacts!!.findContact(4)!!.contactPriority)
            assertEquals("College", populatedContacts!!.findContact(4)!!.contactCategory)
        }
    }

    @Nested
    inner class PersistenceTests {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            // Saving an empty contacts.XML file.
            val storingContacts = contactBookAPI(XMLSerializer(File("contacts.xml")))
            storingContacts.store()

            //Loading the empty contacts.xml file into a new object
            val loadedContacts = contactBookAPI(XMLSerializer(File("contacts.xml")))
            loadedContacts.load()

            //Comparing the source of the contacts (storingContacts) with the XML loaded contacts (loadedContacts)
            assertEquals(0, storingContacts.numberOfContacts())
            assertEquals(0, loadedContacts.numberOfContacts())
            assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
        }

        @Test
        fun `saving and loading an loaded collection in XML doesn't loose data`() {
            // Storing 3 contacts to the contacts.XML file.
            val storingContacts = contactBookAPI(XMLSerializer(File("contacts.xml")))
            storingContacts.add(testApp!!)
            storingContacts.add(swim!!)
            storingContacts.add(summerHoliday!!)
            storingContacts.store()

            //Loading contacts.xml into a different collection
            val loadedContacts = contactBookAPI(XMLSerializer(File("contacts.xml")))
            loadedContacts.load()

            //Comparing the source of the contacts (storingContacts) with the XML loaded contacts (loadedContacts)
            assertEquals(3, storingContacts.numberOfContacts())
            assertEquals(3, loadedContacts.numberOfContacts())
            assertEquals(storingContacts.numberOfContacts(), loadedContacts.numberOfContacts())
            assertEquals(storingContacts.findContact(0), loadedContacts.findContact(0))
            assertEquals(storingContacts.findContact(1), loadedContacts.findContact(1))
            assertEquals(storingContacts.findContact(2), loadedContacts.findContact(2))
        }
    }



}