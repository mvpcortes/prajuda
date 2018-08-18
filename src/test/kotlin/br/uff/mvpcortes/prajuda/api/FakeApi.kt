package br.uff.mvpcortes.prajuda.api

import br.uff.mvpcortes.prajuda.api.dto.ErrorMessage
import br.uff.mvpcortes.prajuda.api.dto.toErrorMessages
import br.uff.mvpcortes.prajuda.loggerFor
import org.reactivestreams.Publisher
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import kotlin.math.abs
import kotlin.streams.asStream

@RestController
@RequestMapping("fake")
class FakeApi{

    val logger = loggerFor(FakeApi::class)
    /**
     * @see problem with bean-validation: http://blog.caelum.com.br/bean-validation-no-kotlin/
     */
    data class FakeData(val id:Long,
                        @field:NotEmpty val fieldString:String="field",
                        @field:Min(0) val fieldNumber:Long=1,
                        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) val fieldDate:LocalDate=LocalDate.now()){
        companion object {
            val fomatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            fun withId(id:Long)= FakeData(id, "string $id", abs(id)+100, LocalDate.of(2018, 1, 1).plusDays(id))
            fun empty()= FakeData(id=0, fieldString = "", fieldNumber = 0, fieldDate = LocalDate.now())
        }

        fun getStrDate():String= fomatter.format(fieldDate)
    }

    data class FakeResponse(val id:String)

    @PostMapping()
    fun defaultPost(@Valid value:FakeData, bindingResult: BindingResult): Mono<ResponseEntity<Any>> {
        logger.info("Try save fakedata")
        return bindingResult.toErrorMessages()
                ?.let{ResponseEntity.badRequest().body(it) as ResponseEntity<Any>}
                ?.let{Mono.fromSupplier{ it } }
                ?: Mono.fromSupplier {  ResponseEntity.ok().body(FakeResponse((Random().nextInt(101)+1).toString())) as ResponseEntity<Any> }
    }

}