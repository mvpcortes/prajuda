//package br.uff.mvpcortes.prajuda.harvester.manager_old
//
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.reactivestreams.Subscriber
//import org.reactivestreams.Subscription
//import reactor.core.publisher.Flux
//import reactor.core.scheduler.Schedulers
//
//@DisplayName("When a GenericHarvesterOrchestrator ")
//internal class GenericHarvesterOrchestratorTest{
//
//    private class StringService(val str:String)
//
//    private val receiveList=ArrayList<StringService>()
//
//    private class TestConsumerOrchestrator(provider:()->Subscriber<StringService>):
//            GenericHarvesterOrchestrator<String, StringService, TestConsumerOrchestrator>
//            (
//                    TestConsumerOrchestrator::class,
//                    {10},
//                    {Schedulers.immediate()},
//                    provider
//            )
//
//    @Test
//    fun `does not have flux and doesnt have service then not send to subscriber`(){
//        val orchestrator = TestConsumerOrchestrator({TestSubscriber({receiveList.add(it)})})
//
//        orchestrator.consumer(emptyList(), {STR_N_SERVICE_ID->Flux.empty()})
//
//        assertThat(receiveList).isEmpty()
//    }
//
//   private class TestSubscriber(val consumer:(ss:StringService) ->Unit) : Subscriber<StringService>{
//        override fun onComplete() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onSubscribe(p0: Subscription?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onNext(ss: StringService?) = consumer(ss!!)
//
//        override fun onError(p0: Throwable?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }
//}