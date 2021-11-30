package by.tigre.samples.presentation.users

import by.tigre.samples.data.bisness.WightSystemTypeSettings
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.data.platform.Resources

data class UserViewData(
    val entity: UserEntity,
    val wightLabel: String,
    val birthdayLabel: String,
    val photoUrl: String?
) {
    companion object {
        private val format = "%.1f %s"

        fun from(entity: UserEntity, resources: Resources, wightType: WightSystemTypeSettings.Type) = UserViewData(
            entity = entity,
            wightLabel = if (entity.wight > 0f) {
                format.format(
                    entity.wight * wightType.factor,
                    resources.string(wightType.title)
                )
            } else {
                "-"
            },
            birthdayLabel = if (entity.birthday > 0) resources.formatDate(entity.birthday) else "-",
            photoUrl = entity.photoUrl
        )
    }
}
