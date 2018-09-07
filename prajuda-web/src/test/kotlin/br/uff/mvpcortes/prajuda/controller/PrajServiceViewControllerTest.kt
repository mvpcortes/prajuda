package br.uff.mvpcortes.prajuda.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


internal class PrajServiceViewControllerTest{

    @ParameterizedTest
    @CsvSource(value=[
        "manager/teste.html, manager, teste, html",
        "service/a.html, service,a, html",
        "a/b/c.html, a, b/c, html",
        "/facebook/person/marcos.html, facebook, person/marcos, html",
        "a/b/c/d/e, a, b/c/d/e, ",
        "a/b/c/d/e.js, a, b/c/d/e, js ",
        "_a/b_/crsdfsdgfsf/4d/e.html, _a, b_/crsdfsdgfsf/4d/e, html"
        ])
    fun `when url is valid then process path and generate valid DocumentPath`(
            strPath:String,
            strService:String?,
            strP:String,
            strExtension:String?){

        val dp =PrajServiceViewController.DocumentPath(strPath)

        assertThat(dp.path).isEqualTo(strP)
        assertThat(dp.serviceName).isEqualTo(strService?:"")
        assertThat(dp.extension).isEqualTo(strExtension?:"")
    }

    @ParameterizedTest
    @CsvSource(value=[
        "a.html, , a, html",
        "main.html, , main, html",
        "lala.js, , lala, js"
    ])
    fun `when url is valid but does not have service then remove service value`(
            strPath:String,
            strService:String?,
            strP:String,
            strExtension:String? ){
        `when url is valid then process path and generate valid DocumentPath`(strPath, strService, strP, strExtension)
    }
}