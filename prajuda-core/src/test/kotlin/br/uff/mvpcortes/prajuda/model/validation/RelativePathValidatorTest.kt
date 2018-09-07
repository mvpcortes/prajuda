package br.uff.mvpcortes.prajuda.model.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value=[SpringExtension::class])
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RelativePathValidatorTest{

    @Autowired
    private val validator: javax.validation.Validator? = null

    class ValidatedClass(@field:RelativePath val path:String)

    @ParameterizedTest(name="when valid path run regex {index} with path name ''{0}'' should matches")
    @ValueSource(strings= ["main", "main/main", "a", "a/b/c", "xuxu_a/bb_c/c_c_c",
        "1main", "m3ain/m5ain", "a",  "1", "43a/b/c", "xuxu_a66/bb_c/c_c_c", "a/b/c/"])
    fun `then regex mathes`(path:String){

        assertThat(RelativePathValidator.REGEX_VALID_PATH.matches(path)).isTrue()

    }

    @ParameterizedTest(name="when valid path run validator {index} with path name ''{0}'' should be valid")
    @ValueSource(strings= ["main", "main/main", "a", "a/b/c", "xuxu_a/bb_c/c_c_c",
        "1main", "m3ain/m5ain", "a",  "1", "43a/b/c", "xuxu_a66/bb_c/c_c_c", "a/b/c/"])
    fun `then validator is valid`(path:String) {

        assertThat(
                validator!!
                .validate(ValidatedClass(path))
        )
        .isEmpty()
    }


    @ParameterizedTest(name="when invalid path run regex {index} with path name ''{0}'' should not matches")
    @ValueSource(strings= ["main iou oiu o89 ", "main/main*", "@a", "a/b/c.xpto", "876876xuxu_a/bb_c /c_c_c", "a/b/c/c.html"])
    fun `then regex not mathes`(path:String){

        assertThat(RelativePathValidator.REGEX_VALID_PATH.matches(path)).isFalse()

    }

    @ParameterizedTest(name="when valid path run validator {index} with path name ''{0}'' should not be valid")
    @ValueSource(strings= ["main iou oiu o89 ", "main/main*", "@a", "a/b/c.xpto", "876876xuxu_a/bb_c /c_c_c", "a/b/c/c.html"])
    fun `then validator is not valid`(path:String) {

        assertThat(
                validator!!
                        .validate(ValidatedClass(path))
        )
                .isNotEmpty
    }

    @Test
    fun `when path to document is valid then regex matches`(){
        assertThat(RelativePathValidator.REGEX_VALID_PATH.matches("document/service_to_document/test/main")).isTrue()
    }

    @Test
    fun `when path to document with slash is valid then regex matches`(){
        assertThat(RelativePathValidator.REGEX_VALID_PATH.matches("document/service_to_document/test/main/")).isTrue()
    }

    @Test
    fun `when path to document with extension is not valid then regex not matches`(){
        assertThat(RelativePathValidator.REGEX_VALID_PATH.matches("document/service_to_document/test/main.exe")).isFalse()
    }
}