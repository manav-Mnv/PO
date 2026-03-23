package com.example.po.personalization

interface PersonalizationRepository {
    fun getAdaptivePrompts(): List<String>
}
