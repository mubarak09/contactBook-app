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
        contact = serializer.read() as ArrayList<Contact>
    }

    //Store contacts
    @Throws(Exception::class)
    fun store(){
        serializer.write(contacts)
    }

    fun add(contact: Contact): Boolean {
        return contact.add(contact)
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

    // method for listing all the Archived contacts
    /*
    * First check if the contact arraylist is empty or not; if so return No contacts stored
    * Then loop through the ArrayList also checking the iscontactArchived variable
    * If iscontactArchived is true then add that contact to the list that will be returned.
    * */
    fun listArchivedcontacts(): String =
        if(numberOfArchivedcontacts() == 0) "No archived contacts stored"
        else formatListString(contacts.filter { contact -> contact.iscontactArchived })

    fun numberOfArchivedcontacts(): Int = contacts.count {contact : contact -> contact.iscontactArchived}

    fun numberOfActivecontacts(): Int = contacts.count{ contact: contact -> !contact.iscontactArchived}


    fun searchByTitle(searchString: String) =
        formatListString(contacts.filter { contact -> contact.contactTitle.contains(searchString, ignoreCase = true) })

    fun listcontactsBySelectedPriority(priority: Int): String =
        if (contacts.isEmpty()) "No contacts stored"
        else {
            val listOfcontacts = formatListString(contacts.filter { contact -> contact.contactPriority == priority })
            if (listOfcontacts.equals("")) "No contacts stored with priority: $priority"
            else "${numberOfcontactsByPriority(priority)} contacts with priority $priority: $listOfcontacts\""
        }

    fun numberOfcontactsByPriority(priority: Int): Int =
        if (contacts.isEmpty()) 0
        else contacts.count { it.contactPriority ==  priority}

    // Delete a contact from the collection of contacts, while also checking if the index is valid
    fun deletecontact(indexToDelete: Int): contact? {
        return if (isValidListIndex(indexToDelete, contacts)) {
            contacts.removeAt(indexToDelete)
        } else null
    }

    /*
    Update a contact by index
    Index of the contact to be updated is passed
    And the new contact is also passed which will be stored in the same index
     */
    fun updatecontact(indexToUpdate: Int, contact: contact?): Boolean {
        //find the contact object by the index number
        val foundcontact = findcontact(indexToUpdate)

        //if the contact exists, use the contact details passed as parameters to update the found contact in the ArrayList.
        if ((foundcontact != null) && (contact != null)) {
            foundcontact.contactTitle = contact.contactTitle
            foundcontact.contactPriority = contact.contactPriority
            foundcontact.contactCategory = contact.contactCategory
            return true
        }

        //if the contact was not found, return false, indicating that the update was not successful
        return false
    }

    /*
    Archive a contact
    get index of contact passed, get contact by the index and set iscontactArchived = true
     */
    fun archivecontactByIndex(indexToArchive: Int): Boolean{
        val contactToArchive = findcontact(indexToArchive)
        if (contactToArchive != null) {
            contactToArchive.iscontactArchived = true
            return true
        }
        return false
    }

    fun sortcontactByDate(): String {
        val sortedcontacts = contacts.sortedByDescending { it.contactTimeStamp }
        return formatListString(sortedcontacts)
    }



    /**
     * Returns a string representation of all contacts filtered by month.
     */
    fun listcontactsByMonth(month: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val filteredcontacts = contacts.filter { contact ->
            LocalDateTime.parse(contact.contactTimeStamp, formatter).month.toString().equals(month, ignoreCase = true)
        }
        if (contacts.isEmpty()) {
            return "No contacts stored"
        }
        if (filteredcontacts.isEmpty()) {
            return "There are no contacts available for the specified month"
        }
        val stringBuilder = StringBuilder()
        filteredcontacts.forEachIndexed { index, contact ->
            stringBuilder.append("$index : $contact")
        }
        return stringBuilder.toString()
    }

    /**
     * Returns a string representation of contacts sorted by year for a given year.
     */
    fun listcontactsByYear(year: Int): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        // Check if there are any contacts available
        if (contacts.isEmpty()) {
            return "No contacts stored"
        }
        // Filter contacts by year using the filter function with a lambda expression
        val filteredcontacts = contacts.filter { contact ->
            LocalDateTime.parse(contact.contactTimeStamp, formatter).year == year
        }
        // Check if there are any contacts available for the specified year
        if (filteredcontacts.isEmpty()) {
            return "There are no contacts available for the specified year"
        }
        // Sort filtered contacts by year using the sortedBy function with a lambda expression
        val sortedcontacts = filteredcontacts.sortedBy { contact ->
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