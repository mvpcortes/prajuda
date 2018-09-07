package br.uff.mvpcortes.prajuda.model.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(value=[SpringExtension::class])
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class URLValidatorTest{

    @Autowired
    private val validator: javax.validation.Validator? = null

    class ValidatedClass(@field:URL val url:String)

    @Test
    fun `when url is valid then validation is ok`(){
        val validatedClass = ValidatedClass("https://gitlab.com/mvpcortes/prajuda")
        val set = validator!!.validate(validatedClass)
        assertThat(set).isEmpty()
    }

    @Test
    fun `when url is in valid then validation then get error`(){
        val validatedClass = ValidatedClass("a sadpoiasdPIU ADS piu sd OUIP ADSoiu asdOI ")
        val set = validator!!.validate(validatedClass)
        assertThat(set).hasSize(1)

        val value = set.iterator().next()
        assertThat(value.message).isEqualTo("Is not a valid URL")
    }
}