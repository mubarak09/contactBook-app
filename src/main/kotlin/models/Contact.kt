package models

import kotlinx.serialization.Serializable

@Serializable
data class Contact (var contactTitle: String,
                    var contactPriority: Int,
                    var contactCategory: String,
                    var iscontactArchived :Boolean,
                    val contactTimeStamp : String
){
    
}