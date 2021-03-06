package br.uff.mvpcortes.prajuda.api.dto

import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import reactor.core.publisher.Mono

data class ErrorMessage (val field:String, val message:String, var invalidValue:String?){

    constructor(field: FieldError):this(
            field.field,
            field.defaultMessage?:"n/a",
            field.rejectedValue.toString()
            )

    constructor(field: ObjectError):this(
            field.objectName,
            field.defaultMessage?:"n/a",
            null
    )

    companion object {
        private fun toErrorMessages(bindingResult:BindingResult):Sequence<ErrorMessage> =
                bindingResult.allErrors.asSequence()
                    .map{if(it is FieldError){
                        ErrorMessage(it)
                    }else{
                        ErrorMessage(it as ObjectError)
                    }}

        fun toErrorMessagesMap(bindingResult: BindingResult)=
                toErrorMessages(bindingResult).associate { it.field to it }

    }
}

fun BindingResult.toErrorMessages():Map<String, ErrorMessage>? =
        if(this.hasErrors()) {
            ErrorMessage.toErrorMessagesMap(this)
        }else {
            null
        }

fun <T> BindingResult.toMonoResponseEntityErrorMessages():Mono<ResponseEntity<T>> =
    ErrorMessage.toErrorMessagesMap(this)
            .let{map->Mono.fromSupplier{ResponseEntity.badRequest().body(map) as ResponseEntity<T> }}

