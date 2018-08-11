package br.uff.mvpcortes.prajuda

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrajudaApplicationTest {

	@Test
	fun contextLoads() {
	}

}
