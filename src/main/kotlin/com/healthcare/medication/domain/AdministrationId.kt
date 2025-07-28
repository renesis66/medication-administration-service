package com.healthcare.medication.domain

import java.util.UUID

@JvmInline
value class AdministrationId(val value: String) {
    companion object {
        fun generate(): AdministrationId = AdministrationId(UUID.randomUUID().toString())
        fun from(value: String): AdministrationId = AdministrationId(value)
    }
    
    override fun toString(): String = value
}