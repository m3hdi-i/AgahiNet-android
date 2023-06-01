package ir.m3hdi.agahinet.domain.model

sealed class NetworkException : Exception() {
    class NetworkError : NetworkException()
    class NoNetwork : NetworkException()
}