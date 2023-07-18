package ir.m3hdi.agahinet.domain.model

import androidx.annotation.Keep

@Keep
enum class SigninState {
    BAD_EMAIL,BAD_PASSWORD,INCORRECT_CREDS,OK
}
@Keep
enum class SignupState {
    BAD_NAME,BAD_EMAIL,BAD_PASSWORD,BAD_PHONE_NUMBER,DUPLICATE_EMAIL,OK
}