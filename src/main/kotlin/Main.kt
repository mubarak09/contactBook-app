import controllers.contactBookAPI
import models.Contact
import mu.KotlinLogging
import persistence.CBORSerializer
import persistence.JSONSerializer
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File
import java.lang.System.exit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}


// Persistence formats
//private val contactAPI = contactAPI(XMLSerializer(File("contacts.xml")))
//private val contactAPI = contactAPI(JSONSerializer(File("contacts.json")))
private val contactAPI = contactBookAPI(CBORSerializer(File("contacts.cbor")))


fun main(args: Array<String>) {
    runMenu()
}

fun mainMenu() : Int {
    return ScannerInput.readNextInt(""" 
         > ----------------------------------
         > |        contact KEEPER APP         |
         > ----------------------------------
         > | contact MENU                      |
         > |   1) Add a contact                |
         > |   2) List contacts                |
         > |   3) Update a contact             |
         > |   4) Delete a contact             |
         > |   5) Archive a contact            |
         > |   6) Search contacts              |
         > |   20) Save contacts               |
         > |   21) Load contacts               |
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">"))
}

fun subMenu() : Int {
    return ScannerInput.readNextInt(""" 
         > ----------------------------------
         > |        LIST contactS MENU         |
         > ----------------------------------
         > | LIST contact SUB-MENU             |
         > |   1) List all contacts            |
         > |   2) List active contacts         |
         > |   3) List archived contacts       |
         > |   4) List contacts by Priority    |
         > |--------------------------------|
         > |        Extra Features          |
         > |   5) List contacts by month       |
         > |   6) List contacts by year        |
         > |   7) List contacts by newest date |
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">"))
}

fun addcontact(){
    //logger.info { "addcontact() function invoked" }
    val contactTitle = readNextLine("Enter a title for the contact: ")
    val contactPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val contactCategory = readNextLine("Enter a category for the contact: ")

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val contactTimeStamp = LocalDateTime.now().format(formatter)

    val isAdded = contactAPI.add(contact(contactTitle, contactPriority, contactCategory, false, contactTimeStamp))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}


fun listcontacts(){
    //logger.info{ "listcontacts() function invoked"}
    println(contactAPI.listAllcontacts())
}

fun updatecontact() {
    //logger.info { "updatecontacts() function invoked" }
    listcontacts()
    if (contactAPI.numberOfcontacts() > 0) {
        //only ask the user to choose the contact if contacts exist
        val indexToUpdate = readNextInt("Enter the index of the contact to update: ")
        if (contactAPI.isValidIndex(indexToUpdate)) {
            val contactTitle = readNextLine("Enter a title for the contact: ")
            val contactPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val contactCategory = readNextLine("Enter a category for the contact: ")

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            val contactTimeStamp = LocalDateTime.now().format(formatter)

            //pass the index of the contact and the new contact details to contactAPI for updating and check for success.
            if (contactAPI.updatecontact(indexToUpdate, contact(contactTitle, contactPriority, contactCategory, false, contactTimeStamp))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no contacts for this index number")
        }
    }
}

fun Searchcontacts() {
    val searchTitle = readNextLine("Enter the title to search by: ")
    val searchResults = contactAPI.searchByTitle(searchTitle)
    if(searchTitle.isEmpty()){
        println("No contacts found")
    } else {
        println(searchResults)
    }
}


fun deletecontact(){
    //logger.info { "deletecontacts() function invoked" }
    listcontacts()
    if (contactAPI.numberOfcontacts() > 0) {
        //only ask the user to choose the contact to delete if contacts exist
        val indexToDelete = readNextInt("Enter the index of the contact to delete: ")
        //pass the index of the contact to contactAPI for deleting and check for success.
        val contactToDelete = contactAPI.deletecontact(indexToDelete)
        if (contactToDelete != null) {
            println("Delete Successful! Deleted contact: ${contactToDelete.contactTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}


fun exitApp(){
    println("Exiting...bye")
    exit(0)
}


fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1  -> addcontact()
            2  -> listcontactsSubmenu()
            3  -> updatecontact()
            4  -> deletecontact()
            5  -> archivecontact()
            6 -> Searchcontacts()
            0  -> exitApp()
            20 -> save()
            21 -> load()
            else -> println("Invalid option entered: ${option}")
        }
    } while (true)
}

/*
SUB-MENU for listing contacts
3 options:
Listing all contacts that are saved within the system
Listing only active contacts
Listing archived contacts
 */
fun listcontactsSubmenu(){
    do {
        val option = subMenu()
        when (option) {
            1 -> listcontacts()
            2 -> println(contactAPI.listActivecontacts())
            3 -> println(contactAPI.listArchivedcontacts())
            4 -> println(contactAPI.listcontactsBySelectedPriority(readNextInt("Please Enter a contact Priority to List: ")))
            5 -> println(contactAPI.listcontactsByMonth(readNextLine("Please enter a month to search contacts, example 'march': ")))
            6 -> println(contactAPI.listcontactsByYear(readNextInt("Please enter a year to search contacts, example '2023': ")))
            7 -> listcontactsByDate()
            0 -> runMenu()
            else -> println("Invalid option entered: ${option}")
        }
    } while(true)
}

fun save() {
    try {
        contactAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun listcontactsByDate(){
    val contacts = contactAPI.sortcontactByDate()
    println(contacts)
}

fun load() {
    try {
        contactAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

/*
Archive a contact by index specified by user
 */

}