package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.HealthCheckDAO
import org.springframework.stereotype.Service

@Service
class HealthCheckHighApiDAO (val clientManager:ClientManager): HealthCheckDAO {
    override fun healthCheck(): HealthCheckDAO.StatusCheck =
            try {
                clientManager.getClient {
                    when (it.ping()) {
                        true -> HealthCheckDAO.StatusCheck.UP
                        false -> HealthCheckDAO.StatusCheck.DOWN
                    }
                }
            }catch(e:Exception){
                HealthCheckDAO.StatusCheck.DOWN
            }
}