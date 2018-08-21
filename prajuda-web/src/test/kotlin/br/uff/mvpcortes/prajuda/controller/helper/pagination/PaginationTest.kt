package br.uff.mvpcortes.prajuda.controller.helper.pagination

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a Pagination")
class PaginationTest {

    @Test
    fun `with zero pages but current 1 then fail`() {
        Assertions.assertThatThrownBy {  Pagination(0, 1) }
                .hasMessage("Empty pagination should have no current page (0). And not 1")
    }

    @Test
    fun `with zero pages but current -1 then fail`() {
        Assertions.assertThatThrownBy {  Pagination(0, -1) }
                .hasMessage("Empty pagination should have no current page (0). And not -1")
    }

    @Test
    fun `with one page but current 0 then fail`() {
        Assertions.assertThatThrownBy {  Pagination(1, 0) }
                .hasMessage("Pagination with one page should have current page equal to 1. And not 0")
    }

    @Test
    fun `with one page but current 2 then fail`() {
        Assertions.assertThatThrownBy {  Pagination(1, 2) }
                .hasMessage("Pagination with one page should have current page equal to 1. And not 2")
    }


    fun createPages(ini:Int, end:Int, current:Int)=(ini..end).map{Page(it, current==it)}.toList()

    //    @Disabled
    abstract class AbstractPaginationTest(protected val expected:Pagination, protected val actual:Pagination){

        @Test
        fun `then has valid empty`(){
            assertThat(actual.isEmpty()).isEqualTo(expected.isEmpty())
        }

        @Test
        fun `then has valid there is prev page`(){
            assertThat(actual.prev.page!=null).isEqualTo(expected.prev.page!=null)
        }

        @Test
        fun `then prev page is enabled or disabled`(){
            assertThat(actual.prev.enable).isEqualTo(expected.prev.enable)
        }

        @Test
        fun `then next page is enabled or disabled`(){
            assertThat(actual.next.enable).isEqualTo(expected.next.enable)
        }

        @Test
        fun `then has valid prev page number`(){
            if(actual.prev.page != null) {
                assertThat(actual.prev.page!!.number).isEqualTo(expected.prev.page!!.number)
            }
        }

        @Test
        fun `then has valid prev page current`(){
            if(actual.prev.page != null) {
                assertThat(actual.prev.page!!.current).isEqualTo(expected.prev.page!!.current)
            }
        }

        @Test
        fun `then has valid there is next page`(){
            assertThat(actual.next.page != null).isEqualTo(expected.next.page != null)
        }

        @Test
        fun `then has valid next page number`(){
            if(actual.next.page != null) {
                assertThat(actual.next.page!!.number).isEqualTo(expected.next.page!!.number)
            }
        }

        @Test
        fun `then has valid next page current`(){
            if(actual.next.page != null) {
                assertThat(actual.next.page!!.current).isEqualTo(expected.next.page!!.current)
            }
        }

        @Test
        fun `then number of pages is valid`(){
            assertThat(actual.pages.size).isEqualTo(expected.pages.size)
        }


        @Test
        fun `then pages in right order`(){
            assertThat(actual.pages.map{it.number}).containsExactly(*(expected.pages.map{it.number}.toTypedArray()))
        }

        @Test
        fun `then current page is valid`(){
            assertThat(actual.pages.map{it.current}).containsExactly(*(expected.pages.map{it.current}.toTypedArray()))
        }


    }

    @Nested
    inner class `with zero pages`: AbstractPaginationTest(
            Pagination(Side(null), Side(null)),
            Pagination(0, 0)
    )


    @Nested
    inner class `with one page`: AbstractPaginationTest(
            Pagination(Side(null), Side(null), createPages(1, 1, 1)),
                    Pagination(1, 1))

    @Nested
    inner class `with two pages and first is current`: AbstractPaginationTest(
            Pagination(Side(null, false), Side(null, true),
                    createPages(1, 2, 1)),
            Pagination(2, 1)
    )

    @Nested
    inner class `with two pages and second is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, false),
                    createPages(1,2,2)),
            Pagination(2, 2)
    )

    @Nested
    inner class `with tree pages and second is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, true),
                    createPages(1, 3, 2)),
            Pagination(3, 2)
    )

    @Nested
    inner class `with four pages and first is current`: AbstractPaginationTest(
            Pagination(Side(null, false), Side(null, true),
                    createPages(1, 4, 1)),
            Pagination(4, 1)
    )

    @Nested
    inner class `with four pages and last is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, false),
                    createPages(1, 4, 4)),
            Pagination(4, 4)
    )

    @Nested
    inner class `with four pages and second is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, true),
                    createPages(1, 4, 2)),
            Pagination(4, 2)
    )

    @Nested
    inner class `with fifth pages and third is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, true),
                    createPages(1, 5, 3)),
            Pagination(5, 3)
    )

    @Nested
    inner class `with ninth pages and fifth is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, true),
                    createPages(1, 9, 5)),
            Pagination(9, 5)
    )

    @Nested
    inner class `with ninth pages and first is current`: AbstractPaginationTest(
            Pagination(Side(null, false), Side(null, true),
                    createPages(1, 9, 1)),
            Pagination(9, 1)
    )

    @Nested
    inner class `with ninth pages and last is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(null, false),
                    createPages(1, 9, 9)),
            Pagination(9, 9)
    )


    @Nested
    inner class `with ten pages and first is current`: AbstractPaginationTest(
            Pagination(Side(null, false), Side(Page(10, false), true),
                    createPages(1, 8, 1)),
            Pagination(10, 1)
    )

    @Nested
    inner class `with ten pages and second_is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(Page(10, false), true),
                    createPages(1, 8, 2)),
            Pagination(10, 2)
    )

    @Nested
    inner class `with ten pages and third_is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(Page(10, false), true),
                    createPages(1, 8, 3)),
            Pagination(10, 3)
    )


    @Nested
    inner class `with ten pages and fourth_is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(Page(10, false), true),
                    createPages(1, 8, 4)),
            Pagination(10, 4)
    )

    @Nested
    inner class `with ten pages and fifth is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(Page(10, false), true),
                    createPages(1, 8, 5)),
            Pagination(10, 5)
    )

    @Nested
    inner class `with ten pages and sixth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(3, 10, 6)),
            Pagination(10, 6)
    )

    @Nested
    inner class `with ten pages and seventh is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(3, 10, 7)),
            Pagination(10, 7)
    )

    @Nested
    inner class `with ten pages and eigth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(3, 10, 8)),
            Pagination(10, 8)
    )

    @Nested
    inner class `with ten pages and nineth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(3, 10, 9)),
            Pagination(10, 9)
    )

    @Nested
    inner class `with ten pages and last is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, false),
                    createPages(3, 10, 10)),
            Pagination(10, 10)
    )

    @Nested
    inner class `with twenty pages and first is current`: AbstractPaginationTest(
            Pagination(Side(null, false), Side(Page(20, false), true),
                    createPages(1, 8, 1)),
            Pagination(20, 1)
    )

    @Nested
    inner class `with twenty pages and fifth is current`: AbstractPaginationTest(
            Pagination(Side(null, true), Side(Page(20, false), true),
                    createPages(1, 8, 5)),
            Pagination(20, 5)
    )

    @Nested
    inner class `with twenty pages and sixth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(3, 9, 6)),
            Pagination(20, 6)
    )

    @Nested
    inner class `with twenty pages and seventh is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(4, 10, 7)),
            Pagination(20, 7)
    )

    @Nested
    inner class `with twenty pages and eighth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(5, 11, 8)),
            Pagination(20, 8)
    )

    @Nested
    inner class `with twenty pages and nineth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(6, 12, 9)),
            Pagination(20, 9)
    )

    @Nested
    inner class `with twenty pages and tenth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(7, 13, 10)),
            Pagination(20, 10)
    )

    @Nested
    inner class `with twenty pages and eleventh is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(8, 14, 11)),
            Pagination(20, 11)
    )

    @Nested
    inner class `with twenty pages and tweteenth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(9, 15, 12)),
            Pagination(20, 12)
    )

    @Nested
    inner class `with twenty pages and thirteenth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(10, 16, 13)),
            Pagination(20, 13)
    )

    @Nested
    inner class `with twenty pages and fourteenth is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(11, 17, 14)),
            Pagination(20, 14)
    )


    @Nested
    inner class `with twenty pages and fifthteen is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(Page(20, false), true),
                    createPages(12, 18, 15)),
            Pagination(20, 15)
    )

    @Nested
    inner class `with twenty pages and sixthteen is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(13, 20, 16)),
            Pagination(20, 16)
    )

    @Nested
    inner class `with twenty pages and seventhteen is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(13, 20, 17)),
            Pagination(20, 17)
    )

    @Nested
    inner class `with twenty pages and eigthteen is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(13, 20, 18)),
            Pagination(20, 18)
    )

    @Nested
    inner class `with twenty pages and ninethteen is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, true),
                    createPages(13, 20, 19)),
            Pagination(20, 19)
    )

    @Nested
    inner class `with twenty pages and twethty is current`: AbstractPaginationTest(
            Pagination(Side(Page(1, false), true), Side(null, false),
                    createPages(13, 20, 20)),
            Pagination(20, 20)
    )
}