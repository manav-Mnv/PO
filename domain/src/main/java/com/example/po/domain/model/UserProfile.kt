package com.example.po.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val preferredPersonaId: String? = null,
    val lastInteractionTimestamp: Long = 0L
)
