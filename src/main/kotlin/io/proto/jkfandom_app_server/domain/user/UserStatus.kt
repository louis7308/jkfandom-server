package io.proto.jkfandom_app_server.domain.user

enum class UserStatus(val description: String) {
    PROVISIONAL("가입 진행 중(온보딩/검증 미완료)"),
    ACTIVE("정상 이용 가능"),
    BANNED("차단"),
    DELETED("탈퇴")
}