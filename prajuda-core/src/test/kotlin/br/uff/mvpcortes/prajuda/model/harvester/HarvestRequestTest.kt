package br.uff.mvpcortes.prajuda.model.harvester

import br.uff.mvpcortes.prajuda.model.HarvesterStatus
import br.uff.mvpcortes.prajuda.model.fixture.HarvesterRequestFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("A HarvestRequest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class HarvestRequestTest{

    @Nested
    inner  class `open`{
        val harvesterRequest=HarvesterRequestFixture.open()

        @Test
        fun `then it should have status OPEN`(){
            assertThat(harvesterRequest.harvesterStatus).isEqualTo(HarvesterStatus.OPEN)
        }

        @Nested
        inner class `then can start request`(){
            val started = harvesterRequest.toStarted()

            @Test
            fun `and status is PROCESSING`(){
                assertThat(started.harvesterStatus).isEqualTo(HarvesterStatus.PROCESSING)
            }

            @Test
            fun `and startedAt is not null`() {
                assertThat(started.startedAt).isNotNull()
            }

            @Test
            fun `and createAt is before or equal to startedAt`(){
                assertThat(started.createAt).isBeforeOrEqualTo(started.startedAt)
            }

            @Test
            fun `and completedAt is null`(){
                assertThat(started.completedAt).isNull()
            }
        }

        @Test
        fun `then cannot complete request`(){
            assertThatIllegalStateException().isThrownBy {
                harvesterRequest.toCompleted()
            }
                .withMessage("Cannot complete a not started harvester request (${harvesterRequest.harvesterStatus})")

        }
    }

    @Nested
    inner  class `processing`{
        val harvesterRequest=HarvesterRequestFixture.started()

        @Test
        fun `then cannot start request`(){
            assertThatIllegalStateException().isThrownBy {
                harvesterRequest.toStarted()
            }
                    .withMessage("Cannot start a not opening harvester request (${harvesterRequest.harvesterStatus})")
        }

        @Nested
        inner class `then can complete request`{
            val completed = harvesterRequest.toCompleted()

            @Test
            fun `and status is PROCESSING`(){
                assertThat(completed.harvesterStatus).isEqualTo(HarvesterStatus.COMPLETE)
            }

            @Test
            fun `and startedAt is not null`() {
                assertThat(completed.startedAt).isNotNull()
            }

            @Test
            fun `and createAt is before or equal to startedAt`(){
                assertThat(completed.createAt).isBeforeOrEqualTo(completed.startedAt)
            }

            @Test
            fun `and completedAt is not null`(){
                assertThat(completed.completedAt).isNotNull()
            }

            @Test
            fun `and startedAt is before or equal to completedAt`(){
                assertThat(completed.startedAt).isBeforeOrEqualTo(completed.completedAt)
            }
        }
    }

}