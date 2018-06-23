//package br.uff.mvpcortes.prajuda.harvester.git
//
//import java.io.File
//
//class GitTemplate (val workDirectory: File, branch:String) {
//
//    fun safeInit(){
//
//    }
//
//    fun cleanAndClone(){
//        clean();
//        clone();
//    }
//
//    private fun clean() {
//    }
//
//    private fun clone(){
//
//    }
//
//    fun getLocalWorkDir():File{
//        return File(configService.getWorkDirectoryForHarvester(getIdHarvester()), service.name)
//                .takeIf { (it.exists() && it.isDirectory()) || it.mkdir() }
//                ?:throw IllegalStateException("Cannot create workdir for  service ${service.name}")
//    }
//}