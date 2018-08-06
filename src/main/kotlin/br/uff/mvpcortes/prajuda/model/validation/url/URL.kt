package br.uff.mvpcortes.prajuda.model.validation.url

import org.springframework.util.ResourceUtils
import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Documented
@Constraint(validatedBy = [URLValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class URL(val message:String="String \${validatedValue} is not a valid URL"){}

class URLValidator: ConstraintValidator<URL, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value!!
        context!!

        return (ResourceUtils.isUrl(value))
    }
}