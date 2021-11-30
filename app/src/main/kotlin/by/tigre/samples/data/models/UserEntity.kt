package by.tigre.samples.data.models

data class UserEntity(
    val id: UserId,
    val wight: Float,
    val birthday: Long,
    val photoUrl: String?
) {
    companion object {
        fun from(new: NewUserEntity, id: UserId) =
            UserEntity(id, new.wight, new.birthday, new.photoUrl)
    }
}
