package com.cduffaut.swiftycompanion.model

import com.google.gson.annotations.SerializedName

// class pour se calquer a la struct json renvoyée par l'API
data class User(
    val id: Int,
    val login: String,
    val email: String,
    val image: ImageData?,
    val wallet: Int,
    @SerializedName("correction_point") val correctionPoint: Int,
    val location: String?,
    val phone: String?,
    @SerializedName("cursus_users") val cursusUsers: List<CursusUser>? = emptyList(),
    @SerializedName("projects_users") val projectsUsers: List<ProjectUser>? = emptyList()
) {
    // methodes pour acceder simplement aux donnees
    fun getLevel(): Double {
        return cursusUsers?.lastOrNull()?.level ?: 0.0
    }

    fun getSkills(): List<Skill> {
        return cursusUsers?.lastOrNull()?.skills ?: emptyList()
    }

    fun getProjects(): List<ProjectUser> {
        return projectsUsers ?: emptyList()
    }
}

// pp
data class ImageData(
    val link: String,
    val versions: ImageVersions?
)

// pp de differentes tailles
data class ImageVersions(
    val large: String?,
    val medium: String?,
    val small: String?,
    val micro: String?
)

data class CursusUser(
    val id: Int,
    @SerializedName("begin_at") val beginAt: String?,
    @SerializedName("end_at") val endAt: String?,
    val level: Double,
    val grade: String?,
    val skills: List<Skill>? = emptyList(),
    @SerializedName("cursus_id") val cursusId: Int
)

data class Skill(
    val id: Int,
    val name: String,
    val level: Double
)

// etat du projet
data class ProjectUser(
    val id: Int,
    @SerializedName("final_mark") val finalMark: Int?,
    val status: String,
    @SerializedName("validated?") val validated: Boolean?,
    val project: Project
)

// details du projet
data class Project(
    val id: Int,
    val name: String,
    // slug: nom simplifié du projet
    val slug: String,
    @SerializedName("parent_id") val parentId: Int?
)