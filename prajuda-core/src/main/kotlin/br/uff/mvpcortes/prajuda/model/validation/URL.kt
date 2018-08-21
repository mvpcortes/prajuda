package br.uff.mvpcortes.prajuda.model.validation

import org.springframework.util.ResourceUtils
import java.lang.annotation.Documented
import javax.validation.Constraint
import kotlin.reflect.KClass
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Documented
@Constraint(validatedBy = [URLValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class URL(
        val message  :String="{br.uff.mvpcortes.prajuda.model.validation.URL.message}",
        val groups   :Array<KClass<out Any>> = [],
        val payload  :Array<KClass<out Any>> = []
)

class URLValidator: ConstraintValidator<URL, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value!!
        context!!

        return (ResourceUtils.isUrl(value))
    }
}