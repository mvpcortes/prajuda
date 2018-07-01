package br.uff.mvpcortes.prajuda.harvester.consumer

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * This class will consume havested operations from harvesters and apply to database (elasticsearch)
 */
@Qualifier("harvestedSubscriber")
@Service
class HarvestedSaveSubscriber (val prajDocumentDAO: PrajDocumentDAO): Subscriber<Harvested> {

    private val logger = loggerFor(HarvestedSaveSubscriber::class)

    override fun onComplete() {

    }

    override fun onSubscribe(subscription: Subscription) {
        subscription.request(Long.MAX_VALUE)
    }

    override fun onNext(harvested: Harvested?) {
        harvested?.let { h->
            when (h.op) {
                HarvestedOp.DELETED -> {
                    delete(h.doc)
                }
                HarvestedOp.UPDATED -> {
                    update(h.doc)
                }
                HarvestedOp.NO_OP -> {
                    noop(h.doc)
                }
            }
        }?:logger.error("Cannot receive a null havested")
    }

    private fun noop(doc:PrajDocument) =
        logger.warn("NOOP for harvested '{}'", doc)

    private fun update(doc: PrajDocument) {
        prajDocumentDAO.save(doc)
    }

    private fun delete(doc: PrajDocument) {
        prajDocumentDAO.delete(doc)
    }

    override fun onError(t: Throwable?) = logger.error("Error on consuer havested", t)
}