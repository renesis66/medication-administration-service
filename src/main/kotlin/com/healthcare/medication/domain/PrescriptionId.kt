package com.healthcare.medication.domain

import java.util.UUID

@JvmInline
value class PrescriptionId(val value: String) {
    companion object {
        fun generate(): PrescriptionId = PrescriptionId(UUID.randomUUID().toString())
        fun from(value: String): PrescriptionId = PrescriptionId(value)
    }
    
    override fun toString(): String = value
}