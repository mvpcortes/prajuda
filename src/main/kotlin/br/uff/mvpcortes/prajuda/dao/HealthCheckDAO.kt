package br.uff.mvpcortes.prajuda.dao

interface HealthCheckDAO {

    enum class StatusCheck{
        UP,
        DOWN
    }
    fun healthCheck():StatusCheck
}