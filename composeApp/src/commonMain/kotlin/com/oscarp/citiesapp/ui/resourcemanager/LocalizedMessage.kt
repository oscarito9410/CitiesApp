package com.oscarp.citiesapp.ui.resourcemanager

import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.city_not_found
import citiesapp.composeapp.generated.resources.failed_to_update_favorite_status
import org.jetbrains.compose.resources.StringResource

/**
 * Represents a localized message that can be displayed to the user.
 * Each message directly holds its associated StringResource ID.
 */
sealed class LocalizedMessage(val resource: StringResource) {
    object FailedToUpdateFavoriteStatus : LocalizedMessage(
        resource = Res.string.failed_to_update_favorite_status
    )

    object CityNotFound : LocalizedMessage(
        resource = Res.string.city_not_found
    )
}
