package br.uff.mvpcortes.prajuda.model.validation

import org.springframework.util.ResourceUtils
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [RelativePathValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class RelativePath (
        val message  :String="{br.uff.mvpcortes.prajuda.model.validation.RelativePath.message}",
        val groups   :Array<KClass<out Any>> = [],
        val payload  :Array<KClass<out Any>> = []
)

class RelativePathValidator: ConstraintValidator<RelativePath, String> {

    companion object {
        const val STR_REGEX_VALID_PATH = "([\\w\\d_]+)(\\/[\\w\\d_]+)*\\/?"
        val REGEX_VALID_PATH = Regex(STR_REGEX_VALID_PATH)

        inline fun isValid(str:String)=REGEX_VALID_PATH.matches(str)
    }

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        return isValid(value)
    }
}