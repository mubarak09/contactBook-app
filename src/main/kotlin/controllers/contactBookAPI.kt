package controllers

import models.Contact
import persistence.Serializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
class contactBookAPI(serializerType: Serializer) {
    private var serializer: Serializer = serializerType

    //ArrayList for contacts

    private var contacts = ArrayList<Contact>()

    //Helper Func for formatting contacts
    private fun formatListString(contactToFormat : List<Contact>) : String=
        contactToFormat
            .joinToString (separator = "\n") {contact ->
                contacts.indexOf(contact).toString() + ": " + contact.toString()
            }

    //Load contacts
    @Throws(Exception::class)
    fun load() {
        contacts = serializer.read() as ArrayList<Contact>
    }

    //Store contacts
    @Throws(Exception::class)
    fun store(){
        serializer.write(contacts)
    }

    fun add(contact: Contact): Boolean {
        return contacts.add(contact)
    }

    fun listAllContacts(): String =
        if (contacts.isEmpty()) "no contacts stored"
    else formatListString(contacts)

    fun numberOfContacts(): Int {
        return contacts.size
    }

    fun findContact(index: Int): Contact? {
        return if (isValidListIndex(index, contacts)){
            contacts[index]
        }else null
    }
    // method to check if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    /*
    function that will return true if the index passed to it is valid in the Contacts collection
     */
    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, contacts);
    }

    /*
    function for listing all the active Contacts

    * First check if the Contact arraylist is empty or not
    * Then loop through the ArrayList also checking the isContactArchived variable
    * If isContactArchived is false then add that Contact to the list that will be returned.
    * */
    fun listActiveContacts(): String =
        if(numberOfActiveContacts() == 0) "No active Contacts stored"
        else formatListString(contacts.filter { Contact -> !Contact.isContactArchived })


    // method for listing all the Archived contacts
    /*
    * First check if the contact arraylist is empty or not; if so return No contacts stored
    * Then loop through the ArrayList also checking the iscontactArchived variable
    * If iscontactArchived is true then add that contact to the list that will be returned.
    * */
    fun listArchivedContacts(): String =
        if(numberOfArchivedContacts() == 0) "No archived contacts stored"
        else formatListString(contacts.filter { contact -> contact.isContactArchived })

    fun numberOfArchivedContacts(): Int = contacts.count {contact : Contact -> contact.isContactArchived}

    fun numberOfActiveContacts(): Int = contacts.count{ contact: Contact -> !contact.isContactArchived}


    fun searchByTitle(searchString: String) =
        formatListString(contacts.filter { contact -> contact.contactTitle.contains(searchString, ignoreCase = true) })

    fun listContactsBySelectedPriority(priority: Int): String =
        if (contacts.isEmpty()) "No contacts stored"
        else {
            val listOfcontacts = formatListString(contacts.filter { contact -> contact.contactPriority == priority })
            if (listOfcontacts.equals("")) "No contacts stored with priority: $priority"
            else "${numberOfContactsByPriority(priority)} contacts with priority $priority: $listOfcontacts\""
        }

    fun numberOfContactsByPriority(priority: Int): Int =
        if (contacts.isEmpty()) 0
        else contacts.count { it.contactPriority ==  priority}

    // Delete a contact from the collection of contacts, while also checking if the index is valid
    fun deleteContact(indexToDelete: Int): Contact? {
        return if (isValidListIndex(indexToDelete, contacts)) {
            contacts.removeAt(indexToDelete)
        } else null
    }

    /*
    Update a contact by index
    Index of the contact to be updated is passed
    And the new contact is also passed which will be stored in the same index
     */
    fun updateContact(indexToUpdate: Int, contact: Contact?): Boolean {
        //find the contact object by the index number
        val foundContact = findContact(indexToUpdate)

        //if the contact exists, use the contact details passed as parameters to update the found contact in the ArrayList.
        if ((foundContact != null) && (contact != null)) {
            foundContact.contactTitle = contact.contactTitle
            foundContact.contactPriority = contact.contactPriority
            foundContact.contactCategory = contact.contactCategory
            return true
        }

        //if the contact was not found, return false, indicating that the update was not successful
        return false
    }

    /*
    Archive a contact
    get index of contact passed, get contact by the index and set iscontactArchived = true
     */
    fun archiveContactByIndex(indexToArchive: Int): Boolean{
        val contactToArchive = findContact(indexToArchive)
        if (contactToArchive != null) {
            contactToArchive.isContactArchived = true
            return true
        }
        return false
    }

    fun sortContactByDate(): String {
        val sortedContacts = contacts.sortedByDescending { it.contactTimeStamp }
        return formatListString(sortedContacts)
    }



    /**
     * Returns a string representation of all contacts filtered by month.
     */
    fun listContactsByMonth(month: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val filteredContacts = contacts.filter { contact ->
            LocalDateTime.parse(contact.contactTimeStamp, formatter).month.toString().equals(month, ignoreCase = true)
        }
        if (contacts.isEmpty()) {
            return "No contacts stored"
        }
        if (filteredContacts.isEmpty()) {
            return "There are no contacts available for the specified month"
        }
        val stringBuilder = StringBuilder()
        filteredContacts.forEachIndexed { index, contact ->
            stringBuilder.append("$index : $contact")
        }
        return stringBuilder.toString()
    }

    /**
     * Returns a string representation of contacts sorted by year for a given year.
     */
    fun listContactsByYear(year: Int): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        // Check if there are any contacts available
        if (contacts.isEmpty()) {
            return "No contacts stored"
        }
        // Filter contacts by year using the filter function with a lambda expression
        val filteredContacts = contacts.filter { contact ->
            LocalDateTime.parse(contact.contactTimeStamp, formatter).year == year
        }
        // Check if there are any contacts available for the specified year
        if (filteredContacts.isEmpty()) {
            return "There are no contacts available for the specified year"
        }
        // Sort filtered contacts by year using the sortedBy function with a lambda expression
        val sortedcontacts = filteredContacts.sortedBy { contact ->
            LocalDateTime.parse(contact.contactTimeStamp, formatter).year
        }
        // Build a string representation of the sorted contacts using a StringBuilder
        val stringBuilder = StringBuilder()
        sortedcontacts.forEachIndexed { index, contact ->
            stringBuilder.append("$index : $contact")
        }
        // Return the string representation of the sorted contacts
        return stringBuilder.toString()
    }

}