package com.healthcare.medication.domain

import java.util.UUID

@JvmInline
value class PatientId(val value: String) {
    companion object {
        fun generate(): PatientId = PatientId(UUID.randomUUID().toString())
        fun from(value: String): PatientId = PatientId(value)
    }
    
    override fun toString(): String = value
}