package br.uff.mvpcortes.prajuda.service

import java.util.concurrent.atomic.AtomicBoolean

class SingleRunSection (private val isBusy: AtomicBoolean =AtomicBoolean()){


    fun tryExecute(block:()->Unit, sucessBlock:()->Unit, busyBlock:()->Unit){
        if(isBusy.compareAndSet(false, true)){
            try {
                block()
            }finally {
                isBusy.set(false)
            }
        }else{
            block()
        }
    }
}