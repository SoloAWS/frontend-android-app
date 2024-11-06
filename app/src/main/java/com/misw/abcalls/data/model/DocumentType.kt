package com.misw.abcalls.data.model

enum class DocumentType(val displayName: String, val backendValue: String) {
    ID_CARD("Cédula de ciudadanía", "id_card"),
    PASSPORT("Pasaporte", "passport"),
    DRIVER_LICENSE("Licencia de conducción", "driver_license");

    companion object {
        val entries = values().toList()
    }
}