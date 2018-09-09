package br.uff.mvpcortes.prajuda.model.validation

import org.springframework.util.ResourceUtils
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [URLValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class URL(
        val message  :String="{br.uff.mvpcortes.prajuda.model.validation.URL.message}",
        val groups   :Array<KClass<out Any>> = [],
        val payload  :Array<KClass<out Any>> = []
)

class URLValidator: ConstraintValidator<URL, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        return (ResourceUtils.isUrl(value))
    }
}