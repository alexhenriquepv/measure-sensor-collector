package br.concy.demo.model.entity

import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("id_patient")
    val id: Int,

    @SerializedName("Name")
    val name: String
)