package br.uff.mvpcortes.prajuda.controller.helper.pagination

import kotlin.math.max
import kotlin.math.min


class Page(val number:Int=1, val current:Boolean = false)

class Side(var page: Page?, val enable:Boolean = false, val number:Int=1){
    fun hasEllipsis():Boolean = page != null
}

class Pagination(val prev: Side, val next: Side, val pages:List<Page>){

    constructor(qtd:Int, current:Int): this(createPrev(qtd, current), createNext(qtd, current), createPages(qtd, current))
    constructor(prev: Side, next: Side, vararg pages: Page): this(prev, next, pages.toList())


    companion object {

        /**
         * Max number of pages showed in pagination.
         */
        const val WINDOW_SIZE:Int = 9

        private fun createPages(qtd: Int, current: Int): List<Page> {

            when (qtd) {
                0 -> {
                    assert(current == 0) {"Empty pagination should have no current page (0). And not $current"}
                    return emptyList()
                }
                1 -> {
                    assert(current == 1) {"Pagination with one page should have current page equal to 1. And not $current"}
                    return listOf(Page(1, true))
                }
                else -> {
                    assert(current in 1..qtd){"Pagination with $qtd should have current page between [1, $qtd] and not $current"}
                    return if(qtd <= WINDOW_SIZE){
                        (1..qtd).map{ Page(it, it == current) }.toList()
                    }else{
                        (iniWindow(qtd, current)..endWindow(qtd, current)).map{ Page(it, it == current) }.toList()
                    }
                }
            }
        }

        private fun endWindow(qtd: Int, current: Int) =
                if(qtd<= WINDOW_SIZE){
                    qtd
                }else {
                    if(current in (qtd-(WINDOW_SIZE /2)..qtd)){
                        qtd
                    }else {
                        if(current <= (WINDOW_SIZE /2)){
                            WINDOW_SIZE -1
                        }else {
                            (current + (WINDOW_SIZE / 2) -1 + if ((current - WINDOW_SIZE / 2) < 0) {
                                ((WINDOW_SIZE / 2) - current + 1)
                            } else {
                                0
                            })
                        }
                    }
                }

        private fun iniWindow(qtd:Int, current: Int) =
                if(qtd<= WINDOW_SIZE){
                    1
                }else{
                    if(current in 1..(WINDOW_SIZE /2)+1){
                        1
                    }else{
                        if(current >= qtd-(WINDOW_SIZE /2)){
                            qtd-(WINDOW_SIZE -2)
                        } else {

//                            current >= WINDOW_SIZE
                            (current - ((WINDOW_SIZE / 2))) + if(current >= (WINDOW_SIZE /2)+2){1}else{0} - if ((qtd - (current + (WINDOW_SIZE / 2))) < 0) {
                                ((current + (WINDOW_SIZE / 2)) - qtd - 1)
                            } else {
                                0
                            }
                        }
                    }
                }

        private fun createPrev(qtd: Int, current: Int)=
                if(qtd <= WINDOW_SIZE){
                    Side(null, current > 1, max(1, current - 1))
                }else{
                    Side(if (iniWindow(qtd, current) == 1) {
                        null
                    } else {
                        Page(1, current == 1)
                    }, current > 1, max(1, current - 1))
                }

        private fun createNext(qtd: Int, current: Int)=
                if(qtd <= WINDOW_SIZE){
                    Side(null, current < qtd, min(qtd, current + 1))
                }else{
                    Side(if (endWindow(qtd, current) == qtd) {
                        null
                    } else {
                        Page(qtd, current == qtd)
                    }, current < qtd, min(current + 1, qtd))
                }
    }

    fun isEmpty()= pages.isEmpty()

}
